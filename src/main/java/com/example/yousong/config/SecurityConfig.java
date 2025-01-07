package com.example.yousong.config;

import com.example.yousong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    @Autowired
    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Konfiguration der Benutzer-Authentifizierung
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    // Konfiguration der HTTP-Sicherheit ohne WebSecurityConfigurerAdapter
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Hier wird die CORS-Bean eingebunden
                .csrf(csrf -> csrf.disable()) // CSRF deaktivieren
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                               /* .requestMatchers("/api/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/songs").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/songs/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/songs/**").authenticated()*/
                                .anyRequest().permitAll() //authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                );
        return http.build();
    }


    // UserDetailsService zur Benutzerauthentifizierung
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var user = userRepository.findByUsername(username);
            System.out.println("DEBUG: Gefundener Benutzer: " + user.getUsername());
            System.out.println("DEBUG: Passwort aus Datenbank: " + user.getPassword());
            if (user == null) {
                System.out.println("DEBUG: Benutzer nicht gefunden: " + username);
                throw new UsernameNotFoundException("User not found");
            }
            System.out.println("DEBUG: Benutzer gefunden: " + username);
            return User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        };
    }

    // Passwort-Encoder
    //BCryptPasswordEncoder verwendet standardmäßig 10 Rounds.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080")); // Deine Frontend-URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // Erlaubte Methoden
        configuration.setAllowedHeaders(Arrays.asList("*")); // Alle Header erlauben
        configuration.setAllowCredentials(true); // Cookies und Authentifizierung erlauben

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

}