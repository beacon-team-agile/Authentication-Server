package com.example.authenticationserver.service;

import com.example.authenticationserver.dao.RegTokenDAO;
import com.example.authenticationserver.dao.impl.RegTokenDAOImpl;
import com.example.authenticationserver.dao.impl.UserDAOImpl;
import com.example.authenticationserver.domain.entity.RegistrationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RegTokenService {

    private final RegTokenDAOImpl regTokenDAO;

    @Autowired
    public RegTokenService(RegTokenDAOImpl regTokenDAO) {
        this.regTokenDAO = regTokenDAO;
    }

    @Transactional
    public RegistrationToken getExistingToken(String token) {
        return regTokenDAO.getTokenByTokenKey(token);
    }

    @Transactional
    public void addRegisterToken(RegistrationToken registrationToken) {
        regTokenDAO.add(registrationToken);
    }
}
