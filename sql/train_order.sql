-- 订单数据库
CREATE DATABASE IF NOT EXISTS train_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE train_order;

-- 订单表
CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    ticket_id BIGINT NOT NULL COMMENT '车票ID',
    train_number VARCHAR(20) COMMENT '车次号',
    start_station VARCHAR(50) COMMENT '出发站',
    end_station VARCHAR(50) COMMENT '到达站',
    travel_date DATE COMMENT '出行日期',
    seat_type VARCHAR(20) COMMENT '座位类型',
    seat_count INT DEFAULT 1 COMMENT '座位数量',
    total_price DECIMAL(10, 2) COMMENT '总价',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待支付，1-已支付，2-已取消，3-已完成',
    pay_time DATETIME COMMENT '支付时间',
    cancel_time DATETIME COMMENT '取消时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_ticket_id (ticket_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
