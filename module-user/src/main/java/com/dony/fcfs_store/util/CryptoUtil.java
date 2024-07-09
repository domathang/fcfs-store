package com.dony.fcfs_store.util;

import com.dony.common.exception.CustomException;
import com.dony.common.exception.ErrorCode;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CryptoUtil {

    private final String ALGORITHM = "AES";

    @Value("${auth.symmetric.key}")
    private String symmetricKey;


    public SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(symmetricKey);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public String encrypt(String field) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] bytes = cipher.doFinal(field.getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR, "개인정보 암호화 중 에러");
        }
    }

    public String decrypt(String field) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            byte[] decodedBytes = Base64.getDecoder()
                    .decode(field);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR, "개인정보 복호화 중 에러");
        }
    }
}
