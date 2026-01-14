CREATE TABLE tb_card (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         balance NUMERIC(38,2) NOT NULL,
                         card_number VARCHAR(255) NOT NULL UNIQUE,
                         password VARCHAR(255)
);

CREATE TABLE tb_user (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         password VARCHAR(255) NOT NULL
);

CREATE TABLE tb_role (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         authority VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE tb_user_role (
                              user_id BIGINT NOT NULL,
                              role_id BIGINT NOT NULL,
                              PRIMARY KEY (user_id, role_id),
                              FOREIGN KEY (user_id) REFERENCES tb_user(id),
                              FOREIGN KEY (role_id) REFERENCES tb_role(id)
);

INSERT INTO tb_user (name, email, password) VALUES ('Maria Brown', 'maria@gmail.com', '$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO');

INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 2);
