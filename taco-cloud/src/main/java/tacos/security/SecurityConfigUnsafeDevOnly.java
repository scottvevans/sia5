package tacos.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Profile("dev")
@Slf4j
public class SecurityConfigUnsafeDevOnly extends SecurityConfig {

  public SecurityConfigUnsafeDevOnly(final UserDetailsService userDetailsService) {
    super(userDetailsService);
    log.warn("To enable h2 console in 'dev' profile, using INSECURE security config {}", this.getClass().getName());
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    super.configure(http);

    // the below are to here to enable the h2 database console when in development mode
    http.csrf().disable();
    http.headers().frameOptions().disable();
  }
}