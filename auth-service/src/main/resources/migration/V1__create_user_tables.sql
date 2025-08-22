-- User
CREATE TABLE users (
       id BIGSERIAL PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       email VARCHAR(255) UNIQUE NOT NULL,
       phone VARCHAR(255) NOT NULL,
       password VARCHAR(255) NOT NULL,
       status VARCHAR(20),

       created_at DATE
);

CREATE INDEX idx_user_name ON users(name);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_phone ON users(phone);
CREATE INDEX idx_user_password ON users(password);