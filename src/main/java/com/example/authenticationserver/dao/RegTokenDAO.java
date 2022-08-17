package com.example.authenticationserver.dao;

import com.example.authenticationserver.domain.entity.RegistrationToken;
import com.example.authenticationserver.domain.entity.User;

import java.util.Optional;

public interface RegTokenDAO {
    public RegistrationToken getTokenByTokenKey(String token);
}
