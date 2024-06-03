package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.InputCriteriaRequest;
import com.yz.pferestapi.dto.UpsertInputDto;
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
    private final RegisterRepository registerRepository;
    private final SubRegisterRepository subRegisterRepository;
    private final OwnerRepository ownerRepository;
    private final SourceRepository sourceRepository;
    private final ArticleRepository articleRepository;
    private final OperationRepository operationRepository;

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
        if (operationRepository.existsByNumberAndYear(upsertInputDto.getNumber(), upsertInputDto.getYear())) {
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

        checkForDuplicateArticleIds(upsertInputDto.getOperationLines());

        Input input = new Input();
        input.setRegister(register);
        input.setSubRegister(subRegister);
        input.setOwner(owner);
        input.setSource(source);

        input.setNumber(upsertInputDto.getNumber());
        input.setYear(upsertInputDto.getYear());
        input.setDateTime(Instant.parse(upsertInputDto.getDateTime()));

        List<OperationLine> operationLines = new ArrayList<>();
        for (UpsertOperationLineDto upsertOperationLineDto : upsertInputDto.getOperationLines()) {
            Article article = articleRepository.findById(upsertOperationLineDto.getArticleId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article not found"));

            OperationLine operationLine = new OperationLine();
            operationLine.setOperation(input);
            operationLine.setArticle(article);
            operationLine.setQuantity(upsertOperationLineDto.getQuantity());
            operationLine.setNightlyAmount(upsertOperationLineDto.getNightlyAmount());
            operationLine.setTransportFee(upsertOperationLineDto.getTransportFee());
            operationLine.setLineTotalAmount(operationLine.getQuantity() * operationLine.getNightlyAmount() + operationLine.getTransportFee());

            operationLines.add(operationLine);
        }
        input.setOperationLines(operationLines);

        return inputRepository.save(input);
    }

    @Transactional
    public Input updateInput(UpsertInputDto upsertInputDto, Long inputId) {
        Input input = inputRepository.findById(inputId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Input not found"));

        if (operationRepository.existsByNumberAndYearAndIdNot(upsertInputDto.getNumber(), upsertInputDto.getYear(), inputId)) {
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

        checkForDuplicateArticleIds(upsertInputDto.getOperationLines());

        input.setRegister(register);
        input.setSubRegister(subRegister);
        input.setOwner(owner);
        input.setSource(source);

        input.setNumber(upsertInputDto.getNumber());
        input.setYear(upsertInputDto.getYear());
        input.setDateTime(Instant.parse(upsertInputDto.getDateTime()));

        for (OperationLine operationLine : input.getOperationLines()) {
            for (UpsertOperationLineDto upsertOperationLineDto: upsertInputDto.getOperationLines()) {
                if (Objects.equals(operationLine.getArticle().getId(), upsertOperationLineDto.getArticleId())) {
                    operationLine.setQuantity(upsertOperationLineDto.getQuantity());
                    operationLine.setNightlyAmount(upsertOperationLineDto.getNightlyAmount());
                    operationLine.setTransportFee(upsertOperationLineDto.getTransportFee());
                    operationLine.setLineTotalAmount(operationLine.getQuantity() * operationLine.getNightlyAmount() + operationLine.getTransportFee());
                }
            }
        }

        Iterator<OperationLine> iter = input.getOperationLines().iterator();
        while (iter.hasNext()) {
            OperationLine operationLine = iter.next();

            boolean existsInDto = false;
            for (UpsertOperationLineDto upsertOperationLineDto : upsertInputDto.getOperationLines()) {
                if (Objects.equals(operationLine.getArticle().getId(), upsertOperationLineDto.getArticleId())) {
                    existsInDto = true;
                    break;
                }
            }
            if (!existsInDto)
                iter.remove();
        }

        for (UpsertOperationLineDto upsertOperationLineDto: upsertInputDto.getOperationLines()) {
            boolean existsInDb = false;
            for (OperationLine operationLine : input.getOperationLines()) {
                if (Objects.equals(upsertOperationLineDto.getArticleId(), operationLine.getArticle().getId())) {
                    existsInDb = true;
                    break;
                }
            }
            if (!existsInDb) {
                Article article = articleRepository.findById(upsertOperationLineDto.getArticleId())
                        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article not found"));

                OperationLine operationLine = new OperationLine();
                operationLine.setOperation(input);
                operationLine.setArticle(article);
                operationLine.setQuantity(upsertOperationLineDto.getQuantity());
                operationLine.setNightlyAmount(upsertOperationLineDto.getNightlyAmount());
                operationLine.setTransportFee(upsertOperationLineDto.getTransportFee());
                operationLine.setLineTotalAmount(operationLine.getQuantity() * operationLine.getNightlyAmount() + operationLine.getTransportFee());

                input.getOperationLines().add(operationLine);
            }
        }

        return inputRepository.save(input);
    }

    public void deleteInput(Long id) {
        Input input = inputRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Input not found"));

        inputRepository.delete(input);
    }

    public static void checkForDuplicateArticleIds(List<UpsertOperationLineDto> upsertOperationLines) {
        Set<Long> idSet = new HashSet<>();

        for (UpsertOperationLineDto upsertOperationLineDto : upsertOperationLines) {
            Long articleId = upsertOperationLineDto.getArticleId();
            if (!idSet.add(articleId)) {
                throw new AppException(HttpStatus.CONFLICT, "Duplicate article ID found: " + articleId);
            }
        }
    }
}
