package com.example.authenticationserver.service;

import com.example.authenticationserver.dao.impl.RoleDAOImpl;
import com.example.authenticationserver.dao.impl.UserDAOImpl;
import com.example.authenticationserver.dao.impl.UserRoleDAOImpl;
import com.example.authenticationserver.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserRoleService {
    private final UserRoleDAOImpl userRoleDAO;

    @Autowired
    public UserRoleService(UserRoleDAOImpl userRoleDAO) {
        this.userRoleDAO = userRoleDAO;
    }

    @Transactional
    public void addUserRole(UserRole userRole) {
        userRoleDAO.add(userRole);
    }

}
