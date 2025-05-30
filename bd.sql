-- Script de creación de base de datos para Red Profesional
-- Base de datos: PostgreSQL

-- Crear base de datos (ejecutar como superusuario si es necesario)
-- CREATE DATABASE red_profesional;

-- Crear esquema principal
CREATE SCHEMA IF NOT EXISTS red_profesional;

-- Establecer el search_path
SET search_path TO red_profesional, public;

-- Tabla de usuarios (datos de autenticación)
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email_verificado BOOLEAN DEFAULT FALSE,
    token_verificacion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    ultimo_acceso TIMESTAMP,
    tipo_registro VARCHAR(50) DEFAULT 'EMAIL', -- EMAIL, GOOGLE, LINKEDIN
    oauth_id VARCHAR(255),
    oauth_provider VARCHAR(50),
    CONSTRAINT chk_tipo_registro CHECK (tipo_registro IN ('EMAIL', 'GOOGLE', 'LINKEDIN'))
);

-- Índices para búsquedas frecuentes
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_token_verificacion ON usuarios(token_verificacion);

-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Insertar roles básicos
INSERT INTO roles (nombre, descripcion) VALUES 
    ('ROLE_USER', 'Usuario estándar'),
    ('ROLE_EMPRESA', 'Usuario empresa'),
    ('ROLE_PREMIUM', 'Usuario premium'),
    ('ROLE_ADMIN', 'Administrador del sistema');

-- Tabla de relación usuarios-roles
CREATE TABLE IF NOT EXISTS usuarios_roles (
    usuario_id BIGINT NOT NULL,
    rol_id INTEGER NOT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Tabla de perfiles profesionales (datos públicos del usuario)
CREATE TABLE IF NOT EXISTS perfiles (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    foto_url VARCHAR(500),
    titular VARCHAR(255),
    extracto TEXT,
    ubicacion VARCHAR(255),
    sector VARCHAR(100),
    sitio_web VARCHAR(255),
    telefono VARCHAR(20),
    fecha_nacimiento DATE,
    visibilidad_perfil VARCHAR(50) DEFAULT 'PUBLICO',
    url_perfil VARCHAR(255) UNIQUE,
    vistas_perfil INTEGER DEFAULT 0,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT chk_visibilidad CHECK (visibilidad_perfil IN ('PUBLICO', 'CONEXIONES', 'PRIVADO'))
);

-- Tabla de tokens de autenticación (para JWT o sesiones)
CREATE TABLE IF NOT EXISTS tokens_autenticacion (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    tipo_token VARCHAR(50) NOT NULL, -- ACCESS, REFRESH, RESET_PASSWORD
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    ip_address VARCHAR(45),
    user_agent TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Índices para tokens
CREATE INDEX idx_tokens_usuario ON tokens_autenticacion(usuario_id);
CREATE INDEX idx_tokens_token ON tokens_autenticacion(token);
CREATE INDEX idx_tokens_expiracion ON tokens_autenticacion(fecha_expiracion);

-- Tabla de intentos de login (seguridad)
CREATE TABLE IF NOT EXISTS intentos_login (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255),
    ip_address VARCHAR(45),
    fecha_intento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exitoso BOOLEAN DEFAULT FALSE,
    mensaje_error VARCHAR(255)
);

-- Índice para limpiar intentos antiguos
CREATE INDEX idx_intentos_fecha ON intentos_login(fecha_intento);

-- Función para actualizar fecha de actualización automáticamente
CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para actualizar fecha en perfiles
CREATE TRIGGER trigger_actualizar_perfil
    BEFORE UPDATE ON perfiles
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

-- Vista para obtener información completa del usuario
CREATE OR REPLACE VIEW vista_usuarios_completo AS
SELECT 
    u.id,
    u.email,
    u.nombre,
    u.apellido,
    u.fecha_registro,
    u.email_verificado,
    u.activo,
    u.ultimo_acceso,
    u.tipo_registro,
    p.foto_url,
    p.titular,
    p.ubicacion,
    p.url_perfil,
    array_agg(r.nombre) AS roles
FROM usuarios u
LEFT JOIN perfiles p ON u.id = p.usuario_id
LEFT JOIN usuarios_roles ur ON u.id = ur.usuario_id
LEFT JOIN roles r ON ur.rol_id = r.id
GROUP BY u.id, u.email, u.nombre, u.apellido, u.fecha_registro, 
         u.email_verificado, u.activo, u.ultimo_acceso, u.tipo_registro,
         p.foto_url, p.titular, p.ubicacion, p.url_perfil;

-- Procedimiento para limpiar tokens expirados
CREATE OR REPLACE PROCEDURE limpiar_tokens_expirados()
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM tokens_autenticacion 
    WHERE fecha_expiracion < CURRENT_TIMESTAMP OR activo = FALSE;
    
    DELETE FROM intentos_login 
    WHERE fecha_intento < CURRENT_TIMESTAMP - INTERVAL '30 days';
END;
$$;

-- Comentarios en las tablas
COMMENT ON TABLE usuarios IS 'Tabla principal de usuarios con datos de autenticación';
COMMENT ON TABLE perfiles IS 'Información pública y profesional de los usuarios';
COMMENT ON TABLE roles IS 'Roles del sistema para control de acceso';
COMMENT ON TABLE tokens_autenticacion IS 'Tokens JWT y de sesión para autenticación';
COMMENT ON TABLE intentos_login IS 'Registro de intentos de login para seguridad';
