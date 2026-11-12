ALTER TABLE `classroom` ADD COLUMN `sign_in_active` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '签到是否激活' AFTER `status`;
