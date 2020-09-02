package com.neu.prattle.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * A Basic POJO for Message.
 *
 * @author CS5500 Fall 2019 Teaching staff, Team 9
 * @version dated 2019-10-29
 */
@Entity
@Table(name = "message")
public class Message implements Serializable {
    /**
     * Message ID.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * the user who sent this message
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_username", insertable = false, updatable = false)
    private transient User fromUser;

    /**
     * The name of the user who sent this message.
     */
    @Column(name = "from_username")
    private String fromUsername;

    /**
     * The avatar id of the user who sent this message
     */
    @Formula("(select user.avatar from user where user.username = from_username)")
    private Integer fromAvatar = null;

    /**
     * the user to whom the message is sent.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_username", insertable = false, updatable = false)
    private transient User toUser;

    /**
     * The name of the user to whom the message is sent.
     */
    @Column(name = "to_username")
    private String toUsername;

    /**
     * The avatar id of the user to whom the message is sent
     */
    @Formula("(select user.avatar from user where user.username = to_username)")
    private Integer toAvatar = null;

    /**
     * The channel to which the message is sent
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_channel", insertable = false, updatable = false)
    private transient Channel toChannel;

    /**
     * The id of the channel to which the message is sent
     */
    @Column(name = "to_channel")
    private Integer toChannelId;

    /**
     * It represents the contents of the message.
     */
    @Column(name = "content")
    private String content;

    /**
     * Current timestamp.
     */
    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("From: ").append(fromUsername)
                .append("To: ").append(toUsername)
                .append("Content: ").append(content)
                .append("Datetime: ").append(timestamp)
                .toString();
    }

    /**
     * Getter for which User the message is from.
     *
     * @return the from value in String format.
     */
    public String getFromUsername() {
        return fromUsername;
    }

    /**
     * Setter for which User the message is from.
     *
     * @param from the from value in String format.
     */
    public void setFromUsername(String from) {
        this.fromUsername = from;
    }

    /**
     * Getter for which User the message is to.
     *
     * @return the to value in String format.
     */
    public String getToUsername() {
        return toUsername;
    }

    /**
     * Setter for which User the message is to.
     *
     * @return the to value in String format.
     */
    public void setToUsername(String to) {
        this.toUsername = to;
    }

    /**
     * Getter for the content of the message.
     *
     * @return content of the message in String format.
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter for the content of the message.
     *
     * @return content of the message in String format.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Getter for the timestamp of the message.
     *
     * @return timestamp of the message.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Setter for the timestamp of the message.
     *
     * @return timestamp of the message in a Timestamp object.
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Getter for the ID of the message.
     *
     * @return ID of the message in Int format.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the ID of the message.
     *
     * @return ID of the message in Int format.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getter for the from user of the message
     *
     * @return from user
     */
    public User getFromUser() {
        return fromUser;
    }

    /**
     * setter for the from user of the message
     *
     * @param fromUser from user
     */
    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    /**
     * getter for the to user of the message
     *
     * @return to user
     */
    public User getToUser() {
        return toUser;
    }

    /**
     * setter for the to user of the message
     *
     * @param toUser to user
     */
    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    /**
     * Setter for the to channel of the message
     *
     * @return to channel
     */
    public Channel getToChannel() {
        return toChannel;
    }

    /**
     * Getter for the to channel of the message
     *
     * @param toChannel to channel
     */
    public void setToChannel(Channel toChannel) {
        this.toChannel = toChannel;
    }

    /**
     * Getter for the to channel id of the message
     *
     * @return to channel id
     */
    public int getToChannelId() {
        if (toChannelId != null) {
            return toChannelId;
        }
        return -1;
    }

    /**
     * Getter for the from user avatar
     *
     * @return from user avatar
     */
    public Integer getFromAvatar() {
        return fromAvatar;
    }

    /**
     * Setter for the from user avatar
     *
     * @param fromAvatar from user avatar
     */
    public void setFromAvatar(Integer fromAvatar) {
        this.fromAvatar = fromAvatar;
    }

    /**
     * Getter for the to user avatar
     *
     * @return to user avatar
     */
    public Integer getToAvatar() {
        return toAvatar;
    }

    /**
     * Setter for the to user avatar
     *
     * @param toAvatar to user avatar
     */
    public void setToAvatar(Integer toAvatar) {
        this.toAvatar = toAvatar;
    }

    /**
     * Setter for the to channel id of the message
     *
     * @param toChannelId to channel id
     */
    public void setToChannelId(Integer toChannelId) {
        this.toChannelId = toChannelId;
    }

    /**
     * A message builder that uses the from, to, content, timestamp, and id to construct a message
     * object.
     *
     * @return a message object.
     */
    public static MessageBuilder messageBuilder() {
        return new MessageBuilder();
    }

    /**
     * A Builder helper class to create instances of {@link Message}.
     */
    public static class MessageBuilder {
        /**
         * Invoking the build method will return this message object.
         */
        Message message;

        /**
         * Constructor for the message builder that takes no parameters, and returns the message
         * object.
         */
        public MessageBuilder() {
            message = new Message();
            message.setFromUsername("Not set");
        }

        /**
         * Setter for which User the message is from.
         *
         * @param from which User the message is from.
         * @return this.
         */
        public MessageBuilder setFrom(String from) {
            message.setFromUsername(from);
            return this;
        }

        /**
         * Setter for which User the message is to.
         *
         * @param to which User the message is to.
         * @return this.
         */
        public MessageBuilder setToUsername(String to) {
            message.setToUsername(to);
            return this;
        }

        /**
         * Setter for which channel the message is to
         *
         * @param to which channel the message is to
         * @return this
         */
        public MessageBuilder setToChannelId(int to) {
            message.setToChannelId(to);
            return this;
        }

        /**
         * Setter for the content of the message.
         *
         * @param content content of the message.
         * @return this.
         */
        public MessageBuilder setMessageContent(String content) {
            message.setContent(content);
            return this;
        }

        /**
         * Setter for the timestamp of the message.
         *
         * @param timestamp timestamp of when the message was created.
         * @return this.
         */
        public MessageBuilder setTimestamp(Timestamp timestamp) {
            message.setTimestamp(timestamp);
            return this;
        }

        /**
         * Build the message using the provided setters.
         *
         * @return message object.
         */
        public Message build() {
            return message;
        }
    }
}