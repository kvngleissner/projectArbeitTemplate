package projekt.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import projekt.database.DatabaseService;
import projekt.model.JsonMapping.ReadingJSON;
import projekt.model.Reading;
import projekt.util.TokenService;
import projekt.util.UUIDMatcher;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;


@Path("readings")
public class ReadingResource {
    // Logger-Instanz
    private static final Logger logger = LogManager.getLogger(ReadingResource.class);


    // GET /reading/{readingId} - Rückgabe von Reading-Daten
    @GET
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadingData(@PathParam("uuid") String readingId, @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) throws JsonProcessingException {
        Response response = TokenService.verifyAuthToken(authHeader);
        if (response != null) {
            return response;
        }
        if (readingId.isEmpty() || !UUIDMatcher.isValid(readingId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"Invalid Customer ID\"}").build();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        logger.info("Attempting to get reading  with ID {}", readingId);
        UUID id = UUID.fromString(readingId); // Parsing der UUID
        Reading reading = DatabaseService.getInstance().readReadingData(id);
        if (reading == null) {
            logger.warn("Reading with ID {} not found", readingId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Reading not found").build();
        } else {
            ReadingJSON readingJSON = reading.toReadingJSON();
            logger.info("Reading with ID {} found", readingId);
            return Response.ok(objectMapper.writeValueAsString(readingJSON)).build(); // JSON-Antwort
        }


    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertReading(String readingJSONString, @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        Response response = TokenService.verifyAuthToken(authHeader);
        if (response != null) {
            return response;
        }
        try {
            logger.info("Attempting to read reading JSON file");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Reading reading = objectMapper.readValue(readingJSONString, ReadingJSON.class).toReading();

            //TODO evtl entfernen, da UUID in column id nie null sein darf
            if (reading.getId() == null || reading.getId().toString().isEmpty()) {
                logger.info("No ID provided, generating new UUID");
                reading.setId(UUID.randomUUID());
                logger.info("UUID generated: {}", reading.getId());
                DatabaseService.getInstance().insertReading(reading);
                logger.info("Reading JSON inserted successfully");
                return Response.status(Response.Status.CREATED)
                        .entity("{\"created UUID\": \"" + reading.getId() + "\"}").build();
            } else {
                if (!DatabaseService.getInstance().checkIfReadingExists(reading.getId()) && !DatabaseService.getInstance().checkIfCustomerExists(reading.getCustomer().getId())) {
                    DatabaseService.getInstance().insertReading(reading);
                    return Response.status(Response.Status.CREATED).build();
                } else {
                    logger.info("Reading already exists in the database!");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"Reading already exists in the database!\"}")
                            .build();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to read JSON file: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Failed to read JSON file\"}")
                    .build();
        }
    }

    @DELETE
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReading(@PathParam("uuid") String readingId, @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) throws JsonProcessingException {
        Response response = TokenService.verifyAuthToken(authHeader);
        if (response != null) {
            return response;
        }
        logger.info("Attempting to delete reading with ID {}", readingId);
        if (readingId.isEmpty() || !UUIDMatcher.isValid(readingId)) {
            logger.info("Invalid reading ID {} ", readingId);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"Invalid Customer ID\"}").build();

        }
        UUID id = UUID.fromString(readingId);
        if (DatabaseService.getInstance().checkIfReadingExists(id)) {
            logger.info("Reading with ID {} found", readingId);
            Reading reading = DatabaseService.getInstance().readReadingData(id);
            ReadingJSON readingJSON = reading.toReadingJSON();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            DatabaseService.getInstance().deleteReading(id);
            return Response.status(Response.Status.OK).entity(objectMapper.writeValueAsString(readingJSON)).build();
        } else {
            logger.warn("Reading with ID {} not found", readingId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Reading not found").build();
        }

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateReading(String readingJSONString, @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) throws JsonProcessingException {
        Response response = TokenService.verifyAuthToken(authHeader);
        if (response != null) {
            return response;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        logger.info("Attempting to read reading JSON file");
        Reading reading = objectMapper.readValue(readingJSONString, ReadingJSON.class).toReading();
        if (reading.getId() == null || reading.getId().toString().isEmpty()) {
            logger.info("Reading Object has no ID");
            ;
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"Invalid Customer object\"}: Reading Object has no ID").build();
        } else {
            if (DatabaseService.getInstance().checkIfReadingExists(reading.getId())) {
                logger.info("Reading with ID {} found", reading.getId());
                DatabaseService.getInstance().updateReading(reading);
                return Response.status(Response.Status.OK).build();
            } else {
                logger.warn("Reading with ID {} not found", reading.getId());
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Reading not found").build();
            }
        }
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadingsFiltered(
            @QueryParam("customer") String customer,
            @QueryParam("start") String start,
            @QueryParam("end") String end,
            @QueryParam("kindOfMeter") String kindOfMeter,
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader
    ) throws JsonProcessingException, SQLException {
        Response response = TokenService.verifyAuthToken(authHeader);
        if (response != null) {
            return response;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        if (customer == null || customer.isEmpty() || !UUIDMatcher.isValid(customer)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"A valid Customer ID is required\"}")
                    .build();
        }

        // check if date is valid format yyyy-mm-dd
        String regex = "^([0-9]{2})[0-9]{2}(-)(1[0-2]|0[1-9])\\2(3[01]|[12][0-9]|0[1-9])$";

        if (start != null && !Pattern.matches(regex, start)) {
            logger.info("Malformed start date, expected format yyyy-MM-dd");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"date format is malformed, expected yyyy-MM-dd\"}")
                    .build();
        } else if (end != null && !Pattern.matches(regex, end)) {
            logger.info("Malformed end date, expected format yyyy-MM-dd");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"date format is malformed, expected yyyy-MM-dd\"}")
                    .build();
        }
        LocalDate startDate = start != null ? LocalDate.parse(start) : null;
        LocalDate endDate = end != null ? LocalDate.parse(end) : null;

        List<Reading> readings = DatabaseService.getInstance().getFilteredReadings(UUID.fromString(customer), startDate, endDate, kindOfMeter);
        List<ReadingJSON> readingJSONList = new ArrayList<>();
        for (Reading reading : readings) {
            readingJSONList.add(reading.toReadingJSON());
        }
        if (readingJSONList.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No readings found\"}")
                    .build();
        }
        return Response.status(Response.Status.OK).entity(objectMapper.writeValueAsString(readingJSONList)).build();
    }
}