package com.data.provider.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DefaultKafkaGenerator implements DataGenerator{

    private static final String KAFKA_BROKER = "localhost:9092";
    private static final String KAFKA_TOPIC = "log-etl-main";

    private static final Integer RECORDS = 1000 * 1000 * 100;

    private static final String APP_ID = "2crdwf5q";

    private static final Random random = new Random();

    @Override
    public void generateData() {
        // Set Kafka producer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_BROKER);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Create Kafka producer
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            // Generate and send random data
            generateAndSendData(producer, KAFKA_TOPIC, RECORDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateAndSendData(Producer<String, String> producer, String topic, int numRecords) {
        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < numRecords; i++) {
            // Generate random data
            DataObject dataObject = generateRandomData();

            // Convert to JSON
            String jsonData = convertToJson(objectMapper, dataObject);

            // Send data to Kafka
            producer.send(new ProducerRecord<>(topic, jsonData));

            System.out.println("Sent data 个数:" + (i + 1));
        }
    }

    private static DataObject generateRandomData() {
        DataObject dataObject = new DataObject();
        dataObject.setCommon(generateCommonData());
        dataObject.setAction(generateActions());
        dataObject.setErrorData(generateErrorData());
        dataObject.setTs(System.currentTimeMillis());
        return dataObject;
    }

    private static CommonData generateCommonData() {
        CommonData commonData = new CommonData();

        int major = random.nextInt(10);   // Assuming major version can be between 0 and 9
        int minor = random.nextInt(10);  // Assuming minor version can be between 0 and 99
        int patch = random.nextInt(10);  // Assuming patch version can be between 0 and 99

        String version =  major + "." + minor + "." + patch;

        commonData.setAppId(APP_ID);

        List<String> fruits = new ArrayList<>();
        fruits.add("ios");
        fruits.add("android");
        fruits.add("win");

        String randomFruit = fruits.get(random.nextInt(3));

        commonData.setAppVersion(version);

        List<String> events = List.of("login", "click", "logout", "add_to_shopcart", "purchase");
        commonData.setEventName(events.get(random.nextInt(events.size())));
        commonData.setOs(randomFruit);
        commonData.setUniqueId(randomString(12));

        return commonData;
    }

    private static ActionData generateActions() {
        ActionData actionData = new ActionData();
        actionData.setActionId(randomString(10));
        actionData.setItem(String.valueOf(random.nextInt(1000)));
        actionData.setItemType(randomString(8));
        return actionData;
    }

    private static ErrorData generateErrorData() {
        ErrorData errorData = new ErrorData();
        errorData.setErrorCode("200");
        errorData.setMsg("正常");
        return errorData;
    }

    private static String convertToJson(ObjectMapper objectMapper, DataObject dataObject) {
        try {
            return objectMapper.writeValueAsString(dataObject);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String randomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    static class ErrorData {
        public String getErrorCode() {
            return error_code;
        }

        public void setErrorCode(String errorCode) {
            this.error_code = errorCode;
        }

        private String error_code;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        private String msg;

        // getters and setters
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    static class ActionData {
        private String action_id;
        private String item;

        public String getActionId() {
            return action_id;
        }

        public void setActionId(String actionId) {
            this.action_id = actionId;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getItemType() {
            return item_type;
        }

        public void setItemType(String itemType) {
            this.item_type = itemType;
        }

        private String item_type;

        // getters and setters
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    static class CommonData {
        private String event_name;

        private String os;

        private String unique_id;
        private String app_id;

        public String getEventName() {
            return event_name;
        }

        public void setEventName(String eventName) {
            this.event_name = eventName;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getUniqueId() {
            return unique_id;
        }

        public void setUniqueId(String uniqueId) {
            this.unique_id = uniqueId;
        }

        public String getAppId() {
            return app_id;
        }

        public void setAppId(String appId) {
            this.app_id = appId;
        }

        public String getAppVersion() {
            return app_version;
        }

        public void setAppVersion(String appVersion) {
            this.app_version = appVersion;
        }

        private String app_version;

        // getters and setters
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    static class DataObject {
        private CommonData common;

        private ActionData action;

        public ErrorData getErrorData() {
            return errorData;
        }

        public void setErrorData(ErrorData errorData) {
            this.errorData = errorData;
        }

        private ErrorData errorData;

        public CommonData getCommon() {
            return common;
        }

        public void setCommon(CommonData common) {
            this.common = common;
        }

        public ActionData getAction() {
            return action;
        }

        public void setAction(ActionData action) {
            this.action = action;
        }

        public long getTs() {
            return ts;
        }

        public void setTs(long ts) {
            this.ts = ts;
        }

        private long ts;

        // getters and setters
    }
}
