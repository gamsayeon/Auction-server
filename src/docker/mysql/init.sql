-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS auction_server;

-- 사용할 데이터베이스 선택
USE auction_server;

-- 테이블 생성
CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `user_type` tinyint DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `last_login_time` datetime(6) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) NOT NULL,
  `bid_min_price` int NOT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name_UNIQUE` (`category_name`)
);

CREATE TABLE IF NOT EXISTS `user_anomaly_log` (
  `anomaly_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `action` varchar(45) DEFAULT NULL,
  `reason` text,
  PRIMARY KEY (`anomaly_id`),
  KEY `FK_user_anomaly_log_user_id_user_id` (`user_id`),
  CONSTRAINT `FK_user_anomaly_log_user_id_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS `product` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `sale_id` bigint DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `category_id` int NOT NULL,
  `explanation` varchar(255) DEFAULT NULL,
  `product_register_time` datetime(6) DEFAULT NULL,
  `start_price` int DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `highest_price` int NOT NULL,
  `product_status` tinyint DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `FK_product_category_id_category_category_id_idx` (`category_id`),
  CONSTRAINT `FK_product_category_id_category_category_id` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
);

CREATE TABLE IF NOT EXISTS `product_image` (
  `image_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `FK_product_image_product_id_product_product_id` (`product_id`),
  CONSTRAINT `FK_product_image_product_id_product_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS `product_comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_comment_id` bigint DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `user_id` int NOT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FK_comment_parent_comment_id_comment_comment_id` (`parent_comment_id`),
  KEY `FK_comment_user_id_user_id` (`user_id`),
  KEY `FK_comment_product_id_product_product_id` (`product_id`),
  CONSTRAINT `FK_comment_product_id_product_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_comment_user_id_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS `notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `content` text,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`notice_id`)
);

CREATE TABLE IF NOT EXISTS `notice_comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `parent_comment_id` int DEFAULT NULL,
  `notice_id` int NOT NULL,
  `user_id` int NOT NULL,
  `comment` text NOT NULL,
  `comment_time` datetime NOT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FK_notice_comment_parent_comment_id_notice_comment_comment_id` (`parent_comment_id`),
  KEY `FK_notice_comment_notice_id_notice_notice_id` (`notice_id`),
  CONSTRAINT `FK_notice_comment_notice_id_notice_notice_id` FOREIGN KEY (`notice_id`) REFERENCES `notice` (`notice_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_notice_comment_parent_comment_id_notice_comment_comment_id` FOREIGN KEY (`parent_comment_id`) REFERENCES `notice_comment` (`comment_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS `bid` (
  `bid_id` bigint NOT NULL AUTO_INCREMENT,
  `buyer_id` int NOT NULL,
  `product_id` int NOT NULL,
  `bid_time` datetime(6) DEFAULT NULL,
  `price` int NOT NULL,
  PRIMARY KEY (`bid_id`),
  KEY `FK_auction_bid_product_id_product_product_id` (`product_id`),
  KEY `FK_auction_bid_user_id_user_id` (`buyer_id`),
  CONSTRAINT `FK_auction_bid_product_id_product_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_auction_bid_user_id_user_id` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS `payment` (
  `payment_id` varchar(255) NOT NULL,
  `order_id` varchar(255) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `payment_date` datetime(6) DEFAULT NULL,
  `payment_status` tinyint DEFAULT NULL,
  `pay_method` tinyint DEFAULT NULL,
  `payment_amount` int DEFAULT NULL,
  PRIMARY KEY (`payment_id`)
);
