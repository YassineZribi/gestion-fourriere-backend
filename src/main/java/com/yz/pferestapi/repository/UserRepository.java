package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByEmailIgnoreCase(String email);

    Boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
