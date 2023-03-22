package org.johannesstm.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.johannesstm.entity.Friend;
import org.johannesstm.entity.FriendRequest;
import org.johannesstm.entity.User;
import org.johannesstm.repository.FriendRepository;
import org.johannesstm.repository.FriendRequestRepository;
import org.johannesstm.repository.UserRepository;
import org.johannesstm.request.DecisionRequest;
import org.johannesstm.request.StringRequest;
import org.johannesstm.service.FriendService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/friends")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class FriendController {

    private final UserRepository userRepository;

    private final FriendRepository friendRepository;

    private final FriendRequestRepository friendRequestRepository;
    private final FriendService friendService;

    @Inject
    JsonWebToken jwt;

    public FriendController(UserRepository userRepository, FriendRepository friendRepository, FriendRequestRepository friendRequestRepository, FriendService friendService) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.friendService = friendService;
    }

    @Operation(summary = "get friends")
    @RolesAllowed("ROLE_APPLICATION")
    @GET
    public List<Friend> getFriends() {

        Optional<User> user = userRepository.findByEmail(jwt.getName());

        return friendRepository.findByUser(user.get().getId());
    }

    @Operation(summary = "get friend requests")
    @RolesAllowed("ROLE_APPLICATION")
    @GET
    @Path("/requests")
    public List<FriendRequest> getFriendRequests() {

        Optional<User> user = userRepository.findByEmail(jwt.getName());

        return friendRequestRepository.findByUser(user.get().getId());
    }

    @Operation(summary = "add one user")
    @RolesAllowed("ROLE_APPLICATION")
    @POST
    @Path("/addFriend")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addFriend(StringRequest addFriendRequest) {

        Optional<User> user = userRepository.findByEmail(jwt.getName());
        Optional<User> user1 = userRepository.findByEmail(addFriendRequest.getContent());

        if (user.isPresent() && user1.isPresent() && !user.equals(user1)) {

            if (friendRequestRepository.existsByFirstUserAndSecondUser(user.get(), user1.get()) || friendRepository.existsByFirstUserAndSecondUser(user.get(), user1.get())) {

                return Response.status(Response.Status.CONFLICT).entity("User already added").build();
            } else {
                FriendRequest friendRequest = new FriendRequest();
                friendRequest.setFirstUser(user.get());
                friendRequest.setSecondUser(user1.get());
                friendRequestRepository.persist(friendRequest);

                return Response.ok(friendRequest).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
    }

    @Operation(summary = "accept or reject a friend request")
    @RolesAllowed("ROLE_APPLICATION")
    @PUT
    @Path("/requests/decide/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response acceptFriendRequest(DecisionRequest decisionRequest, @PathParam("id") Long id) {

        Optional<FriendRequest> friendRequest = Optional.ofNullable(friendRequestRepository.findById(id));

        if (friendRequest.isPresent()) {
            if (decisionRequest.isDecision()) {

                Friend friend = new Friend();
                friend.setFirstUser(friendRequest.get().getFirstUser());
                friend.setSecondUser(friendRequest.get().getSecondUser());

                friendRequestRepository.delete(friendRequest.get());
                friendRepository.persist(friend);

                System.out.println(friend);

                return Response.ok(friend).build();
            } else {
                friendRequestRepository.delete(friendRequest.get());
                return Response.ok("request rejected").build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).entity("request not found").build();
    }

    @Operation(summary = "remove one friendRequest")
    @RolesAllowed("ROLE_APPLICATION")
    @DELETE
    @Path("/requests/remove/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response deleteFriendRequest(@PathParam("id") Long id) {

        Optional<FriendRequest> friendRequest = Optional.ofNullable(friendRequestRepository.findById(id));

        if (friendRequest.isPresent()) {

            friendRequestRepository.delete(friendRequest.get());

            return Response.ok("friendRequest deleted").build();
        }

        return Response.status(Response.Status.NOT_FOUND).entity("friendRequest not found").build();
    }

    @Operation(summary = "remove one friend")
    @RolesAllowed("ROLE_APPLICATION")
    @DELETE
    @Path("/remove/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response removeFriend(@PathParam("id") Long id) {

        Optional<Friend> friend = Optional.ofNullable(friendRepository.findById(id));

        if (friend.isPresent()) {

            friendRepository.delete(friend.get());

            return Response.ok("friend deleted").build();
        }

        return Response.status(Response.Status.NOT_FOUND).entity("friend not found").build();
    }

    @Operation(summary = "search for new friends")
    @RolesAllowed("ROLE_APPLICATION")
    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response searchFriends(StringRequest stringRequest) {

        if (stringRequest.getContent().length() == 0) {

            return Response.status(Response.Status.CONFLICT).entity("length of searchTerm is 0").build();
        }

        Optional<User> user = userRepository.findByEmail(jwt.getName());

        return Response.ok(friendService.searchFriends(stringRequest.getContent(), user.get())).build();

    }

//    @POST
//   @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public TemplateInstance upload(@MultipartForm FileUploadInput input) throws IOException {
//
//        System.out.println(">>>");
//        System.out.println("input.text = " + input.text);
//        System.out.println("input.file = " + input.file.getAbsolutePath());
//        System.out.println("content = " + Files.readString(input.file.toPath()));
//        System.out.println("<<<");
//
//        return template.instance();
//    }
//
//    public static class FileUploadInput {
//
//        @FormParam("text")
//        public String text;
//
//        @FormParam("file")
//        public File file;
//
//    }


}

