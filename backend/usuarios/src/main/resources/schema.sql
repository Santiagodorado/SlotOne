CREATE TABLE rol (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    clave VARCHAR(100) NOT NULL,
    tipoIdentificacion VARCHAR(50),
    numIdentificacion VARCHAR(50),
    idRol INT,
    FOREIGN KEY (idRol) REFERENCES rol(id)
);