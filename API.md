# API接口测试指南

## 测试环境

- 网关地址: http://localhost:8000
- 直接服务访问（开发调试）：
  - 用户服务: http://localhost:8001
  - 车票服务: http://localhost:8002
  - 订单服务: http://localhost:8003
  - 支付服务: http://localhost:8004

## 完整业务流程测试

### 1. 用户注册

```bash
curl -X POST http://localhost:8000/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser001",
    "password": "password123",
    "realName": "测试用户",
    "idCard": "110101199001011234",
    "phone": "13900139000",
    "email": "testuser@example.com"
  }'
```

预期响应：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 3,
    "username": "testuser001",
    "realName": "测试用户",
    "idCard": "110101199001011234",
    "phone": "13900139000",
    "email": "testuser@example.com",
    "token": null
  }
}
```

### 2. 用户登录

```bash
curl -X POST http://localhost:8000/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test001",
    "password": "test"
  }'
```

预期响应：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "test001",
    "realName": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "token": "token-1"
  }
}
```

### 3. 查询用户信息

```bash
# 根据用户名查询
curl -X GET http://localhost:8000/api/user/username/test001

# 根据ID查询
curl -X GET http://localhost:8000/api/user/1
```

### 4. 查询车票

```bash
curl -X POST http://localhost:8000/api/ticket/query \
  -H "Content-Type: application/json" \
  -d '{
    "startStation": "北京南",
    "endStation": "上海虹桥",
    "travelDate": "2025-10-20"
  }'
```

预期响应：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "trainId": 1,
      "trainNumber": "G1001",
      "startStation": "北京南",
      "endStation": "上海虹桥",
      "travelDate": "2025-10-20",
      "seatType": "商务座",
      "price": 1748.00,
      "remainSeats": 28,
      "totalSeats": 28
    },
    {
      "id": 2,
      "trainId": 1,
      "trainNumber": "G1001",
      "startStation": "北京南",
      "endStation": "上海虹桥",
      "travelDate": "2025-10-20",
      "seatType": "一等座",
      "price": 933.00,
      "remainSeats": 90,
      "totalSeats": 90
    },
    {
      "id": 3,
      "trainId": 1,
      "trainNumber": "G1001",
      "startStation": "北京南",
      "endStation": "上海虹桥",
      "travelDate": "2025-10-20",
      "seatType": "二等座",
      "price": 553.00,
      "remainSeats": 882,
      "totalSeats": 882
    }
  ]
}
```

### 5. 查询指定座位类型车票

```bash
curl -X POST http://localhost:8000/api/ticket/query \
  -H "Content-Type: application/json" \
  -d '{
    "startStation": "北京南",
    "endStation": "上海虹桥",
    "travelDate": "2025-10-20",
    "seatType": "二等座"
  }'
```

### 6. 创建订单

```bash
curl -X POST http://localhost:8000/api/order/create \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "ticketId": 3,
    "seatCount": 2
  }'
```

预期响应：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "ORD1234567890123456789",
    "userId": 1,
    "ticketId": 3,
    "seatCount": 2,
    "totalPrice": 200.00,
    "status": 0,
    "statusText": "待支付",
    "createTime": "2025-10-15T11:46:20"
  }
}
```

### 7. 查询用户订单

```bash
curl -X GET http://localhost:8000/api/order/user/1
```

### 8. 根据订单号查询订单

```bash
curl -X GET http://localhost:8000/api/order/no/ORD1234567890123456789
```

### 9. 创建支付订单

```bash
curl -X POST "http://localhost:8000/api/payment/create?orderNo=ORD1234567890123456789&amount=200.00"
```

预期响应：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "PAY9876543210987654321"
}
```

### 10. 支付订单

```bash
curl -X POST http://localhost:8000/api/order/pay/1
```

预期响应：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 11. 查询支付状态

```bash
curl -X GET http://localhost:8000/api/payment/status/PAY9876543210987654321
```

### 12. 取消订单

```bash
curl -X POST http://localhost:8000/api/order/cancel/1
```

## 错误处理测试

### 1. 用户名已存在

```bash
curl -X POST http://localhost:8000/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test001",
    "password": "password123",
    "realName": "重复用户",
    "phone": "13900139999"
  }'
```

预期响应：
```json
{
  "code": 1002,
  "message": "用户已存在",
  "data": null
}
```

### 2. 密码错误

```bash
curl -X POST http://localhost:8000/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test001",
    "password": "wrongpassword"
  }'
