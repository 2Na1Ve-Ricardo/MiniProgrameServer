CREATE TABLE sys_user
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    username     VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NULL,
    password     VARCHAR(255) NOT NULL,
    app_id       VARCHAR(255) NULL,
    phone        VARCHAR(255) NULL,
    company      VARCHAR(255) NULL,
    CONSTRAINT pk_sys_user PRIMARY KEY (id)
);

ALTER TABLE sys_user
    ADD CONSTRAINT uc_sys_user_app UNIQUE (app_id);

ALTER TABLE sys_user
    ADD CONSTRAINT uc_sys_user_username UNIQUE (username);