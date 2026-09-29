package br.edu.securitystore.platform.privacy;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PiiProtection {
    private final byte[] encryptionKey,fingerprintKey;
    private final SecureRandom random=new SecureRandom();
    public PiiProtection(@Value("${security.pii.encryption-key}") String encodedKey){
        try{encryptionKey=Base64.getDecoder().decode(encodedKey);}catch(IllegalArgumentException ex){throw new IllegalStateException("PII_ENCRYPTION_KEY deve ser Base64",ex);}
        if(encryptionKey.length!=32)throw new IllegalStateException("PII_ENCRYPTION_KEY deve conter exatamente 32 bytes");
        try{fingerprintKey=MessageDigest.getInstance("SHA-256").digest(concat(encryptionKey,"cpf-fingerprint-v1".getBytes(StandardCharsets.UTF_8)));}
        catch(NoSuchAlgorithmException ex){throw new IllegalStateException(ex);}
    }
    public String encrypt(String value){if(value==null||value.isEmpty())return value;try{byte[] nonce=new byte[12];random.nextBytes(nonce);Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(encryptionKey,"AES"),new GCMParameterSpec(128,nonce));byte[] encrypted=cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));return "v1:"+Base64.getEncoder().encodeToString(concat(nonce,encrypted));}catch(GeneralSecurityException ex){throw new IllegalStateException("Falha ao proteger dado pessoal",ex);}}
    public String decrypt(String value){if(value==null||value.isEmpty())return value;if(!value.startsWith("v1:"))throw new IllegalStateException("Dado pessoal sem formato criptografado suportado");try{byte[] data=Base64.getDecoder().decode(value.substring(3));byte[] nonce=Arrays.copyOfRange(data,0,12);byte[] encrypted=Arrays.copyOfRange(data,12,data.length);Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.DECRYPT_MODE,new SecretKeySpec(encryptionKey,"AES"),new GCMParameterSpec(128,nonce));return new String(cipher.doFinal(encrypted),StandardCharsets.UTF_8);}catch(GeneralSecurityException|IllegalArgumentException ex){throw new IllegalStateException("Falha ao ler dado pessoal protegido",ex);}}
    public String fingerprint(String value){try{Mac mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(fingerprintKey,"HmacSHA256"));return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));}catch(GeneralSecurityException ex){throw new IllegalStateException(ex);}}
    private static byte[] concat(byte[] a,byte[] b){byte[] result=Arrays.copyOf(a,a.length+b.length);System.arraycopy(b,0,result,a.length,b.length);return result;}
}
