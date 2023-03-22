package org.johannesstm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity
@Table(name = "friend_request")
public class FriendRequest extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendRequestId;

    @OneToOne
    @JoinColumn(name = "first_user_id", referencedColumnName = "id")
    User firstUser;

    @OneToOne
    @JoinColumn(name = "second_user_id", referencedColumnName = "id")
    User secondUser;

    public FriendRequest() {
    }

    public FriendRequest(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

    public Long getFriendRequestId() {
        return friendRequestId;
    }

    public void setFriendRequestId(Long friendRequestId) {
        this.friendRequestId = friendRequestId;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(User secondUser) {
        this.secondUser = secondUser;
    }
}