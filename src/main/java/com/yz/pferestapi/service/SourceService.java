package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.SourceCriteriaRequest;
import com.yz.pferestapi.dto.UpsertSourceDto;
import com.yz.pferestapi.entity.Source;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.SourceMapper;
import com.yz.pferestapi.repository.SourceRepository;
import com.yz.pferestapi.specification.SourceSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceService {
    private final SourceRepository sourceRepository;

    public Source getSource(Long sourceId) {
        return sourceRepository.findById(sourceId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Source not found"));
    }

    public List<Source> findSourcesByName(String search) {
        Specification<Source> spec = Specification.where(null);

        spec = spec.and(SourceSpecifications.nameContains(search));

        return sourceRepository.findAll(spec);
    }

    public Page<Source> findSourcesByCriteria(SourceCriteriaRequest sourceCriteria) {
        Specification<Source> spec = Specification.where(null);

        if (sourceCriteria.getName() != null) {
            spec = spec.and(SourceSpecifications.nameContains(sourceCriteria.getName()));
        }

        if (sourceCriteria.getDescription() != null) {
            spec = spec.and(SourceSpecifications.descriptionContains(sourceCriteria.getDescription()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(sourceCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(sourceCriteria, sort);

        return sourceRepository.findAll(spec, pageable);
    }

    public Source createSource(UpsertSourceDto upsertSourceDto) {
        if (sourceRepository.existsByNameIgnoreCase(upsertSourceDto.getName())) {
            throw new AppException(HttpStatus.CONFLICT, "Name already exists!");
        }

        Source source = SourceMapper.toEntity(upsertSourceDto);

        return sourceRepository.save(source);
    }

    public Source updateSource(UpsertSourceDto upsertSourceDto, Long sourceId) {
        if (sourceRepository.existsByNameIgnoreCaseAndIdNot(upsertSourceDto.getName(), sourceId)) {
            throw new AppException(HttpStatus.CONFLICT, "Name already exists!");
        }

        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Source not found"));

        Source updatedSource = SourceMapper.toEntity(upsertSourceDto, source);

        return sourceRepository.save(updatedSource);
    }

    public void deleteSource(Long id) {
        Source source = sourceRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Source not found"));

        sourceRepository.delete(source);
    }
}
