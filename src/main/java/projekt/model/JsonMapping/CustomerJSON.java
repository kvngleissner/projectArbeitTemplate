package projekt.model.JsonMapping;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import projekt.Interfaces.ICustomer;
import projekt.model.Customer;
@ToString
@Getter
@Setter
public class CustomerJSON {

  @JsonProperty("id")
  private String id;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("birthDate")
  private LocalDate birthDate;

  @JsonProperty("gender")
  private Gender gender;

  public CustomerJSON(String id, String firstName, String lastName, LocalDate birthDate,
      Gender gender) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;
    this.gender = gender;
  }

  public CustomerJSON() {
  }

  public Customer toCustomer() {
    Customer customer = new Customer();
    customer.setId(id == null || id.isEmpty() ? null : UUID.fromString(id));
    customer.setFirstName(firstName);
    customer.setLastName(lastName);
    customer.setBirthDate(birthDate);
    customer.setGender(gender == null ? null : ICustomer.Gender.valueOf(gender.name()));
    return customer;
  }


  public enum Gender {
    D, M, U, W
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CustomerJSON customerJSON)) return false;
    return getId().equals(customerJSON.getId()) && getFirstName().equals(customerJSON.getFirstName()) && getLastName().equals(customerJSON.getLastName()) && getBirthDate().equals(customerJSON.getBirthDate()) && getGender() == customerJSON.getGender();
  }


}