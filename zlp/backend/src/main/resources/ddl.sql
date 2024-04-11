CREATE TABLE `userr` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`username` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户名' COLLATE 'utf8mb4_general_ci',
	`pwd` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '密码' COLLATE 'utf8mb4_general_ci',
	`namee` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '昵称' COLLATE 'utf8mb4_general_ci',
	`img` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像' COLLATE 'utf8mb4_general_ci',
	`typee` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '类型，管理员、药店、用户' COLLATE 'utf8mb4_general_ci',
	`statuss` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '状态，审核中、已通过、未通过' COLLATE 'utf8mb4_general_ci',
	`shopcontent` TEXT COMMENT '店铺介绍' COLLATE 'utf8mb4_general_ci',
	`shopimgs` TEXT  COMMENT '店铺资质' COLLATE 'utf8mb4_general_ci',
	`address` TEXT  COMMENT '地址' COLLATE 'utf8mb4_general_ci',
	`deletetime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '删除时间',
	`createtime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='用户'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `knowledge` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`title` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '标题' COLLATE 'utf8mb4_general_ci',
	`typee` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '类型，用药知识、健康知识、公告' COLLATE 'utf8mb4_general_ci',
	`img` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '图片' COLLATE 'utf8mb4_general_ci',
	`content` TEXT COMMENT '内容' COLLATE 'utf8mb4_general_ci',
	`deletetime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '删除时间',
	`createtime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='知识普及'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `medicine` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`shopid` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '药店ID',
	`namee` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '药品名称' COLLATE 'utf8mb4_general_ci',
	`description` TEXT COMMENT '药品描述' COLLATE 'utf8mb4_general_ci',
	`img` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '图片' COLLATE 'utf8mb4_general_ci',
	`price` INT(11) NOT NULL DEFAULT '0' COMMENT '单价，元' COLLATE 'utf8mb4_general_ci',
	`deletetime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '删除时间',
	`createtime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='药品'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;


CREATE TABLE `bookmark` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`userid` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '用户ID',
	`dataid` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '数据ID',
	`typee` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '类型，药品、店铺' COLLATE 'utf8mb4_general_ci',
	`deletetime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '删除时间',
	`createtime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='收藏'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;


CREATE TABLE `cart` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`userid` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '用户ID',
    `medicineid` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '药品ID',
    `cnt` INT(11) NOT NULL DEFAULT '0' COMMENT '数量',
	`deletetime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '删除时间',
	`createtime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='购物车'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `orderr` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`orderno` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '订单号' COLLATE 'utf8mb4_general_ci',
	`userid` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '用户ID',
	`shopid` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '店铺ID',
	`medicineid` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '药品ID',
	`namee` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '药品名称' COLLATE 'utf8mb4_general_ci',
	`price` INT(11) NOT NULL DEFAULT '0' COMMENT '单价，元' COLLATE 'utf8mb4_general_ci',
	`cnt` INT(11) NOT NULL DEFAULT '0' COMMENT '数量',
    `addcontent` TEXT COMMENT '地址' COLLATE 'utf8mb4_general_ci',
    `statuss` VARCHAR(255) NOT NULL DEFAULT '待发货' COMMENT '发货状态，待发货、已发货、已签收' COLLATE 'utf8mb4_general_ci',
    `evastatus` VARCHAR(255) NOT NULL DEFAULT '未评价' COMMENT '评价状态，未评价、已评价' COLLATE 'utf8mb4_general_ci',
    `content` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '评价内容' COLLATE 'utf8mb4_general_ci',
    `showcontent` VARCHAR(255) NOT NULL DEFAULT '是' COMMENT '是否显示评价，是、否' COLLATE 'utf8mb4_general_ci',
    `rate` INT(11) NOT NULL DEFAULT '0' COMMENT '评分',
	`deletetime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '删除时间',
	`createtime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='订单'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `message` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`userid` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '用户ID',
	`shopid` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '店铺ID',
	`typee` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '类型,toshop、touser' COLLATE 'utf8mb4_general_ci',
	`content` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '内容' COLLATE 'utf8mb4_general_ci',
	`deletetime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '删除时间',
	`createtime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='消息'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;


