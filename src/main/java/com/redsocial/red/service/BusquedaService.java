package com.redsocial.red.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.redsocial.red.dto.PerfilDto;
import com.redsocial.red.dto.SearchCriteria;
import com.redsocial.red.dto.UsuarioSearchDto;
import com.redsocial.red.entity.Usuario;
import com.redsocial.red.repository.UsuarioRepository;
import com.redsocial.red.specification.UsuarioSpecificationBuilder;

@Service
public class BusquedaService {
    
    private static final Logger logger = LoggerFactory.getLogger(BusquedaService.class);
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PerfilService perfilService;
    
    /**
     * Búsqueda simple por término
     */
    public Page<PerfilDto> busquedaSimple(String searchTerm, int pageNum, int pageSize) {
        logger.debug("Realizando búsqueda simple con término: {}", searchTerm);
        
        UsuarioSpecificationBuilder builder = new UsuarioSpecificationBuilder();
        builder.withSimpleSearch(searchTerm);
        
        Pageable pageable = PageRequest.of(pageNum, pageSize, 
                                         Sort.by("nombre").ascending()
                                         .and(Sort.by("apellido").ascending()));
        
        Page<Usuario> usuariosPage = usuarioRepository.findAll(builder.build(), pageable);
        
        return usuariosPage.map(this::convertToPerfilDto);
    }
    
    /**
     * Búsqueda avanzada con criterios múltiples
     */
    public Page<PerfilDto> busquedaAvanzada(UsuarioSearchDto searchDto, int pageNum, int pageSize) {
        logger.debug("Realizando búsqueda avanzada con {} criterios", 
                    searchDto.getSearchCriteriaList() != null ? searchDto.getSearchCriteriaList().size() : 0);
        
        UsuarioSpecificationBuilder builder = new UsuarioSpecificationBuilder();
        
        List<SearchCriteria> criteriaList = searchDto.getSearchCriteriaList();
        if (criteriaList != null) {
            criteriaList.forEach(criteria -> {
                criteria.setDataOption(searchDto.getDataOption());
                builder.with(criteria);
            });
        }
        
        Specification<Usuario> spec = builder.build();
        if (spec == null) {
            // Si no hay criterios, devolver página vacía
            return Page.empty();
        }
        
        Pageable pageable = PageRequest.of(pageNum, pageSize, 
                                         Sort.by("nombre").ascending()
                                         .and(Sort.by("apellido").ascending()));
        
        Page<Usuario> usuariosPage = usuarioRepository.findAll(spec, pageable);
        
        return usuariosPage.map(this::convertToPerfilDto);
    }
    
    /**
     * Obtener valores únicos para filtros
     */
    public List<String> obtenerSectoresDisponibles() {
        return usuarioRepository.findDistinctSectores();
    }
    
    public List<String> obtenerUbicacionesDisponibles() {
        return usuarioRepository.findDistinctUbicaciones();
    }
    
    /**
     * Convierte Usuario a PerfilDto para la respuesta
     */
    private PerfilDto convertToPerfilDto(Usuario usuario) {
        return perfilService.obtenerPerfilPorUsuarioId(usuario.getId())
                          .orElseGet(() -> {
                              // Si no tiene perfil, crear uno básico
                              return perfilService.crearPerfilSiNoExiste(usuario.getId());
                          });
    }
    
    /**
     * Búsqueda combinada (simple + avanzada)
     */
    public Page<PerfilDto> busquedaCombinada(String searchTerm, UsuarioSearchDto searchDto, int pageNum, int pageSize) {
        logger.debug("Realizando búsqueda combinada");
        
        UsuarioSpecificationBuilder builder = new UsuarioSpecificationBuilder();
        
        // Agregar búsqueda simple si existe
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            builder.withSimpleSearch(searchTerm);
        }
        
        // Agregar criterios avanzados si existen
        List<SearchCriteria> criteriaList = searchDto.getSearchCriteriaList();
        if (criteriaList != null) {
            criteriaList.forEach(criteria -> {
                criteria.setDataOption(searchDto.getDataOption());
                builder.with(criteria);
            });
        }
        
        Specification<Usuario> spec = builder.build();
        if (spec == null) {
            // Si no hay criterios, devolver todos los usuarios
            Pageable pageable = PageRequest.of(pageNum, pageSize, 
                                             Sort.by("nombre").ascending()
                                             .and(Sort.by("apellido").ascending()));
            Page<Usuario> usuariosPage = usuarioRepository.findAll(pageable);
            return usuariosPage.map(this::convertToPerfilDto);
        }
        
        Pageable pageable = PageRequest.of(pageNum, pageSize, 
                                         Sort.by("nombre").ascending()
                                         .and(Sort.by("apellido").ascending()));
        
        Page<Usuario> usuariosPage = usuarioRepository.findAll(spec, pageable);
        
        return usuariosPage.map(this::convertToPerfilDto);
    }
} 