package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.*;
import com.yz.pferestapi.entity.*;
import com.yz.pferestapi.entity.report.InputRow;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.repository.*;
import com.yz.pferestapi.specification.InputSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InputService {
    private static final String INPUT_LINES_PHOTOS_SUB_FOLDER_NAME = "input-lines-photos";
    private final FileService fileService;
    private final InputRepository inputRepository;
    private final OutputRepository outputRepository;
    private final RegisterRepository registerRepository;
    private final SubRegisterRepository subRegisterRepository;
    private final OwnerRepository ownerRepository;
    private final SourceRepository sourceRepository;
    private final ArticleRepository articleRepository;
    private final TemplateService templateService;

    public Input getInput(Long inputId) {
        return inputRepository.findById(inputId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Input not found"));
    }

    public Page<Input> findInputsByCriteria(InputCriteriaRequest inputCriteria) {
        Specification<Input> spec = Specification.where(null);

        spec = OperationService.filter(inputCriteria, spec);

        if (inputCriteria.getStatus() != null) {
            spec = spec.and(InputSpecifications.statusEquals(inputCriteria.getStatus()));
        }

        if (inputCriteria.getRegisterId() != null) {
            spec = spec.and(InputSpecifications.hasRegister(inputCriteria.getRegisterId()));
        }

        if (inputCriteria.getSubRegisterId() != null) {
            spec = spec.and(InputSpecifications.hasSubRegister(inputCriteria.getSubRegisterId()));
        }

        if (inputCriteria.getOwnerId() != null) {
            spec = spec.and(InputSpecifications.hasOwner(inputCriteria.getOwnerId()));
        }

        if (inputCriteria.getSourceId() != null) {
            spec = spec.and(InputSpecifications.hasSource(inputCriteria.getSourceId()));
        }

        if (inputCriteria.getDescription() != null) {
            spec = spec.and(InputSpecifications.descriptionContains(inputCriteria.getDescription()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(inputCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(inputCriteria, sort);

        return inputRepository.findAll(spec, pageable);
    }

    private List<Input> getFilteredInputList(InputCriteriaRequest inputCriteria) {
        Specification<Input> spec = getInputSpecification(inputCriteria);

        Sort sort = CriteriaRequestUtil.buildSortCriteria(inputCriteria);

        return inputRepository.findAll(spec, sort);
    }

    public ByteArrayOutputStream generateInputsReport(InputCriteriaRequest inputCriteria) throws IOException, JRException {
        InputStream inputStream = templateService.getTemplateFile("input-operation-list-report.jrxml").getInputStream();

        List<Input> filteredInputList = getFilteredInputList(inputCriteria);

        List<InputRow> inputRowList = filteredInputList.stream().map(InputRow::convert).toList();

        JRBeanCollectionDataSource inputsTableDatasource = new JRBeanCollectionDataSource(inputRowList);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("inputsTableDataset", inputsTableDatasource);

        JasperReport report = JasperCompileManager.compileReport(inputStream);
        JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setCompressed(true);
        exporter.setConfiguration(configuration);
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
        exporter.exportReport();
        return byteArrayOutputStream;
    }

    @Transactional
    public Input createInput(UpsertInputDto upsertInputDto) throws IOException {
        if (inputRepository.existsByNumberAndYear(upsertInputDto.getNumber(), upsertInputDto.getYear())
                || outputRepository.existsByNumberAndYear(upsertInputDto.getNumber(), upsertInputDto.getYear())) {
            throw new AppException(HttpStatus.CONFLICT, "Operation already exists!");
        }

        Register register = null;
        if (upsertInputDto.getRegisterId() != null) {
            register = registerRepository.findById(upsertInputDto.getRegisterId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Register not found"));
        }

        SubRegister subRegister = null;
        if (upsertInputDto.getSubRegisterId() != null) {
            subRegister = subRegisterRepository.findById(upsertInputDto.getSubRegisterId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "SubRegister not found"));
        }

        Owner owner = null;
        if (upsertInputDto.getOwnerId() != null) {
            owner = ownerRepository.findById(upsertInputDto.getOwnerId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Owner not found"));
        }

        Source source = null;
        if (upsertInputDto.getSourceId() != null) {
            source = sourceRepository.findById(upsertInputDto.getSourceId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Source not found"));
        }

        checkForDuplicateArticleIds(upsertInputDto.getInputOperationLines());

        Input input = new Input();
        input.setRegister(register);
        input.setSubRegister(subRegister);
        input.setOwner(owner);
        input.setSource(source);

        input.setNumber(upsertInputDto.getNumber());
        input.setYear(upsertInputDto.getYear());
        input.setDateTime(Instant.parse(upsertInputDto.getDateTime()));

        List<InputOperationLine> inputOperationLines = new ArrayList<>();
        for (UpsertInputOperationLineDto upsertOperationLineDto : upsertInputDto.getInputOperationLines()) {
            Article article = articleRepository.findById(upsertOperationLineDto.getArticleId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article not found"));

            InputOperationLine inputOperationLine = new InputOperationLine();
            inputOperationLine.setInput(input);
            inputOperationLine.setArticle(article);
            inputOperationLine.setQuantity(upsertOperationLineDto.getQuantity());
            inputOperationLine.setRemainingQuantity(inputOperationLine.getQuantity());
            inputOperationLine.setNightlyAmount(upsertOperationLineDto.getNightlyAmount());
            inputOperationLine.setSubTotalNightlyAmount(inputOperationLine.getNightlyAmount() * inputOperationLine.getQuantity());
            inputOperationLine.setTransportFee(upsertOperationLineDto.getTransportFee());
            inputOperationLine.setDescription(upsertOperationLineDto.getDescription());
            inputOperationLine.setObservation(upsertOperationLineDto.getObservation());
            inputOperationLine.setNote(upsertOperationLineDto.getNote());

            if (upsertOperationLineDto.getPhotoFile() != null && !upsertOperationLineDto.getPhotoFile().isEmpty()) {
                String photoPath = fileService.uploadFile(upsertOperationLineDto.getPhotoFile(), INPUT_LINES_PHOTOS_SUB_FOLDER_NAME);
                inputOperationLine.setPhotoPath(photoPath);
            }

            inputOperationLines.add(inputOperationLine);
        }
        input.setInputOperationLines(inputOperationLines);

        return inputRepository.save(input);
    }

    @Transactional
    public Input updateInput(UpsertInputDto upsertInputDto, Long inputId) throws IOException {
        Input input = inputRepository.findById(inputId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Input not found"));

        if (inputRepository.existsByNumberAndYearAndIdNot(upsertInputDto.getNumber(), upsertInputDto.getYear(), inputId)
                || outputRepository.existsByNumberAndYear(upsertInputDto.getNumber(), upsertInputDto.getYear())) {
            throw new AppException(HttpStatus.CONFLICT, "Operation already exists!");
        }

        Register register = null;
        if (upsertInputDto.getRegisterId() != null) {
            register = registerRepository.findById(upsertInputDto.getRegisterId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Register not found"));
        }

        SubRegister subRegister = null;
        if (upsertInputDto.getSubRegisterId() != null) {
            subRegister = subRegisterRepository.findById(upsertInputDto.getSubRegisterId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "SubRegister not found"));
        }

        Owner owner = null;
        if (upsertInputDto.getOwnerId() != null) {
            owner = ownerRepository.findById(upsertInputDto.getOwnerId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Owner not found"));
        }

        Source source = null;
        if (upsertInputDto.getSourceId() != null) {
            source = sourceRepository.findById(upsertInputDto.getSourceId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Source not found"));
        }

        checkForDuplicateArticleIds(upsertInputDto.getInputOperationLines());

        input.setRegister(register);
        input.setSubRegister(subRegister);
        input.setOwner(owner);
        input.setSource(source);

        input.setNumber(upsertInputDto.getNumber());
        input.setYear(upsertInputDto.getYear());
        input.setDateTime(Instant.parse(upsertInputDto.getDateTime()));

        // case: input operation line exists in dto and db
        // -> update db
        for (InputOperationLine inputOperationLine : input.getInputOperationLines()) {
            for (UpsertInputOperationLineDto upsertOperationLineDto: upsertInputDto.getInputOperationLines()) {
                if (Objects.equals(inputOperationLine.getArticle().getId(), upsertOperationLineDto.getArticleId())) {
                    inputOperationLine.setQuantity(upsertOperationLineDto.getQuantity());
                    inputOperationLine.setRemainingQuantity(inputOperationLine.getQuantity());
                    inputOperationLine.setNightlyAmount(upsertOperationLineDto.getNightlyAmount());
                    inputOperationLine.setSubTotalNightlyAmount(inputOperationLine.getNightlyAmount() * inputOperationLine.getQuantity());
                    inputOperationLine.setTransportFee(upsertOperationLineDto.getTransportFee());
                    inputOperationLine.setDescription(upsertOperationLineDto.getDescription());
                    inputOperationLine.setObservation(upsertOperationLineDto.getObservation());
                    inputOperationLine.setNote(upsertOperationLineDto.getNote());

                    if (upsertOperationLineDto.getPhotoFile() != null && !upsertOperationLineDto.getPhotoFile().isEmpty()) {
                        String photoPath = fileService.uploadFile(upsertOperationLineDto.getPhotoFile(), INPUT_LINES_PHOTOS_SUB_FOLDER_NAME);

                        String oldPhotoPath = inputOperationLine.getPhotoPath();
                        if (oldPhotoPath != null && !oldPhotoPath.isEmpty()) {
                            fileService.deleteFile(oldPhotoPath);
                        }

                        inputOperationLine.setPhotoPath(photoPath);
                    } else if (upsertOperationLineDto.getDeletePhotoFileIfExists()) {
                        String oldPhotoPath = inputOperationLine.getPhotoPath();
                        if (oldPhotoPath != null && !oldPhotoPath.isEmpty()) {
                            fileService.deleteFile(oldPhotoPath);
                        }
                        inputOperationLine.setPhotoPath(null);
                    }
                }
            }
        }

        // case: input operation line exists in db but not in dto
        // -> remove it from db
        Iterator<InputOperationLine> iter = input.getInputOperationLines().iterator();
        while (iter.hasNext()) {
            InputOperationLine inputOperationLine = iter.next();

            boolean existsInDto = false;
            for (UpsertOperationLineDto upsertOperationLineDto : upsertInputDto.getInputOperationLines()) {
                if (Objects.equals(inputOperationLine.getArticle().getId(), upsertOperationLineDto.getArticleId())) {
                    existsInDto = true;
                    break;
                }
            }
            if (!existsInDto) {
                iter.remove();

                String oldPhotoPath = inputOperationLine.getPhotoPath();
                if (oldPhotoPath != null && !oldPhotoPath.isEmpty()) {
                    fileService.deleteFile(oldPhotoPath);
                }
            }
        }

        // case: input operation line exists in dto but not in db
        // -> add it to db
        for (UpsertInputOperationLineDto upsertInputOperationLineDto: upsertInputDto.getInputOperationLines()) {
            boolean existsInDb = false;
            for (InputOperationLine inputOperationLine : input.getInputOperationLines()) {
                if (Objects.equals(upsertInputOperationLineDto.getArticleId(), inputOperationLine.getArticle().getId())) {
                    existsInDb = true;
                    break;
                }
            }
            if (!existsInDb) {
                Article article = articleRepository.findById(upsertInputOperationLineDto.getArticleId())
                        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article not found"));

                InputOperationLine inputOperationLine = new InputOperationLine();
                inputOperationLine.setInput(input);
                inputOperationLine.setArticle(article);
                inputOperationLine.setQuantity(upsertInputOperationLineDto.getQuantity());
                inputOperationLine.setRemainingQuantity(inputOperationLine.getQuantity());
                inputOperationLine.setNightlyAmount(upsertInputOperationLineDto.getNightlyAmount());
                inputOperationLine.setSubTotalNightlyAmount(inputOperationLine.getNightlyAmount() * inputOperationLine.getQuantity());
                inputOperationLine.setTransportFee(upsertInputOperationLineDto.getTransportFee());
                inputOperationLine.setDescription(upsertInputOperationLineDto.getDescription());
                inputOperationLine.setObservation(upsertInputOperationLineDto.getObservation());
                inputOperationLine.setNote(upsertInputOperationLineDto.getNote());

                if (upsertInputOperationLineDto.getPhotoFile() != null && !upsertInputOperationLineDto.getPhotoFile().isEmpty()) {
                    String photoPath = fileService.uploadFile(upsertInputOperationLineDto.getPhotoFile(), INPUT_LINES_PHOTOS_SUB_FOLDER_NAME);
                    inputOperationLine.setPhotoPath(photoPath);
                }

                input.getInputOperationLines().add(inputOperationLine);
            }
        }

        return inputRepository.save(input);
    }

    public void deleteInput(Long id) throws IOException {
        Input input = inputRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Input not found"));

        inputRepository.delete(input);

        for (InputOperationLine inputOperationLine: input.getInputOperationLines()) {
            String oldPhotoPath = inputOperationLine.getPhotoPath();
            if (oldPhotoPath != null && !oldPhotoPath.isEmpty()) {
                fileService.deleteFile(oldPhotoPath);
            }
        }
    }

    public Input updateInputOwner(UpdateInputOwnerDto updateInputOwnerDto, Long inputId) {
        Input input = inputRepository.findById(inputId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Input not found"));

        Owner newInputOwner = ownerRepository.findById(updateInputOwnerDto.getNewOwnerId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Owner not found"));

        input.setOwner(newInputOwner);

        return inputRepository.save(input);
    }

    public static <T extends UpsertOperationLineDto> void checkForDuplicateArticleIds(List<T> upsertOperationLines) {
        Set<Long> idSet = new HashSet<>();

        for (UpsertOperationLineDto upsertOperationLineDto : upsertOperationLines) {
            Long articleId = upsertOperationLineDto.getArticleId();
            if (!idSet.add(articleId)) {
                throw new AppException(HttpStatus.CONFLICT, "Duplicate article ID found: " + articleId);
            }
        }
    }
}
