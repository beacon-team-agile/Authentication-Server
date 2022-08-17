package com.example.authenticationserver.dao.impl;

import com.example.authenticationserver.dao.AbstractHibernateDAO;
import com.example.authenticationserver.dao.UserRoleDAO;
import com.example.authenticationserver.domain.entity.UserRole;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleDAOImpl extends AbstractHibernateDAO<UserRole> implements UserRoleDAO {

}
