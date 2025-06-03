package com.redsocial.red.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.redsocial.red.dto.PerfilDto;
import com.redsocial.red.security.UserDetailsImpl;
import com.redsocial.red.service.PerfilService;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private PerfilService perfilService;

    @GetMapping("/")
    public String home(Model model) {
        // Verificar si el usuario está autenticado y agregar información del perfil
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            if (auth.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
                model.addAttribute("username", userDetails.getNombre() + " " + userDetails.getApellido());
                
                // Obtener información del perfil
                Optional<PerfilDto> perfilOpt = perfilService.obtenerPerfilPorUsuarioId(userDetails.getId());
                if (perfilOpt.isPresent()) {
                    model.addAttribute("perfil", perfilOpt.get());
                } else {
                    // Crear perfil si no existe
                    PerfilDto perfilNuevo = perfilService.crearPerfilSiNoExiste(userDetails.getId());
                    model.addAttribute("perfil", perfilNuevo);
                }
            }
        }
        
        return "home";
    }

    @GetMapping("/index")
    public String index(Model model) {
        return home(model);
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas. Por favor, intente de nuevo.");
        }
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        logger.debug("Accediendo a dashboard");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            logger.debug("Usuario autenticado: {}", auth.getName());

            if (auth.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
                model.addAttribute("usuario", userDetails);
                model.addAttribute("email", userDetails.getEmail());
                model.addAttribute("nombre", userDetails.getNombre());
                model.addAttribute("apellido", userDetails.getApellido());
                
                // Obtener información del perfil
                Optional<PerfilDto> perfilOpt = perfilService.obtenerPerfilPorUsuarioId(userDetails.getId());
                if (perfilOpt.isPresent()) {
                    model.addAttribute("perfil", perfilOpt.get());
                } else {
                    // Crear perfil si no existe
                    PerfilDto perfilNuevo = perfilService.crearPerfilSiNoExiste(userDetails.getId());
                    model.addAttribute("perfil", perfilNuevo);
                }
            } else {
                model.addAttribute("email", auth.getName());
            }

            return "dashboard";
        }

        logger.warn("Acceso denegado a dashboard - Usuario no autenticado");
        return "redirect:/login?error";
    }

    @GetMapping("/debug")
    public String debug() {
        return "redirect:/debug_auth.html";
    }
}