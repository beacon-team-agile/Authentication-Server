package com.example.authenticationserver.dao.impl;

import com.example.authenticationserver.dao.AbstractHibernateDAO;
import com.example.authenticationserver.dao.UserDAO;
import com.example.authenticationserver.domain.entity.User;
import com.example.authenticationserver.domain.entity.UserRole;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl extends AbstractHibernateDAO<User> implements UserDAO {
    Session session;
    CriteriaBuilder cb;
    CriteriaQuery<User> userCR;
    Root<User> userRoot;

    @Autowired
    public UserDAOImpl() {
        setClazz(User.class);
    }

    private void initializeUserSession() {
        session = getCurrentSession();
        cb = session.getCriteriaBuilder();
        userCR = cb.createQuery(User.class);
        userRoot = userCR.from(User.class);
    }

    @Override
    public User findUser(Integer userId) {
        initializeUserSession();
        userCR.select(userRoot);
        userCR.where(cb.equal(userRoot.get("id"), userId));
        Query<User> query = session.createQuery(userCR);
        Optional<User> result = query.getResultList().stream().findAny();
        return result.orElse(null);
    }

    public User findUserByUserName(String username) {
        System.out.println(username);
        initializeUserSession();
        userCR.select(userRoot);
        userCR.where(cb.equal(userRoot.get("username"), username));
        Query<User> query = session.createQuery(userCR);
        return query.getResultList().stream().findAny().orElse(null);
    }

    @Override
    public Integer createUser(User user) {
        session = getCurrentSession();
        return (Integer) session.save(user);
    }

    public void createUserDetail(UserRole userRole) {
        session = getCurrentSession();
        session.save(userRole);
    }


    @Override
    public User deleteUser(Integer userId) {
        initializeUserSession();
        //Delete user detail data first
        CriteriaQuery<UserRole> userRoleCR = cb.createQuery(UserRole.class);
        Root<UserRole> userRoleRoot = userRoleCR.from(UserRole.class);
        userRoleCR.select(userRoleRoot);
        userRoleCR.where(cb.equal(userRoleRoot.get("id"), userId));
        Query<UserRole> query1 = session.createQuery(userRoleCR);
        query1.getResultList().stream().findFirst().ifPresent(userDetail -> session.delete(userDetail));

        //Delete user data first
        userCR.select(userRoot);
        userCR.where(cb.equal(userRoot.get("id"), userId));
        Query<User> query2 = session.createQuery(userCR);
        User user = query2.getResultList().stream().findFirst().orElse(null);
        if (user != null) {
            session.delete(user);
        }

        return user;
    }

    @Override
    public void setUserStatus(Integer userId, boolean activate) {
        initializeUserSession();
        userCR.select(userRoot);
        userCR.where(cb.equal(userRoot.get("id"), userId));
        Query<User> query = session.createQuery(userCR);
        User user = query.getResultList().stream().findFirst().orElse(null);
        if (user != null) {
            System.out.println("Updating user: " + user.getId() + " to: " + activate);
            session = getCurrentSession();
            user.setActiveFlag(activate);
            session.update(user);
        }
    }

    public Optional<User> findUserByName(String username) {
        initializeUserSession();
        userCR.select(userRoot);
        userCR.where(cb.equal(userRoot.get("userName"), username));
        Query<User> query = session.createQuery(userCR);
        return query.getResultList().stream().findFirst();
    }

    public Optional<User> findUserByEmail(String email) {
        initializeUserSession();
        userCR.select(userRoot);
        userCR.where(cb.equal(userRoot.get("email"), email));
        Query<User> query = session.createQuery(userCR);
        return query.getResultList().stream().findFirst();
    }
}
