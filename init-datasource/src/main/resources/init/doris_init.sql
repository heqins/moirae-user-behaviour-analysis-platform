CREATE DATABASE IF NOT EXISTS `user_behaviour_analysis`;

DROP TABLE IF EXISTS `user_behaviour_analysis`.`real_time_event_log`;
CREATE TABLE `user_behaviour_analysis`.`real_time_event_log` (
                                          app_id VARCHAR(255),
                                          event_time DATETIME,
                                          event_date DATE,
                                          event_name VARCHAR(255),
                                          event_data VARCHAR(1024)
) ENGINE=OLAP
DUPLICATE KEY (app_id, event_time)
PARTITION BY RANGE (event_date) ()
DISTRIBUTED BY HASH(event_name) BUCKETS 32
PROPERTIES(
    "dynamic_partition.time_unit" = "DAY",
    "dynamic_partition.start" = "-2",
    "dynamic_partition.end" = "2",
    "dynamic_partition.prefix" = "p",
    "dynamic_partition.buckets" = "32",
    "replication_num" = "1"
);

DROP TABLE IF EXISTS `user_behaviour_analysis`.`fail_event_log`;
CREATE TABLE `user_behaviour_analysis`.`fail_event_log` (
                                     app_id VARCHAR(255),
                                     event_time DATETIME,
                                     event_date DATE,
                                     data_name VARCHAR(64),
                                     error_reason VARCHAR(64),
                                     error_handling VARCHAR(64),
                                     event_type VARCHAR(32),
                                     event_data VARCHAR(1024),
                                     status TINYINT,
                                     event_name VARCHAR(255)
) ENGINE=OLAP
DUPLICATE KEY (app_id, event_time)
PARTITION BY RANGE (event_date) ()
DISTRIBUTED BY HASH(event_name) BUCKETS 32
PROPERTIES(
    "dynamic_partition.time_unit" = "DAY",
    "dynamic_partition.start" = "-2",
    "dynamic_partition.end" = "2",
    "dynamic_partition.prefix" = "p",
    "dynamic_partition.buckets" = "32",
    "replication_num" = "1"
);