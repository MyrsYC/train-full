# train-full - 12306火车票订票系统

[![Java](https://img.shields.io/badge/Java-17-red.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2022.0.4-blue.svg)](https://spring.io/projects/spring-cloud)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 项目简介

这是一个仿12306的完整火车票订票系统，采用微服务架构设计，实现了用户注册登录、车票查询、订单管理、支付等核心功能。

## 技术栈

### 后端技术
- **Spring Boot 3.1.5** - 基础框架
- **Spring Cloud 2022.0.4** - 微服务框架
- **Spring Cloud Alibaba** - 微服务组件
- **Nacos** - 服务注册与配置中心
- **Spring Cloud Gateway** - API网关
- **OpenFeign** - 服务调用
- **MyBatis Plus** - ORM框架
- **MySQL 8.0** - 关系型数据库
- **Redis** - 缓存数据库
- **Druid** - 数据库连接池

### 开发工具
- **Lombok** - 简化实体类开发
- **Hutool** - Java工具类库
- **Fastjson2** - JSON处理
- **JWT** - 身份认证

## 系统架构

```
├── train-common          # 公共模块
│   ├── entity           # 基础实体类
│   ├── exception        # 异常处理
│   ├── result           # 统一返回结果
│   └── utils            # 工具类
├── train-gateway         # API网关 (端口:8000)
├── train-user           # 用户服务 (端口:8001)
├── train-ticket         # 车票服务 (端口:8002)
├── train-order          # 订单服务 (端口:8003)
├── train-payment        # 支付服务 (端口:8004)
└── sql                  # 数据库脚本
```

## 核心功能

### 1. 用户服务 (train-user)
- 用户注册
- 用户登录
- 用户信息查询
- 身份认证

### 2. 车票服务 (train-ticket)
- 车次查询
- 车票查询
- 余票管理
- 座位锁定/释放

### 3. 订单服务 (train-order)
- 创建订单
- 订单支付
- 订单取消
- 订单查询

### 4. 支付服务 (train-payment)
- 创建支付订单
- 支付状态查询
- 支付回调处理（模拟）

### 5. 网关服务 (train-gateway)
- 统一入口
- 路由转发
- 负载均衡

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- Nacos 2.2+

### 1. 使用Docker启动基础设施

```bash
# 启动MySQL、Redis和Nacos
docker-compose up -d

# 等待服务启动完成（约30秒）
docker-compose ps
```

服务访问地址：
- MySQL: `localhost:3306` (用户名: root, 密码: root)
- Redis: `localhost:6379`
- Nacos: `http://localhost:8848/nacos` (用户名: nacos, 密码: nacos)

### 2. 初始化数据库

数据库会在Docker启动时自动初始化。如需手动执行：

```bash
# 连接MySQL
mysql -h localhost -u root -p

# 执行SQL脚本
source sql/train_user.sql
source sql/train_ticket.sql
source sql/train_order.sql
```

### 3. 编译项目

```bash
# 编译整个项目
mvn clean package -DskipTests

# 或者分别编译各个模块
cd train-common && mvn clean install
cd ../train-gateway && mvn clean package
cd ../train-user && mvn clean package
cd ../train-ticket && mvn clean package
cd ../train-order && mvn clean package
cd ../train-payment && mvn clean package
```

### 4. 启动服务

按照以下顺序启动各个服务：

```bash
# 1. 启动网关服务
cd train-gateway
java -jar target/train-gateway-1.0.0.jar

# 2. 启动用户服务
cd train-user
java -jar target/train-user-1.0.0.jar

# 3. 启动车票服务
cd train-ticket
java -jar target/train-ticket-1.0.0.jar

# 4. 启动订单服务
cd train-order
java -jar target/train-order-1.0.0.jar

# 5. 启动支付服务
cd train-payment
java -jar target/train-payment-1.0.0.jar
```

## API接口文档

### 用户服务 API

#### 1. 用户注册
```http
POST http://localhost:8000/api/user/register
Content-Type: application/json

{
  "username": "test001",
  "password": "test123",
  "realName": "张三",
  "idCard": "110101199001011234",
  "phone": "13800138000",
  "email": "test@example.com"
}
```

#### 2. 用户登录
```http
POST http://localhost:8000/api/user/login
Content-Type: application/json

{
  "username": "test001",
  "password": "test"
}
```

### 车票服务 API

#### 3. 查询车票
```http
POST http://localhost:8000/api/ticket/query
Content-Type: application/json

{
  "startStation": "北京南",
  "endStation": "上海虹桥",
  "travelDate": "2025-10-20",
  "seatType": "二等座"
}
```

### 订单服务 API

#### 4. 创建订单
```http
POST http://localhost:8000/api/order/create
Content-Type: application/json

{
  "userId": 1,
  "ticketId": 3,
  "seatCount": 2
}
```

#### 5. 支付订单
```http
POST http://localhost:8000/api/order/pay/1
```

#### 6. 查询用户订单
```http
GET http://localhost:8000/api/order/user/1
```

### 支付服务 API

#### 7. 创建支付
```http
POST http://localhost:8000/api/payment/create?orderNo=ORD123456&amount=1106.00
```

## 数据库设计

### 用户数据库 (train_user)
- **t_user**: 用户信息表

### 车票数据库 (train_ticket)
- **t_train**: 列车信息表
- **t_ticket**: 车票信息表

### 订单数据库 (train_order)
- **t_order**: 订单信息表

## 系统特性

### 1. 微服务架构
- 服务拆分合理，各服务独立部署
- 通过Nacos实现服务注册与发现
- 使用Spring Cloud Gateway统一网关入口

### 2. 高可用设计
- 支持服务多实例部署
- 集成负载均衡
- 分布式事务支持（可扩展）

### 3. 安全性
- 密码MD5加密存储
- Token认证机制
- 统一异常处理

### 4. 可扩展性
- 模块化设计，易于扩展新功能
- 预留分布式锁接口
- 支持消息队列集成

## 开发计划

- [x] 基础架构搭建
- [x] 用户服务实现
- [x] 车票服务实现
- [x] 订单服务实现
- [x] 支付服务实现（模拟）
- [x] API网关配置
- [x] 数据库设计
- [x] Docker环境配置
- [ ] 分布式锁实现
- [ ] 消息队列集成
- [ ] 限流熔断
- [ ] 链路追踪
- [ ] 前端界面

## 测试数据

系统已预置测试数据：

**测试用户：**
- 用户名: test001, 密码: test (MD5: 098f6bcd4621d373cade4e832627b4f6)
- 用户名: test002, 密码: test

**测试车次：**
- G1001: 北京南 → 上海虹桥 (08:00-13:30)
- G1002: 上海虹桥 → 北京南 (08:00-13:30)
- D3001: 杭州东 → 南京南 (09:00-11:30)
- K1001: 广州 → 深圳 (10:00-12:00)

## 项目截图

### 服务注册中心
访问 `http://localhost:8848/nacos` 可以看到所有已注册的服务。

### API调用示例
通过网关统一入口 `http://localhost:8000` 访问各个服务。

## 常见问题

### Q1: 服务启动失败？
A: 请确保MySQL、Redis和Nacos已正常启动，检查application.yml中的配置是否正确。

### Q2: 无法访问Nacos控制台？
A: 等待Nacos完全启动（约30秒），确保8848端口未被占用。

### Q3: 数据库连接失败？
A: 检查MySQL是否正常运行，用户名密码是否正确，数据库是否已创建。

## 贡献指南

欢迎提交Issue和Pull Request！

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题，欢迎通过GitHub Issues联系。

---

**注意**: 本项目仅供学习交流使用，请勿用于商业用途。
