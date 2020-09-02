package com.neu.prattle.database;

import com.neu.prattle.model.Channel;
import com.neu.prattle.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ChannelDBHibernate implements IChannelDB {
    /**
     * Singleton
     */
    private static ChannelDBHibernate channelDB = new ChannelDBHibernate();

    /**
     * Get the singleton instance
     * @return channelDB
     */
    public static ChannelDBHibernate getInstance() {
        return channelDB;
    }

    /**
     * Constructor
     */
    private ChannelDBHibernate() {
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
     * Get all channels in the database
     *
     * @return all channels in the database
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public Set<Channel> getAllChannels() throws SQLException {
        List<Channel> channels;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Channel> query = session.createQuery("from Channel ", Channel.class);
            channels = query.list();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
        return new HashSet<>(channels);
    }

    /**
     * Get channel with id in the database
     *
     * @param id id of the channel
     * @return optional channel with input id
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public Optional<Channel> getChannel(int id) throws SQLException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Channel channel = session.get(Channel.class, id);
            if (channel != null) {
                return Optional.of(channel);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Get channel with name in the database
     *
     * @param name name of the channel
     * @return set of channels with input name
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public Set<Channel> getChannels(String name) throws SQLException {
        List<Channel> channels;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Channel> query = session.createQuery("from Channel where name = :name", Channel.class);
            query.setParameter("name", name);
            channels = query.list();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
        return new HashSet<>(channels);
    }

    /**
     * Add a channel to the database
     *
     * @param channel channel to add
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public void addChannel(Channel channel) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(channel);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Remove a channel from the database
     *
     * @param channel channel to remove
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public void removeChannel(Channel channel) throws SQLException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.delete(channel);
            session.flush();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Update the changes in the input channel to the database
     *
     * @param channel channel to change
     * @throws SQLException if the connection failed or there is an error.
     */
    @Override
    public void updateChannel(Channel channel) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(channel);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new SQLException(e.getMessage());
        }
    }
}
