package myBankApplication.config;

import myBankApplication.filter.JwtFilter;
import myBankApplication.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login").permitAll() // Allow access to /login without authentication
                                .requestMatchers("/signup").permitAll()
                                .requestMatchers("/verify").permitAll()
                                // Allow both USER and ADMIN roles to access these URLs
                                .requestMatchers("/accounts/getBalance/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/accounts/getAllTransactions/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/accounts/getAllLoans/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/transactions/add/**").hasAnyRole("USER", "ADMIN")

                                // Allow ADMIN roles only to access these URLs
                                .requestMatchers("/customers/**").hasRole("ADMIN")
                                .requestMatchers("/accounts/**").hasRole("ADMIN")
                                .requestMatchers("/bankers/**").hasRole("ADMIN")
                                .requestMatchers("/loans/**").hasRole("ADMIN")
                                .requestMatchers("/transactions/**").hasRole("ADMIN")
                                .requestMatchers("/visaCards/**").hasRole("ADMIN")
                                .anyRequest().authenticated() // Secure all other requests
                )
                .exceptionHandling(exceptionHandling -> {})
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
