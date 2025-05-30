package com.redsocial.red.controller;

import com.redsocial.red.entity.Usuario;
import com.redsocial.red.repository.UsuarioRepository;
import com.redsocial.red.repository.RolRepository;
import com.redsocial.red.security.UserDetailsServiceImpl;
import com.redsocial.red.security.UserDetailsImpl;
import com.redsocial.red.service.AuthService;
import com.redsocial.red.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/diagnostic")
public class DiagnosticController {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticController.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthService authService;

    @GetMapping("/users")
    public Map<String, Object> getAllUsers() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("total", usuarios.size());
        response.put("usuarios", usuarios.stream().map(u -> {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", u.getId());
            userInfo.put("email", u.getEmail());
            userInfo.put("nombre", u.getNombre());
            userInfo.put("apellido", u.getApellido());
            userInfo.put("activo", u.isActivo());
            userInfo.put("emailVerificado", u.isEmailVerificado());
            userInfo.put("fechaRegistro", u.getFechaRegistro());
            userInfo.put("roles", u.getRoles().stream().map(r -> r.getNombre()).toList());
            return userInfo;
        }).toList());
        return response;
    }

    @GetMapping("/user/{email}")
    public Map<String, Object> getUserByEmail(@PathVariable String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmailWithRoles(email);
        Map<String, Object> response = new HashMap<>();

        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            response.put("found", true);
            response.put("id", u.getId());
            response.put("email", u.getEmail());
            response.put("nombre", u.getNombre());
            response.put("apellido", u.getApellido());
            response.put("activo", u.isActivo());
            response.put("emailVerificado", u.isEmailVerificado());
            response.put("fechaRegistro", u.getFechaRegistro());
            response.put("roles", u.getRoles().stream().map(r -> r.getNombre()).toList());
            response.put("passwordSet", u.getPassword() != null && !u.getPassword().isEmpty());
        } else {
            response.put("found", false);
        }
        return response;
    }

    @PostMapping("/verify-password")
    public Map<String, Object> verifyPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String rawPassword = request.get("password");

        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            boolean passwordMatches = passwordEncoder.matches(rawPassword, u.getPassword());
            response.put("userFound", true);
            response.put("passwordMatches", passwordMatches);
            response.put("activo", u.isActivo());
            response.put("emailVerificado", u.isEmailVerificado());
            response.put("encodedPassword", u.getPassword().substring(0, 20) + "...");
        } else {
            response.put("userFound", false);
        }

        return response;
    }

    @PostMapping("/test-userdetails")
    public Map<String, Object> testUserDetailsService(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Map<String, Object> response = new HashMap<>();

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            response.put("success", true);
            response.put("username", userDetails.getUsername());
            response.put("enabled", userDetails.isEnabled());
            response.put("accountNonExpired", userDetails.isAccountNonExpired());
            response.put("accountNonLocked", userDetails.isAccountNonLocked());
            response.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());
            response.put("authorities", userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority()).toList());
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
        }

        return response;
    }

    @PostMapping("/test-authentication")
    public Map<String, Object> testAuthentication(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        Map<String, Object> response = new HashMap<>();

        try {
            // Paso 1: Probar UserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            response.put("step1_userDetailsService", "SUCCESS");
            response.put("userEnabled", userDetails.isEnabled());

            // Paso 2: Probar AuthenticationManager
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

            Authentication authentication = authenticationManager.authenticate(authToken);
            response.put("step2_authentication", "SUCCESS");
            response.put("authenticated", authentication.isAuthenticated());
            response.put("principal", authentication.getPrincipal().getClass().getSimpleName());

            // Paso 3: Probar AuthService completo
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(email);
            loginRequest.setPassword(password);

            authService.authenticateUser(loginRequest);
            response.put("step3_authService", "SUCCESS");

        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("stackTrace", java.util.Arrays.toString(e.getStackTrace()));
        }

        return response;
    }

    @GetMapping("/roles")
    public Map<String, Object> getAllRoles() {
        Map<String, Object> response = new HashMap<>();
        response.put("roles", rolRepository.findAll());
        return response;
    }

    @GetMapping("/session-info")
    public ResponseEntity<?> getSessionInfo(HttpServletRequest request) {
        logger.debug("Obteniendo información de sesión");

        Map<String, Object> info = new HashMap<>();

        // Información de autenticación
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            info.put("authenticated", auth.isAuthenticated());
            info.put("username", auth.getName());
            info.put("authorities", auth.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.toList()));

            if (auth.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
                info.put("userDetails", Map.of(
                        "id", userDetails.getId(),
                        "email", userDetails.getEmail(),
                        "nombre", userDetails.getNombre(),
                        "apellido", userDetails.getApellido()));
            }
        } else {
            info.put("authenticated", false);
        }

        // Información de sesión
        HttpSession session = request.getSession(false);
        if (session != null) {
            info.put("sessionId", session.getId());
            info.put("sessionCreationTime", new Date(session.getCreationTime()));
            info.put("sessionLastAccessedTime", new Date(session.getLastAccessedTime()));
            info.put("sessionMaxInactiveInterval", session.getMaxInactiveInterval());
        } else {
            info.put("session", "No session");
        }

        return ResponseEntity.ok(info);
    }
}