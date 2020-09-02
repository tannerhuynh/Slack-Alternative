package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;

import java.util.List;

/**
 * The chat subject interface
 *
 * @author Team 9
 * @version dated 2019-11-20
 */
public interface IChatSubject {
    /**
     * Get message
     *
     * @return message
     */
    Message getMessage();

    /**
     * Set message
     *
     * @param message message to set
     */
    void setMessage(Message message);

    /**
     * Attach an observer
     *
     * @param observer the observer to attack
     */
    void attach(IObserver observer);

    /**
     * Detach an observer
     *
     * @param observer the observer to detach
     */
    void detach(IObserver observer);

    /**
     * Notify all attached observers
     */
    void notifyObservers();

    /**
     * Get all observers
     *
     * @return observer list
     */
    List<IObserver> getObservers();
}
