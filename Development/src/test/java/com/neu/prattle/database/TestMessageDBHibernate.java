package com.neu.prattle.database;

import com.neu.prattle.model.Message;
import com.neu.prattle.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

public class TestMessageDBHibernate {
    private MessageDBHibernate messageDB;
    private List<Message> messageList;

    @Before
    public void setUp(){
        messageDB = MessageDBHibernate.getInstance();
        messageDB.testMode();
        messageList = new ArrayList<>();
        messageList.add(Message.messageBuilder().setFrom("dian").setToUsername("alice").setMessageContent("123").build());
    }

    @Test
    public void testMessageDBHibernate() throws SQLException{
        SessionFactory sessionFactoryMock = Mockito.mock(SessionFactory.class);
        Session sessionMock = Mockito.mock(Session.class);
        Query queryMock = Mockito.mock(Query.class);
        Transaction transactionMock = Mockito.mock(Transaction.class);

        HibernateUtil.setSessionFactory(sessionFactoryMock);

        Mockito.doReturn(transactionMock).when(sessionMock).beginTransaction();
        Mockito.doReturn(sessionMock).when(sessionFactoryMock).openSession();
        Mockito.doReturn(queryMock).when(sessionMock).createQuery(any(String.class), any());
        Mockito.doReturn(queryMock).when(queryMock).setParameter(any(String.class), any());
        Mockito.doReturn(messageList).when(queryMock).list();

        messageDB.addMessage(messageList.get(0));
        messageDB.removeMessage(messageList.get(0));
        messageDB.updateMessage(messageList.get(0));

        assertEquals(1, messageDB.getMessagesBetweenUsers("dian", "alice").size());
        assertEquals(1, messageDB.getMessagesByFrom("dian").size());
        assertEquals(1, messageDB.getMessagesByToUser("alice").size());
        assertEquals(1, messageDB.getMessagesByToChannel(1).size());

        Mockito.doThrow(new RuntimeException()).when(transactionMock).commit();

        try{
            messageDB.addMessage(messageList.get(0));
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }

        try{
            messageDB.updateMessage(messageList.get(0));
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }

        Mockito.doThrow(new RuntimeException()).when(sessionFactoryMock).openSession();

        try{
            messageDB.removeMessage(messageList.get(0));
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }

        try{
            messageDB.getMessagesBetweenUsers("dian", "alice");
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }

        try{
            messageDB.getMessagesByFrom("dian");
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }

        try{
            messageDB.getMessagesByToUser("alice");
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }

        try{
            messageDB.getMessagesByToChannel(1);
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }
    }
}
