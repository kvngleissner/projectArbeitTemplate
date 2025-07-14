package projekt.Interfaces;

import java.time.LocalDate;

public interface ICustomer extends IId {

  LocalDate getBirthDate();

  void setBirthDate(LocalDate birtDate);

  String getFirstName();

  void setFirstName(String firstName);

  Gender getGender();

  void setGender(Gender gender);

  String getLastName();

  void setLastName(String lastName);

  enum Gender {
    D, // divers
    M, // männlich
    U, // unbekannt
    W // weiblich
  }

}
