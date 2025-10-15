# 部署指南

本文档详细说明如何在不同环境下部署12306火车票订票系统。

## 目录
- [开发环境部署](#开发环境部署)
- [生产环境部署](#生产环境部署)
- [Docker容器化部署](#docker容器化部署)
- [Kubernetes部署](#kubernetes部署)

---

## 开发环境部署

### 前置条件
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- Nacos 2.2+

### 步骤1: 安装基础环境

#### 安装MySQL
```bash
# 下载MySQL 8.0
# https://dev.mysql.com/downloads/mysql/

# 启动MySQL服务
sudo systemctl start mysql

# 设置root密码
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
```

#### 安装Redis
```bash
# 使用包管理器安装
sudo apt install redis-server  # Ubuntu/Debian
sudo yum install redis          # CentOS/RHEL

# 启动Redis
sudo systemctl start redis
```

#### 安装Nacos
```bash
# 下载Nacos
wget https://github.com/alibaba/nacos/releases/download/2.2.3/nacos-server-2.2.3.tar.gz
tar -xvf nacos-server-2.2.3.tar.gz
cd nacos/bin

# 单机模式启动
sh startup.sh -m standalone

# Windows环境
startup.cmd -m standalone
```

### 步骤2: 初始化数据库

```bash
# 连接MySQL
mysql -u root -p

# 执行初始化脚本
source /path/to/sql/train_user.sql
source /path/to/sql/train_ticket.sql
source /path/to/sql/train_order.sql
```

### 步骤3: 编译项目

```bash
# 克隆项目
git clone https://github.com/MyrsYC/train-full.git
cd train-full

# 编译
mvn clean package -DskipTests
```

### 步骤4: 配置文件

根据实际环境修改各服务的application.yml：

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-mysql-host:3306/train_user
    username: your-username
    password: your-password
  
  redis:
    host: your-redis-host
    port: 6379
  
  cloud:
    nacos:
      discovery:
        server-addr: your-nacos-host:8848
```

### 步骤5: 启动服务

```bash
# 1. 启动网关（必须等待Nacos完全启动）
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

### 步骤6: 验证部署

```bash
# 访问Nacos控制台
http://localhost:8848/nacos

# 测试API
curl http://localhost:8000/api/user/username/test001
```

---

## 生产环境部署

### 架构建议

```
┌─────────────────┐
│   Load Balancer │
│    (Nginx)      │
└────────┬────────┘
         │
         ├─────────────────────────────────┐
         │                                 │
┌────────▼────────┐              ┌────────▼────────┐
│   Gateway-1     │              │   Gateway-2     │
│   (8000)        │              │   (8000)        │
└────────┬────────┘              └────────┬────────┘
         │                                 │
         └─────────────┬───────────────────┘
                       │
         ┌─────────────┼─────────────┬─────────────┐
         │             │             │             │
┌────────▼────┐  ┌─────▼─────┐ ┌────▼────┐  ┌─────▼─────┐
│   User-1    │  │ Ticket-1  │ │ Order-1 │  │ Payment-1 │
│   User-2    │  │ Ticket-2  │ │ Order-2 │  │ Payment-2 │
└─────────────┘  └───────────┘ └─────────┘  └───────────┘
```

### 步骤1: 配置文件优化

创建生产环境配置 `application-prod.yml`:

```yaml
server:
  port: ${SERVICE_PORT:8001}

spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=true
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    druid:
      initial-size: 10
      min-idle: 10
      max-active: 50
      max-wait: 60000
      validation-query: SELECT 1
      test-on-borrow: true
      test-while-idle: true
      time-between-eviction-runs-millis: 60000
  
  redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD}
    database: ${REDIS_DB:0}
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
  
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_ADDR}
        namespace: ${NACOS_NAMESPACE:prod}

logging:
  level:
    root: INFO
    com.train: INFO
  file:
    name: /var/log/train/${spring.application.name}.log
    max-size: 100MB
    max-history: 30
```

### 步骤2: 使用Systemd管理服务

创建服务文件 `/etc/systemd/system/train-user.service`:

```ini
[Unit]
Description=Train User Service
After=network.target

[Service]
Type=simple
User=train
WorkingDirectory=/opt/train-full
ExecStart=/usr/bin/java -jar \
    -Xms512m -Xmx1024m \
    -XX:+UseG1GC \
    -Dspring.profiles.active=prod \
    /opt/train-full/train-user/target/train-user-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务:
```bash
sudo systemctl daemon-reload
sudo systemctl enable train-user
sudo systemctl start train-user
sudo systemctl status train-user
```

### 步骤3: 配置Nginx反向代理

```nginx
upstream gateway {
    server 127.0.0.1:8000 weight=1;
    server 127.0.0.1:8001 weight=1;
}

server {
    listen 80;
    server_name train.example.com;

    location / {
        proxy_pass http://gateway;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

### 步骤4: 配置SSL证书（可选）

```nginx
server {
    listen 443 ssl http2;
    server_name train.example.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    location / {
        proxy_pass http://gateway;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
    }
}
```

---

## Docker容器化部署

### 步骤1: 构建Docker镜像

创建 `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", "app.jar"]
```

构建镜像:
```bash
# 为每个服务构建镜像
cd train-gateway
docker build -t train-gateway:1.0.0 .

cd train-user
docker build -t train-user:1.0.0 .

# ... 其他服务类似
```

### 步骤2: 使用Docker Compose

项目已包含 `docker-compose.yml`，直接使用:

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 步骤3: 扩展服务实例

```bash
# 扩展用户服务到3个实例
docker-compose up -d --scale train-user=3

# 扩展车票服务到2个实例
docker-compose up -d --scale train-ticket=2
```

---

## Kubernetes部署

### 步骤1: 创建命名空间

```yaml
# namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: train-system
```

```bash
kubectl apply -f namespace.yaml
```

### 步骤2: 创建ConfigMap

```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: train-config
  namespace: train-system
data:
  DB_HOST: "mysql-service"
  DB_PORT: "3306"
  REDIS_HOST: "redis-service"
  REDIS_PORT: "6379"
  NACOS_ADDR: "nacos-service:8848"
```

### 步骤3: 创建Deployment

```yaml
# train-user-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: train-user
  namespace: train-system
spec:
  replicas: 3
  selector:
    matchLabels:
      app: train-user
  template:
    metadata:
      labels:
        app: train-user
    spec:
      containers:
      - name: train-user
        image: train-user:1.0.0
        ports:
        - containerPort: 8001
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_HOST
          valueFrom:
            configMapKeyRef:
              name: train-config
              key: DB_HOST
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8001
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8001
          initialDelaySeconds: 30
          periodSeconds: 5
```

### 步骤4: 创建Service

```yaml
# train-user-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: train-user-service
  namespace: train-system
spec:
  selector:
    app: train-user
  ports:
  - protocol: TCP
    port: 8001
    targetPort: 8001
  type: ClusterIP
```

### 步骤5: 创建Ingress

```yaml
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: train-ingress
  namespace: train-system
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: train.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: train-gateway-service
            port:
              number: 8000
```

### 步骤6: 部署

```bash
# 部署所有资源
kubectl apply -f namespace.yaml
kubectl apply -f configmap.yaml
kubectl apply -f train-user-deployment.yaml
kubectl apply -f train-user-service.yaml
kubectl apply -f ingress.yaml

# 查看部署状态
kubectl get pods -n train-system
kubectl get services -n train-system
kubectl get ingress -n train-system

# 查看日志
kubectl logs -f deployment/train-user -n train-system
```

---

## 监控和运维

### 日志管理

使用ELK（Elasticsearch + Logstash + Kibana）收集日志:

```yaml
# logstash配置
input {
  file {
    path => "/var/log/train/*.log"
    start_position => "beginning"
  }
}

filter {
  grok {
    match => { "message" => "%{TIMESTAMP_ISO8601:timestamp} %{LOGLEVEL:level} %{GREEDYDATA:message}" }
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "train-logs-%{+YYYY.MM.dd}"
  }
}
```

### 性能监控

使用Prometheus + Grafana监控:

1. 添加Actuator依赖
2. 配置Prometheus抓取端点
3. 导入Grafana Dashboard
4. 设置告警规则

### 健康检查

```bash
# 检查服务健康状态
curl http://localhost:8001/actuator/health

# 查看服务指标
curl http://localhost:8001/actuator/metrics
```

---

## 常见问题

### Q1: 服务启动慢？
A: 增加JVM内存，优化启动参数，检查网络连接。

### Q2: 服务间调用失败？
A: 检查Nacos注册状态，网络是否互通，防火墙规则。

### Q3: 数据库连接池耗尽？
A: 增加连接池大小，优化SQL查询，添加连接超时配置。

### Q4: Redis连接超时？
A: 检查Redis服务状态，网络延迟，调整超时参数。

---

## 总结

本文档提供了从开发到生产的完整部署方案。根据实际需求选择合适的部署方式：

- **开发环境**: 直接运行jar包
- **测试环境**: Docker Compose
- **生产环境**: Kubernetes集群

建议生产环境采用Kubernetes部署，便于实现自动扩缩容、服务发现、故障恢复等高级特性。