import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import projekt.Interfaces.ICustomer;
import projekt.Interfaces.IReading;
import projekt.Interfaces.IReading.KindOfMeter;
import projekt.database.DatabaseService;
import projekt.model.Customer;
import projekt.model.Reading;
import projekt.util.CSVReader;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseServiceTests {
  // generate the current date
  LocalDate localDate = LocalDate.now();
  // generate random uuID
  UUID customerId = UUID.randomUUID();
  // create a customer for test purposes
  Customer testCustomer = new Customer(customerId, LocalDate.now(), "Bob", "Baum", Customer.Gender.M);

  // Test Database Connection
  // Test Create Table
  // Test Drop Table

  @BeforeAll
  static void recreateTables() {
    DatabaseService.getInstance().removeAllTables();
    DatabaseService.getInstance().createAllTables();
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

  @Test
  @DisplayName("""
          TestTableHandling"""
  )
  void testTableHandling() {
    DatabaseService.getInstance().removeAllTables();
    assertFalse(DatabaseService.getInstance().checkIfTableExists("customer")&&DatabaseService.getInstance().checkIfTableExists("reading"));
    DatabaseService.getInstance().createAllTables();
    assertTrue(DatabaseService.getInstance().checkIfTableExists("customer")&&DatabaseService.getInstance().checkIfTableExists("reading"));

  }


  @BeforeEach
  void truncateTables() {
    DatabaseService.getInstance().truncateAllTables();
  }

  @Test
  @DisplayName("""
          Test DB-Connection
          """)
  void testOpenConnection() {

    // Versuche, die Verbindung zu öffnen
    try {
      Connection connection = DatabaseService.getInstance().getConnection();
      assertNotNull(connection, "Connection should not be null");
      assertFalse(connection.isClosed(), "Connection should be open");

      // Optional: Überprüfen, ob die Verbindung Eigenschaften aufweist
      assertTrue(connection.isValid(2), "Connection should be valid");


    } catch (SQLException e) {
      fail("SQLException was thrown: " + e.getMessage());
    }
  }

  /* Customer Tests */
  @Test
  @DisplayName("""
             Test-Insert
          """)
  void testInsert() {
    // generate random uuID
    UUID uuid = UUID.randomUUID();
    LocalDate localDate = LocalDate.now();
    Customer customer = new Customer(uuid, localDate, "Bob", "Baum", Customer.Gender.M);
    DatabaseService.getInstance().insertCustomer(customer);
    DatabaseService.getInstance().insertUser("Test","password");
    assertEquals(DatabaseService.getInstance().readCustomerData(uuid), customer);

  }


  @Test
  @DisplayName("""
          Test-Update-Customer
          """)
  void testUpdateCustomer() {
    DatabaseService.getInstance().insertCustomer(testCustomer);
    assertEquals(DatabaseService.getInstance().readCustomerData(customerId), testCustomer);
    testCustomer.setFirstName("Test");
    DatabaseService.getInstance().updateCustomer(testCustomer);
    assertEquals(DatabaseService.getInstance().readCustomerData(customerId), testCustomer);
  }

  @Test
  @DisplayName("""
          Test-Truncate""")
  void testTruncate() {
    DatabaseService.getInstance().insertCustomer(testCustomer);
    assertEquals(DatabaseService.getInstance().readCustomerData(customerId), testCustomer);
    DatabaseService.getInstance().truncateAllTables();
    assert (DatabaseService.getInstance().readAllCustomersData().isEmpty() && DatabaseService.getInstance().readAllReadingsData().isEmpty());
  }

  /* Reading Tests */
  @Test
  @DisplayName("""
             Test-Insert
          """)
  void testInsertReading() {
    UUID uuid = UUID.randomUUID();
    DatabaseService.getInstance().insertCustomer(testCustomer);
    assertEquals(DatabaseService.getInstance().readCustomerData(customerId), testCustomer);

    Reading reading = new Reading(uuid, testCustomer, IReading.KindOfMeter.WATER, "M3A411",
            30.1, false, localDate, "repairs needed");
    DatabaseService.getInstance().insertReading(reading);
    assertEquals(reading, DatabaseService.getInstance().readReadingData(uuid), "both Strings are equal");
  }

  @Test
  @DisplayName("""
          Test-Update-Reading
          """)
  void testUpdateReading() {
    UUID readingId = UUID.randomUUID();
    DatabaseService.getInstance().insertCustomer(testCustomer);
    assertEquals(DatabaseService.getInstance().readCustomerData(customerId), testCustomer);

    Reading reading = new Reading(readingId, testCustomer, IReading.KindOfMeter.WATER, "M3A411",
            30.1, false, localDate, "repairs needed");
    DatabaseService.getInstance().insertReading(reading);
    assertEquals(reading, DatabaseService.getInstance().readReadingData(readingId), "both Strings are equal");

    reading.setMeterCount(99.9);
    DatabaseService.getInstance().updateReading(reading);
    assertEquals(DatabaseService.getInstance().readReadingData(readingId), reading, "both doubles are equal");
  }

  @Test
  @DisplayName("""
          Test-Customer-Delete-/ Auto-insert
          """)
  void testCustomerHandling() throws SQLException {
    UUID uuid = UUID.randomUUID();

    Reading reading = new Reading(uuid, testCustomer, KindOfMeter.POWER, "meterid", 25.5, false, localDate, "");
    DatabaseService.getInstance().insertReading(reading);
    assertTrue(DatabaseService.getInstance().checkIfCustomerExists(customerId));
    DatabaseService.getInstance().deleteCustomer(customerId);
    assertFalse(DatabaseService.getInstance().checkIfCustomerExists(customerId));
    assertNull(DatabaseService.getInstance().readReadingData(uuid).getCustomer().getId());
  }

  @Test
  @DisplayName("""
    Test-Customer-Delete-/ Auto-insert
    """)
  void testDeleteReading(){
    UUID readingId = UUID.randomUUID();

    Reading reading = new Reading(readingId, testCustomer, KindOfMeter.POWER,"meterid",25.5,false,localDate,"");
    DatabaseService.getInstance().insertReading(reading);
    assertTrue(DatabaseService.getInstance().checkIfReadingExists(readingId));
    DatabaseService.getInstance().deleteReading(readingId);
    assertFalse(DatabaseService.getInstance().checkIfReadingExists(readingId));
  }

  @Test
  @DisplayName("""
          Test-CSV-Import(Customer)
          """)
  void testImportCustomerList() {
    CSVReader csvReader = new CSVReader();
    Customer testCustomer = new Customer(UUID.fromString("092958db-b395-4865-9030-a22abdb17b8e"),
        null, "Marieluise","Augustin", ICustomer.Gender.W);
    assertEquals(csvReader.readCustomerCSV("src/main/resources/kunden_utf8.csv").get(5),testCustomer);
  }

  @Test
  @DisplayName(
          """
          Test-CSV-Import(Reading)
          """
  )
  void testImportReadingList() {
    CSVReader csvReader = new CSVReader();
    Customer customer = new Customer();
    customer.setId(UUID.fromString("ec617965-88b4-4721-8158-ee36c38e4db3"));
    Reading testReading = new Reading(UUID.fromString("e2bd70b0-94a2-41f9-80b1-6bbd2f284c27"),customer,KindOfMeter.POWER,"MST-af34569",18679.0,false,LocalDate.of(2018,11,1),"");
    assertEquals(csvReader.readReadingCSV("src/main/resources/strom.csv",KindOfMeter.POWER).get(5),testReading);
  }

  @Test
  @DisplayName("""
          Test-Insert-Batch(Customer)
          """)
  void testInsertBatchCustomer() {
    CSVReader csvReader = new CSVReader();
    DatabaseService.getInstance().insertCustomerBatch(csvReader.readCustomerCSV("src/main/resources/kunden_utf8.csv"));
    Customer testCustomer = new Customer(UUID.fromString("092958db-b395-4865-9030-a22abdb17b8e"),
        LocalDate.of(1968,9,12), "Marieluise","Augustin", ICustomer.Gender.W);
    assertEquals(DatabaseService.getInstance().readCustomerData(UUID.fromString("092958db-b395-4865-9030-a22abdb17b8e")),testCustomer);
  }

  @Test
  @DisplayName("""
          Test-Insert-Batch(Reading)
          """)
  void testInsertBatchReading() {
    CSVReader csvReader = new CSVReader();
    List<Reading> readingList = csvReader.readReadingCSV("src/main/resources/strom.csv",KindOfMeter.POWER);
    readingList.get(5).setId(UUID.fromString("e2bd70b0-94a2-41f9-80b1-6bbd2f284c27"));
    DatabaseService.getInstance().insertReadingBatch(readingList);
    Customer customer = new Customer();
    customer.setId(UUID.fromString("ec617965-88b4-4721-8158-ee36c38e4db3"));
    Reading testReading = new Reading(UUID.fromString("e2bd70b0-94a2-41f9-80b1-6bbd2f284c27"),customer,KindOfMeter.POWER,"MST-af34569",18679.0,false,LocalDate.of(2018,11,1),"");
    assertEquals(DatabaseService.getInstance().readReadingData(UUID.fromString("e2bd70b0-94a2-41f9-80b1-6bbd2f284c27")),testReading);
  }

  @Test
  @DisplayName("""
          Test-ReadAll(Reading)
          """)
  void testReadAllReadings() {
    CSVReader csvReader = new CSVReader();
    List<Reading> readingList = csvReader.readReadingCSV("src/main/resources/strom.csv",KindOfMeter.POWER);
    DatabaseService.getInstance().insertReadingBatch(readingList);
    assertEquals(DatabaseService.getInstance().readAllReadingsData().size(),readingList.size());
  }

  @Test
  @DisplayName("""
          Test-ReadAll(Customer)
          """)
  void testReadAllCustomers() {
    CSVReader csvReader = new CSVReader();
    List<Customer> customerList = csvReader.readCustomerCSV("src/main/resources/kunden_utf8.csv");
    DatabaseService.getInstance().insertCustomerBatch(customerList);
    assertEquals(DatabaseService.getInstance().readAllCustomersData().size(),customerList.size());
  }


  @Test
  @DisplayName("""
          Test-ExceptionWhenInsertNoTable
          """)
  void testExceptionWhenInsertNoTable() {
    DatabaseService.getInstance().removeAllTables();
     assertThrows(RuntimeException.class, () -> DatabaseService.getInstance().insertCustomer(testCustomer));
     DatabaseService.getInstance().createAllTables();
  }

  @Test
  @DisplayName("""
          Test-ExceptionWhenInsertCustomerNoID""")
  void testExceptionWhenInsertCustomerNoID() {
    Customer customer = new Customer();
    assertThrows(RuntimeException.class, () -> DatabaseService.getInstance().insertCustomer(customer));
  }

  @Test
  @DisplayName("""
          Test-ExceptionWhenCheckIfExistsInvalidUUID""")
  void testExceptionWhenCheckIfExistsInvalidUUID() {
    assertThrows(IllegalArgumentException.class, () ->DatabaseService.getInstance().checkIfCustomerExists(UUID.fromString("DiesIstKeineUUID")));
    assertThrows(IllegalArgumentException.class, () ->DatabaseService.getInstance().checkIfReadingExists(UUID.fromString("DiesIstKeineUUID")));

  }

}
// TEST