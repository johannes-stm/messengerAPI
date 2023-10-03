package org.johannesstm.sse;

import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.johannesstm.entity.Chat;
import org.johannesstm.entity.Message;
import org.johannesstm.entity.User;
import org.johannesstm.repository.ChatRepository;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.*;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

@Path("/api/v1/sse")
@RolesAllowed("ROLE_APPLICATION")
@Singleton
public class ChatSSE {

    private final ChatRepository chatRepository;

    @Inject
    private final Sse sse;

    //private final Map<Long, Map<String, SseEventSink>> chatRooms = new HashMap<>();

    private final Map<String, SseBroadcaster> sseBroadcasterMap = new HashMap<>();

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
        Set<User> chatAdminsList = chat.get().getChatAdmins();
        Set<User> chatUsersList = chat.get().getChatUsers();

        ChatSSEMessage chatSSEMessage = new ChatSSEMessage(chatId, newMessage);

        final OutboundSseEvent sseEvent = sse.newEventBuilder()
                //.id(UUID.randomUUID().toString())
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(ChatSSE.class, chatSSEMessage)
                .build();

        for (User user : chatAdminsList) {
            if (sseBroadcasterMap.containsKey(user.getEmail()) && !Objects.equals(user.getEmail(), jwt.getName())) {

                try {
                    sseBroadcasterMap.get(user.getEmail()).broadcast(sseEvent);
                } catch (IllegalStateException illegalStateException) {
                    sseBroadcasterMap.remove(user.getEmail());
                }
            }
        }

        for (User user : chatUsersList) {
            if (sseBroadcasterMap.containsKey(user.getEmail()) && !Objects.equals(user.getEmail(), jwt.getName())) {

                try {
                    sseBroadcasterMap.get(user.getEmail()).broadcast(sseEvent);
                } catch (IllegalStateException illegalStateException) {
                    sseBroadcasterMap.remove(user.getEmail());
                }
            }
        }

    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void consume(@Context SseEventSink sseEventSink
    ) {

        String email = jwt.getName();

        sseBroadcasterMap.put(email, sse.newBroadcaster());
        sseBroadcasterMap.get(email).register(sseEventSink);

        // Dieses if statement wird nie gefired weil das sseEventSink nicht automatisch schließt
        if (sseEventSink.isClosed()) {
            System.out.println("removing");

            sseBroadcasterMap.remove(email);
        }

    }
}