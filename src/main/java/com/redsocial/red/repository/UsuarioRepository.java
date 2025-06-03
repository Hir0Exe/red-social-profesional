package com.redsocial.red.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.redsocial.red.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<Usuario> findByTokenVerificacion(String token);

    Optional<Usuario> findByEmailAndEmailVerificadoTrue(String email);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<Usuario> findByEmailWithRoles(@Param("email") String email);

    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoAcceso = :fecha WHERE u.id = :id")
    void actualizarUltimoAcceso(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.activo = true")
    Long countUsuariosActivos();

    @Query("SELECT DISTINCT p.sector FROM Perfil p WHERE p.sector IS NOT NULL AND p.sector != '' ORDER BY p.sector")
    List<String> findDistinctSectores();

    @Query("SELECT DISTINCT p.ubicacion FROM Perfil p WHERE p.ubicacion IS NOT NULL AND p.ubicacion != '' ORDER BY p.ubicacion")
    List<String> findDistinctUbicaciones();
}