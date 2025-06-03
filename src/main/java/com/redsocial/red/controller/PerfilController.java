package com.redsocial.red.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.redsocial.red.dto.ActualizarPerfilRequest;
import com.redsocial.red.dto.PerfilDto;
import com.redsocial.red.security.UserDetailsImpl;
import com.redsocial.red.service.PerfilService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private static final Logger logger = LoggerFactory.getLogger(PerfilController.class);

    @Autowired
    private PerfilService perfilService;

    /**
     * Mostrar el perfil del usuario actual
     */
    @GetMapping
    public String miPerfil(Model model) {
        logger.debug("Mostrando perfil del usuario actual");

        Long usuarioId = obtenerUsuarioActualId();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Optional<PerfilDto> perfilOpt = perfilService.obtenerPerfilPorUsuarioId(usuarioId);
        
        if (perfilOpt.isEmpty()) {
            // Crear perfil si no existe
            PerfilDto perfilNuevo = perfilService.crearPerfilSiNoExiste(usuarioId);
            model.addAttribute("perfil", perfilNuevo);
        } else {
            model.addAttribute("perfil", perfilOpt.get());
        }

        return "perfil/ver";
    }

    /**
     * Mostrar perfil de otro usuario por ID
     */
    @GetMapping("/usuario/{usuarioId}")
    public String verPerfilPorId(@PathVariable Long usuarioId, Model model) {
        logger.debug("Mostrando perfil del usuario ID: {}", usuarioId);

        Optional<PerfilDto> perfilOpt = perfilService.obtenerPerfilPorUsuarioId(usuarioId);
        
        if (perfilOpt.isEmpty()) {
            model.addAttribute("error", "Perfil no encontrado");
            return "error/404";
        }

        model.addAttribute("perfil", perfilOpt.get());
        return "perfil/ver";
    }

    /**
     * Mostrar perfil por URL personalizada
     */
    @GetMapping("/u/{urlPerfil}")
    public String verPerfilPorUrl(@PathVariable String urlPerfil, Model model) {
        logger.debug("Mostrando perfil por URL: {}", urlPerfil);

        Optional<PerfilDto> perfilOpt = perfilService.obtenerPerfilPorUrl(urlPerfil);
        
        if (perfilOpt.isEmpty()) {
            model.addAttribute("error", "Perfil no encontrado");
            return "error/404";
        }

        model.addAttribute("perfil", perfilOpt.get());
        return "perfil/ver";
    }

    /**
     * Mostrar formulario de edición del perfil
     */
    @GetMapping("/editar")
    public String editarPerfil(Model model) {
        logger.debug("Mostrando formulario de edición de perfil");

        Long usuarioId = obtenerUsuarioActualId();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Optional<PerfilDto> perfilOpt = perfilService.obtenerPerfilPorUsuarioId(usuarioId);
        
        if (perfilOpt.isEmpty()) {
            // Crear perfil si no existe
            PerfilDto perfilNuevo = perfilService.crearPerfilSiNoExiste(usuarioId);
            model.addAttribute("perfil", perfilNuevo);
        } else {
            model.addAttribute("perfil", perfilOpt.get());
        }

        // Crear objeto para el formulario
        ActualizarPerfilRequest request = new ActualizarPerfilRequest();
        PerfilDto perfil = (PerfilDto) model.getAttribute("perfil");
        
        if (perfil != null) {
            request.setTitular(perfil.getTitular());
            request.setExtracto(perfil.getExtracto());
            request.setUbicacion(perfil.getUbicacion());
            request.setSector(perfil.getSector());
            request.setSitioWeb(perfil.getSitioWeb());
            request.setTelefono(perfil.getTelefono());
            request.setFechaNacimiento(perfil.getFechaNacimiento());
            request.setVisibilidadPerfil(perfil.getVisibilidadPerfil());
            request.setFotoUrl(perfil.getFotoUrl());
        }

        model.addAttribute("actualizarPerfilRequest", request);
        return "perfil/editar";
    }

    /**
     * Procesar actualización del perfil
     */
    @PostMapping("/actualizar")
    public String actualizarPerfil(@Valid @ModelAttribute ActualizarPerfilRequest request, 
                                   BindingResult bindingResult, 
                                   Model model, 
                                   RedirectAttributes redirectAttributes) {
        logger.debug("Procesando actualización de perfil");

        Long usuarioId = obtenerUsuarioActualId();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            // Recargar perfil para mostrar errores
            Optional<PerfilDto> perfilOpt = perfilService.obtenerPerfilPorUsuarioId(usuarioId);
            model.addAttribute("perfil", perfilOpt.orElse(null));
            return "perfil/editar";
        }

        try {
            PerfilDto perfilActualizado = perfilService.actualizarPerfil(usuarioId, request);
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
            return "redirect:/perfil";
        } catch (Exception e) {
            logger.error("Error al actualizar perfil para usuario ID: {}", usuarioId, e);
            model.addAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            
            // Recargar perfil
            Optional<PerfilDto> perfilOpt = perfilService.obtenerPerfilPorUsuarioId(usuarioId);
            model.addAttribute("perfil", perfilOpt.orElse(null));
            return "perfil/editar";
        }
    }

    /**
     * Mostrar vista previa del perfil (como lo ven otros usuarios)
     */
    @GetMapping("/preview")
    public String previsualizarPerfil(Model model) {
        logger.debug("Mostrando previsualización del perfil");

        Long usuarioId = obtenerUsuarioActualId();
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Optional<PerfilDto> perfilOpt = perfilService.obtenerPerfilPorUsuarioId(usuarioId);
        
        if (perfilOpt.isEmpty()) {
            model.addAttribute("error", "Perfil no encontrado");
            return "error/404";
        }

        PerfilDto perfil = perfilOpt.get();
        // Simular que no es el propietario para la vista previa
        perfil.setEsPropietario(false);
        model.addAttribute("perfil", perfil);
        model.addAttribute("esPreview", true);
        
        return "perfil/ver";
    }

    /**
     * API endpoint para verificar disponibilidad de URL
     */
    @GetMapping("/api/verificar-url")
    @ResponseBody
    public boolean verificarDisponibilidadUrl(@RequestParam String urlPerfil) {
        Long usuarioId = obtenerUsuarioActualId();
        if (usuarioId == null) {
            return false;
        }
        
        return perfilService.verificarDisponibilidadUrl(urlPerfil, usuarioId);
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