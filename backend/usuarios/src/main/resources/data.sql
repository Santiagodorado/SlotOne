INSERT INTO rol (nombre) VALUES 
('CLIENT'), 
('BUSINESS'),
('PLATFORM_ADMIN');

INSERT INTO usuario (nombres, apellidos, correo, clave, tipoIdentificacion, numIdentificacion, idRol) VALUES 
-- BUSINESS (5)
('User', 'Negocio', 'negocio@unicauca.edu.co', 'password123', 'CE', '10000001', 2),
('Ana', 'Diaz', 'ana.diaz@unicauca.edu.co', 'password456', 'CE', '10000002', 2),
('Carlos', 'Ramirez', 'carlos.ramirez@unicauca.edu.co', 'password789', 'CC', '10000003', 2),
('Maria', 'Lopez', 'maria.lopez@unicauca.edu.co', 'password101', 'CC', '10000004', 2),
('Roberto', 'Martinez', 'roberto.martinez@unicauca.edu.co', 'password202', 'CE', '10000005', 2),

-- CLIENT (10)
('User', 'Cliente', 'cliente@unicauca.edu.co', 'password123', 'CC', '10000006', 1),
('Juan', 'Martinez', 'juan.martinez@unicauca.edu.co', 'password222', 'CC', '10000007', 1),
('Sofia', 'Garcia', 'sofia.garcia@unicauca.edu.co', 'password333', 'CE', '10000008', 1),
('Daniel', 'Perez', 'daniel.perez@unicauca.edu.co', 'password444', 'CC', '10000009', 1),
('Valentina', 'Sanchez', 'valentina.sanchez@unicauca.edu.co', 'password555', 'CE', '10000010', 1),
('Andres', 'Torres', 'andres.torres@unicauca.edu.co', 'password666', 'CC', '10000011', 1),
('Camila', 'Diaz', 'camila.diaz@unicauca.edu.co', 'password777', 'CE', '10000012', 1),
('David', 'Gonzalez', 'david.gonzalez@unicauca.edu.co', 'password888', 'CC', '10000013', 1),
('Isabella', 'Herrera', 'isabella.herrera@unicauca.edu.co', 'password999', 'CE', '10000014', 1),
('Sebastian', 'Rojas', 'sebastian.rojas@unicauca.edu.co', 'password000', 'CC', '10000015', 1),

-- PLATFORM_ADMIN (2)
('Juan', 'Lopez', 'juan.lopez@unicauca.edu.co', 'password202', 'CE', '10000016', 3),
('User', 'Admin', 'admin@unicauca.edu.co', 'password123', 'CE', '10000017', 3);