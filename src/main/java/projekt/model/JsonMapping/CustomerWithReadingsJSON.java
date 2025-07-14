package projekt.model.JsonMapping;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import projekt.model.JsonMapping.CustomerJSON;

@Getter @Setter
public class CustomerWithReadingsJSON {

  @JsonProperty("customer")
  private CustomerJSON customer;

  @JsonProperty("readings")
  private List<ReadingJSON> readings;

  public CustomerJSON getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerJSON customer) {
    this.customer = customer;
  }

  public List<ReadingJSON> getReadings() {
    return readings;
  }

  public void setReadings(List<ReadingJSON> readings) {
    this.readings = readings;
  }

  public static class Reading {

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("customer")
    private Object customer; // Null type in JSON Schema, kept generic.

    @JsonProperty("dateOfReading")
    private LocalDate dateOfReading;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("meterId")
    private String meterId;

    @JsonProperty("substitute")
    private boolean substitute;

    @JsonProperty("metercount")
    private double metercount;

    @JsonProperty("kindOfMeter")
    private KindOfMeter kindOfMeter;

    public enum KindOfMeter {
      HEIZUNG, STROM, WASSER, UNBEKANNT
    }

    public double getMetercount() {
      return metercount;
    }

    public void setMetercount(double metercount) {
      this.metercount = metercount;
    }

    public KindOfMeter getKindOfMeter() {
      return kindOfMeter;
    }

    public void setKindOfMeter(
        KindOfMeter kindOfMeter) {
      this.kindOfMeter = kindOfMeter;
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

    public String getComment() {
      return comment;
    }

    public void setComment(String comment) {
      this.comment = comment;
    }

    public LocalDate getDateOfReading() {
      return dateOfReading;
    }

    public void setDateOfReading(LocalDate dateOfReading) {
      this.dateOfReading = dateOfReading;
    }

    public Object getCustomer() {
      return customer;
    }

    public void setCustomer(Object customer) {
      this.customer = customer;
    }

    public String getUuid() {
      return uuid;
    }

    public void setUuid(String uuid) {
      this.uuid = uuid;
    }
  }
}
