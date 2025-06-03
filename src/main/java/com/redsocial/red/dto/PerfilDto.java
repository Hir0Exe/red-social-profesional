package com.redsocial.red.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.redsocial.red.entity.Perfil;

public class PerfilDto {
    
    private Long id;
    private Long usuarioId;
    private String nombreCompleto;
    private String email;
    private String fotoUrl;
    private String titular;
    private String extracto;
    private String ubicacion;
    private String sector;
    private String sitioWeb;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String visibilidadPerfil;
    private String urlPerfil;
    private Integer vistasPerfil;
    private LocalDateTime fechaActualizacion;
    private boolean esPropietario;

    // Constructor vacío
    public PerfilDto() {}

    // Constructor desde entidad
    public PerfilDto(Perfil perfil, boolean esPropietario) {
        this.id = perfil.getId();
        this.usuarioId = perfil.getUsuario().getId();
        this.nombreCompleto = perfil.getUsuario().getNombreCompleto();
        this.email = perfil.getUsuario().getEmail();
        this.fotoUrl = perfil.getFotoUrl();
        this.titular = perfil.getTitular();
        this.extracto = perfil.getExtracto();
        this.ubicacion = perfil.getUbicacion();
        this.sector = perfil.getSector();
        this.sitioWeb = perfil.getSitioWeb();
        this.telefono = perfil.getTelefono();
        this.fechaNacimiento = perfil.getFechaNacimiento();
        this.visibilidadPerfil = perfil.getVisibilidadPerfil() != null ? 
            perfil.getVisibilidadPerfil().name() : "PUBLICO";
        this.urlPerfil = perfil.getUrlPerfil();
        this.vistasPerfil = perfil.getVistasPerfil();
        this.fechaActualizacion = perfil.getFechaActualizacion();
        this.esPropietario = esPropietario;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

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

    public String getUrlPerfil() {
        return urlPerfil;
    }

    public void setUrlPerfil(String urlPerfil) {
        this.urlPerfil = urlPerfil;
    }

    public Integer getVistasPerfil() {
        return vistasPerfil;
    }

    public void setVistasPerfil(Integer vistasPerfil) {
        this.vistasPerfil = vistasPerfil;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public boolean isEsPropietario() {
        return esPropietario;
    }

    public void setEsPropietario(boolean esPropietario) {
        this.esPropietario = esPropietario;
    }
} 