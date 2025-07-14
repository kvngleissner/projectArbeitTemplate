package projekt.rest.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import projekt.database.DatabaseService;
import projekt.util.TokenService;

import java.util.UUID;

@Path( "auth")
public class UserAuthenticationResource {
    @GET
    @Path("token/{username}/{password}")
    public Response getToken(@PathParam("username") String username,@PathParam("password") String password) {
        if(DatabaseService.getInstance().authenticateUser(username, password)){
            String bearerToken = TokenService.generateToken(username);

            return Response.status(Response.Status.OK).entity(bearerToken).build();
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid user or password!").build();
        }
    }
}
