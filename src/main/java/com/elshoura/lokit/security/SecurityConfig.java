package com.elshoura.lokit.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth + Swagger
                        .requestMatchers(
                                "/auth/**",
                                "/error",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Public catalog APIs
                        .requestMatchers("/product/**").permitAll()
                        .requestMatchers("/products/**").permitAll()
                        .requestMatchers("/brand/**").permitAll()
                        .requestMatchers("/category/**").permitAll()
                        .requestMatchers("/department/**").permitAll()
                        .requestMatchers("/material/**").permitAll()
                        .requestMatchers("/variants/**").permitAll()
                        .requestMatchers("/product-images/**").permitAll()

                        // Customer APIs
                        .requestMatchers("/orders/**").hasRole("CUSTOMER")
                        .requestMatchers("/checkout/**").hasRole("CUSTOMER")
                        .requestMatchers("/cart/**").hasRole("CUSTOMER")
                        .requestMatchers("/wishlist/**").hasRole("CUSTOMER")
                        .requestMatchers("/addresses/**").hasRole("CUSTOMER")
                        .requestMatchers("/account/**").hasRole("CUSTOMER")
                        .requestMatchers("/ai/**").hasRole("CUSTOMER")

                        // Admin APIs
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Any other request requires login
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}