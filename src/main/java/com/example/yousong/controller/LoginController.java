package com.example.yousong.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class LoginController {

    private final AuthenticationManager authenticationManager;

    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/api/login")
    public String login(@RequestBody Map<String, String> credentials, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("DEBUG: /api/login wurde aufgerufen.");

        // Prüfe die Anfrage-Details
        System.out.println("DEBUG: Methode: " + request.getMethod());
        System.out.println("DEBUG: Header: " + request.getHeaderNames());
        System.out.println("DEBUG: Credentials: " + credentials);

        String username = credentials.get("username");
        String password = credentials.get("password");
        System.out.println("DEBUG: Login-Anfrage erhalten. Benutzername: " + username);

        try {
            // Authentifizierung ausführen
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Authentifizierung erfolgreich
            System.out.println("DEBUG: Authentifizierung erfolgreich für Benutzer: " + authentication.getName());

            // Setze die Authentifizierung in die Session
            request.getSession().setAttribute("user", authentication.getName());
            response.setStatus(HttpServletResponse.SC_OK);
            return "Login erfolgreich!";
        } catch (AuthenticationException e) {
            // Authentifizierung fehlgeschlagen
            System.out.println("DEBUG: Authentifizierung fehlgeschlagen für Benutzername: " + username);
            System.out.println("DEBUG: Fehler: " + e.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "Login fehlgeschlagen: Benutzername oder Passwort falsch.";
        } catch (Exception e) {
            // Allgemeiner Fehler
            System.out.println("DEBUG: Ein unerwarteter Fehler ist aufgetreten.");
            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "Ein unerwarteter Fehler ist aufgetreten.";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("DEBUG: Logout-Anfrage erhalten.");

        request.getSession().invalidate();  // Session invalidieren
        response.setStatus(HttpServletResponse.SC_OK);
        return "Logout erfolgreich!";
    }
}
