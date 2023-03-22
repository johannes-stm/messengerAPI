package org.johannesstm.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.johannesstm.entity.Chat;
import org.johannesstm.entity.Message;
import org.johannesstm.exception.EmptyBodyException;
import org.johannesstm.repository.ChatRepository;
import org.johannesstm.repository.UserRepository;
import org.johannesstm.request.StringRequest;
import org.johannesstm.request.NameRequest;
import org.johannesstm.service.ChatService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/chat")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ChatController {

    private final ChatRepository chatRepository;

    private final UserRepository userRepository;

    private final ChatService chatService;
//init
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

    @Operation(summary = "add or remove a user from a chat")
    @RolesAllowed("ROLE_APPLICATION")
    @PUT
    @Path("/updateUser/{chatId}/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUsers(@PathParam("chatId") Long chatId, @PathParam("userId") Long userId) throws Exception {

        final String response = chatService.updateUser(chatId, userId);

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

}
