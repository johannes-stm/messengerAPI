package org.johannesstm.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.johannesstm.entity.Chat;
import org.johannesstm.entity.Message;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MessageRepository implements PanacheRepository<Message> {


}
