package org.johannesstm.service;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.johannesstm.entity.Chat;
import org.johannesstm.entity.Message;
import org.johannesstm.entity.User;
import org.johannesstm.exception.EmptyBodyException;
import org.johannesstm.repository.ChatRepository;
import org.johannesstm.repository.FriendRepository;
import org.johannesstm.repository.MessageRepository;
import org.johannesstm.repository.UserRepository;
import org.johannesstm.request.NameRequest;
import org.johannesstm.sse.ChatSSE;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ChatService {
    @Inject
    private final ChatSSE chatSSE;

    private final UserRepository userRepository;

    private final ChatRepository chatRepository;

    private final FriendRepository friendRepository;

    private final MessageRepository messageRepository;

    @Inject
    JsonWebToken jwt;

    public ChatService(ChatSSE chatSSE, UserRepository userRepository, ChatRepository chatRepository, FriendRepository friendRepository, MessageRepository messageRepository) {
        this.chatSSE = chatSSE;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.friendRepository = friendRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public List<Chat> getChats() {

        return chatRepository.findByEmail(jwt.getName());
    }

    @Transactional
    public Chat createChat(final NameRequest nameRequest) throws EmptyBodyException {

        if (nameRequest.getName().length() == 0) {
            throw new EmptyBodyException();
        }

        Optional<User> user = userRepository.findByEmail(jwt.getName());

        Chat newChat = new Chat();
        Date now = new Date();
        newChat.setName(nameRequest.getName());
        newChat.setCreatedAt(now);
        newChat.getChatAdmins().add(user.get());
        chatRepository.persist(newChat);

        return newChat;
    }

    @Transactional
    public String addUser(final Long chatId, final Long userId) throws Exception {

        Optional<User> user = userRepository.findByEmail(jwt.getName());
        Optional<User> user1 = userRepository.findByIdOptional(userId);

        if (user1.isEmpty()) {
            throw new NotFoundException();
        }

        boolean checkIfFriend = friendRepository.existsByFirstUserAndSecondUser(user.get(), user1.get());
        Optional<Chat> chat = chatRepository.findByChatId(chatId);

        if (chat.get().getChatUsers().contains(user1.get())) {
            throw new Exception("user already in chat");
        } else if (checkIfFriend) {

            chat.get().getChatUsers().add(user1.get());
            chatRepository.persist(chat.get());

            return "user added";
        }
        throw new Exception("you and " + user1.get().getEmail() + " are no friends");
    }

    @Transactional
    public String removeUser(final Long chatId, final Long userId) throws Exception {

        Optional<User> user = userRepository.findByEmail(jwt.getName());
        Optional<User> user1 = userRepository.findByIdOptional(userId);

        if (user1.isEmpty()) {
            throw new NotFoundException();
        }

        boolean checkIfFriend = friendRepository.existsByFirstUserAndSecondUser(user.get(), user1.get());
        Optional<Chat> chat = chatRepository.findByChatId(chatId);

        if (chat.get().getChatAdmins().contains(user.get()) && chat.get().getChatUsers().contains(user1.get())) {

            chat.get().getChatUsers().remove(user1.get());
            chatRepository.persist(chat.get());

            return "user removed";
        } else if (user.get() == user1.get() && (chat.get().getChatAdmins().size() + chat.get().getChatUsers().size()) == 1) {

            chatRepository.delete(chat.get());

            return "chat deleted";
        } else if (user.get() == user1.get()) {

            chat.get().getChatAdmins().remove(user.get());
            chat.get().getChatUsers().remove(user.get());
            if (chat.get().getChatAdmins().size() == 0) {

                Optional<User> firstUser = chat.get().getChatUsers().stream().findFirst();
                chat.get().getChatAdmins().add(firstUser.get());
                chat.get().getChatUsers().remove(firstUser.get());
            }
            chatRepository.persist(chat.get());

            return user.get().getEmail() + " left the chat";
        }
        throw new Exception("you and " + user1.get().getEmail() + " are no friends");
    }

    @Transactional
    public String promoteUser(final Long chatId, final Long userId) throws Exception {

        Optional<User> user = userRepository.findByEmail(jwt.getName());
        Optional<User> user1 = userRepository.findByIdOptional(userId);

        if (user1.isEmpty()) {
            throw new NotFoundException();
        }

        Optional<Chat> chat = chatRepository.findByChatId(chatId);

        if (chat.isEmpty()) {
            throw new NotFoundException();
        }

        if (chat.get().getChatAdmins().contains(user.get()) && chat.get().getChatUsers().contains(user1.get())) {

            chat.get().getChatUsers().remove(user1.get());
            chat.get().getChatAdmins().add(user1.get());
            chatRepository.persist(chat.get());

            return "user promoted";
        } else if (chat.get().getChatAdmins().contains(user1.get())) {
            throw new Exception(user1.get().getEmail() + " is already admin in this group");
        }

        throw new Exception("you are no admin in this group");
    }

    @Transactional
    public String demoteUser(final Long chatId, final Long userId) throws Exception {

        Optional<User> user = userRepository.findByEmail(jwt.getName());
        Optional<User> user1 = userRepository.findByIdOptional(userId);

        if (user1.isEmpty()) {
            throw new NotFoundException();
        }

        Optional<Chat> chat = chatRepository.findByChatId(chatId);

        if (chat.isEmpty()) {
            throw new NotFoundException();
        }

        if (chat.get().getChatAdmins().contains(user.get()) && chat.get().getChatAdmins().contains(user1.get())) {
            chat.get().getChatAdmins().remove(user1.get());
            chat.get().getChatUsers().add(user1.get());
            chatRepository.persist(chat.get());

            return "user demoted";
        } else if (chat.get().getChatUsers().contains(user1.get())) {
            throw new Exception(user1.get().getEmail() + " is no admin in this group");
        }

        throw new Exception("you are no admin in this group");
    }

    @Transactional
    public Message createMessage(final Long chatId, final String message) throws Exception {

        if (message.length() == 0) {
            throw new Exception("length of message is 0");
        }

        Optional<Chat> chat = chatRepository.findByChatId(chatId);
        Optional<User> user = userRepository.findByEmail(jwt.getName());

        Message newMessage = new Message(message, new Date(), chat.get(), user.get());

        chat.get().addMessage(newMessage);
        chat.get().setLastMessage(newMessage);





        chatSSE.sendMessage(chatId, newMessage);






        chatRepository.persist(chat.get());

        return newMessage;
    }

    @Transactional
    public String changeChatName(final Long chatId, final String newChatName) throws Exception {

        if (newChatName != null && newChatName.length() == 0) {
            throw new Exception("length of newChatName is 0");
        }

        Optional<Chat> chat = chatRepository.findByChatId(chatId);

        if (chat.isPresent()) {
            Optional<User> user = userRepository.findByEmail(jwt.getName());
            boolean isAdmin = chat.get().getChatAdmins().contains(user.get());

            if (isAdmin) {

                chat.get().setName(newChatName);

                chatRepository.persist(chat.get());
                return "chatName changed";
            }

        }

        throw new Exception("you are not an admin in this chat");
    }

    @Transactional
    public List<Message> getMessagesByPage(Long chatId, int page) {

        Optional<Chat> chat = chatRepository.findByChatId(chatId);
        return chat.get().getMessagesByPage(page);
    }
}
