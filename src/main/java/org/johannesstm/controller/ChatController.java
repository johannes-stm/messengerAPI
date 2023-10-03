package org.johannesstm.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.johannesstm.entity.Chat;
import org.johannesstm.entity.Message;
import org.johannesstm.entity.User;
import org.johannesstm.exception.EmptyBodyException;
import org.johannesstm.hash.HashLong;
import org.johannesstm.repository.ChatRepository;
import org.johannesstm.repository.UserRepository;
import org.johannesstm.request.StringRequest;
import org.johannesstm.request.NameRequest;
import org.johannesstm.service.ChatService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Path("/chat")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ChatController {

    private final ChatRepository chatRepository;

    private final UserRepository userRepository;

    private final ChatService chatService;

    @Inject
    JsonWebToken jwt;

    public ChatController(ChatRepository chatRepository, UserRepository userRepository, ChatService chatService) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
    }

    @Operation(summary = "get all chats")
    @RolesAllowed("ROLE_APPLICATION")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChats() {

        final List<Chat> response = chatService.getChats();

        return Response.ok(response).build();
    }

    @Operation(summary = "create one chat")
    @RolesAllowed("ROLE_APPLICATION")
    @POST
    @Path("/createChat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addChat(NameRequest nameRequest) throws EmptyBodyException {

        final Chat response = chatService.createChat(nameRequest);

        return Response.ok(response).build();
    }

    @Operation(summary = "add a user to a chat")
    @RolesAllowed("ROLE_APPLICATION")
    @PUT
    @Path("/addUser/{chatId}/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(@PathParam("chatId") Long chatId, @PathParam("userId") Long userId) throws Exception {

        final String response = chatService.addUser(chatId, userId);

        return Response.ok(response).build();
    }

    @Operation(summary = "remove a user from a chat")
    @RolesAllowed("ROLE_APPLICATION")
    @DELETE
    @Path("/removeUser/{chatId}/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUser(@PathParam("chatId") Long chatId, @PathParam("userId") Long userId) throws Exception {

        final String response = chatService.removeUser(chatId, userId);

        return Response.ok(response).build();
    }

    @Operation(summary = "promote a user in a chat")
    @RolesAllowed("ROLE_APPLICATION")
    @PUT
    @Path("/promoteUser/{chatId}/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response promoteUser(@PathParam("chatId") Long chatId, @PathParam("userId") Long userId) throws Exception {

        final String response = chatService.promoteUser(chatId, userId);

        return Response.ok(response).build();
    }

    @Operation(summary = "demote a user in a chat")
    @RolesAllowed("ROLE_APPLICATION")
    @PUT
    @Path("/demoteUser/{chatId}/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response demoteUser(@PathParam("chatId") Long chatId, @PathParam("userId") Long userId) throws Exception {

        final String response = chatService.demoteUser(chatId, userId);

        return Response.ok(response).build();
    }

    @Operation(summary = "create a message in a chat")
    @RolesAllowed("ROLE_APPLICATION")
    @POST
    @Path("/createMessage/{chatId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMessage(@PathParam("chatId") Long chatId, StringRequest emailRequest) throws Exception {

        final Message response = chatService.createMessage(chatId, emailRequest.getContent());

        return Response.ok(response).build();
    }

    @Operation(summary = "change the name of one chat")
    @RolesAllowed("ROLE_APPLICATION")
    @PUT
    @Path("/changeChatName/{chatId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeChatName(@PathParam("chatId") Long chatId, StringRequest stringRequest) throws Exception {

        final String response = chatService.changeChatName(chatId, stringRequest.getContent());

        return Response.ok(response).build();
    }

    @Operation(summary = "get messages by chatId and page")
    @RolesAllowed("ROLE_APPLICATION")
    @GET
    @Path("/messages/{chatId}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessagesByPage(@PathParam("chatId") Long chatId, @PathParam("page") int page) throws Exception {

        final List<Message> messages = chatService.getMessagesByPage(chatId, page);

        return Response.ok(messages).build();
    }

}
