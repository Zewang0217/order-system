create database if not exists order_system;
use order_system;

-- 用户表
CREATE TABLE users (
                       user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL, -- 加密存储
                       email VARCHAR(100) UNIQUE NOT NULL,
                       phone VARCHAR(20),
                       address TEXT,
                       role ENUM('USER', 'ADMIN') DEFAULT 'USER',
                       status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
                       create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                       update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       INDEX idx_username (username),
                       INDEX idx_email (email)
);

-- 用户收货地址表（扩展）
CREATE TABLE user_addresses (
                                address_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                user_id BIGINT NOT NULL,
                                recipient_name VARCHAR(50) NOT NULL,
                                phone VARCHAR(20) NOT NULL,
                                province VARCHAR(50) NOT NULL,
                                city VARCHAR(50) NOT NULL,
                                district VARCHAR(50) NOT NULL,
                                detail_address TEXT NOT NULL,
                                is_default BOOLEAN DEFAULT FALSE,
                                create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (user_id) REFERENCES users(user_id),
                                INDEX idx_user_id (user_id)
);

-- 商品表
CREATE TABLE products (
                          product_id VARCHAR(50) PRIMARY KEY,
                          product_name VARCHAR(200) NOT NULL,
                          description TEXT,
                          price DECIMAL(10,2) NOT NULL COMMENT '单位：元',
                          category VARCHAR(100),
                          image_url VARCHAR(500),
                          status ENUM('ON_SALE', 'OFF_SALE', 'DELETED') DEFAULT 'ON_SALE',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                          update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          INDEX idx_category (category),
                          INDEX idx_status (status)
);

-- 商品库存表
CREATE TABLE inventory (
                           inventory_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           product_id VARCHAR(50) NOT NULL,
                           available_stock INT NOT NULL DEFAULT 0 COMMENT '可用库存',
                           locked_stock INT NOT NULL DEFAULT 0 COMMENT '锁定库存',
                           total_stock INT NOT NULL DEFAULT 0 COMMENT '总库存',
                           unit VARCHAR(20) DEFAULT '件',
                           version INT DEFAULT 0 COMMENT '乐观锁版本',
                           last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           UNIQUE KEY uk_product_id (product_id),
                           FOREIGN KEY (product_id) REFERENCES products(product_id),
                           INDEX idx_product_id (product_id)
);

-- 库存变更记录表
CREATE TABLE inventory_history (
                                   history_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   product_id VARCHAR(50) NOT NULL,
                                   change_type ENUM('INCREMENT', 'DECREMENT', 'LOCK', 'RELEASE') NOT NULL,
                                   change_amount INT NOT NULL,
                                   before_stock INT NOT NULL,
                                   after_stock INT NOT NULL,
                                   reference_id VARCHAR(100) COMMENT '关联订单ID等',
                                   reason VARCHAR(200),
                                   operator VARCHAR(50) NOT NULL,
                                   create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   INDEX idx_product_id (product_id),
                                   INDEX idx_reference (reference_id),
                                   INDEX idx_create_time (create_time)
);

-- 订单主表
CREATE TABLE orders (
                        order_id VARCHAR(50) PRIMARY KEY COMMENT '订单号：ORD202510170001',
                        user_id BIGINT NOT NULL,
                        total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
                        actual_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
                        payment_type ENUM('ALIPAY', 'WECHAT', 'CREDIT_CARD', 'BANK_TRANSFER') NOT NULL,
                        status ENUM(
                            'PENDING_PAYMENT',    -- 待支付
                            'PAID',               -- 已支付
                            'PROCESSING',         -- 处理中
                            'SHIPPED',            -- 已发货
                            'DELIVERED',          -- 已送达
                            'CANCELLED',          -- 已取消
                            'REFUNDED'            -- 已退款
                            ) DEFAULT 'PENDING_PAYMENT',
                        address TEXT NOT NULL COMMENT '收货地址',
                        note TEXT COMMENT '订单备注',
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                        update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        paid_time DATETIME COMMENT '支付时间',
                        cancelled_time DATETIME COMMENT '取消时间',
                        FOREIGN KEY (user_id) REFERENCES users(user_id),
                        INDEX idx_user_id (user_id),
                        INDEX idx_status (status),
                        INDEX idx_create_time (create_time)
);

