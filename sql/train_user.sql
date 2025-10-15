-- 用户数据库
CREATE DATABASE IF NOT EXISTS train_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE train_user;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    user_type TINYINT DEFAULT 0 COMMENT '用户类型：0-普通用户，1-VIP用户',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试数据
INSERT INTO t_user (username, password, real_name, id_card, phone, email) VALUES
('test001', '098f6bcd4621d373cade4e832627b4f6', '张三', '110101199001011234', '13800138000', 'zhangsan@example.com'),
('test002', '098f6bcd4621d373cade4e832627b4f6', '李四', '110101199002021234', '13800138001', 'lisi@example.com');
