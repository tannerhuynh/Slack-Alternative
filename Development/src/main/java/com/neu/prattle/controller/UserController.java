package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.Channel;
import com.neu.prattle.model.User;
import com.neu.prattle.service.ChannelService;
import com.neu.prattle.service.IUserService;
import com.neu.prattle.service.UserService;
import com.neu.prattle.utils.GsonUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/***
 * A Resource class responsible for handling CRUD operations on User objects.
 *
 * @author Team 9, CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-30
 *
 * API list:
 * POST /user/create {@link #createUserAccount(User)} create user.
 * POST /user/login {@link #loginUserAccount(User)} user login.
 * GET /user/all {@link #getAllUsers()} get all users.
 * GET /user/active {@link #getActiveUsers()} get all active users.
 * DELETE /user/{username} {@link #deactivateUser(String)} deactivate a user.
 * GET /user/{username}/channels {@link #getUserChannels(String)} get user channels
 * POST /user/update {@link #updateUserAccount(User)} update a user's information.
 */
@Path(value = "/user")
public class UserController {
    /**
     * User service instance.
     */
    private IUserService accountService = UserService.getInstance();

    /**
     * the gson object
     */
    private Gson gson = GsonUtil.getGson();

    /**
     * Setter for the account service instance.
     *
     * @param accountService User service instance.
     */
    public void setAccountService(IUserService accountService) {
        this.accountService = accountService;
    }

    /***
     * Handles a HTTP POST request for user creation.
     *
     * @param user -> The User object decoded from the payload of POST request.
     * @return -> A Response indicating the outcome of the requested operation.
     *      200: ok
     *      409: user with same username exists
     *      500: database error
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUserAccount(User user) {
        try {
            accountService.addUser(user);
            ChannelService.getInstance().addUserToChannel(1, user.getUsername());
        } catch (UserAlreadyPresentException e) {
            return Response.status(409).build();
        } catch (SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

        return Response.ok().build();
    }

    /**
     * Handles a http post request for user login
     *
     * @param user a user object that contains username and password
     * @return a response indicating the outcome of the request 200: ok 401: invalid password 404:
     * username not found 500: database error 403: user locked out
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUserAccount(User user) {
        try {
            int result = accountService.login(user);
            switch (result) {
                case 1:
                    String jsonResp = gson.toJson(accountService.findUserByName(user.getUsername()).orElse(new User()));
                    return Response.ok(jsonResp, MediaType.APPLICATION_JSON).build();
                case 0:
                    return Response.status(404).entity("username not found").build();
                case -1:
                    return Response.status(403).entity("user locked out").build();
                case -2:
                    return Response.status(401).entity("invalid password").build();
                default:
                    assert false;
                    return Response.ok().build();
            }
        } catch (SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Handles a get request for getting all users
     *
     * @return a response with json of user list 200: ok 500: database error
     */
    @GET
    @Path("/all")
    public Response getAllUsers() {
        List<User> userList;
        try {
            userList = accountService.getAllUsers();
        } catch (SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
        String jsonResp = gson.toJson(userList);
        return Response.ok(jsonResp, MediaType.APPLICATION_JSON).build();
    }

    /**
     * Handles a get request for getting all active users
     *
     * @return a response with json of user list 200: ok 500: database error
     */
    @GET
    @Path("/active")
    public Response getActiveUsers() {
        List<User> userList;
        try {
            userList = accountService.getActiveUsers();
        } catch (SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
        String jsonResp = gson.toJson(userList);
        return Response.ok(jsonResp, MediaType.APPLICATION_JSON).build();
    }

    /**
     * Handles a delete request for making a user inactive
     *
     * @param username username of the user to deactivate
     * @return a response indicating the outcome of the request 200: ok 404: username not found 500:
     * database error
     */
    @DELETE
    @Path("/{username}")
    public Response deactivateUser(@PathParam("username") String username) {
        try {
            Optional<User> optionalIUser = accountService.findUserByName(username);
            if (!optionalIUser.isPresent()) {
                return Response.status(404).build();
            }
            accountService.setUserActive(username, false);
        } catch (SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
        return Response.status(200).build();
    }

    /**
     * Handles a get request for getting all channels an user is in
     * @param username the username of the user
     * @return a response indicating the outcome of the request 200: ok 404: username not found 500:
     * database error
     */
    @GET
    @Path("/{username}/channels")
    public Response getUserChannels(@PathParam("username") String username){
        try {
            Optional<User> optionalIUser = accountService.findUserByName(username);
            if (!optionalIUser.isPresent()) {
                return Response.status(404).build();
            }
            User user = optionalIUser.get();
            Set<Channel> channels = new HashSet<>(user.getChannelSet());
            channels.addAll(user.getModChannelSet());
            String jsonResp = gson.toJson(channels);
            return Response.ok(jsonResp, MediaType.APPLICATION_JSON).build();
        } catch (SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Handles a post request for updating user profile information
     *
     * @param user an user object containing username and fields to update
     * @return a response indicating the outcome of the request 200: ok 404: username not found 500:
     * database error
     */
    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserAccount(User user) {
        try {
            int result = accountService.update(user);
            switch (result) {
                case 1:
                    String jsonResp = gson.toJson(accountService.findUserByName(user.getUsername()).orElse(new User()));
                    return Response.ok(jsonResp, MediaType.APPLICATION_JSON).build();
                case 0:
                    return Response.status(404).entity("username not found").build();
                default:
                    assert false;
                    return Response.ok().build();
            }
        } catch (SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

}
