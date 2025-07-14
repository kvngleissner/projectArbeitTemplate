package projekt.util;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import projekt.Interfaces.IReading.KindOfMeter;
import projekt.model.Customer;
import projekt.model.Reading;

public class CSVReader {
  // Parse all Customers of an import csv to Customer Objects (return as List<Customer>;
  public List<Customer> readCustomerCSV (String filePath) {
    try {
      com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new FileReader(filePath));
      List<String[]> csvRows = reader.readAll();
      csvRows.remove(0);
      List<Customer> customers = new ArrayList<>();
      for (String[] row : csvRows) {
        Customer customer = mapRowToCustomer(row);
        customers.add(customer);
      }
      return customers;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  //Map German CSVRows to Customer Attributes
  private static Customer mapRowToCustomer(String[] csvRow) {
    UUID id = UUID.fromString(csvRow[0]); // UUID
    String genderInCSV = csvRow[1]; // Anrede
    String firstName = csvRow[2]; // Vorname
    String lastName = csvRow[3]; // Nachname
    String birthDateStr = csvRow[4]; // Geburtsdatum

    Customer.Gender gender = mapGender(genderInCSV);
    LocalDate birthDate = null;
    if (birthDateStr != null && !birthDateStr.isEmpty()) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      birthDate = LocalDate.parse(birthDateStr, formatter);
    }

    return new Customer(id, birthDate, firstName, lastName, gender);
  }
  // Translate GenderCSV-Value to ENUM
  private static Customer.Gender mapGender(String genderInCSV) {
    return switch (genderInCSV) {
      case "Herr" -> Customer.Gender.M; // Männlich
      case "Frau" -> Customer.Gender.W; // Weiblich
      case "k.A." -> Customer.Gender.U; // Unbekannt
      default -> Customer.Gender.D;     // Divers
    };
  }

  // Parse all Readings of an import csv to Reading Objects (return as List<Reading>;
  public List<Reading> readReadingCSV (String filePath,KindOfMeter kindOfMeter)
    {
      try {
        com.opencsv.CSVParser parser = new CSVParserBuilder()
            .withSeparator(';')
            .build();

        com.opencsv.CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
            .withCSVParser(parser)
            .build();

        UUID customerID = UUID.fromString(reader.readNext()[1]);
        String meterID = reader.readNext()[1];
        List<Reading> readings = new ArrayList<>();

        // Skip first 5 lines
        for (int i = 0; i < 5; i++) {
          reader.readNext();
        }

        List<String[]> csvRows = reader.readAll();
        for (String[] row : csvRows) {
          Reading reading = mapRowToReading(row, customerID, meterID, kindOfMeter);
          readings.add(reading);
        }

        return readings;
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    //Translate German CSV Columns to Reading Attributes
  private static Reading mapRowToReading(String[] csvRow,UUID customerID,String meterID,KindOfMeter kindOfMeter) {
    Double meterCount = Double.valueOf(csvRow[1]);
    String dateOfReading = csvRow[0];
    String comment = csvRow[2];
    LocalDate birthdate = null;
    if(dateOfReading != null && !dateOfReading.isEmpty()){
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      birthdate = LocalDate.parse(dateOfReading, formatter);
    }
    Customer customer = new Customer();
    customer.setId(customerID);
    return new Reading(UUID.randomUUID(),customer, kindOfMeter, meterID, meterCount, false, birthdate, comment);
  }

}
