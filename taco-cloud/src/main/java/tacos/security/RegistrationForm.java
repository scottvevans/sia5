package tacos.security;

import lombok.Data;

@Data
public class RegistrationForm {

  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String street;
  private String city;
  private String state;
  private String zip;
  private String phone;

}