package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.InputOperationLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InputOperationLineRepository extends JpaRepository<InputOperationLine, Long> {

}
