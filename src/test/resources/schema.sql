-- 1. Create the `category` table first
CREATE TABLE IF NOT EXISTS `category` (
  `category_id` int NOT NULL AUTO_INCREMENT COMMENT '카테고리번호',
  `category_name` varchar(255) NOT NULL COMMENT '카테고리명',
  `bid_min_price` int NOT NULL COMMENT '입찰 최저 단위',
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name_UNIQUE` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='카테고리';

-- 2. Create the `product` table
CREATE TABLE IF NOT EXISTS `product` (
  `product_id` int NOT NULL AUTO_INCREMENT COMMENT '상품번호',
  `sale_id` bigint DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `category_id` int NOT NULL COMMENT '카테고리번호',
  `explanation` varchar(255) DEFAULT NULL,
  `product_register_time` datetime(6) DEFAULT NULL,
  `start_price` int DEFAULT NULL COMMENT '경매시작가',
  `start_time` datetime(6) DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `highest_price` int NOT NULL COMMENT '즉시구매가',
  `product_status` tinyint DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `FK_product_category_id_category_category_id_idx` (`category_id`),
  CONSTRAINT `FK_product_category_id_category_category_id` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100036 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='상품';

-- 3. Create the `user` table
CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '유저번호',
  `user_id` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL COMMENT '패스워드',
  `name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL COMMENT '이메일',
  `user_type` tinyint DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `last_login_time` datetime(6) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='회원';

-- 4. Create the `bid` table
CREATE TABLE IF NOT EXISTS `bid` (
  `bid_id` bigint NOT NULL AUTO_INCREMENT,
  `buyer_id` int NOT NULL COMMENT '유저번호',
  `product_id` int NOT NULL COMMENT '상품번호',
  `bid_time` datetime(6) DEFAULT NULL,
  `price` int NOT NULL COMMENT '입찰가',
  PRIMARY KEY (`bid_id`),
  KEY `FK_auction_bid_product_id_product_product_id` (`product_id`),
  KEY `FK_auction_bid_user_id_user_id` (`buyer_id`),
  KEY `idx_bid_time` (`bid_time`),
  CONSTRAINT `FK_auction_bid_product_id_product_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_auction_bid_user_id_user_id` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='경매이력';

-- 5. Create the `payment` table
CREATE TABLE IF NOT EXISTS `payment` (
  `payment_id` varchar(255) NOT NULL,
  `order_id` varchar(255) NOT NULL,
  `user_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `payment_date` datetime(6) DEFAULT NULL,
  `payment_status` tinyint DEFAULT NULL,
  `pay_method` tinyint DEFAULT NULL,
  `payment_amount` int DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `FK_auction_payment_product_id_product_product_id_idx` (`product_id`),
  KEY `FK_auction_payment_user_id_user_id_idx` (`user_id`),
  CONSTRAINT `FK_auction_payment_product_id_product_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  CONSTRAINT `FK_auction_payment_user_id_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6. Create the `product_comment` table
CREATE TABLE IF NOT EXISTS `product_comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_comment_id` bigint DEFAULT NULL,
  `product_id` int DEFAULT NULL COMMENT '상품번호',
  `user_id` int NOT NULL COMMENT '유저번호',
  `comment` varchar(255) DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FK_comment_user_id_user_id` (`user_id`),
  KEY `FK_comment_product_id_product_product_id` (`product_id`),
  KEY `FK_product_comment_parent_comment_id_product_comment_comment_id` (`parent_comment_id`),
  CONSTRAINT `FK_comment_product_id_product_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_comment_user_id_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_product_comment_parent_comment_id_product_comment_comment_id` FOREIGN KEY (`parent_comment_id`) REFERENCES `product_comment` (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='댓글';

-- 7. Create the `product_image` table
CREATE TABLE IF NOT EXISTS `product_image` (
  `image_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL COMMENT '상품번호',
  `image_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `FK_product_image_product_id_product_product_id` (`product_id`),
  CONSTRAINT `FK_product_image_product_id_product_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='상품이미지';
