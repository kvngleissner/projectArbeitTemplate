import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static projekt.rest.Server.restartServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.jsv.JsonSchemaValidator;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.C;
import org.hamcrest.MatcherAssert;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import projekt.Interfaces.ICustomer.Gender;
import projekt.Interfaces.IReading.KindOfMeter;
import projekt.database.DatabaseService;
import projekt.model.Customer;
import projekt.model.JsonMapping.ReadingJSON;
import projekt.model.Reading;
import projekt.rest.Server;
import projekt.util.CSVReader;
import projekt.util.RestClient;;

public class ReadingResourceTests {
  Logger logger = LogManager.getLogger(ReadingResourceTests.class);
  LocalDate testDate = LocalDate.now();
  UUID testCustomerId = UUID.fromString("1ad185f1-15e9-451f-ba14-9bfbdee9c472");
  Customer testCustomer = new Customer(testCustomerId,testDate,"Max","Mustermann", Gender.M);
  UUID testReadingId = UUID.fromString("4ddcb72c-9daf-4cad-b0c1-9d9367903d6c");
  Reading testReading = new Reading(testReadingId,testCustomer, KindOfMeter.POWER,"MTR-ID",69.69,false,testDate,"");
  static ObjectMapper objectMapper = new ObjectMapper();

  @BeforeAll
  static void setup(){
    restartServer("http://localhost:8056/rest");
    objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
  }
  @BeforeAll
  static void setupDB(){
    DatabaseService.getInstance().removeAllTables();
    DatabaseService.getInstance().createAllTables();
    CSVReader csvReader = new CSVReader();
    DatabaseService.getInstance().insertCustomerBatch(csvReader.readCustomerCSV("src/main/resources/kunden_utf8.csv"));
    DatabaseService.getInstance().insertReadingBatch(csvReader.readReadingCSV("src/main/resources/strom.csv",KindOfMeter.POWER));
    DatabaseService.getInstance().insertUser("Test","password");
  }

  @AfterAll
  static void stopServer(){
    Server.stopServer();
  }

  @BeforeEach
  void truncateTables() {
    DatabaseService.getInstance().truncateAllTables();
  }

  @Test
  @DisplayName("""
      TestReadingInsert""")
  void testReadingInsert() throws JsonProcessingException {
    Response response = RestClient.sendReadingPost(testReading);
    assert(response.getStatus() == 201);
  }

  @Test
  @DisplayName("""
      TestReadingInsert-EmptyId""")
  void testReadingInsert_EmptyId() throws JsonProcessingException {
    testReading.setId(null);
    Response response = RestClient.sendReadingPost(testReading);
    assert(response.getStatus() == 201);
  }

  @Test
  @DisplayName("""
      TestReadingInsert-BadRequest""")
  void testReadingInsert_BadRequest() throws JsonProcessingException {
    DatabaseService.getInstance().insertReading(testReading);
    Response response = RestClient.sendReadingPost(testReading);
    assert(response.getStatus() == 400);
    logger.info("Finished with response code {}", response.getStatus());
  }

  @Test
  @DisplayName("""
      TestReadingInsert-AuthFail""")
  void testReadingInsert_AuthFail() throws JsonProcessingException {
    Response response = RestClient.sendReadingPost(testReading, "Wrong", "Token");
    assert(response.getStatus() == 401);
    logger.info("Finished with response code {}", response.getStatus());
  }

  @Test
  @DisplayName("""
      TestReadingGet""")
  void testReadingGet() throws IOException, JSONException {
    DatabaseService.getInstance().insertReading(testReading);
    Response response = RestClient.sendReadingGet(testReading.getId().toString());
    assert(response.getStatus() == 200);
    ReadingJSON readingJSON = objectMapper.readValue(response.readEntity(String.class), ReadingJSON.class);
    assertEquals(readingJSON.toReading(), testReading);
  }

  @Test
  @DisplayName("""
      TestReadingGet-NotFound""")
  void testReadingGet_NotFound() throws IOException, JSONException {
    Response response = RestClient.sendReadingGet(testReading.getId().toString());
    assert(response.getStatus() == 404);
    logger.info("Finished with response code {}", response.getStatus());
  }

  @Test
  @DisplayName("""
      TestReadingGet-AuthFail""")
  void testReadingGet_AuthFail() throws IOException, JSONException {
    Response response = RestClient.sendReadingGet(testReading.getId().toString(), "Wrong", "Token");
    assert(response.getStatus() == 401);
    logger.info("Finished with response code {}", response.getStatus());
  }

