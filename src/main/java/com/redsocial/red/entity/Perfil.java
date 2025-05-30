package com.redsocial.red.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "perfiles", schema = "red_profesional")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(length = 255)
    private String titular;

    @Column(columnDefinition = "TEXT")
    private String extracto;

    @Column(length = 255)
    private String ubicacion;

    @Column(length = 100)
    private String sector;

    @Column(name = "sitio_web", length = 255)
    private String sitioWeb;

    @Column(length = 20)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "visibilidad_perfil", length = 50)
    @Enumerated(EnumType.STRING)
    private VisibilidadPerfil visibilidadPerfil = VisibilidadPerfil.PUBLICO;

    @Column(name = "url_perfil", unique = true)
    private String urlPerfil;

    @Column(name = "vistas_perfil")
    private Integer vistasPerfil = 0;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    public enum VisibilidadPerfil {
        PUBLICO, CONEXIONES, PRIVADO
    }

    // Constructors
    public Perfil() {
    }

    public Perfil(Usuario usuario) {
        this.usuario = usuario;
        this.urlPerfil = generarUrlPerfil(usuario);
    }

    // Helper method
    private String generarUrlPerfil(Usuario usuario) {
        return usuario.getNombre().toLowerCase().replace(" ", "-") +
                "-" + usuario.getApellido().toLowerCase().replace(" ", "-") +
                "-" + usuario.getId();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public VisibilidadPerfil getVisibilidadPerfil() {
        return visibilidadPerfil;
    }

    public void setVisibilidadPerfil(VisibilidadPerfil visibilidadPerfil) {
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
}