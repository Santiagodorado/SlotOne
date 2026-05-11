CREATE TABLE rol (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(30) NOT NULL,
    clave VARCHAR(255) NOT NULL,
    tipo_identificacion VARCHAR(50),
    num_identificacion VARCHAR(50),
    id_rol INT REFERENCES rol(id)
);

INSERT INTO rol (nombre) VALUES ('CLIENT'), ('BUSINESS'), ('PLATFORM_ADMIN');

INSERT INTO usuario (nombres, apellidos, correo, telefono, clave, tipo_identificacion, num_identificacion, id_rol) VALUES
('User', 'Negocio', 'negocio@unicauca.edu.co', '3000000001', '$2b$10$Vc2DgXv3m6Qni3k4kSxrMOoqJuCU1mbqIaeuP2/rFgSGJ7c8XKzVO', 'CE', '10000001', 2),
('Ana', 'Diaz', 'ana.diaz@unicauca.edu.co', '3000000002', '$2b$10$6xvjc2cFQJ.8ct57xs0yxeTSER6VLr6E0luUm7kus0Yuw4ilmOOk2', 'CE', '10000002', 2),
('Carlos', 'Ramirez', 'carlos.ramirez@unicauca.edu.co', '3000000003', '$2b$10$RF3I74grseSn9xXNc4/qSOHo2wy4ncos4nk7BIp9peuNRmek3vIiu', 'CC', '10000003', 2),
('Maria', 'Lopez', 'maria.lopez@unicauca.edu.co', '3000000004', '$2b$10$1lfcQnlzZcy9NVVXvkCrd.gFORtghwqd4X3fpxwpjQ8I2LEZBveFW', 'CC', '10000004', 2),
('Roberto', 'Martinez', 'roberto.martinez@unicauca.edu.co', '3000000005', '$2b$10$MuomgrE802F05viZMvyjTOzZQmmRjYl4Q5lL2MeafaOv73jD6wzGa', 'CE', '10000005', 2),
('User', 'Cliente', 'cliente@unicauca.edu.co', '3100000006', '$2b$10$Vc2DgXv3m6Qni3k4kSxrMOoqJuCU1mbqIaeuP2/rFgSGJ7c8XKzVO', 'CC', '10000006', 1),
('Juan', 'Martinez', 'juan.martinez@unicauca.edu.co', '3100000007', '$2b$10$iBtPXYzY.q74qv/xKrVuwuRyfeKKf5Ce0wV3JGYk2prG4SSpDLmhK', 'CC', '10000007', 1),
('Sofia', 'Garcia', 'sofia.garcia@unicauca.edu.co', '3100000008', '$2b$10$w1LNTLVkPh5cFpSXelV18ukPToiXf0Fp7DkVXEin/.upX65pK2r7e', 'CE', '10000008', 1),
('Daniel', 'Perez', 'daniel.perez@unicauca.edu.co', '3100000009', '$2b$10$cVyysghQqR046VywRMYkUu/u8Cea2lRKADZxE9mbKJvzYuszcOoXq', 'CC', '10000009', 1),
('Valentina', 'Sanchez', 'valentina.sanchez@unicauca.edu.co', '3100000010', '$2b$10$yr1RJCnV4Y6l8/1eTmWueOEb74r87q0n2R15mmyvjLFru5I0EuQjO', 'CE', '10000010', 1),
('Andres', 'Torres', 'andres.torres@unicauca.edu.co', '3100000011', '$2b$10$BdBzxwF65Ley63sUlQkQKepRgAORNe.O7l9w2oUZBn/UbFBU2tb7C', 'CC', '10000011', 1),
('Camila', 'Diaz', 'camila.diaz@unicauca.edu.co', '3100000012', '$2b$10$3fyxxNOXirwxUYo5PqNJLeehLGkzZ5tZQKnnBpbH9spnC2zCZxc/e', 'CE', '10000012', 1),
('David', 'Gonzalez', 'david.gonzalez@unicauca.edu.co', '3100000013', '$2b$10$AjNRmhmIlFafvdpVIOqofeM1m/LC2uSCi8Eyi5l0P6Q/emO2ASv/C', 'CC', '10000013', 1),
('Isabella', 'Herrera', 'isabella.herrera@unicauca.edu.co', '3100000014', '$2b$10$pmSdoxV24ciYAjIlKe4iu..OYCBJwMdT12lUgre5HzWEmO8UAuSSK', 'CE', '10000014', 1),
('Sebastian', 'Rojas', 'sebastian.rojas@unicauca.edu.co', '3100000015', '$2b$10$Hgl0kbDf5Ox5YzuJSrML1u/S1ePrjIU4dIXgddRsGwhjO.MzjUWW2', 'CC', '10000015', 1),
('Juan', 'Lopez', 'juan.lopez@unicauca.edu.co', '3200000016', '$2b$10$MuomgrE802F05viZMvyjTOzZQmmRjYl4Q5lL2MeafaOv73jD6wzGa', 'CE', '10000016', 3),
('User', 'Admin', 'admin@unicauca.edu.co', '3200000017', '$2b$10$Vc2DgXv3m6Qni3k4kSxrMOoqJuCU1mbqIaeuP2/rFgSGJ7c8XKzVO', 'CE', '10000017', 3)
ON CONFLICT (correo) DO NOTHING;
