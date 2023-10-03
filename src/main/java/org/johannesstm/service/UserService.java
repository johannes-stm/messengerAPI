package org.johannesstm.service;

import io.smallrye.jwt.auth.principal.DefaultJWTParser;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.johannesstm.entity.User;
import org.johannesstm.exception.AuthenticationPasswordException;
import org.johannesstm.exception.AuthenticationUsernameException;
import org.johannesstm.repository.UserRepository;
import org.johannesstm.request.AuthenticationRequest;
import org.johannesstm.response.AuthenticationResponse;
import org.jose4j.jwt.JwtClaims;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import javax.crypto.SecretKey;


@ApplicationScoped
public class UserService {

    @Inject
    JsonWebToken jwt;

    @Inject JWTParser parser;


    private final UserRepository userRepository;
    private final CryptoService cryptoService;

    public UserService(UserRepository userRepository, CryptoService cryptoService) {
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
    }

    @Transactional
    public String register(final AuthenticationRequest authRequest) throws Exception {

        if (authRequest.getEmail().length() == 0) {
            throw new Exception("You need to enter a email to create a user");
        } else if (authRequest.getPassword().length() == 0) {

            throw new Exception("You cannot create User with the password length of 0");
        }

        final boolean user = userRepository.findByEmail(authRequest.getEmail())
                .isPresent();
        if (user) {
            return "user already created";
        }

        User newUser = new User(authRequest.getEmail(), cryptoService.encrypt(authRequest.getPassword()), Collections.singleton("ROLE_APPLICATION"));

        userRepository.persist(newUser);

        return "user created";
    }

    public AuthenticationResponse authenticate(final AuthenticationRequest authRequest)
            throws AuthenticationUsernameException, AuthenticationPasswordException {
        final User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(AuthenticationUsernameException::new);
        if (user.getPassword().equals(cryptoService.encrypt(authRequest.getPassword()))) {
            return new AuthenticationResponse(generateToken(user), user);
        }
        throw new AuthenticationPasswordException();
    }

    private String generateToken(final User user) {

        return Jwt.issuer("quarkustodoAPI")
                .upn(user.getEmail())
                .expiresIn(Duration.ofDays(365))
                .groups(user.getRoles())
                .sign();
    }
}
