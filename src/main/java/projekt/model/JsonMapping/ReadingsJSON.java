package projekt.model.JsonMapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import projekt.model.Reading;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
public class ReadingsJSON {

  @JsonProperty("readings")
  private List<ReadingJSON> readings;

  public List<ReadingJSON> getReadings() {
    return readings;
  }

  public void setReadings(List<ReadingJSON> readings) {
    this.readings = readings;
  }


  /**
   * Serializes list of ReadingJSONS into list of Readings
   *
   */
  public List<Reading> toReadings() {
  List<Reading> readingList = new ArrayList<>();
  for (ReadingJSON readingJSON : readings) {
    readingList.add(readingJSON.toReading());
  }
  return readingList;
  }


}