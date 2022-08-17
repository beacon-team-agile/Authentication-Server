package com.example.authenticationserver.dao;

import com.example.authenticationserver.domain.entity.Role;

import java.util.Optional;


public interface RoleDAO {
    public Optional<Role> findRoleById(Integer roleId);
}
