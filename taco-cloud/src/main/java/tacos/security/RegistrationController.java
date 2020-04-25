package tacos.security;

import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegistrationController(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping
  public String registerForm() {
    return "registrationForm";
  }

  @PostMapping
  public String processRegistration(RegistrationForm form) {
    userRepository.save(toUser(form));
    return "redirect:/login";
  }
  
  public User toUser(RegistrationForm form) {
    return new User(
        form.getUsername(), 
        passwordEncoder.encode(form.getPassword()), 
        form.getFirstName(), 
        form.getLastName(), 
        form.getStreet(), 
        form.getCity(), 
        form.getState(), 
        form.getZip(), 
        form.getPhone(), 
        new Date());
  }
}