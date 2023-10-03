package org.johannesstm.controller;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.johannesstm.entity.ResetPasswordToken;
import org.johannesstm.entity.User;
import org.johannesstm.repository.ResetPasswordTokenRepository;
import org.johannesstm.repository.UserRepository;
import org.johannesstm.request.StringRequest;
import org.johannesstm.service.CryptoService;
import org.johannesstm.service.UserService;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Path("/resetPassword")
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ResetPasswordController {

    @Inject
    Mailer mailer;

    private final UserRepository userRepository;

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    private final UserService userService;

    private final CryptoService cryptoService;

    @Inject
    JsonWebToken jwt;

    public ResetPasswordController(UserRepository userRepository, ResetPasswordTokenRepository resetPasswordTokenRepository, UserService userService, CryptoService cryptoService) {
        this.userRepository = userRepository;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.userService = userService;
        this.cryptoService = cryptoService;
    }

    @Operation(summary = "send reset password email")
    @PermitAll
    @POST
    @Path("/sendEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response sendEmail(StringRequest stringRequest) {

        Optional<User> user = userRepository.findByEmail(stringRequest.getContent());

        if (user.isEmpty()) {
            return Response.status(Response.Status.CONFLICT).entity("email does not exist").build();
        }

        ResetPasswordToken token = new ResetPasswordToken(user.get(), new Date());

        resetPasswordTokenRepository.persist(token);

        String link = "https://johannesstegmaier.de:5173/changePassword?resetPasswordToken=" + token.getResetPasswordTokenId();

        Mail mail = Mail.withText(user.get().getEmail(), "Mail sent with quarkus", link);

        mailer.send(mail);

        return Response.ok("resetpasswordtoken created").build();
    }

    @PermitAll
    @GET
    @Path("/validateToken/{uuid}")
    public Response validateToken(@PathParam("uuid") UUID uuid) {

        Optional<ResetPasswordToken> resetPasswordToken = resetPasswordTokenRepository.findByIdOptional(uuid);

        if (resetPasswordToken.isEmpty()) {

            return Response.status(Response.Status.BAD_REQUEST).entity("token is not valid").build();
        }

        Instant instant = resetPasswordToken.get().getCreatedOn().toInstant();
        Instant now = Instant.now();
        Instant then = now.minusSeconds(900);

        if ((!instant.isBefore(then)) && instant.isBefore(now)) {

            return Response.ok(resetPasswordToken.get().getOwner().getEmail()).build();
        }

        return Response.status(Response.Status.CONFLICT).entity("token is expired").build();
    }

    @Operation(summary = "change password")
    @PermitAll
    @POST
    @Path("/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response changePassword(@PathParam("uuid") UUID uuid, StringRequest stringRequest) {

        Optional<ResetPasswordToken> resetPasswordToken = resetPasswordTokenRepository.findByIdOptional(uuid);

        if (resetPasswordToken.isEmpty()) {

            System.out.println("test");

            return Response.status(Response.Status.BAD_REQUEST).entity("token is not valid").build();
        }

        Optional<User> user = userRepository.findByEmail(resetPasswordToken.get().getOwner().getEmail());

        user.get().setPassword(cryptoService.encrypt(stringRequest.getContent()));

        userRepository.persist(user.get());
        resetPasswordTokenRepository.delete(resetPasswordToken.get());

        return Response.ok("password changed").build();
    }

}
