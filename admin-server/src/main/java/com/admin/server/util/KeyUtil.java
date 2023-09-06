package com.admin.server.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.UUID;

public class KeyUtil {

    private static final KeyPair keyPair = initKey();

    private static KeyPair initKey() {
        try {
            Provider provider =new BouncyCastleProvider();
            Security.addProvider(provider);
            SecureRandom random = new SecureRandom();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", provider);
            generator.initialize(1024,random);
            return generator.generateKeyPair();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] decrypt(byte[] byteArray) {
        try {
            Provider provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
            Security.addProvider(provider);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", provider);

            PrivateKey privateKey = keyPair.getPrivate();
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            return cipher.doFinal(byteArray);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String decryptBase64(String string) {
        return new String(decrypt(Base64.decodeBase64(string.getBytes())));
    }

    public static String generateBase64PublicKey() {
        PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return new String(Base64.encodeBase64(publicKey.getEncoded()));
    }

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
