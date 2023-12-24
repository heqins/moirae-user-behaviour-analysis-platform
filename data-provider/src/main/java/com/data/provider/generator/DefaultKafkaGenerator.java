package com.data.provider.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DefaultKafkaGenerator {

    private static final String KAFKA_BROKER = "your_kafka_broker_address";
    private static final String KAFKA_TOPIC = "your_kafka_topic";

    public static void main(String[] args) {
        // Set Kafka producer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_BROKER);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Create Kafka producer
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            // Generate and send random data
            generateAndSendData(producer, KAFKA_TOPIC, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateAndSendData(Producer<String, String> producer, String topic, int numRecords) {
        ObjectMapper objectMapper = new ObjectMapper();
        Random random = new Random();

        for (int i = 0; i < numRecords; i++) {
            // Generate random data
            DataObject dataObject = generateRandomData(random);

            // Convert to JSON
            String jsonData = convertToJson(objectMapper, dataObject);

            // Send data to Kafka
            producer.send(new ProducerRecord<>(topic, jsonData));

            // Sleep for a short time to simulate a delay between records
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static DataObject generateRandomData(Random random) {
        DataObject dataObject = new DataObject();
        dataObject.setCommon(generateCommonData(random));
        dataObject.setActions(generateActions(random));
        dataObject.setDisplays(generateDisplays(random));
        dataObject.setPage(generatePageData(random));
        dataObject.setErr(generateErrorData(random));
        dataObject.setTs(System.currentTimeMillis());
        return dataObject;
    }

    private static CommonData generateCommonData(Random random) {
        CommonData commonData = new CommonData();
        commonData.setAr(randomString(6));
        commonData.setBa(randomString(10));
        commonData.setCh(randomString(8));
        commonData.setIsNew(random.nextBoolean() ? "1" : "0");
        commonData.setMd(randomString(12));
        commonData.setMid(randomString(16));
        commonData.setOs(randomString(10));
        commonData.setUid(String.valueOf(random.nextInt(1000)));
        commonData.setVc(randomString(8));
        return commonData;
    }

    private static List<ActionData> generateActions(Random random) {
        List<ActionData> actions = new ArrayList<>();
        ActionData actionData = new ActionData();
        actionData.setActionId(randomString(10));
        actionData.setItem(String.valueOf(random.nextInt(1000)));
        actionData.setItemType(randomString(8));
        actionData.setTs(System.currentTimeMillis());
        actions.add(actionData);
        return actions;
    }

    private static List<DisplayData> generateDisplays(Random random) {
        List<DisplayData> displays = new ArrayList<>();
        DisplayData displayData = new DisplayData();
        displayData.setDisplayType(randomString(8));
        displayData.setItem(String.valueOf(random.nextInt(1000)));
        displayData.setItemType(randomString(8));
        displayData.setOrder(random.nextInt(10));
        displayData.setPosId(random.nextInt(5));
        displays.add(displayData);
        return displays;
    }

    private static PageData generatePageData(Random random) {
        PageData pageData = new PageData();
        pageData.setDuringTime(random.nextInt(10000));
        pageData.setItem(String.valueOf(random.nextInt(1000)));
        pageData.setItemType(randomString(8));
        pageData.setLastPageId(randomString(10));
        pageData.setPageId(randomString(12));
        pageData.setSourceType(randomString(10));
        return pageData;
    }

    private static ErrorData generateErrorData(Random random) {
        ErrorData errorData = new ErrorData();
        errorData.setErrorCode(randomString(6));
        errorData.setMsg(randomString(20));
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

    class DisplayData {
        private String displayType;
        private String item;
        private String itemType;
        private int order;
        private int posId;

        // getters and setters
    }

    class PageData {
        private int duringTime;
        private String item;
        private String itemType;
        private String lastPageId;
        private String pageId;
        private String sourceType;

        // getters and setters
    }

    class ErrorData {
        private String errorCode;
        private String msg;

        // getters and setters
    }

    class ActionData {
        private String actionId;
        private String item;
        private String itemType;
        private long ts;

        // getters and setters
    }

    class CommonData {
        private String ar;
        private String ba;
        private String ch;
        private String isNew;
        private String md;
        private String mid;
        private String os;
        private String uid;
        private String vc;

        // getters and setters
    }

    class DataObject {
        private CommonData common;
        private List<ActionData> actions;
        private List<DisplayData> displays;
        private PageData page;
        private ErrorData err;
        private long ts;

        // getters and setters
    }
}
