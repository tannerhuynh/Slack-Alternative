package com.neu.prattle.database;

import com.neu.prattle.model.Message;
import com.neu.prattle.utils.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * implementation of {@link IMessageDB} using Hibernate
 *
 * @author Team 9
 * @version 2019-11-11
 */
public class MessageDBHibernate implements IMessageDB {
    /**
     * singleton
     */
    private static MessageDBHibernate messageDB = new MessageDBHibernate();

    public static MessageDBHibernate getInstance() {
        return messageDB;
    }

    private MessageDBHibernate() {
    }

    /**
     * Set test mode for the database interface. Should be called and only be called for test
     * purposes.
     */
    @Override
    public void testMode() {
        //do nothing
    }

    /**
     * add a message to the database
     *
     * @param message message to be added
     * @throws SQLException if database error occurs
     */
    @Override
    public void addMessage(Message message) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(message);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * get all messages between two input users in the database
     *
     * @param user1 the first user's username
     * @param user2 the second user's username
     * @return set of all messages between user1 and user2
     * @throws SQLException if database error occurs
     */
    @Override
    public Set<Message> getMessagesBetweenUsers(String user1, String user2) throws SQLException {
        List<Message> messages;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Message> query = session.createQuery("from Message where (fromUsername = :user1 and toUsername = :user2) or (fromUsername = :user2 and toUsername = :user1)", Message.class);
            query.setParameter("user1", user1);
            query.setParameter("user2", user2);
            messages = query.list();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
        return new HashSet<>(messages);
    }

    /**
     * get all messages from input user in the database
     *
     * @param from user's username
     * @return set of all messages the input user sent
     * @throws SQLException if database error occurs
     */
    @Override
    public Set<Message> getMessagesByFrom(String from) throws SQLException {
        List<Message> messages;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            messages = session.createQuery("from Message where fromUsername = :from_username", Message.class).setParameter("from_username", from).list();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
        return new HashSet<>(messages);
    }

    /**
     * get all messages to input user in the database
     *
     * @param to user's username
     * @return set of all messages sent to the input user
     * @throws SQLException if database error occurs
     */
    @Override
    public Set<Message> getMessagesByToUser(String to) throws SQLException {
        List<Message> messages;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            messages = session.createQuery("from Message where toUsername = :to_username", Message.class).setParameter("to_username", to).list();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
        return new HashSet<>(messages);
    }

    /**
     * get all messages to the input channel in the database
     *
     * @param to channel id
     * @return set of all messages sent to the input channel
     * @throws SQLException if database error occurs
     */
    @Override
    public Set<Message> getMessagesByToChannel(int to) throws SQLException {
        List<Message> messages;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            messages = session.createQuery("from Message where toChannelId = :to_channel", Message.class).setParameter("to_channel", to).list();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
        return new HashSet<>(messages);

    }



    /**
     * delete the input message from database
     *
     * @param message message to delete
     * @throws SQLException if database error occurs
     */
    @Override
    public void removeMessage(Message message) throws SQLException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.delete(message);
            session.flush();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * update the input message in the database
     *
     * @param message message to update
     * @throws SQLException if database error occurs
     */
    @Override
    public void updateMessage(Message message) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(message);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Remove all messages in the database.
     *
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public void removeAllMessages() throws SQLException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("delete Message ");
            query.executeUpdate();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }
}
