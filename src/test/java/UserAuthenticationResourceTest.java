import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import projekt.Interfaces.IReading;
import projekt.database.DatabaseService;
import projekt.util.CSVReader;
import projekt.util.RestClient;

import static projekt.rest.Server.restartServer;
import static projekt.rest.Server.startServer;
import static projekt.rest.Server.stopServer;

public class UserAuthenticationResourceTest {

    Logger logger = LogManager.getLogger(UserAuthenticationResourceTest.class);
    static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void bootServer () {
        restartServer("http://localhost:8056/rest");
        objectMapper.registerModule(new JavaTimeModule());

    }
    @AfterAll
    static void shutdownServer() {
        stopServer();
    }



    @Test
    @DisplayName("""
             Test-Unauthorized-User
          """)
    void testUnauthorizedUser() {
        String response = RestClient.sendUserAuthGet("Fake", "user");
        logger.info(response);
    }
}
