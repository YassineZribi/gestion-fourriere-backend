package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {
    Optional<Warehouse> findByDeletedIsFalseAndId(Long id);

    List<Warehouse> findAllByDeletedIsFalse();

    List<Warehouse> findByManagerId(Long id);
}
