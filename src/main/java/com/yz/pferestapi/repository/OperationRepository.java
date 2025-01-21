package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {
    Boolean existsByNumberAndYear(Long number, Integer year);

    Boolean existsByNumberAndYearAndIdNot(Long number, Integer year, Long id);

    @Query("SELECT MONTH(io.dateTime), COALESCE(SUM(iol.quantity), 0), COALESCE(SUM(ool.quantity), 0) " +
            "FROM Input io " +
            "LEFT JOIN io.inputOperationLines iol " +
            "LEFT JOIN Output oo ON oo.input.id = io.id " +
            "LEFT JOIN oo.outputOperationLines ool " +
            "LEFT JOIN iol.article a " +
            "LEFT JOIN a.articleFamily af " +
            "LEFT JOIN io.register r " +
            "WHERE (:articleFamilyId IS NULL OR af.id = :articleFamilyId) " +
            "AND (:registerId IS NULL OR r.id = :registerId) " +
            "AND YEAR(io.dateTime) = :year " +
            "AND (:month IS NULL OR MONTH(io.dateTime) = :month) " +
            "GROUP BY MONTH(io.dateTime)")
    List<Object[]> getMonthlyCombinedQuantitiesByMonth(@Param("articleFamilyId") Long articleFamilyId,
                                                       @Param("registerId") Long registerId,
                                                       @Param("year") Integer year,
                                                       @Param("month") Integer month);

    @Query("SELECT MONTH(io.dateTime), COALESCE(SUM(iol.quantity), 0), COALESCE(SUM(ool.quantity), 0) " +
            "FROM Input io " +
            "LEFT JOIN io.inputOperationLines iol " +
            "LEFT JOIN Output oo ON oo.input.id = io.id " +
            "LEFT JOIN oo.outputOperationLines ool " +
            "LEFT JOIN iol.article a " +
            "LEFT JOIN a.articleFamily af " +
            "LEFT JOIN io.register r " +
            "WHERE (:articleFamilyId IS NULL OR af.id = :articleFamilyId) " +
            "AND (:registerId IS NULL OR r.id = :registerId) " +
            "AND YEAR(io.dateTime) = :year " +
            "GROUP BY MONTH(io.dateTime)")
    List<Object[]> getMonthlyCombinedQuantitiesByYear(@Param("articleFamilyId") Long articleFamilyId,
                                                      @Param("registerId") Long registerId,
                                                      @Param("year") Integer year);

}
