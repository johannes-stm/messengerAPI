package org.johannesstm.sse;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.johannesstm.entity.Chat;
import org.johannesstm.entity.Message;
import org.johannesstm.entity.User;
import org.johannesstm.repository.ChatRepository;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import java.util.*;

@Path("/api/v1/sse")
@RolesAllowed("ROLE_APPLICATION")
@Singleton
public class ChatSSE {

    private ChatRepository chatRepository;

    private Sse sse;
    private Map<String, SseBroadcaster> sseBroadcasterMap = new HashMap<>();

    @Inject
    JsonWebToken jwt;

    public ChatSSE(ChatRepository chatRepository, Sse sse) {
        this.chatRepository = chatRepository;
        this.sse = sse;
    }


    private static class ChatSSEMessage {

        private final Long chatId;

        private final Message message;

        public ChatSSEMessage(Long chatId, Message message) {
            this.chatId = chatId;
            this.message = message;
        }

        public Long getChatId() {
            return chatId;
        }

        public Message getMessage() {
            return message;
        }
    }

    public void sendMessage(Long chatId, Message newMessage) {

        Optional<Chat> chat = chatRepository.findByChatId(chatId);
        Set<User> chatUsersList = chat.get().getChatUsers();

        ChatSSEMessage chatSSEMessage = new ChatSSEMessage(chatId, newMessage);

        final OutboundSseEvent sseEvent = sse.newEventBuilder()
                //.id(UUID.randomUUID().toString())
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(ChatSSE.class, chatSSEMessage)
                .build();

        for (User user : chatUsersList) {
            if (sseBroadcasterMap.containsKey(user.getEmail()) && !Objects.equals(user.getEmail(), jwt.getName())) {

                sseBroadcasterMap.get(user.getEmail()).broadcast(sseEvent);
            }
        }

    }

    @POST
    public Response produce() {
        String email = jwt.getName();

        if (!sseBroadcasterMap.isEmpty()) {
            final OutboundSseEvent sseEvent = sse.newEventBuilder()
                    .data("test")
                    .build();

            sseBroadcasterMap.get(email).broadcast(sseEvent);
        } else {

            return Response.ok("sseBroadcasterMap is empty").build();
        }

        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void consume(@Context SseEventSink sseEventSink
    ) {
        String email = jwt.getName();

        sseBroadcasterMap.put(email, sse.newBroadcaster());

        sseBroadcasterMap.get(email).register(sseEventSink);

        if (sseEventSink.isClosed()) {

            sseBroadcasterMap.remove(email);
        }

    }
}