-- 角色表
CREATE TABLE sys_role (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
                          role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
                          role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
                          description VARCHAR(200) COMMENT '角色描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 初始化角色数据
INSERT INTO sys_role (id, role_code, role_name, description) VALUES
                                                                 (1, 'ROLE_USER', '普通用户', '普通用户，基础功能权限'),
                                                                 (2, 'ROLE_ADMIN', '管理员', '管理员，可管理用户和内容'),
                                                                 (3, 'ROLE_SUPER_ADMIN', '超级管理员', '超级管理员，拥有所有权限');

-- 用户表添加角色字段
ALTER TABLE sys_user ADD COLUMN role_id BIGINT NOT NULL DEFAULT 1 COMMENT '角色ID';
ALTER TABLE sys_user ADD INDEX idx_role_id (role_id);