  /**
   * Tests the update functionality for a reading entry.
   *
   * This test verifies that an existing reading can be successfully updated
   * via a PUT request and that the updated values can be retrieved
   * correctly with a subsequent GET request.
   *
   * @throws JsonProcessingException if there is an error in processing JSON content.
   */
  @Test
  @DisplayName("""
      TestReadingUpdate""")
  void testReadingUpdate() throws JsonProcessingException {
    Response postResponse = RestClient.sendReadingPost(testReading);     // Post Customer to be inserted into Database
    Reading updatedReading = new Reading(testReading.getId(),testCustomer, KindOfMeter.WATER,"UPD-ID",69.69,false,
        LocalDate.now(),"");
    assertNotEquals(updatedReading,objectMapper.readValue(RestClient.sendReadingGet(testReading.getId().toString()).readEntity(String.class),ReadingJSON.class)); // Assertion: Updated customer is not identical to pre-updated customer
    Response response2 = RestClient.sendReadingPut(updatedReading); // Updating Reading
    assert(response2.getStatus() == 200); // Asserting that response code is 200
    Response response3 = RestClient.sendReadingGet(testReading.getId().toString());
    ReadingJSON readingJSON = objectMapper.readValue(response3.readEntity(String.class), ReadingJSON.class);
    ReadingJSON updateReadingJSON = updatedReading.toReadingJSON();
    assertEquals(readingJSON,updateReadingJSON); // Asserting that reading in DB is equal to update reading
  }

  @Test
  @DisplayName("""
      TestReadingUpdate-BadRequest""")
  void testReadingUpdate_BadRequest() throws JsonProcessingException {
    //@TODO Nullpointer Exception with Double metercount
    //Reading emptyReading = new Reading();
    testReading.setId(null);
    logger.info("Reading id is set to null");
    //Response response = RestClient.sendReadingPut(emptyReading);
    Response response = RestClient.sendReadingPut(testReading);

    assert(response.getStatus() == 400);
    logger.info("Finished with response code {}", response.getStatus());
  }

  @Test
  @DisplayName("""
      TestReadingUpdate-NotFound""")
  void testReadingUpdate_NotFound() throws JsonProcessingException {
    logger.info("Test reading does not exist in database yet");
    Response response = RestClient.sendReadingPut(testReading);

    assert(response.getStatus() == 404);
    logger.info("Finished with response code {}", response.getStatus());
  }

  @Test
  @DisplayName("""
      TestReadingUpdate-AuthFail""")
  void testReadingUpdate_AuthFail() throws JsonProcessingException {
    Response postResponse = RestClient.sendReadingPost(testReading);     // Post Customer to be inserted into Database
    Reading updatedReading = new Reading(testReading.getId(),testCustomer, KindOfMeter.WATER,"UPD-ID",69.69,false,
            LocalDate.now(),"");
    assertNotEquals(updatedReading,objectMapper.readValue(RestClient.sendReadingGet(testReading.getId().toString()).readEntity(String.class),ReadingJSON.class)); // Assertion: Updated customer is not identical to pre-updated customer
    Response response2 = RestClient.sendReadingPut(updatedReading, "Wrong", "Token"); // Updating Reading
    assert(response2.getStatus() == 401);
    logger.info("Finished with response code {}", response2.getStatus());
  }

  @Test
  @DisplayName("""
      TestReadingDelete""")
  public void testReadingDelete() throws JsonProcessingException {
    Response postResponse = RestClient.sendReadingPost(testReading);
    assert(postResponse.getStatus() == 201); // Inserted succesfully
    Response response = RestClient.sendReadingDelete(testReading.getId().toString());
    assert(response.getStatus() == 200); // Deleted succesfully
    assertEquals(objectMapper.readValue(response.readEntity(String.class),ReadingJSON.class),testReading.toReadingJSON()); // Endpoint responds with deleted Reading
  }

  @Test
  @DisplayName("""
      TestReadingDelete-NotFound""")
  public void testReadingDelete_NotFound() throws JsonProcessingException {
    Response response = RestClient.sendReadingDelete(testReading.getId().toString());
    assert(response.getStatus() == 404); // Reading no longer exists
    logger.info("Finished with response code {}", response.getStatus());
  }

  @Test
  @DisplayName("""
      TestReadingDelete-InvalidId""")
  public void testReadingDelete_InvalidId() throws JsonProcessingException {
    Response response = RestClient.sendReadingDelete("falseId");
    assert(response.getStatus() == 400); // Reading no longer exists
    logger.info("Finished with response code {}", response.getStatus());
  }

  @Test
  @DisplayName("""
      TestReadingDelete-AuthFail""")
  public void testReadingDelete_AuthFail() throws JsonProcessingException {
    Response postResponse = RestClient.sendReadingPost(testReading);
    assert (postResponse.getStatus() == 201); // Inserted succesfully
    Response response = RestClient.sendReadingDelete(testReading.getId().toString(), "Wrong", "Token");
    assert (response.getStatus() == 401); // Invalid Token}
    logger.info("Finished with response code {}", response.getStatus());
  }

