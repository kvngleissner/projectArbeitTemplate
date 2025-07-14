import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.client.*;
import org.json.JSONException;
import org.junit.jupiter.api.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import projekt.Interfaces.ICustomer;
import projekt.Interfaces.IReading;
import projekt.database.DatabaseService;
import projekt.model.Customer;
import projekt.model.JsonMapping.CustomerJSON;
import projekt.model.JsonMapping.ReadingJSON;
import projekt.model.Reading;
import projekt.util.CSVReader;
import projekt.util.RestClient;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static projekt.rest.Server.restartServer;
import static projekt.rest.Server.startServer;
import static projekt.rest.Server.stopServer;

//@TODO Klassenvererbung
public class CustomerResourceTests {
    Logger logger = LogManager.getLogger(CustomerResourceTests.class);
    static ObjectMapper objectMapper = new ObjectMapper();
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("http://localhost:8056/rest");
    LocalDate testDate = LocalDate.now();

    // Test Customer
    UUID testCustomerId = UUID.fromString("1ad185f1-15e9-451f-ba14-9bfbdee9c472");
    Customer testCustomer = new Customer(testCustomerId,testDate,"Max","Mustermann", ICustomer.Gender.M);

    @BeforeAll
    static void bootServer () {
        restartServer("http://localhost:8056/rest");
        objectMapper.registerModule(new JavaTimeModule());
    }
    @BeforeAll
    static void setupDB(){
        DatabaseService.getInstance().removeAllTables();
        DatabaseService.getInstance().createAllTables();
        CSVReader csvReader = new CSVReader();
        DatabaseService.getInstance().insertCustomerBatch(csvReader.readCustomerCSV("src/main/resources/kunden_utf8.csv"));
        DatabaseService.getInstance().insertReadingBatch(csvReader.readReadingCSV("src/main/resources/strom.csv", IReading.KindOfMeter.POWER));
        DatabaseService.getInstance().insertUser("Test","password");
    }

    @AfterAll
    static void shutdownServer() {
        stopServer();
    }

    @BeforeEach
    void truncateTables() {
        DatabaseService.getInstance().truncateAllTables();
    }

    //@TODO Parametisierte Tests für Testdatensätze

