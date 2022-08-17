package com.example.authenticationserver.service;

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

    @Autowired
    public UserService(UserDAOImpl hibernateUserDAO) {
        this.hibernateUserDAO = hibernateUserDAO;
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
        Optional<User> userOptional = hibernateUserDAO.findUserByUserName(username);

        if (!userOptional.isPresent()){
            throw new UsernameNotFoundException("Username does not exist");
        }

        User user = userOptional.get(); // database user
        return AuthUserDetail.builder()
                .username(user.getUserName())
                .password(new BCryptPasswordEncoder().encode(user.getPassword()))
                .authorities(getAuthoritiesFromUser(user))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    private List<GrantedAuthority> getAuthoritiesFromUser(User user){
        List<GrantedAuthority> userAuthorities = new ArrayList<>();

        if (user.getUserRoles() == null) {
            return null;
        } else {
            System.out.println(user.getUserRoles());
        }

        for (UserRole userRole : user.getUserRoles()){
            Role role = userRole.getRoleEntity();
            if (role != null) {
                userAuthorities.add(new SimpleGrantedAuthority(role.getRoleName()));
            }
        }

        return userAuthorities;
    }
}
