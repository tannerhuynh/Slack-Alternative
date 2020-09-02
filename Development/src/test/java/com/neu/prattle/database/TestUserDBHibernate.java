package com.neu.prattle.database;

import com.neu.prattle.model.User;
import com.neu.prattle.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.mockito.Mock;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestUserDBHibernate {
    private UserDBHibernate userDB;
    private List<User> testUserList;

    @Before
    public void setUp() {
        userDB = UserDBHibernate.getInstance();
        userDB.testMode();
        testUserList = new ArrayList<>();
        testUserList.add(new User("Test0", "123", "abc0", "first", "last", "bio"));
        testUserList.add(new User("Test1", "123", "abc1", "first", "last", "bio"));
        testUserList.add(new User("Test2", "123", "abc2", "first", "last", "bio"));
    }

    @Test
    public void testUserDBHibernate() throws SQLException{
        SessionFactory sessionFactoryMock = Mockito.mock(SessionFactory.class);
        Session sessionMock = Mockito.mock(Session.class);
        Query queryMock = Mockito.mock(Query.class);
        Transaction transactionMock = Mockito.mock(Transaction.class);

        HibernateUtil.setSessionFactory(sessionFactoryMock);

        Mockito.doReturn(sessionMock).when(sessionFactoryMock).openSession();
        Mockito.doReturn(queryMock).when(sessionMock).createQuery("from User ", User.class);
        Mockito.doReturn(testUserList).when(queryMock).list();

        assertEquals(3, userDB.getAllUsers().size());

        Mockito.doReturn(testUserList.get(0)).when(sessionMock).get(User.class, testUserList.get(0).getUsername());
        assertTrue(userDB.findUserByName(testUserList.get(0).getUsername()).isPresent());
        assertEquals(testUserList.get(0).getUsername(), userDB.findUserByName(testUserList.get(0).getUsername()).get().getUsername());
        assertFalse(userDB.findUserByName(testUserList.get(1).getUsername()).isPresent());

        Mockito.doReturn(transactionMock).when(sessionMock).beginTransaction();
        userDB.addUser(testUserList.get(0));
        userDB.removeUser(testUserList.get(0));

        Mockito.doReturn(queryMock).when(sessionMock).createQuery("delete User ");
        userDB.removeAllUsers();

        userDB.updateUser(testUserList.get(0));

        Mockito.doThrow(new RuntimeException()).when(transactionMock).commit();
        try{
            userDB.addUser(testUserList.get(0));
        } catch (SQLException e){
            assertEquals(SQLException.class, e.getClass());
        }
        try{
            userDB.removeUser(testUserList.get(0));
        } catch (SQLException e){
            assertEquals(SQLException.class, e.getClass());
        }
        try{
            userDB.updateUser(testUserList.get(0));
        } catch (SQLException e){
            assertEquals(SQLException.class, e.getClass());
        }

        Mockito.doThrow(new RuntimeException()).when(sessionFactoryMock).openSession();
        try{
            userDB.getAllUsers();
        } catch (SQLException e){
            assertEquals(SQLException.class, e.getClass());
        }
        try{
            userDB.findUserByName(testUserList.get(0).getUsername());
        } catch (SQLException e){
            assertEquals(SQLException.class, e.getClass());
        }
        try{
            userDB.removeAllUsers();
        } catch (SQLException e){
            assertEquals(SQLException.class, e.getClass());
        }
    }
}