  @Test
  @DisplayName("""
      FilteredReadingGet""")
  public void testFilteredReadingGet() throws JsonProcessingException {
    
    //TestData
    List<Reading> testReadings = new ArrayList<>();

    for (int i = 0; i < 30; i++) {
      testReadings.add(new Reading(
          UUID.randomUUID(),
          testCustomer,
          KindOfMeter.POWER,
          "MTR-00" + i,
          50.0 + i * 5,
          i % 2 == 0,
          LocalDate.now().plusDays(i),
          "Comment" + i
      ));
    }
    DatabaseService.getInstance().insertCustomer(testCustomer);
    DatabaseService.getInstance().insertReadingBatch(testReadings);
    LocalDate startDate = LocalDate.now().minusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(1);

    // Readings exist without date filter
    Response response = RestClient.getFilteredReadings(testReading.getCustomer().getId().toString(),null,null,KindOfMeter.POWER);
    assert(response.getStatus() == 200);
    try {
      String jsonInput = response.readEntity(String.class);
      JsonNode rootNode = objectMapper.readTree(jsonInput);
      if (rootNode.isArray()) {
        List<ReadingJSON> readings = objectMapper.readValue(jsonInput, new TypeReference<List<ReadingJSON>>() {});
        assertEquals(readings.size(), testReadings.size());
      } else {
        ReadingJSON singleReading = objectMapper.readValue(jsonInput, ReadingJSON.class);
        assertEquals(singleReading, testReadings.getFirst().toReadingJSON());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Readings exist and filtered with date
    Response response2 = RestClient.getFilteredReadings(testReading.getCustomer().getId().toString(),startDate,endDate,KindOfMeter.POWER);
    assert(response.getStatus() == 200);
    logger.info("Finished with response code {}", response.getStatus());

    // Readings don't exist
    Response response3 = RestClient.getFilteredReadings(testReading.getCustomer().getId().toString(),startDate,endDate,KindOfMeter.WATER);
    assert(response3.getStatus() == 404);
    logger.info("Finished with response code {}", response3.getStatus());

    // CustomerId is null, empty or doesn't match
    Response response4 = RestClient.getFilteredReadings(null,startDate,endDate,KindOfMeter.WATER);
    assert(response4.getStatus() == 400);
    logger.info("Finished with response code {}", response4.getStatus());

    Response response5 = RestClient.getFilteredReadings("",startDate,endDate,KindOfMeter.WATER);
    assert(response5.getStatus() == 400);
    logger.info("Finished with response code {}", response5.getStatus());

    Response response6 = RestClient.getFilteredReadings("invalidId",startDate,endDate,KindOfMeter.WATER);
    assert(response6.getStatus() == 400);
    logger.info("Finished with response code {}", response6.getStatus());

    Response response7 = RestClient.getFilteredReadings(testReading.getCustomer().getId().toString(),startDate,endDate,KindOfMeter.POWER, "Wrong", "Token");
    assert(response7.getStatus() == 401);
    logger.info("Finished with response code {}", response7.getStatus());
  }

  @Test
  @DisplayName("""
      FilteredReadingGet-MalformedDate""")
  public void testFilteredReadingGet_MalformedDate() throws JsonProcessingException {
    //TestData
    List<Reading> testReadings = new ArrayList<>();

    for (int i = 0; i < 3; i++) {
      testReadings.add(new Reading(
              UUID.randomUUID(),
              testCustomer,
              KindOfMeter.POWER,
              "MTR-00" + i,
              50.0 + i * 5,
              i % 2 == 0,
              LocalDate.now().plusDays(i),
              "Comment" + i
      ));
    }
    DatabaseService.getInstance().insertCustomer(testCustomer);
    DatabaseService.getInstance().insertReadingBatch(testReadings);

    LocalDate startDate = LocalDate.now().minusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(1);

    // alter to malformed date format
    String startDateMalformed = startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    String endDateMalformed = null;

    // request for malformed start date
    Response response = RestClient.getFilteredReadingsFailure(testReading.getCustomer().getId().toString(), startDateMalformed, endDateMalformed,KindOfMeter.POWER);
    assert(response.getStatus() == 400);
    logger.info("Finished with response code {}", response.getStatus());

    // request for malformed end date
    startDateMalformed = null;
    endDateMalformed = endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    Response response2 = RestClient.getFilteredReadingsFailure(testReading.getCustomer().getId().toString(), startDateMalformed, endDateMalformed,KindOfMeter.POWER);
    assert(response2.getStatus() == 400);
    logger.info("Finished with response code {}", response2.getStatus());
  }
}

