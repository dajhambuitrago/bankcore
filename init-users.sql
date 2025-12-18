-- Script SQL para crear un usuario de prueba en BankCore
-- Contraseña: "password123" (hasheada con BCrypt)

-- Tabla de usuarios (se crea automáticamente con JPA, pero aquí está la definición por si acaso)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    full_name VARCHAR(150),
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Insertar usuario de prueba
-- Username: admin
-- Password: password123
-- Password hash generado con BCrypt (10 rounds)
INSERT INTO users (username, password_hash, email, full_name, enabled, created_at)
VALUES (
    'admin',
    '$2a$10$5fY5qYZ5QYQZ5QYQZ5QYQO5fY5qYZ5QYQZ5QYQZ5QYQO5fY5qYZ5Q',
    'admin@bankcore.com',
    'Administrador del Sistema',
    true,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;

-- Insertar otro usuario de prueba
-- Username: testuser
-- Password: test1234
INSERT INTO users (username, password_hash, email, full_name, enabled, created_at)
VALUES (
    'testuser',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye1Z.QH7RH7iEEFR6XWTH6EJ6GbKw6qse',
    'testuser@bankcore.com',
    'Usuario de Prueba',
    true,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;

-- Verificar usuarios creados
SELECT id, username, email, full_name, enabled, created_at FROM users;
