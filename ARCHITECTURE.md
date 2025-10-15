# 系统架构文档

## 目录
1. [系统概述](#系统概述)
2. [架构设计](#架构设计)
3. [模块说明](#模块说明)
4. [数据库设计](#数据库设计)
5. [接口设计](#接口设计)
6. [技术选型](#技术选型)
7. [性能优化](#性能优化)
8. [安全设计](#安全设计)

---

## 系统概述

### 项目背景
本项目是一个仿12306的火车票订票系统，采用微服务架构设计，实现了用户管理、车票查询、订单管理、支付处理等核心功能。

### 系统特点
- **微服务架构**: 服务独立部署，松耦合
- **分布式系统**: 支持水平扩展，高可用
- **统一网关**: API Gateway统一入口
- **服务注册**: Nacos实现服务发现
- **数据隔离**: 每个服务独立数据库

### 业务流程
```
用户注册/登录 → 查询车票 → 创建订单 → 支付订单 → 出票成功
```

---

## 架构设计

### 系统架构图

```
                           ┌─────────────────┐
                           │   用户/客户端    │
                           └────────┬────────┘
                                    │
                         ┌──────────▼──────────┐
                         │   API Gateway       │
                         │   (Spring Cloud     │
                         │    Gateway)         │
                         └──────────┬──────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
         ┌──────────▼────────┐  ┌──▼────────┐  ┌──▼─────────┐
         │  Service Registry │  │  Config   │  │  Monitor   │
         │     (Nacos)       │  │  Center   │  │            │
         └───────────────────┘  └───────────┘  └────────────┘
                    │
      ┌─────────────┼─────────────┬─────────────┬─────────────┐
      │             │             │             │             │
┌─────▼──────┐ ┌───▼──────┐ ┌────▼─────┐ ┌────▼─────┐ ┌─────▼──────┐
│   User     │ │  Ticket  │ │  Order   │ │ Payment  │ │   Admin    │
│  Service   │ │ Service  │ │ Service  │ │ Service  │ │  Service   │
└─────┬──────┘ └───┬──────┘ └────┬─────┘ └────┬─────┘ └─────┬──────┘
      │            │              │            │              │
┌─────▼──────┐ ┌───▼──────┐ ┌────▼─────┐ ┌────▼─────┐ ┌─────▼──────┐
│   MySQL    │ │  MySQL   │ │  MySQL   │ │  Redis   │ │   MySQL    │
│ (user_db)  │ │(ticket)  │ │ (order)  │ │  Cache   │ │ (admin_db) │
└────────────┘ └──────────┘ └──────────┘ └──────────┘ └────────────┘
```

### 微服务划分

#### 1. 网关服务 (train-gateway)
- **职责**: 统一入口，路由转发，负载均衡
- **端口**: 8000
- **技术**: Spring Cloud Gateway

#### 2. 用户服务 (train-user)
- **职责**: 用户注册、登录、信息管理
- **端口**: 8001
- **数据库**: train_user

#### 3. 车票服务 (train-ticket)
- **职责**: 车次管理、车票查询、余票管理
- **端口**: 8002
- **数据库**: train_ticket

#### 4. 订单服务 (train-order)
- **职责**: 订单创建、支付、取消、查询
- **端口**: 8003
- **数据库**: train_order

#### 5. 支付服务 (train-payment)
- **职责**: 支付处理、退款处理
- **端口**: 8004
- **存储**: Redis

---

## 模块说明

### 公共模块 (train-common)

#### 目录结构
```
train-common/
├── entity/           # 基础实体类
│   └── BaseEntity.java
├── exception/        # 异常处理
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── result/           # 统一返回结果
│   ├── Result.java
│   └── ResultCode.java
├── utils/            # 工具类
└── constant/         # 常量定义
```

#### 核心类说明

**BaseEntity**
```java
- id: Long                    // 主键
- createTime: LocalDateTime   // 创建时间
- updateTime: LocalDateTime   // 更新时间
- createBy: String           // 创建人
- updateBy: String           // 更新人
- delFlag: Integer           // 删除标识
```

**Result<T>**
```java
- code: Integer      // 状态码
- message: String    // 提示信息
- data: T           // 返回数据
```

### 用户服务 (train-user)

#### 核心功能
1. 用户注册 (密码MD5加密)
2. 用户登录 (返回Token)
3. 用户信息查询
4. 用户信息修改

#### 数据模型
**User**
- username: 用户名（唯一）
- password: 密码（MD5）
- realName: 真实姓名
- idCard: 身份证号
- phone: 手机号
- email: 邮箱
- userType: 用户类型（普通/VIP）
- status: 状态（正常/禁用）

### 车票服务 (train-ticket)

#### 核心功能
1. 车次信息管理
2. 车票查询（按站点、日期）
3. 余票查询
4. 座位锁定/释放

#### 数据模型
**Train**
- trainNumber: 车次号
- trainType: 列车类型（G/D/K/T）
- startStation: 始发站
- endStation: 终点站
- startTime: 发车时间
- endTime: 到达时间

**Ticket**
- trainId: 列车ID
- seatType: 座位类型（商务座/一等座/二等座）
- price: 票价
- remainSeats: 剩余座位
- totalSeats: 总座位数

### 订单服务 (train-order)

#### 核心功能
1. 创建订单
2. 订单支付
3. 订单取消
4. 订单查询

#### 订单状态流转
```
待支付(0) → 已支付(1) → 已完成(3)
    ↓
已取消(2)
```

#### 数据模型
**Order**
- orderNo: 订单号（唯一）
- userId: 用户ID
- ticketId: 车票ID
- trainNumber: 车次号
- seatCount: 座位数量
- totalPrice: 总价
- status: 订单状态

### 支付服务 (train-payment)

#### 核心功能
1. 创建支付订单（模拟）
2. 查询支付状态
3. 支付回调处理

---

## 数据库设计

### 用户数据库 (train_user)

#### t_user - 用户表
| 字段名 | 类型 | 说明 | 索引 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY |
| username | VARCHAR(50) | 用户名 | UNIQUE |
| password | VARCHAR(100) | 密码(MD5) | - |
| real_name | VARCHAR(50) | 真实姓名 | - |
| id_card | VARCHAR(18) | 身份证号 | - |
| phone | VARCHAR(20) | 手机号 | INDEX |
| email | VARCHAR(100) | 邮箱 | - |
| user_type | TINYINT | 用户类型 | - |
| status | TINYINT | 状态 | - |

### 车票数据库 (train_ticket)

#### t_train - 列车表
| 字段名 | 类型 | 说明 | 索引 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY |
| train_number | VARCHAR(20) | 车次号 | UNIQUE |
| train_type | VARCHAR(10) | 列车类型 | - |
| start_station | VARCHAR(50) | 始发站 | - |
| end_station | VARCHAR(50) | 终点站 | - |
| start_time | TIME | 发车时间 | - |
| end_time | TIME | 到达时间 | - |

#### t_ticket - 车票表
| 字段名 | 类型 | 说明 | 索引 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY |
| train_id | BIGINT | 列车ID | - |
| train_number | VARCHAR(20) | 车次号 | INDEX |
| start_station | VARCHAR(50) | 出发站 | INDEX |
| end_station | VARCHAR(50) | 到达站 | INDEX |
| travel_date | DATE | 出行日期 | INDEX |
| seat_type | VARCHAR(20) | 座位类型 | - |
| price | DECIMAL(10,2) | 票价 | - |
| remain_seats | INT | 剩余座位 | - |

### 订单数据库 (train_order)

#### t_order - 订单表
| 字段名 | 类型 | 说明 | 索引 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY |
| order_no | VARCHAR(50) | 订单号 | UNIQUE |
| user_id | BIGINT | 用户ID | INDEX |
| ticket_id | BIGINT | 车票ID | INDEX |
| train_number | VARCHAR(20) | 车次号 | - |
| seat_count | INT | 座位数量 | - |
| total_price | DECIMAL(10,2) | 总价 | - |
| status | TINYINT | 订单状态 | - |
| pay_time | DATETIME | 支付时间 | - |

---

## 接口设计

### RESTful API 规范

#### 请求格式
```http
POST /api/user/register
Content-Type: application/json

{
  "username": "test001",
  "password": "test123"
}
```

#### 响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "test001"
  }
}
```

### 状态码规范

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 500 | 系统错误 |
| 1001-1999 | 用户相关错误 |
| 2001-2999 | 车票相关错误 |
| 3001-3999 | 订单相关错误 |
| 4001-4999 | 支付相关错误 |

---

## 技术选型

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.1.5 | 基础框架 |
| Spring Cloud | 2022.0.4 | 微服务框架 |
| Spring Cloud Gateway | 4.0.8 | API网关 |
| Nacos | 2.2.3 | 服务注册与配置 |
| MyBatis Plus | 3.5.4 | ORM框架 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7.0 | 缓存数据库 |
| Druid | 1.2.20 | 连接池 |

### 工具类库

| 库 | 说明 |
|----|------|
| Lombok | 简化代码 |
| Hutool | Java工具类 |
| Fastjson2 | JSON处理 |
| JWT | 认证授权 |

---

## 性能优化

### 1. 缓存策略

#### Redis缓存
- 用户信息缓存（30分钟）
- 车票信息缓存（10分钟）
- 热门路线缓存（1小时）

#### 本地缓存
- 车站信息（Caffeine）
- 字典数据（Guava Cache）

### 2. 数据库优化

#### 索引优化
- 复合索引：(train_number, travel_date)
- 覆盖索引：减少回表查询

#### 分库分表
- 订单表按用户ID分表
- 历史订单归档

### 3. 接口优化

#### 批量操作
- 批量查询车票
- 批量创建订单

#### 异步处理
- 订单通知异步发送
- 日志异步写入

### 4. 限流熔断

#### Sentinel限流
- QPS限流：1000/s
- 线程数限流：200

#### Hystrix熔断
- 错误率阈值：50%
- 熔断时长：10s

---

## 安全设计

### 1. 认证授权

#### JWT Token
- 有效期：2小时
- 刷新机制：7天内可刷新
- 加密算法：HS256

### 2. 数据安全

#### 密码加密
- 算法：MD5 + Salt
- 盐值：用户唯一标识

#### 敏感信息
- 身份证脱敏：显示前6后4位
- 手机号脱敏：显示前3后4位

### 3. 接口安全

#### 防重放攻击
- 时间戳验证
- Nonce值验证

#### 防刷接口
- 同一IP限流
- 验证码验证

### 4. SQL注入防护
- 使用MyBatis预编译
- 参数校验

---

## 扩展性设计

### 1. 水平扩展
- 服务无状态设计
- 支持多实例部署
- 负载均衡

### 2. 垂直扩展
- 模块化设计
- 插件化机制
- 易于新增功能

### 3. 消息队列
- RabbitMQ/Kafka集成
- 异步解耦
- 削峰填谷

### 4. 分布式事务
- Seata集成
- TCC模式
- Saga模式

---

## 监控告警

### 1. 应用监控
- Spring Boot Actuator
- Prometheus + Grafana
- 实时性能指标

### 2. 日志监控
- ELK（Elasticsearch + Logstash + Kibana）
- 日志收集与分析
- 异常告警

### 3. 链路追踪
- Skywalking/Zipkin
- 分布式链路追踪
- 性能分析

### 4. 告警机制
- 钉钉/企业微信告警
- 邮件告警
- 短信告警

---

## 总结

本系统采用微服务架构，具有高可用、高性能、易扩展的特点。通过合理的服务拆分、完善的技术选型、全面的安全设计，能够支撑大规模的并发访问和业务增长。