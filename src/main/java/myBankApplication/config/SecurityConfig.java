package myBankApplication.config;

import myBankApplication.filter.JwtFilter;
import myBankApplication.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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

    @Value("${bank.cors.allowed-origins:}")
    private String corsAllowedOrigins;

    // מאפשר ללקוח שמתארח בדומיין אחר (Cloudflare Pages) לפנות לשרת.
    // בפיתוח הרשימה ריקה והלקוח עובר דרך ה-proxy של Vite.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login").permitAll() // Allow access to /login without authentication
                                .requestMatchers("/signup").permitAll()
                                .requestMatchers("/verify").permitAll()
                                .requestMatchers("/health").permitAll()
                                // בלי זה כל שגיאה שמועברת ל-/error נחסמת ומגיעה ללקוח כ-403
                                .requestMatchers("/error").permitAll()
                                // Allow both USER and ADMIN roles to access these URLs
                                .requestMatchers("/accounts/my").hasAnyRole("USER", "ADMIN")
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
