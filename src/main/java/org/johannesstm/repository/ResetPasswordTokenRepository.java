package org.johannesstm.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.johannesstm.entity.ResetPasswordToken;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ResetPasswordTokenRepository implements PanacheRepository<ResetPasswordToken> {

    public Optional<ResetPasswordToken> findByIdOptional(UUID uuid) {
        return Optional.of(find("resetPasswordTokenId = ?1", uuid).firstResult());
    }
}
