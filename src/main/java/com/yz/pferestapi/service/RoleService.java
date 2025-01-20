package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.RoleDto;
import com.yz.pferestapi.entity.RoleEnum;
import com.yz.pferestapi.mapper.RoleMapper;
import com.yz.pferestapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<RoleDto> getNonAdminRoles() {
        return roleRepository.findAllByNameNot(RoleEnum.ADMIN).stream().map(RoleMapper::toDto).toList();
    }

    public List<RoleDto> getAllRoles() {
        List<RoleDto> roles = new ArrayList<>();
        roleRepository.findAll().forEach(role -> roles.add(RoleMapper.toDto(role)));
        return roles;
    }
}
