package com.admin.server.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class KeyUtil {

    public static UUID generateAppId() {
        return UUID.randomUUID();
    }

    public static String generateAppKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        // 使用 AES-256 位密钥
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();

        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}
