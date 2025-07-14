package projekt.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import projekt.database.DatabaseService;
import projekt.model.Customer;
import projekt.model.JsonMapping.CustomerJSON;
import projekt.model.JsonMapping.CustomerWithReadingsJSON;
import projekt.model.JsonMapping.ReadingJSON;
import projekt.model.Reading;
import projekt.util.TokenService;
import projekt.util.UUIDMatcher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("customers")
public class CustomerResource {

    Logger logger = LogManager.getLogger(CustomerResource.class);

    /**
     * Retrieves a customer by their unique identifier (UUID) and returns the
     * customer information as a JSON response.
     *
     * @param id The unique identifier (UUID) of the customer to be retrieved.
     * @return A Response object containing the customer information in JSON format
     * if the operation is successful. If an error occurs, a Response with
     * an error message and an INTERNAL_SERVER_ERROR status is returned.
     */
    @GET
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerByID(@PathParam("uuid") String id, @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) throws SQLException, JsonProcessingException {
        Response response = TokenService.verifyAuthToken(authHeader);
        if (response != null) {
            return response;
        }
        if (id.isEmpty() || !UUIDMatcher.isValid(id)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"Invalid Customer ID\"}").build();
        }
        if (!DatabaseService.getInstance().checkIfCustomerExists(UUID.fromString(id))) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"Customer not found\"}").build();
        }

            CustomerJSON customer = DatabaseService.getInstance().readCustomerData(
                            UUID.fromString(id))
                    .toCustomerJSON();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            return Response.ok(objectMapper.writeValueAsString(customer)).build();

        }



    /**
     * REST endpoint for /rest/customers (returns all customers as JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers( @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) throws JsonProcessingException {
        Response response = TokenService.verifyAuthToken(authHeader);
        if (response != null) {
            return response;
        }
        List<CustomerJSON> allCustomers = new ArrayList<>();
        for (Customer customer : DatabaseService.getInstance().readAllCustomersData()) {
            allCustomers.add(customer.toCustomerJSON());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return Response.status(Response.Status.OK).entity(objectMapper.writeValueAsString(allCustomers)).build();
    }


    /**
     * Retrieves customer data from a JSON source and inserts a new
     * customer into the database if the id is null or does not already exist.
     *
     * @return A new customer entry
     * if the operation is successful. If an error occurs, a Response with
     * an error message and an INTERNAL_SERVER_ERROR status is returned.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertCustomer(String customerJsonString, @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        Response response = TokenService.verifyAuthToken(authHeader);
        if (response != null) {
            return response;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            CustomerJSON customerJSON = objectMapper.readValue(customerJsonString, CustomerJSON.class);
            Customer customer = customerJSON.toCustomer();
            logger.info("Customer object converted from JSON");

            if (customer.getId() == null || customer.getId().toString().isEmpty()) {
                logger.info("Customer ID is null or empty");
                customer.setId(UUID.randomUUID());
                logger.info("Customer ID generated");
                DatabaseService.getInstance().insertCustomer(customer);
                logger.info("New Customer inserted");
                return Response.status(Response.Status.CREATED).entity("{\"created UUID\": \"" + customer.getId() + "\"}").build();

            } else {
                if (!DatabaseService.getInstance().checkIfCustomerExists(customer.getId())) {
                    logger.info("Customer ID does not already exist");
                    DatabaseService.getInstance().insertCustomer(customer);
                    logger.info("New Customer inserted");
                    return Response.status(Response.Status.CREATED).build();
                } else {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"Customer already exists\"}")
                            .build();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Failed to read JSON file\"}")
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateCustomer(String customerJsonString, @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        Response response = TokenService.verifyAuthToken(authHeader);
        if (response != null) {
            return response;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            CustomerJSON customerJSON = objectMapper.readValue(customerJsonString, CustomerJSON.class);
            Customer customer = customerJSON.toCustomer();

            logger.info("Customer object converted from JSON");

            if (customer.getId() == null || customer.getId().toString().isEmpty()) {
                logger.info("Customer Object has no ID");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"Invalid Customer object\"}").build();
            } else {
                if (DatabaseService.getInstance().checkIfCustomerExists(customer.getId())) {
                    logger.info("Customer found");
                    DatabaseService.getInstance().updateCustomer(customer);
                    logger.info("Customer updated");
                    return Response.status(Response.Status.OK).build();
                } else {
                    logger.warn("Customer with ID {} not found", customer.getId());
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Customer not found").build();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Failed to read JSON file\"}")
                    .build();
        }
    }

    @DELETE
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("uuid") String customerId, @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) throws SQLException, JsonProcessingException {
        Response response = TokenService.verifyAuthToken(authHeader);
        if (response != null) {
            return response;
        }
        logger.info("Attempting to delete customer with ID {}", customerId);
        if (customerId.isEmpty() || !UUIDMatcher.isValid(customerId)) {
            logger.info("Invalid Customer ID: {} ", customerId);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"Invalid Customer ID\"}").build();
        }
            UUID id = UUID.fromString(customerId);

            if (DatabaseService.getInstance().checkIfCustomerExists(id)) {
                logger.info("Customer with ID {} found", customerId);
                Customer customer = DatabaseService.getInstance().readCustomerData(id);

                // List of readings
                logger.info("Creating List of readings");
                List<Reading> readings = DatabaseService.getInstance().getFilteredReadings(id, null, null, null);
                // Customer in Readings auf null
                List<ReadingJSON> readingJSONList = new ArrayList<>();
                for (Reading reading : readings) {
                    reading.setCustomer(null);
                    readingJSONList.add(reading.toReadingJSON());
                }

                CustomerJSON customerJSON = customer.toCustomerJSON();
                CustomerWithReadingsJSON customerWithReadingsJSON = new CustomerWithReadingsJSON();
                customerWithReadingsJSON.setCustomer(customerJSON);
                customerWithReadingsJSON.setReadings(readingJSONList);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                // Customer löschen
                logger.info("Trying to delete customer");
                DatabaseService.getInstance().deleteCustomer(id);

                //Customer Object und Readings ausgeben
                return Response.status(Response.Status.OK).entity(objectMapper.writeValueAsString(customerWithReadingsJSON)).build();
            } else {
                logger.warn("Customer with ID {} not found", customerId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Customer not found").build();
            }
    }
}