package com.example.authenticationserver.service;

import com.example.authenticationserver.dao.impl.RoleDAOImpl;
import com.example.authenticationserver.dao.impl.UserDAOImpl;
import com.example.authenticationserver.domain.entity.Role;
import com.example.authenticationserver.domain.entity.User;
import com.example.authenticationserver.domain.entity.UserRole;
import com.example.authenticationserver.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserDAOImpl hibernateUserDAO;
    private final RoleDAOImpl roleDAO;

    @Autowired
    public UserService(UserDAOImpl hibernateUserDAO,
                       RoleDAOImpl roleDAO) {
        this.hibernateUserDAO = hibernateUserDAO;
        this.roleDAO = roleDAO;
    }

    @Transactional
    public User findUser(Integer userId) {
        return hibernateUserDAO.findUser(userId);
    }

    @Transactional
    public Integer creatUser(User user) {
        return hibernateUserDAO.createUser(user);
    }

    @Transactional
    public void creatUserDetail(UserRole userRole) {
        hibernateUserDAO.createUserDetail(userRole);
    }

    @Transactional
    public void removeUser(Integer userId) {
        hibernateUserDAO.deleteUser(userId);
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = hibernateUserDAO.findUserByUserName(username);

        if (user == null){
            throw new UsernameNotFoundException("Username does not exist");
        }

        return AuthUserDetail.builder()
                .username(user.getUsername())
                .password(new BCryptPasswordEncoder().encode(user.getPassword()))
                .authorities(getAuthoritiesFromUser(user))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    @Transactional
    public List<GrantedAuthority> getAuthoritiesFromUser(User user){
        List<GrantedAuthority> userAuthorities = new ArrayList<>();

        if (user.getUserRoles() == null) {
            return null;
        }

        for (UserRole userRole : user.getUserRoles()){
            Integer id = userRole.getRoleId();
            Optional<Role> roleOptional = roleDAO.findRoleById(id);
            roleOptional.ifPresent(role -> userAuthorities.add(new SimpleGrantedAuthority(role.getRoleName())));
        }

        return userAuthorities;
    }

    @Transactional
    public User getUserByName(String username) {
        return hibernateUserDAO.findUserByUserName(username);
    }
}
