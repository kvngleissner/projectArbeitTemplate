package projekt.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import projekt.ExcludeFromJacocoGeneratedReport;
import projekt.Interfaces.ICustomer;
import projekt.model.Customer;
import projekt.Interfaces.IReading;
import projekt.model.JsonMapping.CustomerJSON;
import projekt.model.JsonMapping.ReadingJSON;

@EqualsAndHashCode
public class Reading implements IReading {

  private UUID id;
  private ICustomer customer;
  private KindOfMeter kindOfMeter;
  private String meterId;
  private Double meterCount;
  private Boolean substitute;
  private LocalDate dateOfReading;
  private String comment;


  public Reading(UUID id, ICustomer customer, KindOfMeter kindOfMeter, String meterId,
                 Double meterCount, Boolean substitute, LocalDate dateOfReading, String comment) {

    this.id = id;
    this.customer = customer;
    this.kindOfMeter = kindOfMeter;
    this.meterId = meterId;
    this.meterCount = meterCount;
    this.substitute = substitute;
    this.dateOfReading = dateOfReading;
    this.comment = comment;
  }

  public Reading() {
  }

  @Override
  public String getComment() {
    return comment;
  }

  @Override
  public ICustomer getCustomer() {
    return customer;
  }

  @Override
  public LocalDate getDateOfReading() {
    return dateOfReading;
  }

  @Override
  public KindOfMeter getKindOfMeter() {
    return kindOfMeter;
  }

  @Override
  public Double getMeterCount() {
    return meterCount;
  }

  @Override
  public String getMeterId() {
    return meterId;
  }

  @Override
  public Boolean getSubstitute() {
    return substitute;
  }
  @ExcludeFromJacocoGeneratedReport
  @Override
  public String printDateOfReading() {
    return dateOfReading.toString();
  }

  @Override
  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public void setCustomer(ICustomer customer) {
    this.customer = customer;
  }

  @Override
  public void setDateOfReading(LocalDate dateOfReading) {
    this.dateOfReading = dateOfReading;
  }

  @Override
  public void setKindOfMeter(KindOfMeter kindOfMeter) {
    this.kindOfMeter = kindOfMeter;
  }

  @Override
  public void setMeterCount(Double meterCount) {
    this.meterCount = meterCount;
  }

  @Override
  public void setMeterId(String meterId) {
    this.meterId = meterId;
  }

  @Override
  public void setSubstitute(Boolean substitute) {
    this.substitute = substitute;
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }


  @ExcludeFromJacocoGeneratedReport
  @Override
  public String toString() {

    return (String.format
            ("%1$s %10$s %2$s %11$s %3$s %12$s %4$s %13$s %5$s %14$s %6$s %15$s %7$s %16$s %8$s %17$s %9$s",
                    "{Reading ID:", "| Customer ID", "| Kind of Meter", "| Meter ID", "| Meter count", "| substitute", "| Date of reading", "| comment", "}",
                    id, (customer != null ? customer.getId() : "null"), kindOfMeter, meterId, meterCount, substitute, dateOfReading, comment));

  }
  @ExcludeFromJacocoGeneratedReport
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Reading reading)) return false;
    return Objects.equals(getCustomer(), reading.getCustomer()) && getKindOfMeter() == reading.getKindOfMeter() && Objects.equals(getMeterId(), reading.getMeterId()) && Objects.equals(getMeterCount(), reading.getMeterCount()) && Objects.equals(getSubstitute(), reading.getSubstitute()) && Objects.equals(getDateOfReading(), reading.getDateOfReading()) && Objects.equals(getComment(), reading.getComment());
  }


  /**
   * Converts a Reading object to a ReadingJSON object.
   *
   * @return ReadingJSON
   */
  public ReadingJSON toReadingJSON() {
    CustomerJSON  customerJSON  = this.customer == null ? new CustomerJSON(null, null, null, null, null) : new CustomerJSON(this.customer.getId().toString(),this.customer.getFirstName(),this.customer.getLastName(),this.customer.getBirthDate(),this.customer.getGender()==null?null:CustomerJSON.Gender.valueOf(this.customer.getGender().name()));
    ReadingJSON readingJSON = new ReadingJSON();
    readingJSON.setId(id == null || id.toString().isEmpty() ? null : String.valueOf(this.id));
    readingJSON.setComment(this.comment);
    readingJSON.setDateOfReading(this.dateOfReading);
    readingJSON.setMeterCount(this.meterCount);
    readingJSON.setMeterId(this.meterId);
    readingJSON.setSubstitute(this.substitute);
    readingJSON.setCustomer(customerJSON);
    readingJSON.setKindOfMeter(this.kindOfMeter==null?null:ReadingJSON.KindOfMeter.valueOf(this.kindOfMeter.name()));
    return readingJSON;
  }

}





