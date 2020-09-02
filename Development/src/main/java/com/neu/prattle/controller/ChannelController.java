package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.neu.prattle.model.Channel;
import com.neu.prattle.model.ChannelDTO;
import com.neu.prattle.service.ChannelService;
import com.neu.prattle.service.IChannelService;
import com.neu.prattle.utils.GsonUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

/**
 * A Resource class responsible for handling CRUD operations on Channel
 *
 * @author Team 9
 * @version 2019-11-14
 *
 * API list:
 * GET /channel/all {@link #getAllChannels()}
 * GET /channel/{channel_id} {@link #getChannelById(int)}}
 * DELETE /channel/{channel_id} {@link #removeChannel(int)}
 * POST /channel/create {@link #addChannel(ChannelDTO)}
 * PUT /channel/{channel_id}/user/{username} {@link #addUserToChannel(int, String)}
 * DELETE /channel/{channel_id}/user/{username} {@link #removeUserFromChannel(int, String)}
 * PUT /channel/{channel_id}/mod/{username} {@link #promoteUserInChannel(int, String)}
 * DELETE /channel/{channel_id}/mod/{username} {@link #demoteUserInChannel(int, String)}
 */
@Path(value = "/channel")
public class ChannelController {
    /**
     * Channel service instance
     */
    private IChannelService channelService = ChannelService.getInstance();
    /**
     * Gson helper
     */
    private Gson gson = GsonUtil.getGson();
    /**
     * Constants - used for error messages
     */
    private static final String USER_NOT_FOUND = "user not found";
    private static final String CHANNEL_NOT_FOUND = "channel not found";

    /**
     * Setter for channel service
     * @param channelService channel service instance
     */
    void setChannelService(IChannelService channelService) {
        this.channelService = channelService;
    }

    /**
     * Handles a http GET request for getting all channels
     * @return A response indicating the outcome of the request.
     *  200: ok, body contains Json
     *  500: database error
     */
    @GET
    @Path("/all")
    public Response getAllChannels(){
        Set<Channel> channels;
        try {
            channels = channelService.getAllChannels();
        } catch (SQLException e){
            return Response.status(500).entity(e.getMessage()).build();
        }
        return Response.ok(gson.toJson(channels), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Handles http GET request for getting a specific channel by id
     * @param id channel id
     * @return A response indicating the outcome of the request.
     *  200: ok, body contains Json
     *  404: channel not found
     *  500: database error
     */
    @GET
    @Path("/{channel_id}")
    public Response getChannelById(@PathParam("channel_id") int id){
        Optional<Channel> optionalChannel;
        try {
            optionalChannel = channelService.getChannelById(id);
        } catch (SQLException e){
            return Response.status(500).entity(e.getMessage()).build();
        }
        if (optionalChannel.isPresent()){
            return Response.ok(gson.toJson(optionalChannel.get()), MediaType.APPLICATION_JSON).build();
        }
        else {
            return Response.status(404).entity(CHANNEL_NOT_FOUND).build();
        }
    }

    /**
     * Handles http DELETE request for deleting a specific channel by id
     * @param id channel id
     * @return A response indicating the outcome of the request.
     *  200: ok
     *  404: channel not found
     *  500: database error
     */
    @DELETE
    @Path("/{channel_id}")
    public Response removeChannel(@PathParam("channel_id") int id){
        try {
            int result = channelService.removeChannel(id);
            switch (result){
                case 1:
                    return Response.ok().build();
                case 0:
                    return Response.status(404).entity(CHANNEL_NOT_FOUND).build();
                default:
                    return Response.status(500).build();
            }
        } catch (SQLException e){
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Handles http POST request for creating channel
     * @param channelDTO a channel data transfer object {@link ChannelDTO}
     * @return A response indicating the outcome of the request.
     *  200: ok
     *  500: database error
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addChannel(ChannelDTO channelDTO){
        try {
            channelService.addChannel(channelDTO);
            return Response.ok().build();
        } catch (SQLException e){
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Handles a http PUT request for adding a user to a channel
     * @param id channel to add user
     * @param username user to add
     * @return A response indicating the outcome of the request.
     *  200: ok
     *  404: channel or user not found
     *  500: database error
     */
    @PUT
    @Path("/{channel_id}/user/{username}")
    public Response addUserToChannel(@PathParam("channel_id") int id, @PathParam("username") String username){
        try {
            int result = channelService.addUserToChannel(id, username);
            switch (result){
                case 1:
                    return Response.ok().build();
                case 0:
                    return Response.status(404).entity(CHANNEL_NOT_FOUND).build();
                case -1:
                    return Response.status(404).entity(USER_NOT_FOUND).build();
                default:
                    return Response.status(500).build();
            }
        } catch (SQLException e){
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Handles a http DELETE request for removing a user from a channel
     * @param id channel to remove user
     * @param username user to remove
     * @return A response indicating the outcome of the request.
     *  200: ok
     *  404: channel or user not found
     *  500: database error
     */
    @DELETE
    @Path("/{channel_id}/user/{username}")
    public Response removeUserFromChannel(@PathParam("channel_id") int id, @PathParam("username") String username){
        try {
            int result = channelService.removeUserFromChannel(id, username);
            switch (result){
                case 1:
                    return Response.ok().build();
                case 0:
                    return Response.status(404).entity(CHANNEL_NOT_FOUND).build();
                case -1:
                    return Response.status(404).entity(USER_NOT_FOUND).build();
                default:
                    return Response.status(500).build();
            }
        } catch (SQLException e){
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Handles a http PUT request for adding a mod to a channel
     * @param id channel to add user
     * @param username user to add as mod
     * @return A response indicating the outcome of the request.
     *  200: ok
     *  404: channel or user not found
     *  500: database error
     */
    @PUT
    @Path("/{channel_id}/mod/{username}")
    public Response promoteUserInChannel(@PathParam("channel_id") int id, @PathParam("username") String username){
        try {
            int result = channelService.promoteUserInChannel(id, username);
            switch (result){
                case 1:
                    return Response.ok().build();
                case 0:
                    return Response.status(404).entity(CHANNEL_NOT_FOUND).build();
                case -1:
                    return Response.status(404).entity(USER_NOT_FOUND).build();
                default:
                    return Response.status(500).build();
            }
        } catch (SQLException e){
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Handles a http DELETE request for removing a mod from a channel
     * @param id channel to remove mod
     * @param username mod to remove
     * @return A response indicating the outcome of the request.
     *  200: ok
     *  404: channel or user not found
     *  500: database error
     */
    @DELETE
    @Path("/{channel_id}/mod/{username}")
    public Response demoteUserInChannel(@PathParam("channel_id") int id, @PathParam("username") String username){
        try {
            int result = channelService.demoteUserInChannel(id, username);
            switch (result){
                case 1:
                    return Response.ok().build();
                case 0:
                    return Response.status(404).entity(CHANNEL_NOT_FOUND).build();
                case -1:
                    return Response.status(404).entity(USER_NOT_FOUND).build();
                default:
                    return Response.status(500).build();
            }
        } catch (SQLException e){
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
}
