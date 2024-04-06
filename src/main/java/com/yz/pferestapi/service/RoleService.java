package com.yz.pferestapi.service;

import com.yz.pferestapi.entity.Role;
import com.yz.pferestapi.entity.RoleEnum;
import com.yz.pferestapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> getNonAdminRoles() {
        return roleRepository.findAllByNameNot(RoleEnum.ADMIN);
    }

    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        roleRepository.findAll().forEach(roles::add);
        return roles;
    }
}
