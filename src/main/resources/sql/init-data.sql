
-- Insertar roles iniciales
INSERT INTO rol (name, description) VALUES 
('ADMIN', 'Administrador - Acceso completo a todo el sistema'),
('AUX_BODEGA', 'Auxiliar de Bodega - Gestión de inventario, entrada y salida de productos'),
('VENDEDOR', 'Vendedor - Gestión de ventas y consulta de productos'),
('GERENTE', 'Gerente - Reportes, estadísticas y supervisión general')
ON CONFLICT (name) DO NOTHING;

-- Insertar usuario admin (:  - cambiar en producción)
-- La contraseña está hasheada con BCrypt
INSERT INTO usuario (email, password, active, date_create) VALUES 
('', '', true, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- Asignar rol ADMIN al usuario admin
INSERT INTO usuario_rol (usuario_id, rol_id, fecha_asignacion)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM usuario u, rol r
WHERE u.email = 'admin@gestioninventarios.com' AND r.name = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM usuario_rol ur 
    WHERE ur.usuario_id = u.id AND ur.rol_id = r.id
);
