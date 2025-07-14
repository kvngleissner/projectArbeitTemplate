package projekt.model.JsonMapping;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.List;
import projekt.model.Customer;

public class CustomersJSON {

  @JsonProperty("customers")
  private List<CustomerJSON> customers;

  public List<CustomerJSON> getCustomers() {
    return customers;
  }

  public void setCustomers(List<CustomerJSON> customers) {
    this.customers = customers;
  }


  /**
   *
   */
  public List<Customer> toCustomers() {
    List<Customer> customerList = new java.util.ArrayList<>();
    for (CustomerJSON customerJSON : customers) {
      customerList.add(customerJSON.toCustomer());
    }
    return customerList;
  }
}
