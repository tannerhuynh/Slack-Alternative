package com.neu.prattle.controller;

import com.google.gson.Gson;

import com.neu.prattle.model.Message;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.IMessageService;
import com.neu.prattle.utils.GsonUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

/**
 * A Resource class responsible for handling CRUD operations on DM.
 *
 * @author Team 9
 * @version 2019-10-30
 *
 * API list:
 * GET /dm/from/{username} {@link #getDMFrom(String)} get dm from user.
 * GET /dm/to/{username} {@link #getDMTo(String)} get dm to user.
 * GET /dm/between/{username1}/{username2} {@link #getDMBetween(String, String)} get user between
 * users.
 */
@Path(value = "/dm")
public class DMController {
    /**
     * The MessageService instance.
     */
    private IMessageService messageService = MessageService.getInstance();

    /**
     * Set the MessageService instance.
     *
     * @param messageService message service.
     */
    void setMessageService(IMessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Getter for the JSON object from the message list.
     *
     * @param msgList the list of messages.
     * @return the JSON String.
     */
    private String getJsonFromMsgList(List<Message> msgList) {
        Gson gson = GsonUtil.getGson();
        return gson.toJson(msgList);
    }

    /**
     * Handles http GET request for message from user.
     *
     * @param username username of the from user.
     * @return response with JSON of message list. 200: ok 500: database error
     */
    @GET
    @Path("/from/{username}")
    public Response getDMFrom(@PathParam("username") String username) {
        List<Message> dmList;
        try {
            dmList = messageService.getMessagesByFrom(username);
        } catch (SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
        return Response.ok(getJsonFromMsgList(dmList), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Handles http GET request for message to user.
     *
     * @param username username of the to user.
     * @return response with JSON of message list. 200: ok 500: database error
     */
    @GET
    @Path("/to/{username}")
    public Response getDMTo(@PathParam("username") String username) {
        List<Message> dmList;
        try {
            dmList = messageService.getMessagesByTo(username);
        } catch (SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
        return Response.ok(getJsonFromMsgList(dmList), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Handles http GET request for message between two users.
     *
     * @param username1 username of the first user.
     * @param username2 username of the second user.
     * @return response with JSON of message list. 200: ok 500: database error
     */
    @GET
    @Path("/between/{username1}/{username2}")
    public Response getDMBetween(@PathParam("username1") String username1, @PathParam("username2") String username2) {
        List<Message> dmList;
        try {
            dmList = messageService.getMessagesByUsers(username1, username2);
        } catch (SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
        return Response.ok(getJsonFromMsgList(dmList), MediaType.APPLICATION_JSON).build();
    }
}
