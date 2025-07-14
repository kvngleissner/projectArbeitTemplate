package projekt.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import projekt.ExcludeFromJacocoGeneratedReport;
import projekt.Interfaces.ICustomer;
import projekt.model.JsonMapping.CustomerJSON;

public class Customer implements ICustomer {

  private LocalDate birthDate;
  private UUID id;
  private String firstName;
  private String lastName;
  private Gender gender;

  public Customer(UUID id, LocalDate birthDate, String firstName, String lastName, Gender gender) {
    this.birthDate = birthDate;
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
  }

  public Customer() {

  }

  @Override
  public LocalDate getBirthDate() {
    return birthDate;
  }

  @Override
  public String getFirstName() {
    return firstName;
  }

  @Override
  public Gender getGender() {
    return gender;
  }

  @Override
  public String getLastName() {
    return lastName;
  }

  @Override
  public void setBirthDate(LocalDate birtDate) {
    this.birthDate = birtDate;
  }

  @Override
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @Override
  public void setGender(Gender gender) {
    this.gender = gender;
  }

  @Override
  public void setLastName(String lastName) {
    this.lastName = lastName;
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
              ("%1$s %7$s %2$s %8$s %3$s %9$s %4$s %10$s %5$s %11$s %6$s",
                      "{ Customer ID:", "| First Name:", "| Last Name:", "| Birth Date:", "| Gender:", "}",
                      id, firstName, lastName, birthDate, gender));
  }
  @ExcludeFromJacocoGeneratedReport
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Customer customer)) return false;
    return Objects.equals(getBirthDate(), customer.getBirthDate()) && Objects.equals(getId(), customer.getId()) && Objects.equals(getFirstName(), customer.getFirstName()) && Objects.equals(getLastName(), customer.getLastName()) && getGender() == customer.getGender();
  }

  public CustomerJSON toCustomerJSON() {
    return new CustomerJSON(id == null || id.toString().isEmpty() ? null : String.valueOf(this.id), this.firstName, this.lastName, this.birthDate, this.gender == null ? null :CustomerJSON.Gender.valueOf(this.gender.name()));
  }
  

}
