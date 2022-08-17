package com.example.authenticationserver.dao.impl;

import com.example.authenticationserver.dao.AbstractHibernateDAO;
import com.example.authenticationserver.dao.RoleDAO;
import com.example.authenticationserver.domain.entity.Role;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
public class RoleDAOImpl extends AbstractHibernateDAO<Role> implements RoleDAO {

    Session session;
    CriteriaBuilder cb;
    CriteriaQuery<Role> roleCR;
    Root<Role> roleRoot;

    @Autowired
    public RoleDAOImpl() {
        setClazz(Role.class);
    }

    private void initializeHouseSession() {
        session = getCurrentSession();
        cb = session.getCriteriaBuilder();
        roleCR = cb.createQuery(Role.class);
        roleRoot = roleCR.from(Role.class);
    }

    @Override
    public Optional<Role> findRoleById(Integer roleId) {
        initializeHouseSession();
        roleCR.select(roleRoot);
        roleCR.where(cb.equal(roleRoot.get("id"), roleId));
        Query<Role> inner_query = session.createQuery(roleCR);
        return inner_query.getResultList().stream().findAny();
    }
}
