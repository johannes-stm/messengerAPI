package org.johannesstm.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.common.constraint.NotNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.xml.crypto.Data;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "message")
public class Message extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @NotNull
    private String content;

    private Date createdAt;

    @JsonBackReference
    @ManyToOne
    private Chat chat;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", creator=" + creator +
                '}';
    }

    public Message() {
    }

    public Message(String content, Date createdAt, Chat chat, User creator) {
        this.content = content;
        this.createdAt = createdAt;
        this.chat = chat;
        this.creator = creator;
    }

    public Long getMessageId() {
        return messageId;
    }

    public String getContent() {
        return content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
