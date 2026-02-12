ALTER TABLE `sys_user`
    ADD COLUMN `page_permissions` JSON NOT NULL
        DEFAULT (JSON_ARRAY());