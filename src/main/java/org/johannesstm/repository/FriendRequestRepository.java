package org.johannesstm.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.johannesstm.entity.FriendRequest;
import org.johannesstm.entity.User;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class FriendRequestRepository implements PanacheRepository<FriendRequest> {

    public boolean existsByFirstUserAndSecondUser(User first, User second) {
        if (count("first_user_id = ?1 and second_user_id = ?2 or first_user_id = ?2 and second_user_id = ?1", first.getId(), second.getId()) > 0) {
            return true;
        };
        return false;
    }

    public List<FriendRequest> findByUser(Long id) {

        return list("first_user_id = ?1 or second_user_id = ?1", id);
    }
}
