package com.neu.prattle.database;

import com.neu.prattle.model.User;
import com.neu.prattle.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * implementation of {@link IUserDB} using Hibernate
 *
 * @author Team 9
 * @version 2019-11-11
 */
public class UserDBHibernate implements IUserDB {
    /**
     * singleton
     */
    private static UserDBHibernate userDB = new UserDBHibernate();
    public static UserDBHibernate getInstance() {
        return userDB;
    }
    private UserDBHibernate(){}

    /**
     * set test mode for the database
     */
    @Override
    public void testMode() {
        // do nothing
    }

    /**
     * get all users from the user table
     *
     * @return set of all users
     */
    @Override
    public Set<User> getAllUsers() throws SQLException {
        Set<User> userSet = new HashSet<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("from User ", User.class).list();
            userSet.addAll(users);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
        return userSet;
    }

    /***
     * Returns an optional object which might be empty or wraps an object
     * if the Database contains a {@link User} object having the same name
     * as the parameter.
     *
     * @param name The name of the user
     * @return Optional object.
     */
    @Override
    public Optional<User> findUserByName(String name) throws SQLException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, name);
            if (user != null) {
                return Optional.of(user);
            }
            else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    /***
     * Add a user in the database
     * @param user User object
     *
     */
    @Override
    public void addUser(User user) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * remove a user in the database
     *
     * @param user User Object
     */
    @Override
    public void removeUser(User user) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(user);
            transaction.commit();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * remove all users in the database
     */
    @Override
    public void removeAllUsers() throws SQLException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("delete User ");
            query.executeUpdate();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * update a user in the database
     *
     * @param user the user to update on
     */
    @Override
    public void updateUser(User user) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new SQLException(e.getMessage());
        }
    }
}