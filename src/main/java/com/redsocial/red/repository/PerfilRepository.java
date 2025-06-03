package com.redsocial.red.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.redsocial.red.entity.Perfil;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    /**
     * Buscar perfil por ID de usuario
     */
    Optional<Perfil> findByUsuarioId(Long usuarioId);

    /**
     * Buscar perfil por URL personalizada
     */
    Optional<Perfil> findByUrlPerfil(String urlPerfil);

    /**
     * Verificar si existe un perfil para un usuario
     */
    boolean existsByUsuarioId(Long usuarioId);

    /**
     * Incrementar vistas del perfil
     */
    @Modifying
    @Query("UPDATE Perfil p SET p.vistasPerfil = p.vistasPerfil + 1 WHERE p.id = :perfilId")
    void incrementarVistas(@Param("perfilId") Long perfilId);

    /**
     * Verificar si una URL de perfil está disponible
     */
    boolean existsByUrlPerfil(String urlPerfil);

    /**
     * Obtener perfiles públicos por sector
     */
    @Query("SELECT p FROM Perfil p WHERE p.visibilidadPerfil = 'PUBLICO' AND p.sector = :sector")
    Optional<Perfil> findPublicosBySector(@Param("sector") String sector);
} 