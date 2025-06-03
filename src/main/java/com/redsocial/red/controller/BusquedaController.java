package com.redsocial.red.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redsocial.red.dto.PerfilDto;
import com.redsocial.red.dto.UsuarioSearchDto;
import com.redsocial.red.service.BusquedaService;

@Controller
@RequestMapping("/buscar")
public class BusquedaController {
    
    private static final Logger logger = LoggerFactory.getLogger(BusquedaController.class);
    
    @Autowired
    private BusquedaService busquedaService;
    
    /**
     * Página de resultados de búsqueda
     */
    @GetMapping
    public String paginaBusqueda(
            @RequestParam(value = "q", required = false) String searchTerm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        
        logger.debug("Accediendo a página de búsqueda con término: {}", searchTerm);
        
        Page<PerfilDto> resultados;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            resultados = busquedaService.busquedaSimple(searchTerm, page, size);
            model.addAttribute("searchTerm", searchTerm);
        } else {
            resultados = Page.empty();
        }
        
        model.addAttribute("resultados", resultados);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", resultados.getTotalPages());
        model.addAttribute("totalElements", resultados.getTotalElements());
        
        // Obtener datos para filtros
        model.addAttribute("sectoresDisponibles", busquedaService.obtenerSectoresDisponibles());
        model.addAttribute("ubicacionesDisponibles", busquedaService.obtenerUbicacionesDisponibles());
        
        return "busqueda/resultados";
    }
    
    /**
     * API para búsqueda simple (AJAX)
     */
    @GetMapping("/api/simple")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> busquedaSimpleApi(
            @RequestParam("q") String searchTerm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        logger.debug("API búsqueda simple: {}", searchTerm);
        
        Page<PerfilDto> resultados = busquedaService.busquedaSimple(searchTerm, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", resultados.getContent());
        response.put("currentPage", page);
        response.put("totalPages", resultados.getTotalPages());
        response.put("totalElements", resultados.getTotalElements());
        response.put("searchTerm", searchTerm);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API para búsqueda avanzada (AJAX)
     */
    @PostMapping("/api/avanzada")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> busquedaAvanzadaApi(
            @RequestBody UsuarioSearchDto searchDto,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        logger.debug("API búsqueda avanzada con {} criterios", 
                    searchDto.getSearchCriteriaList() != null ? searchDto.getSearchCriteriaList().size() : 0);
        
        Page<PerfilDto> resultados = busquedaService.busquedaAvanzada(searchDto, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", resultados.getContent());
        response.put("currentPage", page);
        response.put("totalPages", resultados.getTotalPages());
        response.put("totalElements", resultados.getTotalElements());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API para búsqueda combinada (simple + avanzada)
     */
    @PostMapping("/api/combinada")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> busquedaCombinadaApi(
            @RequestParam(value = "q", required = false) String searchTerm,
            @RequestBody UsuarioSearchDto searchDto,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        logger.debug("API búsqueda combinada");
        
        Page<PerfilDto> resultados = busquedaService.busquedaCombinada(searchTerm, searchDto, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", resultados.getContent());
        response.put("currentPage", page);
        response.put("totalPages", resultados.getTotalPages());
        response.put("totalElements", resultados.getTotalElements());
        response.put("searchTerm", searchTerm);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API para obtener valores de filtros
     */
    @GetMapping("/api/filtros")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerFiltros() {
        
        Map<String, Object> filtros = new HashMap<>();
        filtros.put("sectores", busquedaService.obtenerSectoresDisponibles());
        filtros.put("ubicaciones", busquedaService.obtenerUbicacionesDisponibles());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", filtros);
        
        return ResponseEntity.ok(response);
    }
} 