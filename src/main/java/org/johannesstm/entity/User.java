package org.johannesstm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.common.constraint.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.IOException;
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

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JsonIgnore
    protected byte[] profilePicture;

    @ElementCollection(fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<String> roles = new HashSet<>();

    @ManyToMany(mappedBy = "chatUsers", cascade = CascadeType.ALL)
    Set<Chat> users = new HashSet<>();

    @ManyToMany(mappedBy = "chatAdmins", cascade = CascadeType.ALL)
    Set<Chat> admins = new HashSet<>();


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

    /*public String getProfilePicture() throws IOException {
        if (profilePicture == null) {
            return null;
        }

        String encryptionKey = "Spaßvogeltruppe";

        return HashLong.encryptLong(id, encryptionKey);
    }*/

    @JsonIgnore
    public byte[] getProfilePicture1() throws IOException {
        if (profilePicture == null) {
            return null;
        }

        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
