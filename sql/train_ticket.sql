-- 车票数据库
CREATE DATABASE IF NOT EXISTS train_ticket DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE train_ticket;

-- 列车表
CREATE TABLE IF NOT EXISTS t_train (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    train_number VARCHAR(20) NOT NULL COMMENT '车次号',
    train_type VARCHAR(10) COMMENT '列车类型：G-高铁，D-动车，K-快速，T-特快',
    start_station VARCHAR(50) NOT NULL COMMENT '始发站',
    end_station VARCHAR(50) NOT NULL COMMENT '终点站',
    start_time TIME NOT NULL COMMENT '出发时间',
    end_time TIME NOT NULL COMMENT '到达时间',
    total_seats INT DEFAULT 0 COMMENT '总座位数',
    status TINYINT DEFAULT 1 COMMENT '状态：0-停运，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_train_number (train_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='列车表';

-- 车票表
CREATE TABLE IF NOT EXISTS t_ticket (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    train_id BIGINT NOT NULL COMMENT '列车ID',
    train_number VARCHAR(20) NOT NULL COMMENT '车次号',
    start_station VARCHAR(50) NOT NULL COMMENT '出发站',
    end_station VARCHAR(50) NOT NULL COMMENT '到达站',
    travel_date DATE NOT NULL COMMENT '出行日期',
    seat_type VARCHAR(20) NOT NULL COMMENT '座位类型：商务座，一等座，二等座',
    price DECIMAL(10, 2) NOT NULL COMMENT '票价',
    remain_seats INT DEFAULT 0 COMMENT '剩余座位',
    total_seats INT DEFAULT 0 COMMENT '总座位',
    status TINYINT DEFAULT 1 COMMENT '状态：0-停售，1-在售',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_train_date (train_number, travel_date),
    KEY idx_station_date (start_station, end_station, travel_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车票表';

-- 插入测试数据
INSERT INTO t_train (train_number, train_type, start_station, end_station, start_time, end_time, total_seats) VALUES
('G1001', 'G', '北京南', '上海虹桥', '08:00:00', '13:30:00', 1000),
('G1002', 'G', '上海虹桥', '北京南', '08:00:00', '13:30:00', 1000),
('D3001', 'D', '杭州东', '南京南', '09:00:00', '11:30:00', 800),
('K1001', 'K', '广州', '深圳', '10:00:00', '12:00:00', 500);

INSERT INTO t_ticket (train_id, train_number, start_station, end_station, travel_date, seat_type, price, remain_seats, total_seats) VALUES
(1, 'G1001', '北京南', '上海虹桥', '2025-10-20', '商务座', 1748.00, 28, 28),
(1, 'G1001', '北京南', '上海虹桥', '2025-10-20', '一等座', 933.00, 90, 90),
(1, 'G1001', '北京南', '上海虹桥', '2025-10-20', '二等座', 553.00, 882, 882),
(2, 'G1002', '上海虹桥', '北京南', '2025-10-20', '商务座', 1748.00, 28, 28),
(2, 'G1002', '上海虹桥', '北京南', '2025-10-20', '一等座', 933.00, 90, 90),
(2, 'G1002', '上海虹桥', '北京南', '2025-10-20', '二等座', 553.00, 882, 882);
