package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.CombinedQuantityDto;
import com.yz.pferestapi.dto.IncomeStatisticsCriteriaRequest;
import com.yz.pferestapi.dto.IncomeStatisticsDto;
import com.yz.pferestapi.dto.OperationsQuantityStatisticsCriteriaRequest;
import com.yz.pferestapi.entity.InputOperationLine;
import com.yz.pferestapi.entity.Output;
import com.yz.pferestapi.entity.OutputOperationLine;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.repository.InputOperationLineRepository;
import com.yz.pferestapi.repository.OperationRepository;
import com.yz.pferestapi.repository.OutputOperationLineRepository;
import com.yz.pferestapi.repository.OutputRepository;
import com.yz.pferestapi.specification.InputOperationLineSpecifications;
import com.yz.pferestapi.specification.OutputOperationLineSpecifications;
import com.yz.pferestapi.specification.OutputSpecifications;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final InputOperationLineRepository inputOperationLineRepository;
    private final OutputOperationLineRepository outputOperationLineRepository;

    private final OutputRepository outputRepository;

    private final OperationRepository operationRepository;

    public List<CombinedQuantityDto> getCombinedQuantitiesV2(OperationsQuantityStatisticsCriteriaRequest criteria) {
        if (criteria.getArticleFamilyId() != null && criteria.getRegisterId() != null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Only one of articleFamilyId or registerId should have a value!");
        }

        DateRange dateRange = getDateRange(criteria.getMonth(), criteria.getYear());

        Specification<InputOperationLine> inputSpec = Specification.where(null);
        Specification<OutputOperationLine> outputSpec = Specification.where(null);

        if (criteria.getArticleFamilyId() != null) {
            inputSpec = inputSpec.and(InputOperationLineSpecifications.hasArticleFamily(criteria.getArticleFamilyId()));
            outputSpec = outputSpec.and(OutputOperationLineSpecifications.hasArticleFamily(criteria.getArticleFamilyId()));
        }

        if (criteria.getRegisterId() != null) {
            inputSpec = inputSpec.and(InputOperationLineSpecifications.hasRegister(criteria.getRegisterId()));
            outputSpec = outputSpec.and(OutputOperationLineSpecifications.hasRegister(criteria.getRegisterId()));
        }

        if (criteria.getSourceId() != null) {
            inputSpec = inputSpec.and(InputOperationLineSpecifications.hasSource(criteria.getSourceId()));
            outputSpec = outputSpec.and(OutputOperationLineSpecifications.hasSource(criteria.getSourceId()));
        }

        inputSpec = inputSpec.and(InputOperationLineSpecifications.isWithinDateRange(dateRange.getStartDate(), dateRange.getEndDate()));
        outputSpec = outputSpec.and(OutputOperationLineSpecifications.isWithinDateRange(dateRange.getStartDate(), dateRange.getEndDate()));

        List<InputOperationLine> inputLines = inputOperationLineRepository.findAll(inputSpec);
        List<OutputOperationLine> outputLines = outputOperationLineRepository.findAll(outputSpec);

        return combineQuantities(inputLines, outputLines);
    }

    private List<CombinedQuantityDto> combineQuantities(List<InputOperationLine> inputLines, List<OutputOperationLine> outputLines) {
        Map<Instant, CombinedQuantityDto> combinedMap = new HashMap<>();

        for (InputOperationLine input : inputLines) {
            Instant dateTime = input.getInput().getDateTime();
            combinedMap.putIfAbsent(dateTime, new CombinedQuantityDto(dateTime, 0.0, 0.0));
            combinedMap.get(dateTime).setInputQuantity(
                    combinedMap.get(dateTime).getInputQuantity() + input.getQuantity());
        }

        for (OutputOperationLine output : outputLines) {
            Instant dateTime = output.getOutput().getDateTime();
            combinedMap.putIfAbsent(dateTime, new CombinedQuantityDto(dateTime, 0.0, 0.0));
            combinedMap.get(dateTime).setOutputQuantity(
                    combinedMap.get(dateTime).getOutputQuantity() + output.getQuantity());
        }

        return combinedMap.values().stream().sorted(Comparator.comparing(CombinedQuantityDto::getDate)).collect(Collectors.toList());
    }

    public List<CombinedQuantityDto> getCombinedQuantities(OperationsQuantityStatisticsCriteriaRequest criteria) {
        if (criteria.getArticleFamilyId() != null && criteria.getRegisterId() != null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Only one of articleFamilyId or registerId should have a value!");
        }

        DateRange dateRange = getDateRange(criteria.getMonth(), criteria.getYear());

        Specification<InputOperationLine> inputSpec = Specification.where(null);
        Specification<OutputOperationLine> outputSpec = Specification.where(null);

        if (criteria.getArticleFamilyId() != null) {
            inputSpec = inputSpec.and(InputOperationLineSpecifications.hasArticleFamily(criteria.getArticleFamilyId()));
            outputSpec = outputSpec.and(OutputOperationLineSpecifications.hasArticleFamily(criteria.getArticleFamilyId()));
        }

        if (criteria.getRegisterId() != null) {
            inputSpec = inputSpec.and(InputOperationLineSpecifications.hasRegister(criteria.getRegisterId()));
            outputSpec = outputSpec.and(OutputOperationLineSpecifications.hasRegister(criteria.getRegisterId()));
        }

        if (criteria.getSourceId() != null) {
            inputSpec = inputSpec.and(InputOperationLineSpecifications.hasSource(criteria.getSourceId()));
            outputSpec = outputSpec.and(OutputOperationLineSpecifications.hasSource(criteria.getSourceId()));
        }

        inputSpec = inputSpec.and(InputOperationLineSpecifications.isWithinDateRange(dateRange.getStartDate(), dateRange.getEndDate()));
        outputSpec = outputSpec.and(OutputOperationLineSpecifications.isWithinDateRange(dateRange.getStartDate(), dateRange.getEndDate()));

        List<InputOperationLine> inputLines = inputOperationLineRepository.findAll(inputSpec);
        List<OutputOperationLine> outputLines = outputOperationLineRepository.findAll(outputSpec);

        if (criteria.getMonth() != null) {
            return fillMissingDays(criteria.getYear(), criteria.getMonth(), inputLines, outputLines);
        } else {
            return fillMissingMonths(criteria.getYear(), inputLines, outputLines);
        }
    }

    private List<CombinedQuantityDto> fillMissingDays(int year, int month, List<InputOperationLine> inputLines, List<OutputOperationLine> outputLines) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        Map<LocalDate, CombinedQuantityDto> resultMap = new HashMap<>();

        for (InputOperationLine input : inputLines) {
            LocalDate date = input.getInput().getDateTime().atZone(ZoneOffset.UTC).toLocalDate();
            resultMap.putIfAbsent(date, new CombinedQuantityDto(date.atStartOfDay(ZoneOffset.UTC).toInstant(), 0.0, 0.0));
            resultMap.get(date).setInputQuantity(
                    resultMap.get(date).getInputQuantity() + input.getQuantity());
        }

        for (OutputOperationLine output : outputLines) {
            LocalDate date = output.getOutput().getDateTime().atZone(ZoneOffset.UTC).toLocalDate();
            resultMap.putIfAbsent(date, new CombinedQuantityDto(date.atStartOfDay(ZoneOffset.UTC).toInstant(), 0.0, 0.0));
            resultMap.get(date).setOutputQuantity(
                    resultMap.get(date).getOutputQuantity() + output.getQuantity());
        }

        List<CombinedQuantityDto> dtos = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dtos.add(resultMap.getOrDefault(date, new CombinedQuantityDto(date.atStartOfDay(ZoneOffset.UTC).toInstant(), 0.0, 0.0)));
        }

        return dtos;
    }

    private List<CombinedQuantityDto> fillMissingMonths(int year, List<InputOperationLine> inputLines, List<OutputOperationLine> outputLines) {
        YearMonth startMonth = YearMonth.of(year, 1);
        YearMonth endMonth = YearMonth.of(year, 12);

        Map<YearMonth, CombinedQuantityDto> resultMap = new HashMap<>();

        for (InputOperationLine input : inputLines) {
            YearMonth month = YearMonth.from(input.getInput().getDateTime().atZone(ZoneOffset.UTC));
            resultMap.putIfAbsent(month, new CombinedQuantityDto(month.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant(), 0.0, 0.0));
            resultMap.get(month).setInputQuantity(
                    resultMap.get(month).getInputQuantity() + input.getQuantity());
        }

        for (OutputOperationLine output : outputLines) {
            YearMonth month = YearMonth.from(output.getOutput().getDateTime().atZone(ZoneOffset.UTC));
            resultMap.putIfAbsent(month, new CombinedQuantityDto(month.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant(), 0.0, 0.0));
            resultMap.get(month).setOutputQuantity(
                    resultMap.get(month).getOutputQuantity() + output.getQuantity());
        }

        List<CombinedQuantityDto> dtos = new ArrayList<>();
        for (YearMonth month = startMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
            dtos.add(resultMap.getOrDefault(month, new CombinedQuantityDto(month.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant(), 0.0, 0.0)));
        }

        return dtos;
    }

    public List<IncomeStatisticsDto> getIncomeStatisticsV2(IncomeStatisticsCriteriaRequest criteria) {
        if (criteria.getArticleFamilyId() != null && criteria.getRegisterId() != null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Only one of articleFamilyId or registerId should have a value!");
        }

        DateRange dateRange = getDateRange(criteria.getMonth(), criteria.getYear());

        Specification<Output> outputSpec = Specification.where(null);

//        if (criteria.getArticleFamilyId() != null) {
//            outputSpec = outputSpec.and(OutputSpecifications.hasArticleFamily(criteria.getArticleFamilyId()));
//        }

        if (criteria.getRegisterId() != null) {
            outputSpec = outputSpec.and(OutputSpecifications.hasRegister(criteria.getRegisterId()));
        }

        if (criteria.getSourceId() != null) {
            outputSpec = outputSpec.and(OutputSpecifications.hasSource(criteria.getSourceId()));
        }

        outputSpec = outputSpec.and(OutputSpecifications.isWithinDateRange(dateRange.getStartDate(), dateRange.getEndDate()));

        List<Output> outputs = outputRepository.findAll(outputSpec);

        return combineIncome(outputs);
    }

    private List<IncomeStatisticsDto> combineIncome(List<Output> outputs) {
        Map<Instant, IncomeStatisticsDto> combinedMap = new HashMap<>();

        for (Output output : outputs) {
            Instant dateTime = output.getDateTime();
            combinedMap.putIfAbsent(dateTime, new IncomeStatisticsDto(dateTime, 0.0));
            combinedMap.get(dateTime).setIncomes(
                    combinedMap.get(dateTime).getIncomes() + output.getTotalPaymentAmount());
        }

        return combinedMap.values().stream().sorted(Comparator.comparing(IncomeStatisticsDto::getDate)).collect(Collectors.toList());
    }

    public List<IncomeStatisticsDto> getIncomeStatistics(IncomeStatisticsCriteriaRequest criteria) {
        if (criteria.getArticleFamilyId() != null && criteria.getRegisterId() != null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Only one of articleFamilyId or registerId should have a value!");
        }

        DateRange dateRange = getDateRange(criteria.getMonth(), criteria.getYear());

        Specification<Output> outputSpec = Specification.where(null);

//        if (criteria.getArticleFamilyId() != null) {
//            outputSpec = outputSpec.and(OutputSpecifications.hasArticleFamily(criteria.getArticleFamilyId()));
//        }

        if (criteria.getRegisterId() != null) {
            outputSpec = outputSpec.and(OutputSpecifications.hasRegister(criteria.getRegisterId()));
        }

        if (criteria.getSourceId() != null) {
            outputSpec = outputSpec.and(OutputSpecifications.hasSource(criteria.getSourceId()));
        }

        outputSpec = outputSpec.and(OutputSpecifications.isWithinDateRange(dateRange.getStartDate(), dateRange.getEndDate()));

        List<Output> outputs = outputRepository.findAll(outputSpec);

        if (criteria.getMonth() != null) {
            return fillMissingDays(criteria.getYear(), criteria.getMonth(), outputs);
        } else {
            return fillMissingMonths(criteria.getYear(), outputs);
        }
    }

    private List<IncomeStatisticsDto> fillMissingDays(int year, int month, List<Output> outputs) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        Map<LocalDate, IncomeStatisticsDto> resultMap = new HashMap<>();

        for (Output output : outputs) {
            LocalDate date = output.getDateTime().atZone(ZoneOffset.UTC).toLocalDate();
            resultMap.putIfAbsent(date, new IncomeStatisticsDto(date.atStartOfDay(ZoneOffset.UTC).toInstant(), 0.0));
            resultMap.get(date).setIncomes(
                    resultMap.get(date).getIncomes() + output.getTotalPaymentAmount());
        }

        List<IncomeStatisticsDto> dtos = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dtos.add(resultMap.getOrDefault(date, new IncomeStatisticsDto(date.atStartOfDay(ZoneOffset.UTC).toInstant(), 0.0)));
        }

        return dtos;
    }

    private List<IncomeStatisticsDto> fillMissingMonths(int year, List<Output> outputs) {
        YearMonth startMonth = YearMonth.of(year, 1);
        YearMonth endMonth = YearMonth.of(year, 12);

        Map<YearMonth, IncomeStatisticsDto> resultMap = new HashMap<>();

        for (Output output : outputs) {
            YearMonth month = YearMonth.from(output.getDateTime().atZone(ZoneOffset.UTC));
            resultMap.putIfAbsent(month, new IncomeStatisticsDto(month.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant(), 0.0));
            resultMap.get(month).setIncomes(
                    resultMap.get(month).getIncomes() + output.getTotalPaymentAmount());
        }

        List<IncomeStatisticsDto> dtos = new ArrayList<>();
        for (YearMonth month = startMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
            dtos.add(resultMap.getOrDefault(month, new IncomeStatisticsDto(month.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant(), 0.0)));
        }

        return dtos;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class DateRange {
        Date startDate, endDate;
    }

    private DateRange getDateRange(Integer month, Integer year) {
        DateRange dateRange = new DateRange();
        if (month == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0);
            dateRange.setStartDate(calendar.getTime());
            calendar.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
            dateRange.setEndDate(calendar.getTime());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, 1, 0, 0, 0);
            dateRange.setStartDate(calendar.getTime());
            calendar.set(year, month - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            dateRange.setEndDate(calendar.getTime());
        }
        return dateRange;
    }

}