import asyncio
from aiokafka import AIOKafkaProducer

async def send_message():
    # Kafka 服务器地址和端口
    bootstrap_servers = 'localhost:9092'
    # Kafka 主题
    topic = 'flume_test'

    # 创建 Kafka 生产者
    producer = AIOKafkaProducer(bootstrap_servers=bootstrap_servers)

    # 连接到 Kafka 服务器
    await producer.start()

    try:
        # 发送消息
        for i in range(10):
            message = f'Message {i}'
            await producer.send_and_wait(topic, message.encode('utf-8'))
            print(f'Sent: {message}')

    finally:
        # 关闭 Kafka 生产者连接
        await producer.stop()

if __name__ == '__main__':
    # 在异步上下文中运行发送消息的函数
    asyncio.run(send_message())
