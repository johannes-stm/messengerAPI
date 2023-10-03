package org.johannesstm.service;

import org.johannesstm.entity.Friend;
import org.johannesstm.entity.FriendRequest;
import org.johannesstm.entity.User;
import org.johannesstm.repository.FriendRepository;
import org.johannesstm.repository.FriendRequestRepository;
import org.johannesstm.repository.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class FriendService {

    private final UserRepository userRepository;

    private final FriendRepository friendRepository;

    private final FriendRequestRepository friendRequestRepository;

    public FriendService(UserRepository userRepository, FriendRepository friendRepository, FriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    public List<User> searchFriends(final String searchTerm, final User user) {

        List<User> userResults = userRepository.findUsersBySearchTerm(searchTerm, user);

        userResults.remove(user);

        List<Friend> friendResults = friendRepository.findByUser(user.getId());
        List<FriendRequest> friendRequestResults = friendRequestRepository.findByUser(user.getId());

        HashSet<User> set = new HashSet<>();

        for (Friend current : friendResults) {
            set.add(current.getFirstUser());
            set.add(current.getSecondUser());
        }

        for (FriendRequest current : friendRequestResults) {
            set.add(current.getFirstUser());
            set.add(current.getSecondUser());
        }

        ArrayList<User> result = new ArrayList<>();

        for (int k = 0; k < userResults.size(); k++) {
            User current = userResults.get(k);
            if (!set.contains(current)) {
                result.add(current);
            }
        }

        return result.stream().limit(10).toList();
    }
}