```

预期响应：
```json
{
  "code": 1003,
  "message": "密码错误",
  "data": null
}
```

### 3. 用户不存在

```bash
curl -X GET http://localhost:8000/api/user/username/nonexistent
```

预期响应：
```json
{
  "code": 1001,
  "message": "用户不存在",
  "data": null
}
```

### 4. 订单不存在

```bash
curl -X POST http://localhost:8000/api/order/pay/999999
```

预期响应：
```json
{
  "code": 3001,
  "message": "订单不存在",
  "data": null
}
```

## 直接服务访问（跳过网关）

如果需要直接访问服务进行调试，可以使用以下地址：

### 用户服务 (8001)

```bash
# 注册
curl -X POST http://localhost:8001/user/register -H "Content-Type: application/json" -d '{"username":"direct001","password":"test"}'

# 登录
curl -X POST http://localhost:8001/user/login -H "Content-Type: application/json" -d '{"username":"test001","password":"test"}'
```

### 车票服务 (8002)

```bash
# 查询车票
curl -X POST http://localhost:8002/ticket/query -H "Content-Type: application/json" -d '{"startStation":"北京南","endStation":"上海虹桥","travelDate":"2025-10-20"}'

# 查询车票详情
curl -X GET http://localhost:8002/ticket/3
```

### 订单服务 (8003)

```bash
# 创建订单
curl -X POST http://localhost:8003/order/create -H "Content-Type: application/json" -d '{"userId":1,"ticketId":3,"seatCount":1}'

# 查询订单
curl -X GET http://localhost:8003/order/user/1
```

### 支付服务 (8004)

```bash
# 创建支付
curl -X POST "http://localhost:8004/payment/create?orderNo=ORD123&amount=100.00"

# 查询支付状态
curl -X GET http://localhost:8004/payment/status/PAY123
```

## 使用Postman测试

可以导入以下Postman Collection进行测试：

1. 创建新的Collection: "Train Ticket System"
2. 添加Environment变量:
   - `base_url`: http://localhost:8000
   - `gateway_prefix`: /api

3. 创建以下请求组：
   - User Service
     - POST {{base_url}}{{gateway_prefix}}/user/register
     - POST {{base_url}}{{gateway_prefix}}/user/login
     - GET {{base_url}}{{gateway_prefix}}/user/1
   
   - Ticket Service
     - POST {{base_url}}{{gateway_prefix}}/ticket/query
     - GET {{base_url}}{{gateway_prefix}}/ticket/1
   
   - Order Service
     - POST {{base_url}}{{gateway_prefix}}/order/create
     - POST {{base_url}}{{gateway_prefix}}/order/pay/1
     - GET {{base_url}}{{gateway_prefix}}/order/user/1
   
   - Payment Service
     - POST {{base_url}}{{gateway_prefix}}/payment/create
     - GET {{base_url}}{{gateway_prefix}}/payment/status/PAY123

## 压力测试

### 使用Apache Bench测试

```bash
# 测试登录接口
ab -n 1000 -c 10 -p login.json -T application/json http://localhost:8000/api/user/login

# 测试车票查询接口
ab -n 1000 -c 10 -p query.json -T application/json http://localhost:8000/api/ticket/query
```

### 使用JMeter测试

1. 创建测试计划
2. 添加线程组（用户数、循环次数）
3. 添加HTTP请求采样器
4. 配置断言和监听器
5. 运行测试并查看结果

## 监控端点

### 服务健康检查

```bash
# 检查网关服务
curl http://localhost:8000/actuator/health

# 检查用户服务
curl http://localhost:8001/actuator/health
```

### 查看服务注册信息

访问Nacos控制台: http://localhost:8848/nacos

- 用户名: nacos
- 密码: nacos

在"服务管理" > "服务列表"中可以看到所有已注册的服务。

## 常见问题

### Q: 为什么返回404？
A: 检查服务是否启动，网关路由配置是否正确。

### Q: 为什么返回500？
A: 查看服务日志，检查数据库连接、配置文件等。

### Q: 如何查看详细日志？
A: 日志级别在application.yml中配置，可以设置为debug查看更详细的日志。

---

**提示**: 建议先使用curl进行简单测试，确认接口可用后再使用Postman或其他工具进行更复杂的测试。