package com.redsocial.red.dto;

public class SearchCriteria {
    
    private String filterKey;
    private String operation;
    private Object value;
    private String dataOption;

    public SearchCriteria() {}

    public SearchCriteria(String filterKey, String operation, Object value) {
        this.filterKey = filterKey;
        this.operation = operation;
        this.value = value;
    }

    public SearchCriteria(String filterKey, String operation, Object value, String dataOption) {
        this.filterKey = filterKey;
        this.operation = operation;
        this.value = value;
        this.dataOption = dataOption;
    }

    // Getters y Setters
    public String getFilterKey() {
        return filterKey;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDataOption() {
        return dataOption;
    }

    public void setDataOption(String dataOption) {
        this.dataOption = dataOption;
    }
} 