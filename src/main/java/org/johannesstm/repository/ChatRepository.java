package org.johannesstm.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.johannesstm.entity.Chat;
import org.johannesstm.entity.Message;
import org.johannesstm.entity.User;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ApplicationScoped
public class ChatRepository implements PanacheRepository<Chat> {

    public Optional<Chat> findByChatId(final Long chatId) {
        return find("chatId", chatId).singleResultOptional();
    }

    public List<Chat> findByEmail(final String email) {

        List<Chat> chatAdmins = find("select c from Chat c INNER JOIN c.chatAdmins u where u.email = ?1", email).list();
        List<Chat> chatUsers = find("select c from Chat c INNER JOIN c.chatUsers u where u.email = ?1", email).list();

        return Stream.concat(chatAdmins.stream(), chatUsers.stream()).toList();
    }

}