-- 订单商品项表
CREATE TABLE order_items (
                             item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             order_id VARCHAR(50) NOT NULL,
                             product_id VARCHAR(50) NOT NULL,
                             product_name VARCHAR(200) NOT NULL,
                             quantity INT NOT NULL,
                             unit_price DECIMAL(10,2) NOT NULL COMMENT '下单时单价',
                             total_price DECIMAL(10,2) NOT NULL COMMENT '商品总价',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (order_id) REFERENCES orders(order_id),
                             FOREIGN KEY (product_id) REFERENCES products(product_id),
                             INDEX idx_order_id (order_id),
                             INDEX idx_product_id (product_id)
);

-- 订单状态变更记录表
CREATE TABLE order_status_log (
                                  log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  order_id VARCHAR(50) NOT NULL,
                                  from_status VARCHAR(50),
                                  to_status VARCHAR(50) NOT NULL,
                                  remark TEXT,
                                  operator VARCHAR(50) NOT NULL,
                                  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  INDEX idx_order_id (order_id),
                                  INDEX idx_create_time (create_time)
);


-- 支付交易表
CREATE TABLE payments (
                          transaction_id VARCHAR(50) PRIMARY KEY COMMENT '支付流水号',
                          order_id VARCHAR(50) NOT NULL,
                          user_id BIGINT NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          payment_type ENUM('ALIPAY', 'WECHAT', 'CREDIT_CARD', 'BANK_TRANSFER') NOT NULL,
                          status ENUM('PENDING', 'PAID', 'FAILED', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING',
                          pay_url VARCHAR(500) COMMENT '支付链接',
                          callback_url VARCHAR(500) NOT NULL,
                          third_party_trade_no VARCHAR(100) COMMENT '第三方交易号',
                          payment_time DATETIME COMMENT '支付成功时间',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                          update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          UNIQUE KEY uk_order_id (order_id),
                          FOREIGN KEY (order_id) REFERENCES orders(order_id),
                          FOREIGN KEY (user_id) REFERENCES users(user_id),
                          INDEX idx_user_id (user_id),
                          INDEX idx_status (status),
                          INDEX idx_create_time (create_time)
);

-- 支付回调记录表
CREATE TABLE payment_callbacks (
                                   callback_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   transaction_id VARCHAR(50) NOT NULL,
                                   callback_data TEXT NOT NULL COMMENT '原始回调数据',
                                   processed BOOLEAN DEFAULT FALSE,
                                   process_result TEXT,
                                   create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   INDEX idx_transaction_id (transaction_id),
                                   INDEX idx_processed (processed)
);


-- 发货记录表
CREATE TABLE fulfillments (
                              fulfillment_id VARCHAR(50) PRIMARY KEY COMMENT '发货单号',
                              order_id VARCHAR(50) NOT NULL,
                              carrier VARCHAR(100) NOT NULL COMMENT '承运商',
                              tracking_number VARCHAR(100) NOT NULL COMMENT '物流单号',
                              status ENUM('PENDING', 'SHIPPED', 'IN_TRANSIT', 'DELIVERED', 'FAILED') DEFAULT 'PENDING',
                              shipping_address TEXT NOT NULL,
                              estimated_delivery DATETIME COMMENT '预计送达时间',
                              actual_delivery DATETIME COMMENT '实际送达时间',
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                              update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              UNIQUE KEY uk_order_id (order_id),
                              FOREIGN KEY (order_id) REFERENCES orders(order_id),
                              INDEX idx_tracking_number (tracking_number),
                              INDEX idx_status (status)
);

-- 物流轨迹表
CREATE TABLE shipment_tracks (
                                 track_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 fulfillment_id VARCHAR(50) NOT NULL,
                                 location VARCHAR(200) NOT NULL,
                                 description TEXT NOT NULL,
                                 track_time DATETIME NOT NULL,
                                 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (fulfillment_id) REFERENCES fulfillments(fulfillment_id),
                                 INDEX idx_fulfillment_id (fulfillment_id),
                                 INDEX idx_track_time (track_time)
);


-- Outbox表（消息发件箱）
CREATE TABLE outbox_messages (
                                 message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 topic VARCHAR(100) NOT NULL COMMENT '消息主题',
                                 message_key VARCHAR(100) NOT NULL COMMENT '消息键（如订单ID）',
                                 message_body JSON NOT NULL COMMENT '消息内容',
                                 message_status ENUM('PENDING', 'SENT', 'FAILED') DEFAULT 'PENDING',
                                 retry_count INT DEFAULT 0,
                                 max_retry_count INT DEFAULT 3,
                                 last_retry_time DATETIME,
                                 error_message TEXT,
                                 created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 sent_time DATETIME,
                                 INDEX idx_topic (topic),
                                 INDEX idx_message_key (message_key),
                                 INDEX idx_status (message_status),
                                 INDEX idx_created_time (created_time)
);

-- 死信消息表
CREATE TABLE dead_letter_messages (
                                      dlq_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      original_message_id BIGINT,
                                      topic VARCHAR(100) NOT NULL,
                                      message_key VARCHAR(100) NOT NULL,
                                      message_body JSON NOT NULL,
                                      failure_reason TEXT NOT NULL,
                                      retry_count INT DEFAULT 0,
                                      create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      last_retry_time DATETIME,
                                      INDEX idx_topic (topic),
                                      INDEX idx_message_key (message_key),
                                      INDEX idx_create_time (create_time)
);

select * from users;
update users set role = 'ADMIN' where username = 'admin';

show tables;

-- 角色表
CREATE TABLE roles (
                       role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       role_name VARCHAR(50) UNIQUE NOT NULL,
                       description VARCHAR(255)
);

-- 权限表
CREATE TABLE permissions (
                             permission_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             permission_name VARCHAR(100) UNIQUE NOT NULL,
                             resource VARCHAR(100),
                             action VARCHAR(50)
);

-- 角色权限关联表
CREATE TABLE role_permissions (
                                  role_id BIGINT,
                                  permission_id BIGINT,
                                  PRIMARY KEY (role_id, permission_id)
);

-- 用户角色关联表（支持多角色）
CREATE TABLE user_roles (
                            user_id BIGINT,
                            role_id BIGINT,
                            PRIMARY KEY (user_id, role_id)
);

-- 为 role_permissions 表添加外键约束
ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id) REFERENCES roles(role_id);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id) REFERENCES permissions(permission_id);

