package projekt.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.JacksonFeature;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import projekt.Interfaces.ICustomer;
import projekt.Interfaces.IReading.KindOfMeter;
import projekt.database.DatabaseService;
import projekt.model.Customer;
import projekt.util.CSVReader;
import projekt.util.RestClient;

public class Server {

  private static final Logger logger = LogManager.getLogger(Server.class);
  private static HttpServer server;
  private static boolean running;

  public static void startServer(String url) {
    final String pack = "projekt.rest.resources";
    logger.info("Starting REST server with URL: {}", url);

    final ResourceConfig rc = new ResourceConfig()
        .packages(pack)
        .register(CorsFilter.class);
    server = JdkHttpServerFactory.createHttpServer(URI.create(url), rc);
    logger.info("Server started...");
    running=true;

  }
  public static void stopServer() {
    logger.info("stopping rest server");
    server.stop(0);
    running=false;
  }

  public static void restartServer(String url){
    if(running){
      stopServer();
    }
    startServer(url);
  }

  // For test purposes
  public static void main(String[] args) throws JsonProcessingException {

    restartServer("http://localhost:8056/rest");
  }
}