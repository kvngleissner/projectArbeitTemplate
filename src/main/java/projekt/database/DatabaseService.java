package projekt.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import projekt.Interfaces.ICustomer;
import projekt.Interfaces.IDatabaseConnection;
import projekt.Interfaces.IReading.KindOfMeter;
import projekt.model.Customer;
import projekt.model.Reading;
import projekt.rest.Server;

public class DatabaseService implements IDatabaseConnection {
  //@TODO in Singleton umbauen, Multipler DB-Aufruf
  private static DatabaseService instance;
  static Connection connection = null;
  private static final Logger logger = LogManager.getLogger(DatabaseService.class);
  // opens a connection to the Database and throws an exception, if the connection fails
  public IDatabaseConnection openConnection(){
    try {
      logger.info("Attempting to open database connection");
      Properties properties = new Properties();
      try (FileInputStream input = new FileInputStream("src/main/resources/System.properties")) {
        properties.load(input);
      } catch (FileNotFoundException e) {
        logger.error("System properties file not found\n Error: {}", e.getMessage());
      } catch (IOException e) {
        logger.error("Error reading system properties file\n Error: {}", e.getMessage());
      }
      // gets the necessary properties to login into the Database
      String DB_URL = properties.getProperty(".db.url");
      String DB_USER = properties.getProperty(".db.user");
      String DB_PW = properties.getProperty(".db.pw");
      logger.info("Connection established successfully");
      connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
      return this;
    }
    catch (SQLException e) {
      logger.error("Error opening database connection\n Error: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private DatabaseService() {
    openConnection();
  }

  public static synchronized DatabaseService getInstance() {
    if (instance == null) {
      instance = new DatabaseService();
    }
    return instance;
  }

  public static Connection getConnection() {
    return connection;
  }


  // opens a connection to the database and creates the reading table with the necessary SQL-Parameters
  public void createAllTables() {
    logger.info("Attempting to create tables");
    ;
    try  {
      String createCustomerTable =
          """ 
              CREATE TABLE IF NOT EXISTS customer (
              id UUID PRIMARY KEY,
              gender Enum('D', 'M', 'U', 'W') ,
              lastName VARCHAR(255),
              firstName VARCHAR(255),
              birthDate DATE
              );
              """;

      String createReadingTable =
          """ 
              CREATE TABLE IF NOT EXISTS reading (
              id UUID PRIMARY KEY,
              customerId UUID,
              kindOfMeter ENUM('WATER', 'POWER', 'HEAT', 'UNKNOWN') NOT NULL,
              meterId CHAR(36) NOT NULL,
              meterCount DOUBLE NOT NULL,
              substitute BOOL NOT NULL,
              dateofReading DATE NOT NULL,
              comment VARCHAR(255),
              FOREIGN KEY (customerId) REFERENCES customer(id) ON DELETE SET NULL
              );
              """;

      String createUserTable =
              """
                 CREATE TABLE IF NOT EXISTS users (
                 id UUID PRIMARY KEY,
                 username VARCHAR(255) NOT NULL,
                 password VARCHAR(255) NOT NULL
                 );
                 """;
// ON DELETE SET NULL -> Wenn Customer gelöscht wird -> Customer_ID in reading auf Null
      PreparedStatement stmtReading = connection.prepareStatement(createCustomerTable);
        stmtReading.executeUpdate();
      logger.info("Successfully created a customer table");
      PreparedStatement stmtCustomer = connection.prepareStatement(createReadingTable);
        stmtCustomer.executeUpdate();
      logger.info("Successfully created a reading table");
        PreparedStatement stmtUserTable = connection.prepareStatement(createUserTable);
        stmtUserTable.executeUpdate();
      logger.info("Successfully created a user table");



    } catch (SQLException e) {
      logger.error("Error executing SQL queries\n Error: {}", e.getMessage());
    }
  }

  // opens a connection to the database and truncates the tables "reading " and "customer"
  public void truncateAllTables() {
    String disableFKChecks = "SET FOREIGN_KEY_CHECKS = 0;";
    String enableFKChecks = "SET FOREIGN_KEY_CHECKS = 1;";
    String truncateReading = "TRUNCATE TABLE reading;";
    String truncateCustomer = "TRUNCATE TABLE customer;";
    String truncateUserTable = "TRUNCATE TABLE users;";
    ;
    try  {
      Statement statement = connection.createStatement();
        // Disable foreign key checks
        statement.execute(disableFKChecks);

        // Truncate tables
        statement.execute(truncateReading);
        statement.execute(truncateCustomer);
       // statement.execute(truncateUserTable);

        // Enable foreign key checks
        statement.execute(enableFKChecks);

        logger.info("Tables truncated successfully");
    } catch (SQLException e) {
      logger.error("Error truncating tables\n Error: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }
  // opens a connection to the database and drops the tables "reading " and "customer"
  public void removeAllTables() {
    logger.info("Attempting to remove tables");
    String dropReading =
        """ 
            DROP TABLE IF EXISTS reading;
            """;
    String dropCustomer =
        """ 
            DROP TABLE IF EXISTS customer;
            """;

    String dropUserTable =
            """ 
                DROP TABLE IF EXISTS users;
                """;
    ;
    try  {
      PreparedStatement stmtReading = connection.prepareStatement(dropReading);
        stmtReading.executeUpdate();

      PreparedStatement stmtCustomer = connection.prepareStatement(dropCustomer);
        stmtCustomer.executeUpdate();

        PreparedStatement stmtUserTable = connection.prepareStatement(dropUserTable);
        stmtUserTable.executeUpdate();

      logger.info("Tables removed successfully");
    } catch (SQLException e) {
      logger.error("Error removing tables\n Error: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  // CRUD-Part
  // FRAGE an Lehrkraft: eigene Methoden für bestimmte Tabellen in Ordnung oder flexibel?
  public void insertCustomer(ICustomer customer) {
    logger.info("Attempting to insert customer with ID: {} into database", customer.getId());
    ;
    try  {
      // SQL Query
      String statement =
          """ 
              INSERT INTO customer (
              id, firstname, lastname, birthdate, gender)
              VALUES (?, ?, ?, ?, ?);
              """;
      // PreparedStatement Erklärung dokumentieren!!
      // notwendig, da außerhalb der Query Werte gesetzt werden
      PreparedStatement preparedStatement = connection.prepareStatement(statement);
      preparedStatement.setString(1, String.valueOf(customer.getId()));
      preparedStatement.setString(2, customer.getFirstName());
      preparedStatement.setString(3, customer.getLastName());
        preparedStatement.setDate(4, customer.getBirthDate() != null ? Date.valueOf(customer.getBirthDate()) : null);
        preparedStatement.setString(5,customer.getGender() != null ? String.valueOf(customer.getGender()) : "U");

      preparedStatement.executeUpdate();
      logger.info("Customer inserted successfully");
    } catch (SQLException e) {
      logger.error("Error inserting customer into database\n Error: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }


  public void insertReading(Reading reading) {
    logger.info("Attempting to insert reading with ID: {} into database", reading.getId());
    ;
    try  {
      if (!checkIfCustomerExists(reading.getCustomer().getId())) {
        insertCustomer(reading.getCustomer());
      }
      // SQL Query
      String statement =
          """ 
              INSERT INTO reading (
              id, customerId, kindOfMeter, meterId, meterCount, substitute, dateOfReading, comment)
              VALUES (?, ?, ?, ?, ?, ?, ?, ?);
              """;
      // PreparedStatement Erklärung dokumentieren!!
      // notwendig, da außerhalb der Query Werte gesetzt werden
      PreparedStatement preparedStatement = connection.prepareStatement(statement);
      preparedStatement.setString(1, String.valueOf(reading.getId()));
      preparedStatement.setString(2, reading.getCustomer().getId().toString());
      preparedStatement.setString(3, reading.getKindOfMeter().toString());
      preparedStatement.setString(4, reading.getMeterId());
      preparedStatement.setDouble(5, reading.getMeterCount());
      preparedStatement.setBoolean(6, reading.getSubstitute());
      preparedStatement.setDate(7, Date.valueOf(reading.getDateOfReading()));
      preparedStatement.setString(8, reading.getComment());
      preparedStatement.executeUpdate();
      logger.info("Reading inserted successfully");
    } catch (SQLException e) {
      logger.error("Error inserting reading into database\n Error: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public void insertUser(String userName , String password){
    logger.info("Attempting to insert user with username: {} into database", userName);
    String sql = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, UUID.randomUUID().toString());
      preparedStatement.setString(2, userName);
      preparedStatement.setString(3, password);
      preparedStatement.executeUpdate();
      logger.info("User inserted successfully");
    } catch (SQLException e) {
      logger.error("Error inserting user into database\n Error: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }


  public Customer readCustomerData(UUID customerId) {
    logger.info("Attempting to read customer with ID: {} from database", customerId);
    String query =
        """ 
            SELECT birthDate, id, firstName, lastName, gender
            FROM customer
            WHERE id = ?;
            """;
    ;
    try  {
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, String.valueOf(customerId));
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        Customer resultCustomer = new Customer();
        if(resultSet.getString("birthDate") != null){
          resultCustomer.setBirthDate(resultSet.getDate("birthDate").toLocalDate());
        }
        resultCustomer.setId(UUID.fromString(resultSet.getString("id")));
        resultCustomer.setFirstName(resultSet.getString("firstName"));
        resultCustomer.setLastName(resultSet.getString("lastName"));
        if(resultSet.getString("gender") != null){
          resultCustomer.setGender(ICustomer.Gender.valueOf(resultSet.getString("gender")));
        }
        logger.info("Customer read successfully");
        return resultCustomer;
      }
    } catch (SQLException e) {

      logger.error("Error reading customer from database\n Error: {}", e.getMessage());
      throw new RuntimeException(e);
    }
    return null;
  }

  public Reading readReadingData(UUID readingId) {
    logger.info("Attempting to read reading with ID: {} from database", readingId);
    String query =
        """ 
            SELECT id, customerId, dateOfReading, comment, substitute, meterCount, kindOfMeter, meterId
            FROM reading
            WHERE id = ?;
            """;
    ;
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, String.valueOf(readingId));
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        Reading resultReading = new Reading();
        resultReading.setId(UUID.fromString(resultSet.getString("id")));
        if (resultSet.getString("customerId") != null) {
          resultReading.setCustomer(
              readCustomerData(UUID.fromString(resultSet.getString("customerId"))));
        } else {
          resultReading.setCustomer(new Customer());
        }
        resultReading.setDateOfReading(resultSet.getDate("dateOfReading").toLocalDate());
        resultReading.setComment(resultSet.getString("comment"));
        resultReading.setSubstitute(resultSet.getBoolean("substitute"));
        resultReading.setMeterCount(resultSet.getDouble("meterCount"));
        resultReading.setKindOfMeter(
            KindOfMeter.valueOf(resultSet.getString("kindOfMeter")));
        resultReading.setMeterId(resultSet.getString("meterId"));
        logger.info("Reading read successfully");
        return resultReading;
      }
    } catch (SQLException e) {
      logger.error("Error reading reading from database\n Error: {}", e.getMessage());
      throw new RuntimeException(e);
    }
    return null;
  }

  public List<Reading> readAllReadingsData() {
    logger.info("Attempting to read all readings from database");
    String query =
        """ 
            SELECT id, customerId, dateOfReading, comment, substitute, meterCount, kindOfMeter, meterId
            FROM reading;
            """;
    ;
    try  {
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      ResultSet resultSet = preparedStatement.executeQuery();
      List<Reading> resultReadings = new java.util.ArrayList<>();
      while (resultSet.next()) {
        Reading resultReading = new Reading();
        resultReading.setId(UUID.fromString(resultSet.getString("id")));
        resultReading.setCustomer(
            readCustomerData(UUID.fromString(resultSet.getString("customerId"))));
        resultReading.setDateOfReading(resultSet.getDate("dateOfReading").toLocalDate());
        resultReading.setComment(resultSet.getString("comment"));
        resultReading.setSubstitute(resultSet.getBoolean("substitute"));
        resultReading.setMeterCount(resultSet.getDouble("meterCount"));
        resultReading.setKindOfMeter(
            KindOfMeter.valueOf(resultSet.getString("kindOfMeter")));
        resultReading.setMeterId(resultSet.getString("meterId"));
        resultReadings.add(resultReading);
      }
      logger.info("All readings read successfully");
      return resultReadings;
    } catch (SQLException e) {
      logger.error("Error reading all readings from database\n Error: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public List<Customer> readAllCustomersData() {
    logger.info("Attempting to read all customers from database");
    String query =
        """ 
            SELECT birthDate, id, firstName, lastName, gender
            FROM customer;
            """;
    ;
    try  {
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      ResultSet resultSet = preparedStatement.executeQuery();
      List<Customer> resultCustomers = new java.util.ArrayList<>();
      while (resultSet.next()) {
        Customer resultCustomer = new Customer();
        resultCustomer.setBirthDate(resultSet.getDate("birthdate")==null?null:resultSet.getDate("birthDate").toLocalDate());
        resultCustomer.setId(resultSet.getString("id")==null?null:UUID.fromString(resultSet.getString("id")));
        resultCustomer.setFirstName(resultSet.getString("firstName"));
        resultCustomer.setLastName(resultSet.getString("lastName"));
        resultCustomer.setGender(resultSet.getString("gender")==null?null:ICustomer.Gender.valueOf(resultSet.getString("gender")));
        resultCustomers.add(resultCustomer);
      }
      logger.info("All customers read successfully");
      return resultCustomers;
    } catch (SQLException e) {
logger.error("Error reading all customers from database\n Error: {}", e.getMessage());
    throw new RuntimeException(e);
    }
  }

  public void updateReading(Reading reading) {
    logger.info("Attempting to update Reading with ID: {} in database", reading.getId());
    String query =
        """ 
            UPDATE reading
            SET customerId = ?, kindOfMeter = ?, meterId = ?, meterCount = ?,
            substitute = ?, dateOfReading = ?, comment = ?
            WHERE id = ?;
            """;
    ;
    try  {
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, String.valueOf(reading.getCustomer().getId()));
      preparedStatement.setString(2, String.valueOf(reading.getKindOfMeter()));
      preparedStatement.setString(3, reading.getMeterId());
      preparedStatement.setDouble(4, reading.getMeterCount());
      preparedStatement.setBoolean(5, reading.getSubstitute());
      preparedStatement.setDate(6, Date.valueOf(reading.getDateOfReading()));
      preparedStatement.setString(7, reading.getComment());
      preparedStatement.setString(8, String.valueOf(reading.getId()));
      preparedStatement.executeUpdate();
      logger.info("Reading updated successfully");
    } catch (SQLException e) {

logger.error("Error updating reading in database\n Error: {}", e.getMessage());

    throw new RuntimeException(e);
    }

  }

  public void updateCustomer(Customer customer) {
    logger.info("Attempting to update Customer with ID: {} in database", customer.getId());
    String query =
        """ 
            UPDATE customer
            SET gender = ?, lastName = ?, firstName = ?, birthDate = ?
            WHERE id = ?;
            """;
    ;
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, String.valueOf(customer.getGender()));
      preparedStatement.setString(2, customer.getLastName());
      preparedStatement.setString(3, customer.getFirstName());
      preparedStatement.setDate(4, Date.valueOf(customer.getBirthDate()));
      preparedStatement.setString(5, String.valueOf(customer.getId()));
      preparedStatement.executeUpdate();
      logger.info("Customer updated successfully");
    } catch (SQLException e) {
logger.error("Error updating customer in database\n Error: {}", e.getMessage());
    throw new RuntimeException(e);}

  }

  public void deleteReading(UUID readingId) {
    logger.info("Attempting to delete reading with ID: {} from database", readingId);
    String query =
        """ 
            DELETE FROM reading WHERE id = ?;
            """;
    ;
    try  {
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, String.valueOf(readingId));
      preparedStatement.executeUpdate();
      logger.info("Reading deleted successfully");
    } catch (SQLException e) {
    logger.error("Error deleting reading from database\n Error: {}", e.getMessage());}
  }

  public void deleteCustomer(UUID customerId) {
    logger.info("Attempting to delete customer with ID: {} from database", customerId);
    String query =
        """ 
            DELETE FROM customer WHERE id = ?;
            """;
    ;
    try  {
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, String.valueOf(customerId));
      preparedStatement.executeUpdate();
      logger.info("Customer deleted successfully");
    } catch (SQLException e) {

    logger.error("Error deleting customer from database\n Error: {}", e.getMessage());
    throw new RuntimeException(e);
    }
  }

  public void insertCustomerBatch(List<Customer> customers) {
    logger.info("Attempting to insert customer-batch into database");
    String statement =
        """ 
            INSERT INTO customer (id, firstname, lastname, birthdate, gender)
            VALUES (?, ?, ?, ?, ?);
            """;
    ;
    try  {
      PreparedStatement preparedStatement = connection.prepareStatement(statement);
      for (Customer customer : customers) {
        preparedStatement.setString(1, String.valueOf(customer.getId()));
        preparedStatement.setString(2, customer.getFirstName());
        preparedStatement.setString(3, customer.getLastName());
        if (customer.getBirthDate() != null) {
          preparedStatement.setDate(4, Date.valueOf(customer.getBirthDate()));
        }
        preparedStatement.setString(5, String.valueOf(customer.getGender()));
        preparedStatement.addBatch();
      }
      preparedStatement.executeBatch();
      logger.info("customer-batch inserted successfully");
    } catch (SQLException e) {
      logger.error("Error inserting customer-batch into database\n Error: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public void insertReadingBatch(List<Reading> readings) {
    logger.info("Attempting to insert reading-batch into database");
    String insertReadingStatement =
            """ 
            INSERT INTO reading (id, customerId, kindOfMeter, meterId, meterCount, substitute, dateOfReading, comment)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            """;
    String checkCustomerStatement = "SELECT 1 FROM customer WHERE id = ?";
    String insertCustomerStatement = "INSERT INTO customer (id) VALUES (?)";
    ;
    try  {
      PreparedStatement insertReadingPreparedStatement = connection.prepareStatement(insertReadingStatement);
      PreparedStatement checkCustomerPreparedStatement = connection.prepareStatement(checkCustomerStatement);
      PreparedStatement insertCustomerPreparedStatement = connection.prepareStatement(insertCustomerStatement);

      for (Reading reading : readings) {
        // Check if customerId exists
        checkCustomerPreparedStatement.setString(1, reading.getCustomer().getId().toString());
        ResultSet rs = checkCustomerPreparedStatement.executeQuery();
        if (!rs.next()) {
          // Customer does not exist, so insert the customer
          insertCustomerPreparedStatement.setString(1, reading.getCustomer().getId().toString());
          // Assuming there are other fields in the customer table, set them here as needed
          insertCustomerPreparedStatement.executeUpdate();
          logger.info("Inserted new customer with id {}", reading.getCustomer().getId());
        }

        insertReadingPreparedStatement.setString(1, String.valueOf(reading.getId()));
        insertReadingPreparedStatement.setString(2, reading.getCustomer().getId().toString());
        insertReadingPreparedStatement.setString(3, reading.getKindOfMeter().toString());
        insertReadingPreparedStatement.setString(4, reading.getMeterId());
        insertReadingPreparedStatement.setDouble(5, reading.getMeterCount());
        insertReadingPreparedStatement.setBoolean(6, reading.getSubstitute());
        insertReadingPreparedStatement.setDate(7, Date.valueOf(reading.getDateOfReading()));
        insertReadingPreparedStatement.setString(8, reading.getComment());
        insertReadingPreparedStatement.addBatch();
      }

      insertReadingPreparedStatement.executeBatch();
      logger.info("Reading-batch inserted successfully");
    } catch (SQLException e) {
      logger.error("Error inserting reading-batch into database\n Error: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }


  // UTILITY

  public boolean checkIfCustomerExists(UUID customerId) throws SQLException {
    logger.info("Attempting to check if customer with id: {} exists",customerId);
    String statement =
            """ 
                SELECT id FROM customer WHERE id = ?;
                """;
    ;
    try{
      PreparedStatement preparedStatement = connection.prepareStatement(statement);
      preparedStatement.setString(1, String.valueOf(customerId));
      ResultSet resultSet = preparedStatement.executeQuery();
      Boolean result = resultSet.next();
      logger.info("Customer exists: {}", result);
      return result;
    }
    catch (SQLException e) {
      logger.error("Failed to check if customer with id: {} exists\n Error: {}", customerId,e.getMessage()) ;
      throw new RuntimeException(e);
    }

  }

  public boolean checkIfReadingExists(UUID readingId)  {
    logger.info("Attempting to check if reading with id: {} exists",readingId);
    String statement =
            """   
                    SELECT id FROM reading WHERE id = ?;
                    """;
    try{
      PreparedStatement preparedStatement = connection.prepareStatement(statement);
      preparedStatement.setString(1, String.valueOf(readingId));
      ResultSet resultSet = preparedStatement.executeQuery();
      Boolean result = resultSet.next();
      logger.info("Reading exists: {}", result);
      return result;
    }
    catch (SQLException e) {
      logger.error("Failed to check if reading with id: {} exists\n Error: {}", readingId,e.getMessage()) ;
      throw new RuntimeException(e);
    }
  }
  public boolean checkIfTableExists(String tableName) {
    logger.info("Attempting to check if table {} exists", tableName);
    String statement = """
            SELECT EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = ? AND table_name = ?
            )
            """;

    try  {
      PreparedStatement preparedStatement = connection.prepareStatement(statement);
      preparedStatement.setString(1, connection.getCatalog());
      preparedStatement.setString(2, tableName);
      ResultSet resultSet = preparedStatement.executeQuery();

      if (resultSet.next()) {
        return resultSet.getBoolean(1);
      }
    } catch (SQLException e) {
      logger.error("Failed to check if table {} exists\n Error: {}", tableName, e.getMessage());
    }
    return false;
  }



  public List<Reading> getFilteredReadings(UUID customerId, LocalDate startDate, LocalDate endDate, String kindOfMeter) throws SQLException {
    List<Reading> filterResults = new java.util.ArrayList<>();
    logger.info("Attempting to retrieve filtered Readings with customerID: {}, startDate: {}, endDate: {}, kindOfMeter: {}", customerId, startDate, endDate, kindOfMeter);
    String baseQuery = "SELECT id, customerId, dateOfReading, comment, substitute, meterCount, kindOfMeter, meterId FROM reading WHERE customerId = ?";

    List<Object> parameters = new ArrayList<>();
    parameters.add(customerId);

    if (startDate != null) {
      baseQuery += " AND dateOfReading > ?";
      parameters.add(Date.valueOf(startDate));
    }
    if (endDate != null) {
      baseQuery += " AND dateOfReading < ?";
      parameters.add(Date.valueOf(endDate));
    }
    if (kindOfMeter != null) {
      baseQuery += " AND kindOfMeter = ?";
      parameters.add(kindOfMeter);
    }

    try {
      PreparedStatement preparedStatement = connection.prepareStatement(baseQuery);
      for (int i = 0; i < parameters.size(); i++) {
        preparedStatement.setString(i + 1, parameters.get(i).toString());
      }

      try {
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
          Reading resultReading = new Reading();
          resultReading.setId(UUID.fromString(resultSet.getString("id")));
          if (resultSet.getString("customerId") != null) {
            resultReading.setCustomer(readCustomerData(UUID.fromString(resultSet.getString("customerId"))));
          } else {
            resultReading.setCustomer(new Customer());
          }
          resultReading.setDateOfReading(resultSet.getDate("dateOfReading").toLocalDate());
          resultReading.setComment(resultSet.getString("comment"));
          resultReading.setSubstitute(resultSet.getBoolean("substitute"));
          resultReading.setMeterId(resultSet.getString("meterId"));
          resultReading.setMeterCount(resultSet.getDouble("meterCount"));
          resultReading.setKindOfMeter(KindOfMeter.valueOf(resultSet.getString("kindOfMeter")));
          filterResults.add(resultReading);
        }
      }catch (SQLException e){
        logger.error("SQL Error while retrieving filtered readings", e);
      }
    } catch (SQLException e) {
      logger.error("SQL Error while retrieving filtered readings", e);
    }
    return filterResults;
  }

  public Boolean authenticateUser(String userName, String password) {
    String sql = """
            SELECT id, username, password FROM users WHERE username = ? AND password = ?;""";
    try{
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, userName);
      preparedStatement.setString(2, password);
      ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
  }

}
