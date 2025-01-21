package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.CombinedQuantityDto;
import com.yz.pferestapi.dto.IncomeStatisticsCriteriaRequest;
import com.yz.pferestapi.dto.IncomeStatisticsDto;
import com.yz.pferestapi.dto.OperationsQuantityStatisticsCriteriaRequest;
import com.yz.pferestapi.service.DashboardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/dashboard")
@RestController
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/operations-quantity-statistics")
    public ResponseEntity<List<CombinedQuantityDto>> getOperationsQuantityStatistics(@Valid OperationsQuantityStatisticsCriteriaRequest criteria) {
        System.out.println("criteria = " + criteria);
        List<CombinedQuantityDto> combinedQuantities = dashboardService.getCombinedQuantities(criteria);
        return ResponseEntity.ok(combinedQuantities);
    }
    @GetMapping("/operations-quantity-statisticsV2")
    public ResponseEntity<List<CombinedQuantityDto>> getOperationsQuantityStatisticsV2(@Valid OperationsQuantityStatisticsCriteriaRequest criteria) {
        System.out.println("criteria = " + criteria);
        List<CombinedQuantityDto> combinedQuantities = dashboardService.getCombinedQuantitiesV2(criteria);
        return ResponseEntity.ok(combinedQuantities);
    }

    @GetMapping("/income-statistics")
    public ResponseEntity<List<IncomeStatisticsDto>> getIncomeStatistics(@Valid IncomeStatisticsCriteriaRequest criteria) {
        System.out.println("criteria = " + criteria);
        List<IncomeStatisticsDto> incomeStatistics = dashboardService.getIncomeStatistics(criteria);
        return ResponseEntity.ok(incomeStatistics);
    }

    @GetMapping("/income-statisticsV2")
    public ResponseEntity<List<IncomeStatisticsDto>> getIncomeStatisticsV2(@Valid IncomeStatisticsCriteriaRequest criteria) {
        System.out.println("criteria = " + criteria);
        List<IncomeStatisticsDto> incomeStatistics = dashboardService.getIncomeStatisticsV2(criteria);
        return ResponseEntity.ok(incomeStatistics);
    }
}
