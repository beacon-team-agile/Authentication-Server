package com.example.authenticationserver.dao.impl;

import com.example.authenticationserver.dao.AbstractHibernateDAO;
import com.example.authenticationserver.dao.UserRoleDAO;
import com.example.authenticationserver.domain.entity.User;
import com.example.authenticationserver.domain.entity.UserRole;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;

@Repository
public class UserRoleDAOImpl extends AbstractHibernateDAO<UserRole> implements UserRoleDAO {
    Session session;
    CriteriaBuilder cb;
    CriteriaQuery<User> userRoleCR;
    Root<User> userRoleRoot;

    @Autowired
    public UserRoleDAOImpl() {
        setClazz(UserRole.class);
    }

    private void initializeUserSession() {
        session = getCurrentSession();
        cb = session.getCriteriaBuilder();
        userRoleCR = cb.createQuery(User.class);
        userRoleRoot = userRoleCR.from(User.class);
    }


}
