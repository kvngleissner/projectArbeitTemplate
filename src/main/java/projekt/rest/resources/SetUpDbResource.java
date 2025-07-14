package projekt.rest.resources;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import projekt.database.DatabaseService;
import projekt.util.TokenService;

@Path("setupDB")
public class SetUpDbResource {
  private static final Logger logger = LogManager.getLogger(SetUpDbResource.class);


  @DELETE
  public Response setupDb(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
    Response response = TokenService.verifyAuthToken(authHeader);
    if (response != null) {
      return response;
    }
    logger.info("Attempting to set up Database");
      DatabaseService.getInstance().removeAllTables();
      DatabaseService.getInstance().createAllTables();
      return Response.status(Response.Status.OK).build();

  }
}

