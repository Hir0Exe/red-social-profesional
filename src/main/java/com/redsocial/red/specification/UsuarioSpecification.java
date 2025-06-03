package com.redsocial.red.specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.data.jpa.domain.Specification;

import com.redsocial.red.dto.SearchCriteria;
import com.redsocial.red.entity.Usuario;
import com.redsocial.red.enums.SearchOperation;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UsuarioSpecification implements Specification<Usuario> {
    
    private final SearchCriteria searchCriteria;
    
    public UsuarioSpecification(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }
    
    @Override
    public Predicate toPredicate(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        
        String filterKey = searchCriteria.getFilterKey();
        String operation = searchCriteria.getOperation();
        Object value = searchCriteria.getValue();
        
        if (value == null || value.toString().trim().isEmpty()) {
            return criteriaBuilder.conjunction(); // Devuelve true (no filter)
        }
        
        switch (SearchOperation.getOperation(operation)) {
            case CONTAINS:
                return handleContains(root, query, criteriaBuilder, filterKey, value);
                
            case DOES_NOT_CONTAIN:
                return criteriaBuilder.not(handleContains(root, query, criteriaBuilder, filterKey, value));
                
            case EQUAL:
                return handleEqual(root, query, criteriaBuilder, filterKey, value);
                
            case NOT_EQUAL:
                return criteriaBuilder.not(handleEqual(root, query, criteriaBuilder, filterKey, value));
                
            case BEGINS_WITH:
                return handleBeginsWith(root, query, criteriaBuilder, filterKey, value);
                
            case ENDS_WITH:
                return handleEndsWith(root, query, criteriaBuilder, filterKey, value);
                
            case GREATER_THAN:
                return handleGreaterThan(root, query, criteriaBuilder, filterKey, value);
                
            case GREATER_THAN_EQUAL:
                return handleGreaterThanEqual(root, query, criteriaBuilder, filterKey, value);
                
            case LESS_THAN:
                return handleLessThan(root, query, criteriaBuilder, filterKey, value);
                
            case LESS_THAN_EQUAL:
                return handleLessThanEqual(root, query, criteriaBuilder, filterKey, value);
                
            case IN:
                return handleIn(root, query, criteriaBuilder, filterKey, value);
                
            case NOT_IN:
                return criteriaBuilder.not(handleIn(root, query, criteriaBuilder, filterKey, value));
                
            default:
                return criteriaBuilder.conjunction();
        }
    }
    
    private Predicate handleContains(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String filterKey, Object value) {
        if (filterKey.contains(".")) {
            return handleJoinField(root, query, criteriaBuilder, filterKey, value, "contains");
        }
        return criteriaBuilder.like(criteriaBuilder.lower(root.get(filterKey).as(String.class)), 
                                  "%" + value.toString().toLowerCase() + "%");
    }
    
    private Predicate handleEqual(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String filterKey, Object value) {
        if (filterKey.contains(".")) {
            return handleJoinField(root, query, criteriaBuilder, filterKey, value, "equal");
        }
        
        if (filterKey.equals("fechaNacimiento") && value instanceof String) {
            LocalDate date = LocalDate.parse(value.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return criteriaBuilder.equal(root.get(filterKey), date);
        }
        
        return criteriaBuilder.equal(root.get(filterKey), value);
    }
    
    private Predicate handleBeginsWith(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String filterKey, Object value) {
        if (filterKey.contains(".")) {
            return handleJoinField(root, query, criteriaBuilder, filterKey, value, "beginsWith");
        }
        return criteriaBuilder.like(criteriaBuilder.lower(root.get(filterKey).as(String.class)), 
                                  value.toString().toLowerCase() + "%");
    }
    
    private Predicate handleEndsWith(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String filterKey, Object value) {
        if (filterKey.contains(".")) {
            return handleJoinField(root, query, criteriaBuilder, filterKey, value, "endsWith");
        }
        return criteriaBuilder.like(criteriaBuilder.lower(root.get(filterKey).as(String.class)), 
                                  "%" + value.toString().toLowerCase());
    }
    
    private Predicate handleGreaterThan(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String filterKey, Object value) {
        if (filterKey.contains(".")) {
            return handleJoinField(root, query, criteriaBuilder, filterKey, value, "greaterThan");
        }
        return criteriaBuilder.greaterThan(root.get(filterKey), value.toString());
    }
    
    private Predicate handleGreaterThanEqual(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String filterKey, Object value) {
        if (filterKey.contains(".")) {
            return handleJoinField(root, query, criteriaBuilder, filterKey, value, "greaterThanEqual");
        }
        return criteriaBuilder.greaterThanOrEqualTo(root.get(filterKey), value.toString());
    }
    
    private Predicate handleLessThan(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String filterKey, Object value) {
        if (filterKey.contains(".")) {
            return handleJoinField(root, query, criteriaBuilder, filterKey, value, "lessThan");
        }
        return criteriaBuilder.lessThan(root.get(filterKey), value.toString());
    }
    
    private Predicate handleLessThanEqual(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String filterKey, Object value) {
        if (filterKey.contains(".")) {
            return handleJoinField(root, query, criteriaBuilder, filterKey, value, "lessThanEqual");
        }
        return criteriaBuilder.lessThanOrEqualTo(root.get(filterKey), value.toString());
    }
    
    private Predicate handleIn(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String filterKey, Object value) {
        if (filterKey.contains(".")) {
            return handleJoinField(root, query, criteriaBuilder, filterKey, value, "in");
        }
        
        String[] values = value.toString().split(",");
        return root.get(filterKey).in(Arrays.asList(values));
    }
    
    private Predicate handleJoinField(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String filterKey, Object value, String operation) {
        // Para manejar campos como "perfil.sector", "perfil.ubicacion", etc.
        String[] fieldParts = filterKey.split("\\.");
        if (fieldParts.length == 2) {
            String joinField = fieldParts[0]; // ej: "perfil"
            String targetField = fieldParts[1]; // ej: "sector"
            
            Join<Object, Object> join = root.join(joinField, JoinType.LEFT);
            
            switch (operation) {
                case "contains":
                    return criteriaBuilder.like(criteriaBuilder.lower(join.get(targetField).as(String.class)), 
                                              "%" + value.toString().toLowerCase() + "%");
                case "equal":
                    return criteriaBuilder.equal(join.get(targetField), value);
                case "beginsWith":
                    return criteriaBuilder.like(criteriaBuilder.lower(join.get(targetField).as(String.class)), 
                                              value.toString().toLowerCase() + "%");
                case "endsWith":
                    return criteriaBuilder.like(criteriaBuilder.lower(join.get(targetField).as(String.class)), 
                                              "%" + value.toString().toLowerCase());
                case "in":
                    String[] values = value.toString().split(",");
                    return join.get(targetField).in(Arrays.asList(values));
                default:
                    return criteriaBuilder.equal(join.get(targetField), value);
            }
        }
        
        return criteriaBuilder.conjunction();
    }
} 