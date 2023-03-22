package org.johannesstm.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.johannesstm.entity.User;
import org.johannesstm.exception.AuthenticationPasswordException;
import org.johannesstm.exception.AuthenticationUsernameException;
import org.johannesstm.repository.UserRepository;
import org.johannesstm.request.AuthenticationRequest;
import org.johannesstm.response.AuthenticationResponse;
import org.johannesstm.service.UserService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
@Path("/authentication")
@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserController {

    private final UserRepository userRepository;

    private final UserService userService;

    @Inject
    JsonWebToken jwt;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Operation(summary = "register with email and password")
    @PermitAll
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(AuthenticationRequest authRequest) {
        final String token = userService.register(authRequest);

        return Response.ok(token).build();
    }

    @Operation(summary = "login with email and password")
    @PermitAll
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(AuthenticationRequest authRequest) throws AuthenticationUsernameException, AuthenticationPasswordException {
        final AuthenticationResponse response = userService.authenticate(authRequest);
        return Response.ok(response).build();
    }

    @RolesAllowed("ROLE_APPLICATION")
    @GET
    @Path("/validateToken")
    public Optional<User> validateToken() {

        return userRepository.findByEmail(jwt.getName());
    }



}
