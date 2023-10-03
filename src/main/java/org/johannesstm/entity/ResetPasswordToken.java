package org.johannesstm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "resetpasswordtoken")
public class ResetPasswordToken extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(length = 16)
    private UUID resetPasswordTokenId;

    @OneToOne
    private User owner;

    private Date createdOn;

    public ResetPasswordToken() {
    }

    public ResetPasswordToken(User owner, Date createdOn) {
        this.owner = owner;
        this.createdOn = createdOn;
    }

    public UUID getResetPasswordTokenId() {
        return resetPasswordTokenId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
