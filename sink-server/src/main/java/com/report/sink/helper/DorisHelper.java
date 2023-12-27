package com.report.sink.helper;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.api.common.enums.AttributeDataTypeEnum;
import com.api.common.model.dto.sink.EventLogDTO;
import com.api.common.model.dto.sink.TableColumnDTO;
import com.report.sink.service.ICacheService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.jni.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author heqin
 */
@Component
public class DorisHelper {

    private final Logger log = LoggerFactory.getLogger(DorisHelper.class);
    
    private static final String COLUMN_QUERY_SQL = "select column_name, column_type, is_nullable\n" +
            "FROM information_schema.columns\n" +
            "WHERE table_schema = ? AND table_name = ?;";

    private static final String ALTER_ADD_COLUMN_SQL = "ALTER TABLE `%s`.`%s` ADD COLUMN %s %s;";

    @Resource(name = "redisCacheService")
    private ICacheService redisCacheService;

    @Resource(name = "localCacheServiceImpl")
    private ICacheService localCacheService;

    @Resource(name = "dorisDataSource")
    private DataSource dataSource;

    /**
     * 构建HTTP客户端
     */
    private final static HttpClientBuilder httpClientBuilder = HttpClients
            .custom()
            .setRedirectStrategy(new DefaultRedirectStrategy() {
                @Override
                protected boolean isRedirectable(String method) {
                    // If the connection target is FE, you need to deal with 307 redirect。
                    return true;
                }
            });

    /**
     * FE IP Address
     */
    private final static String HOST = "localhost";
    /**
     * FE port
     */
    private final static int PORT = 8030;
    /**
     * db name
     */
    private final static String DATABASE = "user_behaviour_analysis";
    /**
     * table name
     */
    private final static String TABLE = "event_log_detail_2crdwf5q";

    /**
     * user name
     */
    private final static String USER = "root";
    /**
     * user password
     */
    private final static String PASSWD = "";

    /**
     * The path of the local file to be imported
     */
    private final static String LOAD_FILE_NAME = "c:/es/1.csv";

    /**
     * http path of stream load task submission
     */
    private final static String loadUrl = String.format("http://%s:%s/api/%s/%s/_stream_load",
            HOST, PORT, DATABASE, TABLE);

