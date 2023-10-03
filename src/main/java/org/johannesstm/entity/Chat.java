package org.johannesstm.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.common.constraint.NotNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "chat")
public class Chat extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @NotNull
    private String name;

    private Date createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "last_message_id", referencedColumnName = "messageId")
    private Message lastMessage;

    @ManyToMany @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "chat_user",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<User> chatUsers = new HashSet<>();

    @ManyToMany @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "chat_admin",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_id"))
    Set<User> chatAdmins = new HashSet<>();

    @JsonManagedReference
    @OneToMany(
            mappedBy = "chat"//,
            //cascade = CascadeType.ALL//,
            //orphanRemoval = true
    )  @Fetch(FetchMode.JOIN)
    private List<Message> messages = new ArrayList<>();

    public Chat() {
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setChat(this);
    }

    public List<Message> getMessages() {
        int startIndex = Math.max(messages.size() - 25, 0);
        return messages.subList(startIndex, messages.size());
    }

    public List<Message> getMessagesByPage(int page) {
        int pageSize = 25;
        int startIndex = Math.max(messages.size() - (page + 1) * pageSize, 0);
        int endIndex = Math.max(messages.size() - page * pageSize, 0);
        return messages.subList(startIndex, endIndex);
    }

    public Long getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getChatUsers() {
        return chatUsers;
    }

    public Set<User> getChatAdmins() {
        return chatAdmins;
    }

    public void setChatAdmins(Set<User> chatAdmins) {
        this.chatAdmins = chatAdmins;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setChatUsers(Set<User> chatUsers) {
        this.chatUsers = chatUsers;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
