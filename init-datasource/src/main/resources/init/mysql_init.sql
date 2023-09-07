DROP TABLE IF EXISTS `user_behaviour_analysis`.`app`;
CREATE TABLE `user_behaviour_analysis`.`app`  (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                 `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                 `app_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                 `app_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                 `create_user` varchar(64) NOT NULL,
                                 `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 `status` tinyint(4) NULL DEFAULT 0 COMMENT '是否关闭 0 - false 1 - true',
                                 `save_month` int(11) NULL DEFAULT 1 COMMENT '保存n个月',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE INDEX `app_name`(`app_name`) USING BTREE,
                                 UNIQUE INDEX `app_id`(`app_id`) USING BTREE,
                                 INDEX `app_create_by`(`create_user`, `app_name`, `status`) USING BTREE,
                                 INDEX `app_isclose`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

DROP TABLE IF EXISTS `user_behaviour_analysis`.`attribute`;
CREATE TABLE `user_behaviour_analysis`.`attribute`  (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `attribute_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '属性名',
                                       `show_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '显示名',
                                       `data_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '数据类型',
                                       `attribute_type` tinyint(4) NULL DEFAULT 1 COMMENT '默认为1 （1为预置属性，2为自定义属性）',
                                       `attribute_source` tinyint(4) NULL DEFAULT 1 COMMENT '默认为1 （1为用户属性，2为事件属性）',
                                       `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `app_id` varchar(255) NULL DEFAULT 0 COMMENT 'app_id',
                                       `status` tinyint(4) NULL DEFAULT 0 COMMENT '是否显示 0为不显示 1为显示 默认不显示',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE INDEX `attribute_name_attribute_source`(`attribute_name`, `attribute_source`, `app_id`) USING BTREE,
                                       INDEX `attribute_id_source`(`app_id`, `attribute_source`, `attribute_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4022 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

DROP TABLE IF EXISTS `user_behaviour_analysis`.`debug_device`;
CREATE TABLE `user_behaviour_analysis`.`debug_device`  (
                                          `id` int(11) NOT NULL AUTO_INCREMENT,
                                          `app_id` varchar(255) NULL DEFAULT 0,
                                          `device_id` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                          `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                          `create_by` int(11) NULL DEFAULT NULL,
                                          `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                          PRIMARY KEY (`id`) USING BTREE,
                                          UNIQUE INDEX `debug_device_uq`(`app_id`, `device_id`) USING BTREE,
                                          INDEX `debug_device_appid_createby`(`app_id`, `create_by`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- DROP TABLE IF EXISTS `user_behaviour_analysis`.`gm_operator_log`;
-- CREATE TABLE `user_behaviour_analysis`.`gm_operator_log`  (
--                                              `id` int(11) NOT NULL AUTO_INCREMENT,
--                                              `operator_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '操作者名字',
--                                              `operator_id` int(11) NULL DEFAULT 0 COMMENT '操作者id',
--                                              `operator_action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '请求路由',
--                                              `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--                                              `method` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求方法',
--                                              `body` blob NOT NULL COMMENT '请求body',
--                                              `operator_role_id` int(11) NOT NULL,
--                                              PRIMARY KEY (`id`) USING BTREE,
--                                              INDEX `operator_action`(`operator_action`) USING BTREE,
--                                              INDEX `operator_id`(`operator_id`) USING BTREE,
--                                              INDEX `operator_role_id`(`operator_role_id`) USING BTREE,
--                                              INDEX `operator_id_act_role`(`operator_action`, `operator_id`, `operator_role_id`) USING BTREE
-- ) ENGINE = InnoDB AUTO_INCREMENT = 2940 CHARACTER SET = utf8 COLLATE = utf8_general_ci;

DROP TABLE IF EXISTS `user_behaviour_analysis`.`sys_role`;
CREATE TABLE `user_behaviour_analysis`.`sys_role`  (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                     `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `role_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                     `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;
INSERT INTO `user_behaviour_analysis`.`sys_role` VALUES (1, 'admin', '超级管理员', '[{\"path\":\"/behavior-analysis\",\"component\":\"layout\",\"redirect\":\"/behavior-analysis/index\",\"alwaysShow\":false,\"meta\":{\"title\":\"行为分析\",\"icon\":\"el-icon-link\"},\"children\":[{\"path\":\"event/:id\",\"component\":\"views/behavior-analysis/event\",\"name\":\"event\",\"meta\":{\"title\":\"事件分析\",\"dynamic\":true,\"icon\":\"el-icon-data-line\"}},{\"path\":\"retention/:id\",\"component\":\"views/behavior-analysis/retention\",\"name\":\"retention\",\"meta\":{\"title\":\"留存分析\",\"dynamic\":true,\"icon\":\"el-icon-data-analysis\"}},{\"path\":\"funnel/:id\",\"component\":\"views/behavior-analysis/funnel\",\"name\":\"funnel\",\"meta\":{\"title\":\"漏斗分析\",\"dynamic\":true,\"icon\":\"el-icon-data-board\"}},{\"path\":\"trace/:id\",\"component\":\"views/behavior-analysis/trace\",\"name\":\"trace\",\"meta\":{\"title\":\"智能路径分析\",\"dynamic\":true,\"icon\":\"el-icon-bicycle\"}}]},{\"path\":\"/user-analysis\",\"component\":\"layout\",\"redirect\":\"/user-analysis/attr\",\"alwaysShow\":false,\"meta\":{\"title\":\"用户分析\",\"icon\":\"el-icon-pie-chart\"},\"children\":[{\"path\":\"attr/:id\",\"component\":\"views/user-analysis/index\",\"name\":\"attr\",\"meta\":{\"title\":\"用户属性分析\",\"dynamic\":true,\"icon\":\"el-icon-s-custom\"}},{\"path\":\"group\",\"component\":\"views/user-analysis/group\",\"name\":\"group\",\"meta\":{\"title\":\"用户分群\",\"icon\":\"el-icon-user\"}},{\"isInside\":true,\"path\":\"user_list\",\"component\":\"views/user-analysis/user_list\",\"name\":\"user_list\",\"meta\":{\"title\":\"用户列表\",\"icon\":\"el-icon-user-solid\"}},{\"isInside\":true,\"path\":\"user_info/:uid/:index\",\"component\":\"views/user-analysis/user_info\",\"name\":\"user_info\",\"meta\":{\"title\":\"用户事件详情\",\"dynamic\":true,\"icon\":\"el-icon-s-custom\"}}]},{\"path\":\"/manager\",\"component\":\"layout\",\"redirect\":\"/manager/event\",\"alwaysShow\":false,\"meta\":{\"title\":\"数据管理\",\"icon\":\"el-icon-edit\"},\"children\":[{\"path\":\"event\",\"component\":\"views/manager/event\",\"name\":\"event\",\"meta\":{\"title\":\"事件管理\",\"icon\":\"el-icon-s-management\"}},{\"path\":\"log\",\"component\":\"views/manager/log\",\"name\":\"log\",\"meta\":{\"title\":\"埋点管理\",\"icon\":\"el-icon-notebook-1\"}}]},{\"path\":\"/permission\",\"component\":\"layout\",\"redirect\":\"/permission/role\",\"alwaysShow\":true,\"meta\":{\"title\":\"权限\",\"icon\":\"el-icon-user-solid\"},\"children\":[{\"path\":\"role\",\"component\":\"views/permission/role\",\"name\":\"RolePermission\",\"meta\":{\"title\":\"角色管理\",\"icon\":\"el-icon-s-check\"}},{\"path\":\"user\",\"component\":\"views/permission/user\",\"name\":\"user\",\"meta\":{\"title\":\"用户管理\",\"icon\":\"el-icon-user\"}},{\"path\":\"operator_log\",\"component\":\"views/permission/operator_log\",\"name\":\"operator_log\",\"meta\":{\"title\":\"操作日志列表\",\"icon\":\"el-icon-s-order\"}}]},{\"path\":\"/app\",\"component\":\"layout\",\"children\":[{\"path\":\"/app/app\",\"component\":\"views/app/index\",\"name\":\"index\",\"meta\":{\"title\":\"应用管理\",\"icon\":\"el-icon-s-goods\"}}]}]', '2022-02-24 21:03:07', '2022-01-07 14:56:23');

DROP TABLE IF EXISTS `user_behaviour_analysis`.`sys_user`;
CREATE TABLE `user_behaviour_analysis`.`sys_user`  (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                     `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                     `role_id` bigint(20) NULL DEFAULT NULL COMMENT '角色id',
                                     `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     `last_login_time` bigint(20) NULL DEFAULT 0,
                                     `status` tinyint NULL DEFAULT 0 COMMENT '是否禁止该账号 0 - 否 1 - 是',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE INDEX `sys_user_username`(`username`) USING BTREE COMMENT '角色名唯一索引',
                                     INDEX `sys_user_username_pwd`(`username`, `password`, `status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

DROP TABLE IF EXISTS `user_behaviour_analysis`.`meta_attribute_relation`;
CREATE TABLE `user_behaviour_analysis`.`meta_attribute_relation`  (
                                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                `app_id` varchar(255) NULL DEFAULT 0,
                                                `event_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                                `event_attribute` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                                PRIMARY KEY (`id`) USING BTREE,
                                                UNIQUE INDEX `event_name_event_attr`(`app_id`, `event_name`, `event_attribute`) USING BTREE,
                                                INDEX `event_name_event_attr1`(`app_id`, `event_name`) USING BTREE,
                                                INDEX `event_name_event_attr2`(`app_id`, `event_attribute`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

DROP TABLE IF EXISTS `user_behaviour_analysis`.`meta_event`;
CREATE TABLE `user_behaviour_analysis`.`meta_event`  (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `app_id` varchar(255) NULL DEFAULT NULL,
                                        `event_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                        `show_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                        `yesterday_count` int(11) NULL DEFAULT 0,
                                        `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                        `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                        `status` tinyint DEFAULT 1 COMMENT "启动状态 1-启用 0-关闭",
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `meta_event_appid_event_name`(`app_id`, `event_name`) USING BTREE,
                                        INDEX `meta_event_appid`(`app_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

DROP TABLE IF EXISTS `user_behaviour_analysis`.`panel`;
CREATE TABLE `user_behaviour_analysis`.`panel`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `folder_id` int(11) NULL DEFAULT 0,
                                    `panel_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                    `managers` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                    `create_by` int(11) NULL DEFAULT 0,
                                    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    `report_tables` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `panel_unique`(`folder_id`, `panel_name`, `create_by`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

DROP TABLE IF EXISTS `user_behaviour_analysis`.`panel_folder`;
CREATE TABLE `user_behaviour_analysis`.`panel_folder`  (
                                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                           `folder_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                           `create_by` int(11) NULL DEFAULT 0,
                                           `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                           `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                           `app_id` varchar(255) NULL DEFAULT 0,
                                           PRIMARY KEY (`id`) USING BTREE,
                                           UNIQUE INDEX `panel_folder_unique`(`folder_name`, `create_by`, `app_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

DROP TABLE IF EXISTS `user_behaviour_analysis`.`report_table`;
CREATE TABLE `user_behaviour_analysis`.`report_table`  (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                          `app_id` varchar(255) NULL DEFAULT NULL,
                                          `user_id` int(11) NULL DEFAULT NULL,
                                          `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                          `rt_type` tinyint(8) NULL DEFAULT 0,
                                          `data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                          `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                          `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          UNIQUE INDEX `report_table_appid_user_id_name_type`(`app_id`, `user_id`, `name`, `rt_type`) USING BTREE,
                                          INDEX `report_table_appid_user_id`(`app_id`, `user_id`, `rt_type`) USING BTREE,
                                          INDEX `report_table_id_user_id`(`id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

DROP TABLE IF EXISTS `user_behaviour_analysis`.`user_group`;
CREATE TABLE `user_behaviour_analysis`.`user_group` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `group_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
                                       `group_remark` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
                                       `create_by` int(11) NOT NULL DEFAULT '0',
                                       `user_count` int(11) NOT NULL DEFAULT '0',
                                       `user_list` blob NOT NULL,
                                       `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       `app_id` varchar(255) DEFAULT '0',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `user_group_name` (`group_name`,`app_id`) USING BTREE,
                                       KEY `user_group_appid` (`id`,`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci

