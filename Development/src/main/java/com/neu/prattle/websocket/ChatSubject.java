package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * The chat subject using observer pattern
 *
 * @author Team 9
 * @version dated 2019-11-20
 */
public class ChatSubject implements IChatSubject {
    /**
     * The message of the subject
     */
    private Message message;

    /**
     * The list of observers attached
     */
    private List<IObserver> observers = new ArrayList<>();

    /**
     * Get message
     *
     * @return message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Set message and notify observers
     *
     * @param message message to set
     */
    public void setMessage(Message message) {
        this.message = message;
        notifyObservers();
    }

    /**
     * Attach an observer
     *
     * @param observer the observer to attack
     */
    @Override
    public void attach(IObserver observer) {
        observers.add(observer);
    }

    /**
     * Detach an observer
     *
     * @param observer the observer to detach
     */
    @Override
    public void detach(IObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notify all attached observers
     */
    @Override
    public void notifyObservers() {
        for (IObserver observer : observers) {
            observer.update();
        }
    }

    /**
     * Get all observers
     *
     * @return observer list
     */
    @Override
    public List<IObserver> getObservers() {
        return observers;
    }
}
