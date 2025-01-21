package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.OutputCriteriaRequest;
import com.yz.pferestapi.dto.UpsertOutputDto;
import com.yz.pferestapi.entity.*;
import com.yz.pferestapi.entity.report.OutputRow;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.repository.ArticleRepository;
import com.yz.pferestapi.repository.InputOperationLineRepository;
import com.yz.pferestapi.repository.InputRepository;
import com.yz.pferestapi.repository.OutputRepository;
import com.yz.pferestapi.specification.OutputSpecifications;
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
public class OutputService {
    private final OutputRepository outputRepository;
    private final InputRepository inputRepository;
    private final ArticleRepository articleRepository;
    private final InputOperationLineRepository inputOperationLineRepository;
    private final TemplateService templateService;

    public Output getOutput(Long outputId) {
        return outputRepository.findById(outputId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Output not found"));
    }

    public Page<Output> findOutputsByCriteria(OutputCriteriaRequest outputCriteria) {
        Specification<Output> spec = Specification.where(null);

        spec = OperationService.filter(outputCriteria, spec);

        if (outputCriteria.getDiscount() != null) {
            spec = spec.and(OutputSpecifications.discountIs(outputCriteria.getDiscount()));
        }

        if (outputCriteria.getTotalPaymentAmount() != null) {
            spec = spec.and(OutputSpecifications.totalPaymentAmountContains(outputCriteria.getTotalPaymentAmount()));
        }

        if (outputCriteria.getReceiptNumber() != null) {
            spec = spec.and(OutputSpecifications.receiptNumberContains(outputCriteria.getReceiptNumber()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(outputCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(outputCriteria, sort);

        return outputRepository.findAll(spec, pageable);
    }

    private List<Output> getFilteredOutputList(OutputCriteriaRequest outputCriteria) {
        Specification<Output> spec = getOutputSpecification(outputCriteria);

        Sort sort = CriteriaRequestUtil.buildSortCriteria(outputCriteria);

        return outputRepository.findAll(spec, sort);
    }

    public ByteArrayOutputStream generateOutputsReport(OutputCriteriaRequest outputCriteria) throws IOException, JRException {
        InputStream inputStream = templateService.getTemplateFile("output-operation-list-report.jrxml").getInputStream();

        List<Output> filteredOutputList = getFilteredOutputList(outputCriteria);

        List<OutputRow> outputRowList = filteredOutputList.stream().map(OutputRow::convert).toList();

        JRBeanCollectionDataSource outputsTableDatasource = new JRBeanCollectionDataSource(outputRowList);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("outputsTableDataset", outputsTableDatasource);

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
    public Output createOutput(UpsertOutputDto upsertOutputDto) {
        if (outputRepository.existsByNumberAndYear(upsertOutputDto.getNumber(), upsertOutputDto.getYear())
                || inputRepository.existsByNumberAndYear(upsertOutputDto.getNumber(), upsertOutputDto.getYear())) {
            throw new AppException(HttpStatus.CONFLICT, "Operation already exists!");
        }

        Input input = null;
        if (upsertOutputDto.getInputId() != null) {
            input = inputRepository.findById(upsertOutputDto.getInputId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Input not found"));
        }

        checkForDuplicateInputOperationLineIds(upsertOutputDto.getOutputOperationLines());

        Output output = new Output();

        output.setNumber(upsertOutputDto.getNumber());
        output.setYear(upsertOutputDto.getYear());
        output.setDateTime(Instant.parse(upsertOutputDto.getDateTime()));

        output.setNightCount(upsertOutputDto.getNightCount());
        output.setTotalTransportFee(upsertOutputDto.getTotalTransportFee());
        output.setTotalPaymentAmountWithoutDiscount(upsertOutputDto.getTotalPaymentAmountWithoutDiscount());
        output.setDiscountAmount(upsertOutputDto.getDiscountAmount());
        output.setDiscountObservation(upsertOutputDto.getDiscountObservation());
        output.setDiscount(output.getDiscountAmount() > 0);
        output.setTotalPaymentAmount(output.getTotalPaymentAmountWithoutDiscount() - output.getDiscountAmount());
        output.setReceiptNumber(upsertOutputDto.getReceiptNumber());
        output.setReceiptDateTime(Instant.parse(upsertOutputDto.getReceiptDateTime()));
        output.setInput(input);

        List<OutputOperationLine> outputOperationLines = new ArrayList<>();
        for (UpsertOutputOperationLineDto upsertOutputOperationLineDto : upsertOutputDto.getOutputOperationLines()) {
            Article article = articleRepository.findById(upsertOutputOperationLineDto.getArticleId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article not found"));

            InputOperationLine inputOperationLine = inputOperationLineRepository.findById(upsertOutputOperationLineDto.getInputOperationLineId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Input operation line not found"));

            double inputOperationLineRemainingQuantity = inputOperationLine.getRemainingQuantity() - upsertOutputOperationLineDto.getQuantity();
            if (inputOperationLineRemainingQuantity < 0)
                throw new AppException(HttpStatus.BAD_REQUEST, "The remaining quantity is insufficient for the input operation line with id " + inputOperationLine.getId());
            inputOperationLine.setRemainingQuantity(inputOperationLineRemainingQuantity);
            if (inputOperationLineRemainingQuantity == 0) {
                inputOperationLine.setStatus(ProcessingStatus.FULLY_OUT);
                inputOperationLine.setFullyOut(true);
            } else {
                inputOperationLine.setStatus(ProcessingStatus.PARTIALLY_OUT);
                inputOperationLine.setFullyOut(false);
            }
            inputOperationLineRepository.save(inputOperationLine);

            updateInputStatus(inputOperationLine.getInput());

            OutputOperationLine outputOperationLine = new OutputOperationLine();
            outputOperationLine.setOutput(output);
            outputOperationLine.setArticle(article);
            outputOperationLine.setInputOperationLine(inputOperationLine);
            outputOperationLine.setQuantity(upsertOutputOperationLineDto.getQuantity());
            Boolean unitCalculation = article.getArticleFamily().getUnitCalculation();
            outputOperationLine.setSubTotalPaymentAmount(
                    upsertOutputDto.getNightCount() * inputOperationLine.getNightlyAmount() * (
                            unitCalculation ? outputOperationLine.getQuantity() : 1
                            )
            );

            outputOperationLines.add(outputOperationLine);
        }
        output.setOutputOperationLines(outputOperationLines);

        return outputRepository.save(output);
    }

    @Transactional
    public void deleteOutput(Long id) {
        Output output = outputRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Output not found"));

        outputRepository.delete(output);

        for (OutputOperationLine outputOperationLine: output.getOutputOperationLines()) {
            InputOperationLine inputOperationLine = outputOperationLine.getInputOperationLine();
            double inputOperationLinePreviousRemainingQuantity = inputOperationLine.getRemainingQuantity() + outputOperationLine.getQuantity();
            inputOperationLine.setRemainingQuantity(inputOperationLinePreviousRemainingQuantity);
            if (inputOperationLine.getRemainingQuantity() < inputOperationLine.getQuantity()) {
                inputOperationLine.setStatus(ProcessingStatus.PARTIALLY_OUT);
            } else {
                inputOperationLine.setStatus(ProcessingStatus.FULLY_IN);
            }
            inputOperationLine.setFullyOut(false);
            inputOperationLineRepository.save(inputOperationLine);

            updateInputStatus(inputOperationLine.getInput());
        }
    }

    private void updateInputStatus(Input inputOperation) {
        boolean hasFullyIn = false;
        boolean hasPartiallyOut = false;
        boolean hasFullyOut = false;

        for (InputOperationLine inputLine : inputOperation.getInputOperationLines()) {
            switch (inputLine.getStatus()) {
                case FULLY_IN:
                    hasFullyIn = true;
                    break;
                case PARTIALLY_OUT:
                    hasPartiallyOut = true;
                    break;
                case FULLY_OUT:
                    hasFullyOut = true;
                    break;
            }
        }

        if (hasFullyIn && !hasPartiallyOut && !hasFullyOut) { // -> Fully In
            inputOperation.setStatus(ProcessingStatus.FULLY_IN);
        } else if (hasFullyOut && !hasFullyIn && !hasPartiallyOut) { // -> Fully Out
            inputOperation.setStatus(ProcessingStatus.FULLY_OUT);
        } else { // -> Partially Out
            inputOperation.setStatus(ProcessingStatus.PARTIALLY_OUT);
        }
        inputRepository.save(inputOperation);
    }

    public static void checkForDuplicateInputOperationLineIds(List<UpsertOutputOperationLineDto> upsertOutputOperationLines) {
        Set<Long> idSet = new HashSet<>();

        for (UpsertOutputOperationLineDto upsertOutputOperationLineDto : upsertOutputOperationLines) {
            Long inputOperationLineId = upsertOutputOperationLineDto.getInputOperationLineId();
            if (!idSet.add(inputOperationLineId)) {
                throw new AppException(HttpStatus.CONFLICT, "Duplicate input operation line ID found: " + inputOperationLineId);
            }
        }
    }
}
