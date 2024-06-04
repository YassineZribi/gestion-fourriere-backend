package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.InputCriteriaRequest;
import com.yz.pferestapi.dto.UpsertInputDto;
import com.yz.pferestapi.dto.UpsertInputOperationLineDto;
import com.yz.pferestapi.dto.UpsertOperationLineDto;
import com.yz.pferestapi.entity.*;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.repository.*;
import com.yz.pferestapi.specification.InputSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InputService {
    private final InputRepository inputRepository;
    private final OutputRepository outputRepository;
    private final RegisterRepository registerRepository;
    private final SubRegisterRepository subRegisterRepository;
    private final OwnerRepository ownerRepository;
    private final SourceRepository sourceRepository;
    private final ArticleRepository articleRepository;

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

        Sort sort = CriteriaRequestUtil.buildSortCriteria(inputCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(inputCriteria, sort);

        return inputRepository.findAll(spec, pageable);
    }

    @Transactional
    public Input createInput(UpsertInputDto upsertInputDto) {
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

            inputOperationLines.add(inputOperationLine);
        }
        input.setInputOperationLines(inputOperationLines);

        return inputRepository.save(input);
    }

    @Transactional
    public Input updateInput(UpsertInputDto upsertInputDto, Long inputId) {
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

        for (InputOperationLine inputOperationLine : input.getInputOperationLines()) {
            for (UpsertInputOperationLineDto upsertOperationLineDto: upsertInputDto.getInputOperationLines()) {
                if (Objects.equals(inputOperationLine.getArticle().getId(), upsertOperationLineDto.getArticleId())) {
                    inputOperationLine.setQuantity(upsertOperationLineDto.getQuantity());
                    inputOperationLine.setRemainingQuantity(inputOperationLine.getQuantity());
                    inputOperationLine.setNightlyAmount(upsertOperationLineDto.getNightlyAmount());
                    inputOperationLine.setSubTotalNightlyAmount(inputOperationLine.getNightlyAmount() * inputOperationLine.getQuantity());
                    inputOperationLine.setTransportFee(upsertOperationLineDto.getTransportFee());
                }
            }
        }

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
            if (!existsInDto)
                iter.remove();
        }

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

                input.getInputOperationLines().add(inputOperationLine);
            }
        }

        return inputRepository.save(input);
    }

    public void deleteInput(Long id) {
        Input input = inputRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Input not found"));

        inputRepository.delete(input);
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
