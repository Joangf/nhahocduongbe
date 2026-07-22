package vn.viettel.bvrhm.nhahocduong.api.system.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import vn.viettel.bvrhm.nhahocduong.api.auth.filter.JwtAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired 
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.setAllowedOriginPatterns(
        List.of(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "https://localhost:*",
            "https://127.0.0.1:*"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Authorization"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth -> {
              auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                  .permitAll();
              auth.requestMatchers(
                  "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml")
                  .permitAll();
              auth.requestMatchers("/actuator/**")
                  .permitAll()
                  .requestMatchers("/api/auth/login")
                  .permitAll()
                  .requestMatchers("/api/auth/logout")
                  .permitAll()
                  .requestMatchers("/api/auth/refresh")
                  .permitAll()
                  .requestMatchers("/api/auth/guest-login")
                  .permitAll()
                  .requestMatchers("/api/auth/forgot-password")
                  .permitAll()
                  .requestMatchers("/api/roles")
                  .permitAll()
                  .requestMatchers("/api/organization/search*")
                  .permitAll()
                  .requestMatchers("/api/auth/register-send-otp")
                  .permitAll()
                  .requestMatchers("/api/auth/change-password-send-otp")
                  .permitAll()
                  .requestMatchers("/api/auth/verify-otp")
                  .permitAll()
                  .requestMatchers("/api/auth/reset-password")
                  .permitAll()
                  .requestMatchers("/api/user/register")
                  .permitAll()
                  .requestMatchers("/api/user/hello")
                  .permitAll()
                  .requestMatchers("/api/areas/**")
                  .permitAll()
                  .anyRequest()
                  .authenticated();
            })
        // .authenticationProvider(jwtAuthenticationFilter)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

}
