package com.redsocial.red.dto;

import java.util.List;

public class UsuarioSearchDto {
    
    private String dataOption; // "all" o "any"
    private List<SearchCriteria> searchCriteriaList;
    private String simpleSearch; // Para búsqueda simple

    public UsuarioSearchDto() {}

    public UsuarioSearchDto(String dataOption, List<SearchCriteria> searchCriteriaList) {
        this.dataOption = dataOption;
        this.searchCriteriaList = searchCriteriaList;
    }

    // Getters y Setters
    public String getDataOption() {
        return dataOption;
    }

    public void setDataOption(String dataOption) {
        this.dataOption = dataOption;
    }

    public List<SearchCriteria> getSearchCriteriaList() {
        return searchCriteriaList;
    }

    public void setSearchCriteriaList(List<SearchCriteria> searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    public String getSimpleSearch() {
        return simpleSearch;
    }

    public void setSimpleSearch(String simpleSearch) {
        this.simpleSearch = simpleSearch;
    }
} 