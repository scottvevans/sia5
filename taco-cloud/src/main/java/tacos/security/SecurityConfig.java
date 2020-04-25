package tacos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@Profile("!dev")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  
  private final UserDetailsService userDetailsService;
 
  @Autowired
  public SecurityConfig(final UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }
  
  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth
    .userDetailsService(userDetailsService)
    .passwordEncoder(encoder());
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http.authorizeRequests()
      .antMatchers("/design", "/orders").access("hasRole('ROLE_USER')") 
      .antMatchers("/", "/**").access("permitAll")
    .and()
      .formLogin().loginPage("/login").defaultSuccessUrl("/design", true)
    .and()
      .logout().logoutSuccessUrl("/");
  }
}