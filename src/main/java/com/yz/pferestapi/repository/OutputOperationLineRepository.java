package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.OutputOperationLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OutputOperationLineRepository extends JpaRepository<OutputOperationLine, Long>, JpaSpecificationExecutor<OutputOperationLine> {

}
