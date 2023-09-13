from locust import HttpUser, task


class KafkaUser(HttpUser):
    #wait_time = between(1, 3)

    @task
    def send_kafka_message(self):
        self.client.get("/hello")


if __name__ == "__main__":
    import os

    os.system("locust -f ReportLocustFile.py")