    /**
     * 文件数据导入
     * @param file
     * @throws Exception
     */
    public void load(File file) throws Exception {
        try (CloseableHttpClient client = httpClientBuilder.build()) {
            HttpPut put = new HttpPut(loadUrl);
            put.removeHeaders(HttpHeaders.CONTENT_LENGTH);
            put.removeHeaders(HttpHeaders.TRANSFER_ENCODING);
            put.setHeader(HttpHeaders.EXPECT, "100-continue");
            put.setHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader(USER, PASSWD));

            // You can set stream load related properties in the Header, here we set label and column_separator.
            put.setHeader("label", UUID.randomUUID().toString());
            put.setHeader("column_separator", ",");

            // Set up the import file. Here you can also use StringEntity to transfer arbitrary data.
            FileEntity entity = new FileEntity(file);
            put.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(put)) {
                String loadResult = "";
                if (response.getEntity() != null) {
                    loadResult = EntityUtils.toString(response.getEntity());
                }

                final int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new IOException(String.format("Stream load failed. status: %s load result: %s", statusCode, loadResult));
                }
                System.out.println("Get load result: " + loadResult);
            }
        }
    }

    /**
     * 封装认证信息
     * @param username
     * @param password
     * @return
     */
    private String basicAuthHeader(String username, String password) {
        final String tobeEncode = username + ":" + password;
        return "Basic " +  Base64.encode(tobeEncode.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JSON格式的数据导入
     * @param jsonData
     * @throws Exception
     */
    public void loadJson(String jsonData) throws Exception {
        try (CloseableHttpClient client = httpClientBuilder.build()) {
            HttpPut put = new HttpPut(loadUrl);
            put.removeHeaders(HttpHeaders.CONTENT_LENGTH);
            put.removeHeaders(HttpHeaders.TRANSFER_ENCODING);
            put.setHeader(HttpHeaders.EXPECT, "100-continue");
            put.setHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader(USER, PASSWD));

            // You can set stream load related properties in the Header, here we set label and column_separator.
            put.setHeader("label", UUID.randomUUID().toString());
            put.setHeader("column_separator", ",");
            put.setHeader("format", "json");

            // Set up the import file. Here you can also use StringEntity to transfer arbitrary data.
            StringEntity entity = new StringEntity(jsonData);
            put.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(put)) {
                String loadResult = "";
                if (response.getEntity() != null) {
                    loadResult = EntityUtils.toString(response.getEntity());
                }

                final int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new IOException(String.format("Stream load failed. status: %s load result: %s", statusCode, loadResult));
                }
                System.out.println("Get load result: " + loadResult);
            }catch (Exception e) {
                System.out.println("test");
            }
        }
    }

    public static void convertJsonToCsv(String jsonString, String outputCsvFile, int numRecords) {
        try (FileWriter fileWriter = new FileWriter(outputCsvFile);
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader("eventName", "os", "uniqueId", "appId", "appVersion", "actionId", "item", "itemType", "errorCode", "msg", "ts"))) {

            for (int i = 0; i < numRecords; i++) {
                JSONObject jsonObject = JSONUtil.parseObj(jsonString);
                String eventName = jsonObject.getJSONObject("common").getStr("eventName");
                String os = jsonObject.getJSONObject("common").getStr("os");
                String uniqueId = jsonObject.getJSONObject("common").getStr("uniqueId");
                String appId = jsonObject.getJSONObject("common").getStr("appId");
                String appVersion = jsonObject.getJSONObject("common").getStr("appVersion");
                String actionId = jsonObject.getJSONObject("action").getStr("actionId");
                String item = jsonObject.getJSONObject("action").getStr("item");
                String itemType = jsonObject.getJSONObject("action").getStr("itemType");
                String errorCode = jsonObject.getJSONObject("errorData").getStr("errorCode");
                String msg = jsonObject.getJSONObject("errorData").getStr("msg");
                long ts = jsonObject.getLong("ts");

                csvPrinter.printRecord(eventName, os, uniqueId, appId, appVersion, actionId, item, itemType, errorCode, msg, ts);
            }

            System.out.println("CSV file generated successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<TableColumnDTO> getTableColumnInfos(String dbName, String tableName) {
        if (StringUtils.isBlank(dbName) || StringUtils.isBlank(tableName)) {
            return null;
        }

        List<TableColumnDTO> columns = localCacheService.getColumnCache(dbName, tableName);
        if (columns != null) {
            return columns;
        }

        columns = redisCacheService.getColumnCache(dbName, tableName);
        if (columns != null) {
            return columns;
        }

        columns = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(COLUMN_QUERY_SQL)) {
                statement.setString(1, dbName);
                statement.setString(2, tableName);

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String columnName = resultSet.getString("column_name");
                    String columnType = resultSet.getString("column_type");
                    String isNullable = resultSet.getString("is_nullable");

                    String columnJavaType = transferColumnTypeFromDorisToJava(columnType.toLowerCase(Locale.ROOT));
                    if (StringUtils.isBlank(columnJavaType)) {
                        throw new IllegalStateException("column java type not found");
                    }

                    TableColumnDTO columnDTO = new TableColumnDTO();
                    columnDTO.setColumnName(columnName);
                    columnDTO.setStatus(1);
                    columnDTO.setNullable(Objects.equals(isNullable.toLowerCase(Locale.ROOT), "yes"));
                    columnDTO.setTableName(tableName);
                    columnDTO.setColumnType(columnJavaType);

                    columns.add(columnDTO);
                }
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                }
                log.error("DorisHelper getTableColumnInfos sql error", e);
            }
        }catch (SQLException e) {
            log.error("DorisHelper getTableColumnInfos sql error", e);
        }

        localCacheService.setColumnCache(dbName, tableName, columns);
        redisCacheService.setColumnCache(dbName, tableName, columns);

        return columns;
    }

    public String transferColumnTypeFromDorisToJava(String columnType) {
        if (columnType == null) {
            return "";
        }

        if (columnType.startsWith("varchar")) {
            return "java.lang.String";
        }

        if (columnType.startsWith("int")) {
            return "java.lang.Integer";
        }

        if (columnType.startsWith("bigint")) {
            return "java.lang.Long";
        }

        if (columnType.startsWith("decimal")) {
            return "java.math.BigDecimal";
        }

        if (columnType.startsWith("date")) {
            return "java.util.Date";
        }

        if (columnType.startsWith("tinyint")) {
            return "java.lang.Byte";
        }

        if (columnType.startsWith("smallint")) {
            return "java.lang.Short";
        }

        if (columnType.startsWith("tinyint(0)") || columnType.startsWith("tinyint(1)")) {
            return "java.lang.Boolean";
        }

        return "";
    }

    public void addTableColumn(EventLogDTO eventLogDTO, Set<String> jsonFields) {
        List<String> alterQueries = new ArrayList<>(jsonFields.size());
        for (String jsonField: jsonFields) {
            // todo:
            Object obj = eventLogDTO.getFieldValueMap().get(jsonField);
            if (obj == null) {
                throw new IllegalStateException("DorisHelper changeTableSchema column obj is null");
            }

            String className = obj.getClass().getCanonicalName();
            String type = AttributeDataTypeEnum.getDefaultDataTypeByClass(className);

            if (StringUtils.isBlank(type)) {
                log.warn("DorisHelper type not found className:{} field:{}", className, jsonField);
                continue;
            }

            String query = String.format(ALTER_ADD_COLUMN_SQL, eventLogDTO.getDbName(), eventLogDTO.getTableName(), jsonField, type);
            alterQueries.add(query);
        }

        if (!CollectionUtils.isEmpty(alterQueries)) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);

                for (String sql: alterQueries) {
                    try (PreparedStatement statement = connection.prepareStatement(sql)){
                        statement.execute();
                    } catch (SQLException e) {
                        connection.rollback();
                        log.warn("DorisHelper changeTableSchema execute error msg:{}", e.getMessage());
                    }
                }

                connection.commit();
            }catch (SQLException e) {
                log.warn("DorisHelper changeTableSchema alter column commit error msg:{}", e.getMessage());
            }
        }

        localCacheService.removeColumnCache(eventLogDTO.getDbName(), eventLogDTO.getTableName());
        redisCacheService.removeColumnCache(eventLogDTO.getDbName(), eventLogDTO.getTableName());
    }

    public void tableInsertData(String sql, List<TableColumnDTO> columnDTOList, List<Map<String, Object>> jsonDataList) {
        if (sql == null || jsonDataList == null) {
            return;
        }

        Connection insertConnection;
        try {
            insertConnection = dataSource.getConnection();
        }catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }

        try {
            insertConnection.setAutoCommit(false);
            PreparedStatement statement = insertConnection.prepareStatement(sql);
            for (Map<String, Object> jsonObject: jsonDataList) {
                if (columnDTOList.size() < jsonObject.size()) {
                    log.error("DorisHelper tableInsertData columnDTOList size < jsonObject size");
                    return;
                }

                for (int i = 0; i < columnDTOList.size(); i++) {
                    String columnName = columnDTOList.get(i).getColumnName();
                    String columnType = columnDTOList.get(i).getColumnType();

                    Object value = jsonObject.get(columnName);
                    if (value == null) {
                        if (!columnDTOList.get(i).getNullable()) {
                            log.warn("DorisHelper tableInsertData column is not nullable but yet null jsonObject:{}", JSONUtil.toJsonStr(jsonObject));
                            return;
                        }

                        switch (columnType) {
                            case "java.lang.Byte":
                                value = 0;
                                break;
                            case "java.lang.Short":
                                value = 0;
                                break;
                            case "java.lang.String":
                                value = "";
                                break;
                            case "java.lang.Integer":
                                value = 0;
                                break;
                            case "java.lang.Float":
                                value = 0.0f;
                                break;
                            case "java.lang.Double":
                                value = 0.0d;
                                break;
                            case "java.lang.Long":
                                value = 0L;
                                break;
                            case "java.util.Date":
                                value = new Date();
                                break;
                            default:
                        }
                    }

                    statement.setObject(i + 1, value);
                }

                statement.addBatch();
                statement.executeBatch();
            }

            insertConnection.commit();
        }catch (SQLException e) {
            try {
                insertConnection.rollback();
            }catch (SQLException e1) {
            }

            log.error("DorisHelper tableInsertData insert execute error", e);
            throw new IllegalStateException("插入失败");
        }finally {
            try {
                insertConnection.close();
            }catch (SQLException e) {
                log.error("DorisHelper tableInsertData insert close error", e);
            }
        }
    }
}
