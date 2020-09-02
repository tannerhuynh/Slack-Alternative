package com.neu.prattle.database;

import com.neu.prattle.model.Channel;
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
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;

public class TestChannelDBHibernate {
    private ChannelDBHibernate channelDB;
    private List<Channel> channelList;

    @Before
    public void setUp() {
        channelDB = ChannelDBHibernate.getInstance();
        channelDB.testMode();
        channelList = new ArrayList<>();
        channelList.add(new Channel(1, "channel1"));
    }

    @Test
    public void testChannelDBHibernate() throws SQLException {
        SessionFactory sessionFactoryMock = Mockito.mock(SessionFactory.class);
        Session sessionMock = Mockito.mock(Session.class);
        Query queryMock = Mockito.mock(Query.class);
        Transaction transactionMock = Mockito.mock(Transaction.class);

        HibernateUtil.setSessionFactory(sessionFactoryMock);

        Mockito.doReturn(transactionMock).when(sessionMock).beginTransaction();
        Mockito.doReturn(sessionMock).when(sessionFactoryMock).openSession();
        Mockito.doReturn(queryMock).when(sessionMock).createQuery(any(String.class), any());
        Mockito.doReturn(queryMock).when(queryMock).setParameter(any(String.class), any());
        Mockito.doReturn(channelList).when(queryMock).list();
        Mockito.doReturn(channelList.get(0)).when(sessionMock).get(Channel.class, 1);
        Mockito.doReturn(null).when(sessionMock).get(Channel.class, 2);

        channelDB.addChannel(channelList.get(0));
        channelDB.removeChannel(channelList.get(0));
        channelDB.updateChannel(channelList.get(0));

        assertEquals(1, channelDB.getAllChannels().size());
        assertEquals("channel1", Objects.requireNonNull(channelDB.getChannel(1).orElse(null)).getName());
        assertFalse(channelDB.getChannel(2).isPresent());
        assertEquals(1, channelDB.getChannels("channel1").size());

        Mockito.doThrow(new RuntimeException()).when(transactionMock).commit();

        try{
            channelDB.addChannel(channelList.get(0));
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }

        try{
            channelDB.removeChannel(channelList.get(0));
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }

        try{
            channelDB.updateChannel(channelList.get(0));
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }

        try{
            channelDB.getAllChannels();
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }

        try{
            channelDB.getChannel(1);
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }

        try{
            channelDB.getChannels("channel1");
        }
        catch (SQLException e) {
            assertEquals(SQLException.class, e.getClass());
        }
    }
}
