CREATE TABLE `user_behaviour_analysis`.`event_log_test_app` (
                                                                unique_id VARCHAR(64),
                                                                event_time BIGINT(20),
                                                                event_date DATE,
                                                                event_name VARCHAR(255),
                                                                event_data VARCHAR(1024),
                                                                event_type VARCHAR(32),
                                                                device_id VARCHAR(64),
                                                                country VARCHAR(64),
                                                                screen_width INT,
                                                                screen_height INT,
                                                                os VARCHAR(64)
) ENGINE=OLAP
DUPLICATE KEY (unique_id, event_time)
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