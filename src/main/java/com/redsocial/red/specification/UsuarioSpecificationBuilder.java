package com.redsocial.red.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.redsocial.red.dto.SearchCriteria;
import com.redsocial.red.entity.Usuario;
import com.redsocial.red.enums.SearchOperation;

public class UsuarioSpecificationBuilder {
    
    private final List<SearchCriteria> params;
    
    public UsuarioSpecificationBuilder() {
        this.params = new ArrayList<>();
    }
    
    public final UsuarioSpecificationBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }
    
    public final UsuarioSpecificationBuilder with(String key, String operation, Object value, String dataOption) {
        params.add(new SearchCriteria(key, operation, value, dataOption));
        return this;
    }
    
    public final UsuarioSpecificationBuilder with(SearchCriteria searchCriteria) {
        params.add(searchCriteria);
        return this;
    }
    
    public Specification<Usuario> build() {
        if (params.size() == 0) {
            return null;
        }
        
        Specification<Usuario> result = new UsuarioSpecification(params.get(0));
        
        for (int idx = 1; idx < params.size(); idx++) {
            SearchCriteria criteria = params.get(idx);
            result = SearchOperation.getDataOption(criteria.getDataOption()) == SearchOperation.ALL
                    ? Specification.where(result).and(new UsuarioSpecification(criteria))
                    : Specification.where(result).or(new UsuarioSpecification(criteria));
        }
        
        return result;
    }
    
    /**
     * Construye una especificación para búsqueda simple
     * Busca en nombre, apellido, titular y sector
     */
    public UsuarioSpecificationBuilder withSimpleSearch(String searchTerm) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Buscar en múltiples campos usando OR
            this.with("nombre", "contains", searchTerm, "any")
                .with("apellido", "contains", searchTerm, "any")
                .with("perfil.titular", "contains", searchTerm, "any")
                .with("perfil.sector", "contains", searchTerm, "any");
        }
        return this;
    }
    
    /**
     * Reinicia el builder
     */
    public UsuarioSpecificationBuilder reset() {
        params.clear();
        return this;
    }
    
    /**
     * Obtiene el número de criterios de búsqueda
     */
    public int size() {
        return params.size();
    }
} 