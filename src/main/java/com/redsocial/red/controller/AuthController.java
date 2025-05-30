package com.redsocial.red.controller;

import com.redsocial.red.dto.AuthResponse;
import com.redsocial.red.dto.LoginRequest;
import com.redsocial.red.dto.RegistroRequest;
import com.redsocial.red.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Credenciales inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistroRequest registroRequest) {
        try {
            System.out.println("Recibida petición de registro para email: " + registroRequest.getEmail());
            String message = authService.registerUser(registroRequest);
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            System.out.println("Registro exitoso para email: " + registroRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println(
                    "Error en registro para email: " + registroRequest.getEmail() + " - Error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            String message = authService.verifyEmail(token);
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = authService.checkEmailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}