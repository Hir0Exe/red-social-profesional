package com.redsocial.red.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.redsocial.red.dto.ActualizarPerfilRequest;
import com.redsocial.red.dto.PerfilDto;
import com.redsocial.red.entity.Perfil;
import com.redsocial.red.entity.Usuario;
import com.redsocial.red.repository.PerfilRepository;
import com.redsocial.red.repository.UsuarioRepository;
import com.redsocial.red.security.UserDetailsImpl;

@Service
public class PerfilService {

    private static final Logger logger = LoggerFactory.getLogger(PerfilService.class);

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtener perfil por ID de usuario
     */
    public Optional<PerfilDto> obtenerPerfilPorUsuarioId(Long usuarioId) {
        logger.debug("Obteniendo perfil para usuario ID: {}", usuarioId);

        Optional<Perfil> perfilOpt = perfilRepository.findByUsuarioId(usuarioId);
        if (perfilOpt.isPresent()) {
            Perfil perfil = perfilOpt.get();
            boolean esPropietario = esUsuarioActual(usuarioId);
            
            // Incrementar vistas solo si no es el propietario
            if (!esPropietario) {
                incrementarVistas(perfil.getId());
            }
            
            return Optional.of(new PerfilDto(perfil, esPropietario));
        }
        
        return Optional.empty();
    }

    /**
     * Obtener perfil por URL personalizada
     */
    public Optional<PerfilDto> obtenerPerfilPorUrl(String urlPerfil) {
        logger.debug("Obteniendo perfil por URL: {}", urlPerfil);

        Optional<Perfil> perfilOpt = perfilRepository.findByUrlPerfil(urlPerfil);
        if (perfilOpt.isPresent()) {
            Perfil perfil = perfilOpt.get();
            boolean esPropietario = esUsuarioActual(perfil.getUsuario().getId());
            
            // Incrementar vistas solo si no es el propietario
            if (!esPropietario) {
                incrementarVistas(perfil.getId());
            }
            
            return Optional.of(new PerfilDto(perfil, esPropietario));
        }
        
        return Optional.empty();
    }

    /**
     * Crear perfil para un usuario (si no existe)
     */
    @Transactional
    public PerfilDto crearPerfilSiNoExiste(Long usuarioId) {
        logger.debug("Creando perfil para usuario ID: {}", usuarioId);

        Optional<Perfil> perfilExistente = perfilRepository.findByUsuarioId(usuarioId);
        if (perfilExistente.isPresent()) {
            return new PerfilDto(perfilExistente.get(), esUsuarioActual(usuarioId));
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Perfil nuevoPerfil = new Perfil(usuario);
        Perfil perfilGuardado = perfilRepository.save(nuevoPerfil);

        logger.info("Perfil creado exitosamente para usuario ID: {}", usuarioId);
        return new PerfilDto(perfilGuardado, esUsuarioActual(usuarioId));
    }

    /**
     * Actualizar perfil
     */
    @Transactional
    public PerfilDto actualizarPerfil(Long usuarioId, ActualizarPerfilRequest request) {
        logger.debug("Actualizando perfil para usuario ID: {}", usuarioId);

        // Verificar que el usuario actual es el propietario
        if (!esUsuarioActual(usuarioId)) {
            throw new RuntimeException("No tienes permisos para actualizar este perfil");
        }

        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));

        // Actualizar campos
        if (request.getTitular() != null) {
            perfil.setTitular(request.getTitular());
        }
        if (request.getExtracto() != null) {
            perfil.setExtracto(request.getExtracto());
        }
        if (request.getUbicacion() != null) {
            perfil.setUbicacion(request.getUbicacion());
        }
        if (request.getSector() != null) {
            perfil.setSector(request.getSector());
        }
        if (request.getSitioWeb() != null) {
            perfil.setSitioWeb(request.getSitioWeb());
        }
        if (request.getTelefono() != null) {
            perfil.setTelefono(request.getTelefono());
        }
        if (request.getFechaNacimiento() != null) {
            perfil.setFechaNacimiento(request.getFechaNacimiento());
        }
        if (request.getVisibilidadPerfil() != null) {
            perfil.setVisibilidadPerfil(Perfil.VisibilidadPerfil.valueOf(request.getVisibilidadPerfil()));
        }
        if (request.getFotoUrl() != null) {
            perfil.setFotoUrl(request.getFotoUrl());
        }

        perfil.setFechaActualizacion(LocalDateTime.now());
        Perfil perfilActualizado = perfilRepository.save(perfil);

        logger.info("Perfil actualizado exitosamente para usuario ID: {}", usuarioId);
        return new PerfilDto(perfilActualizado, true);
    }

    /**
     * Verificar disponibilidad de URL de perfil
     */
    public boolean verificarDisponibilidadUrl(String urlPerfil, Long usuarioId) {
        Optional<Perfil> perfilExistente = perfilRepository.findByUrlPerfil(urlPerfil);
        
        // Si no existe, está disponible
        if (perfilExistente.isEmpty()) {
            return true;
        }
        
        // Si existe pero es del mismo usuario, está disponible
        return perfilExistente.get().getUsuario().getId().equals(usuarioId);
    }

    /**
     * Incrementar vistas del perfil
     */
    @Transactional
    public void incrementarVistas(Long perfilId) {
        try {
            perfilRepository.incrementarVistas(perfilId);
        } catch (Exception e) {
            logger.warn("Error al incrementar vistas para perfil ID: {}", perfilId, e);
        }
    }

    /**
     * Verificar si el usuario actual es el propietario
     */
    private boolean esUsuarioActual(Long usuarioId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            if (auth.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
                return userDetails.getId().equals(usuarioId);
            }
        }
        return false;
    }

    /**
     * Obtener ID del usuario actual
     */
    private Long obtenerUsuarioActualId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            if (auth.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
                return userDetails.getId();
            }
        }
        return null;
    }
} 