package org.johannesstm.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.johannesstm.entity.Friend;
import org.johannesstm.entity.User;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class FriendRepository implements PanacheRepository<Friend> {

    public boolean existsByFirstUserAndSecondUser(User first, User second) {
        if (count("first_user_id = ?1 and second_user_id = ?2 or first_user_id = ?2 and second_user_id = ?1", first.getId(), second.getId()) > 0) {
            return true;
        };
        return false;
    }

    public List<Friend> findByUser(Long id) {

        return list("first_user_id = ?1 or second_user_id = ?1", id);
    }


}
