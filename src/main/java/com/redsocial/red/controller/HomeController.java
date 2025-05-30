package com.redsocial.red.controller;

import com.redsocial.red.security.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/index")
    public String index() {
        return "home";
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