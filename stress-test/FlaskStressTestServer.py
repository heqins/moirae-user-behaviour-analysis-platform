from flask import Flask
from producer.KafkaReportProducer import KafkaProducer

# 创建一个Flask应用
app = Flask(__name__)

# 定义一个路由，处理根目录的请求
@app.route('/hello')
def hello_world():
    num_messages = 1  # 每次发送的消息数量
    kafka_producer = KafkaProducer(
        bootstrap_servers="127.0.0.1:9092",  # 替换为你的Kafka服务器地址
        topic="report-log-data",  # 替换为你的Kafka topic名称
    )
    kafka_producer.produce_messages(num_messages)

    return 'Hello, World!'

# 启动Flask应用
if __name__ == '__main__':
    app.run(port = 6616)  # 默认情况下会在本地主机的端口5000上运行
