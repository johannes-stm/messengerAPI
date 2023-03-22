package org.johannesstm.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(final Exception exception) {
        if (exception instanceof AuthenticationPasswordException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Wrong password")
                    .build();
        }
        if (exception instanceof AuthenticationUsernameException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Wrong username")
                    .build();
        }
        if (exception instanceof EmptyBodyException) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("input string is empty")
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(exception.getMessage())
                .build();
    }

}