    /* Customer Tests */
    @Test
    @DisplayName("""
             Test-POST-Customer
          """)
    void testCustomerInsert() throws JsonProcessingException {
        Response response = RestClient.sendCustomerPost(testCustomer);
        assert(response.getStatus() == 201);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
             Test-POST-Fail-Customer
          """)
    void testCustomerInsert_Fail() throws JsonProcessingException {
        RestClient.sendCustomerPost(testCustomer);
        Response response = RestClient.sendCustomerPost(testCustomer);
        assert(response.getStatus() == 400);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
             Test-POST-AuthFail-Customer
          """)
    void testCustomerInsert_AuthFail() throws JsonProcessingException {
        Response response = RestClient.sendCustomerPost(testCustomer, "Wrong","Token");
        assert(response.getStatus() == 401);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
             Test-GET-ById-Customer
          """)
    void testCustomerGetByID() throws IOException {
        RestClient.sendCustomerPost(testCustomer);
        Response response = RestClient.sendCustomerGet(testCustomerId.toString());
        assert(response.getStatus() == 200);
        logger.info("Finished with response code {}", response.getStatus());
        CustomerJSON customerJSON = objectMapper.readValue(response.readEntity(String.class), CustomerJSON.class);
        assertEquals(customerJSON.toCustomer(), testCustomer);
    }

    @Test
    @DisplayName("""
             Test-GET-ById-NotFound-Customer
          """)
    void testCustomerGetByID_NotFound() throws IOException {
        Response response = RestClient.sendCustomerGet(testCustomerId.toString());
        assert(response.getStatus() == 404);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
             Test-GET-ById-AuthFail-Customer
          """)
    void testCustomerGetByID_AuthFail() throws IOException, JSONException {
        RestClient.sendCustomerPost(testCustomer);
        Response response = RestClient.sendCustomerGet(testCustomerId.toString(), "Wrong", "Token");
        assert(response.getStatus() == 401);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
             Test-DELETE-Customer
          """)
    void testCustomerDelete() throws IOException, SQLException {
        RestClient.sendCustomerPost(testCustomer);

        Response response = RestClient.sendCustomerDelete(testCustomerId.toString());
        assert(response.getStatus() == 200);
        logger.info("Finished with response code {}", response.getStatus());
        assertFalse(DatabaseService.getInstance().checkIfCustomerExists(testCustomerId));
    }

    @Test
    @DisplayName("""
             Test-DELETE-NotFound-Customer
          """)
    void testCustomerDelete_NotFound() throws IOException {
        RestClient.sendCustomerPost(testCustomer);
        testCustomerId = UUID.fromString("ed4af1d8-4a62-41fc-8189-dd5cda445ef7");

        Response response = RestClient.sendCustomerDelete(testCustomerId.toString());
        assert(response.getStatus() == 404);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
             Test-DELETE-AuthFail-Customer
          """)
    void testCustomerDelete_AuthFail() throws IOException, SQLException {
        RestClient.sendCustomerPost(testCustomer);

        Response response = RestClient.sendCustomerDelete(testCustomerId.toString(),"Wrong","Token");
        assert(response.getStatus() == 401);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
             Test-PUT-Customer
          """)
    void testCustomerUpdate() throws IOException, JSONException {
        RestClient.sendCustomerPost(testCustomer);

        testCustomer.setFirstName("UpdatedText");
        Response response = RestClient.sendCustomerPut(testCustomer);

        Response testResponse = RestClient.sendCustomerGet(testCustomerId.toString());
        Customer resultCustomer = objectMapper.readValue(testResponse.readEntity(String.class), CustomerJSON.class).toCustomer();
        assertEquals(testCustomer, resultCustomer);

        assert(response.getStatus() == 200);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
             Test-PUT-Fail-Customer
          """)
    void testCustomerUpdate_Fail() throws IOException, SQLException {
        Customer emptyCustomer = new Customer();
        logger.info("Customer object is empty");
        Response response = RestClient.sendCustomerPut(emptyCustomer);

        assert(response.getStatus() == 400);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
             Test-PUT-NotFound-Customer
          """)
    void testCustomerUpdate_NotFound() throws IOException, JSONException {
        RestClient.sendCustomerPost(testCustomer);

        testCustomer.setId(UUID.fromString("ed4af1d8-4a62-41fc-8189-dd5cda445ef7"));
        logger.info("id is changed");
        Response response = RestClient.sendCustomerPut(testCustomer);

        assert(response.getStatus() == 404);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
             Test-PUT-AuthFail-Customer
          """)
    void testCustomerUpdate_AuthFail() throws IOException, SQLException, JSONException {
        RestClient.sendCustomerPost(testCustomer);

        testCustomer.setFirstName("UpdatedText");
        Response response = RestClient.sendCustomerPut(testCustomer, "Wrong","Token");

        assert(response.getStatus() == 401);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
            TestGetAllCustomers""")
    void testGetAllCustomers() throws JsonProcessingException {

        Response response = RestClient.sendAllCustomersGet();
        assert(response.getStatus() == 200);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("""
            TestGetAllCustomers-AuthFail""")
    void testGetAllCustomers_AuthFail() throws JsonProcessingException {
        Response response = RestClient.sendAllCustomersGet("Wrong", "Token");
        assert(response.getStatus() == 401);
        logger.info("Finished with response code {}", response.getStatus());
    }

    @Test
    @DisplayName("TestReadingsSetCustomerToNullAndAddToList")
    void testReadingsSetCustomerToNullAndAddToList() {
        // Erstelle ein tatsächliches Reading-Objekt
        Reading reading = new Reading();

        // Setze den Customer auf null und setze Werte für die Felder
        reading.setCustomer(null);
        reading.setMeterCount(100.0);  // Setze den meterCount, um NullPointerException zu vermeiden
        reading.setSubstitute(false);  // Setze den substitute auf false, um NullPointerException zu verhindern

        // Konvertiere das Reading in ein ReadingJSON
        ReadingJSON readingJSON = reading.toReadingJSON();  // Füge das ReadingJSON hinzu

        // Sicherstellen, dass der Customer des Readings tatsächlich null ist
        assertNull(reading.getCustomer(), "Der Customer sollte null sein.");

        // Sicherstellen, dass das ReadingJSON nicht null ist und den erwarteten Wert enthält
        assertNotNull(readingJSON, "Das ReadingJSON sollte nicht null sein.");
        assertEquals(100.0, readingJSON.getMeterCount(), "Der meterCount im ReadingJSON sollte 100.0 sein.");
        assertFalse(readingJSON.toReading().getSubstitute(), "Das substitute im ReadingJSON sollte false sein.");
    }
}
