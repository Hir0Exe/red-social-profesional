package com.redsocial.red.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ActualizarPerfilRequest {

    @Size(max = 255, message = "El titular no puede exceder 255 caracteres")
    private String titular;

    @Size(max = 1000, message = "El extracto no puede exceder 1000 caracteres")
    private String extracto;

    @Size(max = 255, message = "La ubicación no puede exceder 255 caracteres")
    private String ubicacion;

    @Size(max = 100, message = "El sector no puede exceder 100 caracteres")
    private String sector;

    @Size(max = 255, message = "El sitio web no puede exceder 255 caracteres")
    @Pattern(regexp = "^(https?://).*", message = "El sitio web debe comenzar con http:// o https://")
    private String sitioWeb;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]+$", message = "El formato del teléfono no es válido")
    private String telefono;

    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private LocalDate fechaNacimiento;

    @Pattern(regexp = "^(PUBLICO|CONEXIONES|PRIVADO)$", message = "Visibilidad debe ser PUBLICO, CONEXIONES o PRIVADO")
    private String visibilidadPerfil = "PUBLICO";

    @Size(max = 500, message = "La URL de foto no puede exceder 500 caracteres")
    private String fotoUrl;

    // Constructor vacío
    public ActualizarPerfilRequest() {}

    // Getters y Setters
    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getExtracto() {
        return extracto;
    }

    public void setExtracto(String extracto) {
        this.extracto = extracto;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getVisibilidadPerfil() {
        return visibilidadPerfil;
    }

    public void setVisibilidadPerfil(String visibilidadPerfil) {
        this.visibilidadPerfil = visibilidadPerfil;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
} 