package org.johannesstm.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.johannesstm.entity.User;
import org.johannesstm.exception.AuthenticationPasswordException;
import org.johannesstm.exception.AuthenticationUsernameException;
import org.johannesstm.hash.HashLong;
import org.johannesstm.repository.UserRepository;
import org.johannesstm.request.AuthenticationRequest;
import org.johannesstm.request.StringRequest;
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
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("/authentication")
@Consumes(MediaType.APPLICATION_JSON)
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
    public Response register(AuthenticationRequest authRequest) throws Exception {
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

    //@RolesAllowed("ROLE_APPLICATION")
    @POST
    @Path("/changeProfilePicture")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public String uploadProfilePicture(@MultipartForm ProfilePictureUploadForm form) {
        Optional<User> user = userRepository.findByEmail(jwt.getName());

        byte[] profilePicture = form.getProfilePicture();

        user.get().setProfilePicture(profilePicture);

        String base64Image = Base64.getEncoder().encodeToString(profilePicture);

        userRepository.persist(user.get());
        return base64Image;
    }

    //@Operation(summary = "get a profilePicture by userId as base64 String")
    //@RolesAllowed("ROLE_APPLICATION")
    @RolesAllowed("ROLE_APPLICATION")
    @POST
    @Path("/profilePicture")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfilePictureByEncryptedUserId(StringRequest stringRequest) throws IOException {

        //Long userId = HashLong.decryptLong(stringRequest.getContent(), "Spaßvogeltruppe");
        Long userId = Long.valueOf(stringRequest.getContent());

        Optional<User> user = userRepository.findByIdOptional(userId);

        if (user.get().getProfilePicture1() != null) {

            String base64Image = Base64.getEncoder().encodeToString(user.get().getProfilePicture1());
            return Response.ok(base64Image).build();
        }

        return Response.ok(Response.Status.NOT_FOUND).build();
    }

}
