package com.example.authenticationserver.dao;

import com.example.authenticationserver.domain.entity.User;

import java.util.Optional;

public interface UserDAO {
    public User findUser(Integer userId);
    public Optional<User> findUserByUserName(String username);
    public Integer createUser(User user);
    public User deleteUser(Integer userId);
    public void setUserStatus(Integer userId, boolean activate);
}
