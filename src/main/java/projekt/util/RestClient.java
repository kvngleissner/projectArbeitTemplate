package projekt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.UUID;
import org.apache.logging.log4j.Logger;
import projekt.Interfaces.ICustomer.Gender;
import projekt.Interfaces.IReading;
import projekt.Interfaces.IReading.KindOfMeter;
import projekt.database.DatabaseService;
import projekt.model.Customer;
import projekt.model.JsonMapping.CustomerJSON;
import projekt.model.JsonMapping.ReadingJSON;
import projekt.model.Reading;

import static projekt.rest.Server.restartServer;


public class RestClient {

  static Client client = ClientBuilder.newClient();
  static WebTarget target = client.target("http://localhost:8056/rest");
  static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(RestClient.class);
  /**
   * Sends a {@code DELETE} setupDB request to the REST-Endpoint
   * (for testing purposes -> sets up the database)
   * @return response
   */
  public static Response sendSetupDB(String userName, String password) {
    logger.info("Sending setupDB request to REST-Endpoint...");
    return target.path("setupDB")
        .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet(userName, password))
        .delete();
  }

  // Overload von sendSetupDB, damit für Validation Errors getestet werden können
  public static Response sendSetupDB() {
    return sendSetupDB("Test", "password");
  }

  public static String sendUserAuthGet(String userName, String password) {
    logger.info("Sending User Auth request to REST-Endpoint...");
      return target.path("auth/token").path(userName).path(password).request(MediaType.TEXT_PLAIN).get().readEntity(String.class);
  }

  /**
   * Sends a GET Customer request to the REST-Endpoint
   *
   * @param id of the customer
   * @return Customer Object with the given id (if exists)
   */
  public static Response sendCustomerGet(String id, String userName, String password) throws JsonProcessingException {
    logger.info("Sending GET Customer with ID {} request to REST-Endpoint...",id);
    return target.path("customers")
        .path(id)
        .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet(userName, password))
            .get();
  }

  public static Response sendCustomerGet(String id) throws JsonProcessingException {
    return sendCustomerGet(id, "Test", "password");
  }

  /**
   * Sends a post request to the REST-Endpoint containing a customer object
   *
   * @param customer
   * @return response
   * @throws JsonProcessingException
   */
  public static Response sendCustomerPost(Customer customer, String userName, String password) throws JsonProcessingException {
    logger.info("Sending POST Customer with ID {} request to REST-Endpoint...",customer.getId().toString());
    CustomerJSON inserCustomerJSON = customer.toCustomerJSON();
    return target.path("customers")
        .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet(userName,password))
            .post(Entity.entity(
            objectMapper.writeValueAsString(inserCustomerJSON),
            MediaType.APPLICATION_JSON));
  }

  public static Response sendCustomerPost(Customer customer) throws JsonProcessingException {
    return sendCustomerPost(customer,"Test", "password");
  }

  /**
   * Sends a put request to the REST-Endpoint containing an updated customer object
   *
   * @param customer
   * @return response
   * @throws JsonProcessingException
   */
  public static Response sendCustomerPut(Customer customer, String userName, String password) throws JsonProcessingException {
      logger.info("Sending PUT Customer with ID {} request to REST-Endpoint...",customer.getId()==null ? null : customer.getId().toString());
    CustomerJSON updateCustomerJSON = customer.toCustomerJSON();
    return target.path("customers")
        .request()
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet(userName,password))
            .put(Entity.entity(
            objectMapper.writeValueAsString(updateCustomerJSON),
            MediaType.APPLICATION_JSON));
  }

  public static Response sendCustomerPut(Customer customer) throws JsonProcessingException {
    return sendCustomerPut(customer, "Test", "password");
  }

  /**
   *  Sends a delete request to the REST-Endpoint
   * @param id of the Customer to be deleted
   * @return response
   */
  public static Response sendCustomerDelete(String id, String userName, String password) {
    logger.info("Sending DELETE Customer with ID {} request to REST-Endpoint...",id);
    return target.path("customers")
        .path(id)
        .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet(userName,password))
            .delete();
  }

  public static Response sendCustomerDelete(String id) {
    return sendCustomerDelete(id, "Test", "password");
  }

  /**
   * Sends a GET request to the REST-Endpoint, response contains a list of all Customers in the database
   * @return response
   * @throws JsonProcessingException
   */
  public static Response sendAllCustomersGet(String userName, String password) throws JsonProcessingException {
    logger.info("Sending GET request to retrieve all Customers from REST-Endpoint...");
    return target.path("customers")
        .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet(userName,password))
            .get();
  }

  public static Response sendAllCustomersGet() throws JsonProcessingException {
    return sendAllCustomersGet("Test", "password");
  }


  /**
   * Sends a GET reading request to the REST-Endpoint
   *
   * @param id of the reading
   * @return Reading Object with the given id (if exists)
   */
  public static Response sendReadingGet(String id, String userName, String password) throws JsonProcessingException {
    logger.info("Sending GET reading with ID {} request to REST-Endpoint...",id);
    return target.path("readings")
        .path(id)
        .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet(userName,password))
            .get();
  }

  public static Response sendReadingGet(String id) throws JsonProcessingException {
    return sendReadingGet(id, "Test", "password");
  }

  /**
   * Sends a post request to the REST-Endpoint containing a customer object
   *
   * @param reading
   * @return response
   * @throws JsonProcessingException
   */
  public static Response sendReadingPost(Reading reading, String userName, String password) throws JsonProcessingException {
    logger.info("Sending POST reading with ID {} request to REST-Endpoint...",reading.getId()==null? null: reading.getId().toString());
    ReadingJSON insertReadingJSON = reading.toReadingJSON();
    return target.path("readings")
        .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet(userName, password))
            .post(Entity.entity(
            objectMapper.writeValueAsString(insertReadingJSON),
            MediaType.APPLICATION_JSON));
  }

  public static Response sendReadingPost(Reading reading) throws JsonProcessingException {
    return sendReadingPost(reading, "Test", "password");
  }

  /**
   * Sends a put request to the REST-Endpoint containing an updated reading object
   *
   * @param reading
   * @return response
   * @throws JsonProcessingException
   */
  public static Response sendReadingPut(Reading reading, String userName, String password) throws JsonProcessingException {
    logger.info("Sending PUT reading with ID {} request to REST-Endpoint...",reading.getId()==null ? null : reading.getId().toString());
    ReadingJSON updatedReadingJSON = reading.toReadingJSON();
    return target.path("readings").request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet(userName,password))
            .put(Entity.entity(
            objectMapper.writeValueAsString(updatedReadingJSON),
            MediaType.APPLICATION_JSON));
  }

  public static Response sendReadingPut(Reading reading) throws JsonProcessingException {
    return sendReadingPut(reading, "Test", "password");
  }

  /**
   * Sends a DELETE request to the REST-Endpoint to delete a reading with the specified ID.
   *
   * @param id the unique identifier of the reading to be deleted
   * @return a Response object containing the result of the DELETE request
   */
  public static Response sendReadingDelete(String id, String userName, String password) {
    logger.info("Sending DELETE reading with ID {} request to REST-Endpoint...",id);
    return target.path("readings")
        .path(id)
        .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet(userName,password))
            .delete();
  }

  public static Response sendReadingDelete(String id) {
    return sendReadingDelete(id, "Test", "password");
  }

  /**
   * Sends a GET request to retrieve filtered meter readings from the REST-Endpoint.
   *
   * @param customerID the ID of the customer for whom the readings are requested
   * @param startDate the start date for the readings filter; can be null
   * @param endDate the end date for the readings filter; can be null
   * @param kindOfMeter the type of meter for which readings are requested
   * @return a Response object containing the result of the GET request
   * @throws JsonProcessingException if there is an error in processing the JSON response
   */
  public static Response getFilteredReadings(String customerID, LocalDate startDate, LocalDate endDate, KindOfMeter kindOfMeter, String userName, String password) throws JsonProcessingException {
    logger.info("Sending GET request to retrieve filtered meter readings (ID: {} , start: {}, end: {}, kindOfMeter: {}) from REST-Endpoint...",customerID,startDate,endDate,kindOfMeter == null ? null : kindOfMeter.toString());
    return target.path("readings")
        .queryParam("customer", customerID)
        .queryParam("start", startDate == null ? null : startDate.toString())
        .queryParam("end", endDate == null ? null : endDate.toString())
        .queryParam("kindOfMeter", kindOfMeter)
        .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet(userName,password))
            .get();
  }

  public static Response getFilteredReadings(String customerID, LocalDate startDate, LocalDate endDate, KindOfMeter kindOfMeter) throws JsonProcessingException {
    return getFilteredReadings(customerID, startDate, endDate, kindOfMeter, "Test", "password");
  }

  /**
   * Testing purpose - in the case of a malformed date format in the request path
   *
   * @param customerID the ID of the customer for whom the readings are requested
   * @param startDate the start date for the readings filter as a String; can be null
   * @param endDate the end date for the readings filter as a String; can be null
   * @param kindOfMeter the type of meter for which readings are requested
   * @return a Response object containing the result of the GET request
   * @throws JsonProcessingException if there is an error in processing the JSON response
   */
  public static Response getFilteredReadingsFailure(String customerID, String startDate, String endDate, KindOfMeter kindOfMeter) throws JsonProcessingException {
    logger.info("Sending GET request to retrieve filtered meter readings (ID: {} , start: {}, end: {}, kindOfMeter: {}) from REST-Endpoint...",customerID,startDate,endDate,kindOfMeter == null ? null : kindOfMeter.toString());
    return target.path("readings")
            .queryParam("customer", customerID)
            .queryParam("start", startDate)
            .queryParam("end", endDate)
            .queryParam("kindOfMeter", kindOfMeter)
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ sendUserAuthGet("Test","password"))
            .get();
  }

  public static void main(String[] args) throws JsonProcessingException {
    restartServer("http://localhost:8056/rest"); // Stelle sicher, dass du den Server in dieser Methode startest
    objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    DatabaseService.getInstance().removeAllTables();
    DatabaseService.getInstance().createAllTables();
    CSVReader csvReader = new CSVReader();
    DatabaseService.getInstance().insertCustomerBatch(csvReader.readCustomerCSV("src/main/resources/kunden_utf8.csv"));
    DatabaseService.getInstance().insertReadingBatch(csvReader.readReadingCSV("src/main/resources/strom.csv",KindOfMeter.POWER));
    DatabaseService.getInstance().insertUser("Test","password");
  }
}