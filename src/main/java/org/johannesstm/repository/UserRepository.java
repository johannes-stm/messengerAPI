package org.johannesstm.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.johannesstm.entity.User;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public Optional<User> findByEmail(final String email){
        return find("email", email).singleResultOptional();
    }

    public List<User> findUsersBySearchTerm(final String searchTerm, final User user){

        String searchInput = "%" + searchTerm + "%";


        return find("email like ?1", searchInput).range(0, 25).list();
    }
}
