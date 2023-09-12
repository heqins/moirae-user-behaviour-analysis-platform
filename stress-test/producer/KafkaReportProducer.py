import time

from confluent_kafka import Producer
from faker import Faker
import json
import random

fake = Faker()

# 随机生成要添加的字段
additional_fields = []
if random.choice([True, False]):  # 50% 的概率添加 app_height 字段
    additional_fields.append({"app_height": fake.random_int(min=100, max=200)})

if random.choice([True, False]):  # 50% 的概率添加 app_width 字段
    additional_fields.append({"app_width": fake.random_int(min=200, max=300)})

if random.choice([True, False]):  # 50% 的概率添加 view_name 字段
    additional_fields.append({"view_name": fake.word()})

class KafkaProducer:

    def __init__(self, bootstrap_servers, topic):
        self.bootstrap_servers = bootstrap_servers
        self.topic = topic
        self.producer = Producer({'bootstrap.servers': self.bootstrap_servers, "broker.address.family": "v4"})

    def generate_random_json(self):
        fake = Faker()
        data = {
            "event_name": "测试",
            "event_type": "测试",
            "event_time": int(time.time() * 1000),
            "app_id": "2crdwf5q",
            "app_version": "3.14.0",
            "app_beta_flag4": round(random.uniform(1.0, 5.0), 2),
            "unique_id": fake.md5(),
            **{k: v for d in additional_fields for k, v in d.items()},  # 添加随机字段
        }

        return json.dumps(data)

    def produce_messages(self, num_messages):
        for _ in range(num_messages):
            message = self.generate_random_json()
            self.producer.produce(self.topic, key=None, value=message)
            self.producer.flush()

if __name__ == "__main__":
    print("test")
    producer = KafkaProducer(bootstrap_servers="127.0.0.1:9092", topic="report-log-data")
    producer.produce_messages(1)