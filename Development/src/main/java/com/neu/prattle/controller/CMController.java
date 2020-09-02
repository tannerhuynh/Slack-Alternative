package com.neu.prattle.controller;

import com.google.gson.Gson;

import com.neu.prattle.model.Message;
import com.neu.prattle.service.IMessageService;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.utils.GsonUtil;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A Resource class responsible for handling CRUD operations on a CM (channel message).
 *
 * @author Team 9
 * @version 2019-11-26
 *
 * API list: GET /cm/from/{channelID}/all get all channel messages within a channel (from any user)
 */
@Path(value = "/cm")
public class CMController {
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
     * Handles http GET request to retrieve all messages within a channel
     *
     * @param channelID channel ID - a string but will be converted to an int
     * @return response with JSON of message list. 200: ok 500: database error
     */
    @GET
    @Path("/from/{channelID}/all")
    public Response getAllMessages(@PathParam("channelID") String channelID) {
        List<Message> dmList;
        try {
            int channelIDint = Integer.parseInt(channelID);
            dmList = messageService.getMessagesWithinChannel(channelIDint);
        } catch (SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
        return Response.ok(getJsonFromMsgList(dmList), MediaType.APPLICATION_JSON).build();
    }
}


