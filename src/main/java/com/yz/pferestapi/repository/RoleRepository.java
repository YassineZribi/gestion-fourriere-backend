package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.Role;
import com.yz.pferestapi.entity.RoleEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);
    List<Role> findAllByNameNot(RoleEnum name);
}
