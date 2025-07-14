package projekt.rest.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("world")
public class TestResource {

  @GET
  @Path("hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String getHelloWorld() {
    return "Hello World!";
  }
}