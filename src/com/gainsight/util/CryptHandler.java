package com.gainsight.util;



import com.gainsight.testdriver.Log;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;
/**
 * Created by Giribabu on 08/08/15.
 */
public class CryptHandler {
    //Revert this change
    private static final String AES_ECB_PKCS5_PADDING = "AES/ECB/PKCS5Padding";
    private static final String SECERET_KEY_ALGORITH = "PBKDF2WithHmacSHA1";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String KEY_SPEC_ALGORITHM = "AES";
    private static SecretKeySpec secretKeySpec;

    private static volatile CryptHandler instance = null;
    private static final Object LOCK = new Object();

    private CryptHandler() {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECERET_KEY_ALGORITH);

            //Production will have the env var set with crypt key
            String encriptionKey = "jbara123";

            //Note a space in crypt_salt - which wasted 5 hr of my life time(Giribabu).
            PBEKeySpec keySpec = new PBEKeySpec(encriptionKey.toCharArray(), " crypt_salt".getBytes(CharEncoding.UTF_8), ITERATION_COUNT, KEY_LENGTH);
            secretKeySpec = new SecretKeySpec(factory.generateSecret(keySpec).getEncoded(), KEY_SPEC_ALGORITHM);
        } catch (Exception e) {
            Log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static CryptHandler getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new CryptHandler();
                }
            }
        }
        return instance;
    }

    public byte[] encrypt(String plainText) {
        if (StringUtils.isBlank(plainText)) {
            return null;
        }
        try {
            // This is where we mention PCKS5Padding
            Cipher aes = Cipher.getInstance(AES_ECB_PKCS5_PADDING);
            aes.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return aes.doFinal(plainText.getBytes(CharEncoding.UTF_8));
        } catch (Exception e) {
           e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String decrypt(byte[] cipherText) {
        if (ArrayUtils.isEmpty(cipherText)) {
            return null;
        }
        try {
            // This is where we mention PCKS5Padding
            Cipher aes = Cipher.getInstance(AES_ECB_PKCS5_PADDING);
            aes.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(aes.doFinal(cipherText), CharEncoding.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void encryptAllValues(Map<String, Object> map) {
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object obj = entry.getValue();
                if (obj != null) {
                    entry.setValue(encrypt(String.valueOf(obj)));
                }
            }
        }
    }

    public void encryptValues(Map<String, Object> map, String... keys) {
        if (MapUtils.isNotEmpty(map) && ArrayUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                Object obj = map.get(key);
                if (obj != null) {
                    map.put(key, encrypt(String.valueOf(obj)));
                }
            }
        }
    }
}
