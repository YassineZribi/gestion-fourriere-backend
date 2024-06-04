package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.OperationLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationLineRepository extends JpaRepository<OperationLine, Long> {

}
