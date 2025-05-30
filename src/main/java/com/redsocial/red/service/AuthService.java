package com.redsocial.red.service;

import com.redsocial.red.dto.AuthResponse;
import com.redsocial.red.dto.LoginRequest;
import com.redsocial.red.dto.RegistroRequest;
import com.redsocial.red.entity.Perfil;
import com.redsocial.red.entity.Rol;
import com.redsocial.red.entity.Usuario;
import com.redsocial.red.repository.RolRepository;
import com.redsocial.red.repository.UsuarioRepository;
import com.redsocial.red.security.JwtUtils;
import com.redsocial.red.security.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        logger.debug("Intentando autenticar usuario con email: {}", loginRequest.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            logger.debug("Autenticación exitosa para usuario: {}", loginRequest.getEmail());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Actualizar último acceso
            usuarioRepository.actualizarUltimoAcceso(userDetails.getId(), LocalDateTime.now());

            // Obtener foto del perfil si existe
            Usuario usuario = usuarioRepository.findById(userDetails.getId()).orElse(null);
            String fotoUrl = null;
            if (usuario != null && usuario.getPerfil() != null) {
                fotoUrl = usuario.getPerfil().getFotoUrl();
            }

            logger.debug("Respuesta de autenticación preparada para usuario ID: {}", userDetails.getId());

            return new AuthResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getEmail(),
                    userDetails.getNombre(),
                    userDetails.getApellido(),
                    fotoUrl);
        } catch (Exception e) {
            logger.error("Error en autenticación para usuario {}: {}", loginRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    @Transactional
    public String registerUser(RegistroRequest registroRequest) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
            throw new RuntimeException("Error: El email ya está registrado!");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario(
                registroRequest.getEmail(),
                passwordEncoder.encode(registroRequest.getPassword()),
                registroRequest.getNombre(),
                registroRequest.getApellido());

        // Generar token de verificación
        usuario.setTokenVerificacion(UUID.randomUUID().toString());

        // TEMPORALMENTE: Verificar email automáticamente hasta que tengamos el sistema
        // de emails
        usuario.setEmailVerificado(true);

        // Asignar rol por defecto
        Set<Rol> roles = new HashSet<>();
        Rol userRole = rolRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
        roles.add(userRole);
        usuario.setRoles(roles);

        // Guardar usuario
        usuario = usuarioRepository.save(usuario);

        // Crear perfil vacío
        Perfil perfil = new Perfil(usuario);
        usuario.setPerfil(perfil);

        usuarioRepository.save(usuario);

        // TODO: Enviar email de verificación

        return "Usuario registrado exitosamente! Puedes iniciar sesión ahora.";
    }

    @Transactional
    public String verifyEmail(String token) {
        Usuario usuario = usuarioRepository.findByTokenVerificacion(token)
                .orElseThrow(() -> new RuntimeException("Token de verificación inválido"));

        usuario.setEmailVerificado(true);
        usuario.setTokenVerificacion(null);
        usuarioRepository.save(usuario);

        return "Email verificado exitosamente!";
    }

    public boolean checkEmailExists(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}