package org.johannesstm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.common.constraint.NotNull;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User extends PanacheEntityBase {

    public User(@NotNull String email, @NotNull String password, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User() {
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotNull
    @Column(unique = true, length = 100)
    protected String email;

    @NotNull
    @Column
    @JsonIgnore
    protected String password;

    @RestForm
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    String file;

    @ElementCollection(fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<String> roles = new HashSet<>();

    @ManyToMany(mappedBy = "chatUsers")
    Set<Chat> chats = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
