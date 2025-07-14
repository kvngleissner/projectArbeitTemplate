import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import projekt.Interfaces.IReading;
import projekt.database.DatabaseService;
import projekt.rest.resources.SetUpDbResource;
import projekt.util.CSVReader;
import projekt.util.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static projekt.rest.Server.restartServer;
import static projekt.rest.Server.startServer;
import static projekt.rest.Server.stopServer;

public class SetUpDbResourceTest {

    private static final Logger logger = LogManager.getLogger(SetUpDbResourceTest.class);
    static ObjectMapper objectMapper = new ObjectMapper();


    @BeforeAll
    static void setUp() {// Beispiel: Server starten (dies hängt von deinem Setup ab)
        restartServer("http://localhost:8056/rest"); // Stelle sicher, dass du den Server in dieser Methode startest
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
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



    @Test
    @DisplayName("Test DELETE /setupDB returns HTTP 200")
    void testSetUpDb() {
        Response response = RestClient.sendSetupDB();
        assertEquals(200, response.getStatus(), "Expected HTTP status 200 OK");
        logger.info("Finished with response code {}", response.getStatus());
        response.close();
    }

    @Test
    @DisplayName("Test DELETE /setupDB failed validation")
    void testSetUpDb_AuthFail() {
        Response response = RestClient.sendSetupDB("Wrong", "Token");
        assert(response.getStatus() == 401);
        logger.info("Finished with response code {}", response.getStatus());
        response.close();
    }
}