-- 为 user_roles 表添加外键约束
ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(role_id);

-- 初始化角色
INSERT INTO roles (role_name, description) VALUES
                                               ('ADMIN', '管理员角色'),
                                               ('USER', '普通用户角色');

-- 初始化权限
INSERT INTO permissions (permission_name, resource, action) VALUES
                                                                ('订单管理', 'orders', 'manage'),
                                                                ('用户管理', 'users', 'manage'),
                                                                ('商品管理', 'products', 'manage');

-- 为ADMIN角色分配所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r, permissions p
WHERE r.role_name = 'ADMIN';

delete from users where user_id = 1;

select * from users;
SELECT user_id, username, role FROM users WHERE username = 'admin';
SELECT role_id, role_name FROM roles WHERE role_name = 'ADMIN';
SELECT user_id FROM users WHERE username = 'admin';
SELECT * FROM user_roles WHERE user_id = 2;
insert into user_roles (user_id, role_id) values (2, 1);

select * from order_items;
select * from orders;

-- 向products表中插入5个示例商品
INSERT INTO products (product_id, product_name, description, price, category, image_url, status, create_time, update_time)
VALUES
    ('101', '无线蓝牙耳机', '高品质真无线蓝牙耳机，支持主动降噪', 299.00, '电子产品', 'https://example.com/images/product101.jpg', 'ON_SALE', NOW(), NOW()),
    ('102', '智能手环', '多功能健康监测智能手环，防水设计', 199.00, '智能设备', 'https://example.com/images/product102.jpg', 'ON_SALE', NOW(), NOW()),
    ('103', '机械键盘', '87键RGB背光机械键盘，青轴', 459.00, '电脑配件', 'https://example.com/images/product103.jpg', 'ON_SALE', NOW(), NOW()),
    ('104', '移动电源', '20000mAh大容量快充移动电源', 129.00, '手机配件', 'https://example.com/images/product104.jpg', 'ON_SALE', NOW(), NOW()),
    ('105', '无线鼠标', '人体工学设计无线鼠标，静音按键', 89.00, '电脑配件', 'https://example.com/images/product105.jpg', 'ON_SALE', NOW(), NOW());

delete from orders;
show tables;
select * from products;
select * from order_items;
select * from inventory;

insert into inventory (product_id, available_stock, locked_stock, total_stock, unit, version, last_updated)
values
    ('103', 200, 0, 200, '件', 0, NOW()),
    ('104', 250, 0, 250, '件', 0, NOW());

-- 修改 payments 表结构
ALTER TABLE payments
    MODIFY COLUMN transaction_id VARCHAR(64) COMMENT '支付流水号';

ALTER TABLE payments
    CHANGE COLUMN transaction_id payment_id VARCHAR(64) COMMENT '支付流水号';

ALTER TABLE payments
    MODIFY COLUMN amount INT NOT NULL COMMENT '支付金额（分）';

-- 确保必要的索引存在
ALTER TABLE payments
    ADD UNIQUE INDEX uk_order_id (order_id);