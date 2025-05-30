-- Verificar usuarios existentes en la base de datos
\c red_profesional

-- Ver todos los usuarios registrados
SELECT 
    id,
    email,
    nombre,
    apellido,
    fecha_registro,
    email_verificado,
    activo,
    ultimo_acceso
FROM red_profesional.usuarios 
ORDER BY fecha_registro DESC;

-- Ver roles disponibles
SELECT * FROM red_profesional.roles;

-- Ver relación usuarios-roles
SELECT 
    u.id,
    u.email,
    u.nombre,
    r.nombre as rol
FROM red_profesional.usuarios u
JOIN red_profesional.usuarios_roles ur ON u.id = ur.usuario_id
JOIN red_profesional.roles r ON ur.rol_id = r.id;

-- Verificar si existen usuarios sin roles asignados
SELECT 
    u.id,
    u.email,
    u.nombre
FROM red_profesional.usuarios u
LEFT JOIN red_profesional.usuarios_roles ur ON u.id = ur.usuario_id
WHERE ur.usuario_id IS NULL; 