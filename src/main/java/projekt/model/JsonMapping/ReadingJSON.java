package projekt.model.JsonMapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import projekt.ExcludeFromJacocoGeneratedReport;
import projekt.model.Reading;
import projekt.Interfaces.ICustomer;
import projekt.rest.resources.ReadingResource;
public class ReadingJSON {
  private static final Logger logger = LogManager.getLogger(ReadingJSON.class);

  @JsonProperty("id")
  private String id;

  @JsonProperty("customer")
  private CustomerJSON customer;

  @JsonProperty("dateOfReading")
  private LocalDate dateOfReading;

  @JsonProperty("comment")
  private String comment;

  @JsonProperty("meterId")
  private String meterId;

  @JsonProperty("substitute")
  private boolean substitute;

  @JsonProperty("meterCount")
  private double meterCount;

  @JsonProperty("kindOfMeter")
  private KindOfMeter kindOfMeter;

  public enum KindOfMeter {
    WATER, POWER, HEAT, UNKNOWN
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public KindOfMeter getKindOfMeter() {
    return kindOfMeter;
  }

  public void setKindOfMeter(KindOfMeter kindOfMeter) {
    this.kindOfMeter = kindOfMeter;
  }

  public double getMeterCount() {
    return meterCount;
  }

  public void setMeterCount(double meterCount) {
    this.meterCount = meterCount;
  }

  public boolean isSubstitute() {
    return substitute;
  }

  public void setSubstitute(boolean substitute) {
    this.substitute = substitute;
  }

  public String getMeterId() {
    return meterId;
  }

  public void setMeterId(String meterId) {
    this.meterId = meterId;
  }

  public LocalDate getDateOfReading() {
    return dateOfReading;
  }

  public void setDateOfReading(LocalDate dateOfReading) {
    this.dateOfReading = dateOfReading;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  // Resolve the conflicting setter methods by ensuring only one setCustomer method
  public CustomerJSON getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerJSON customer) {
    this.customer = customer;
  }

  /**
   * Converts a ReadingJSON object to a Reading object.
   *
   * @return Reading
   */
  public Reading toReading() {
    logger.info("Attempting to convert ReadingJSON to Reading");
    Reading reading = new Reading();
    reading.setId(this.id == null || this.id.isEmpty() ? null : UUID.fromString(id));
    reading.setCustomer(this.customer.toCustomer());
    reading.setDateOfReading(this.dateOfReading);
    reading.setComment(this.comment);
    reading.setMeterId(this.meterId);
    reading.setSubstitute(this.substitute);
    reading.setMeterCount(this.meterCount);
    reading.setKindOfMeter(this.kindOfMeter==null?null:Reading.KindOfMeter.valueOf(this.kindOfMeter.name()));;
    return reading;
  }
  @ExcludeFromJacocoGeneratedReport
  @Override
  public String toString() {
    return String.format("ReadingJSON [id=%s, customer=%s, dateOfReading=%s, comment=%s, meterId=%s, substitute=%s, meterCount=%s, kindOfMeter=%s]", id, customer, dateOfReading, comment, meterId, substitute, meterCount, kindOfMeter);
  }

  @ExcludeFromJacocoGeneratedReport
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ReadingJSON readingJSON)) return false;
    return Objects.equals(getCustomer(), readingJSON.getCustomer()) && getKindOfMeter() == readingJSON.getKindOfMeter() && Objects.equals(getMeterId(), readingJSON.getMeterId()) && Objects.equals(getMeterCount(), readingJSON.getMeterCount()) && Objects.equals(isSubstitute(), readingJSON.isSubstitute()) && Objects.equals(getDateOfReading(), readingJSON.getDateOfReading()) && Objects.equals(getComment(), readingJSON.getComment());
  }

}