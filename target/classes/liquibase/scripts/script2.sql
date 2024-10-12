-- liquibase formatted sql

-- changeset denis:1

create TABLE users(
    user_id BIGSERIAL PRIMARY KEY,
    chat_Id BIGINT,
    name TEXT,
    phone TEXT,
    login TEXT
);

-- changeset denis:2

CREATE TABLE files(
    file_id SERIAL PRIMARY KEY,           -- Уникальный идентификатор файла
    user_id INT NOT NULL,                 -- Идентификатор пользователя, который загрузил файл
    file_name VARCHAR(255) NOT NULL,      -- Имя файла
    file_data BYTEA NOT NULL,             -- Бинарные данные файла
    CONSTRAINT fk_user FOREIGN KEY (user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE                     -- Удаление файлов при удалении пользователя
);



