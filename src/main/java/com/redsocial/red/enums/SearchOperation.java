package com.redsocial.red.enums;

public enum SearchOperation {
    
    CONTAINS("contains"),
    DOES_NOT_CONTAIN("doesNotContain"),
    EQUAL("eq"),
    NOT_EQUAL("ne"),
    BEGINS_WITH("bw"),
    ENDS_WITH("ew"),
    GREATER_THAN("gt"),
    GREATER_THAN_EQUAL("gte"),
    LESS_THAN("lt"),
    LESS_THAN_EQUAL("lte"),
    IN("in"),
    NOT_IN("nin"),
    
    // Opciones de datos
    ALL("all"),
    ANY("any");
    
    private final String operation;
    
    SearchOperation(String operation) {
        this.operation = operation;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public static SearchOperation getDataOption(String dataOption) {
        switch (dataOption) {
            case "all":
                return ALL;
            case "any":
                return ANY;
            default:
                return ALL;
        }
    }
    
    public static SearchOperation getOperation(String operation) {
        for (SearchOperation op : SearchOperation.values()) {
            if (op.getOperation().equals(operation)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Operación de búsqueda no válida: " + operation);
    }
} 