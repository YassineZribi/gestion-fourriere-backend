package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.Input;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InputRepository extends JpaRepository<Input, Long>, JpaSpecificationExecutor<Input> {

}
