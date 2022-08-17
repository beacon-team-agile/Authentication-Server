package com.example.authenticationserver.dao.impl;

import com.example.authenticationserver.dao.AbstractHibernateDAO;
import com.example.authenticationserver.dao.RegTokenDAO;
import com.example.authenticationserver.domain.entity.RegistrationToken;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
public class RegTokenDAOImpl extends AbstractHibernateDAO<RegistrationToken> implements RegTokenDAO {
    Session session;
    CriteriaBuilder cb;
    CriteriaQuery<RegistrationToken> registrationTokenCR;
    Root<RegistrationToken> registrationTokenRoot;

    @Autowired
    public RegTokenDAOImpl() {
        setClazz(RegistrationToken.class);
    }

    private void initializeHouseSession() {
        session = getCurrentSession();
        cb = session.getCriteriaBuilder();
        registrationTokenCR = cb.createQuery(RegistrationToken.class);
        registrationTokenRoot = registrationTokenCR.from(RegistrationToken.class);
    }


    @Override
    public RegistrationToken getTokenByTokenKey(String token) {
        initializeHouseSession();
        registrationTokenCR.select(registrationTokenRoot);
        registrationTokenCR.where(cb.equal(registrationTokenRoot.get("token"), token));
        Query<RegistrationToken> inner_query = session.createQuery(registrationTokenCR);
        Optional<RegistrationToken> options = inner_query.getResultList().stream().findAny();
        return options.orElse(null);
    }

    public void addRegisterToken(RegistrationToken registrationToken) {
        initializeHouseSession();
        add(registrationToken);
    }
}
