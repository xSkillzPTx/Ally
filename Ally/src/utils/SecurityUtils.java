/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author skillz
 */
public class SecurityUtils {

    private static final String ASSIMETRIC_ALGORITHM = "RSA";
    private static final String SIMETRIC_ALGORITHM = "AES";
    private static final String HASH_ALGORITHM = "SHA3-256";
    private static final String SIGN_ALGORITHM = "SHA3-256withRSA";

    //////////////////////////////////////
    //////  Código para os Hash's  //////
    ////////////////////////////////////
    
    /**
     * Gera a HASH.
     */
    
    public static String getHash(String data) {
        try {
            MessageDigest mc = MessageDigest.getInstance(HASH_ALGORITHM);
            return Base64.getEncoder().encodeToString(mc.digest(data.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            System.getLogger(SecurityUtils.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            return "ERROR in hash";
        }
    }

    //////////////////////////////////////
    //////  Código para as chaves  //////
    ////////////////////////////////////
    
    
    /**
     * Gera uma chave AES.
     */
    public static SecretKey generatorKey(int bits) throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance(SIMETRIC_ALGORITHM);
        keygen.init(bits, new SecureRandom());
        return keygen.generateKey();
    }

    public static void saveKey(SecretKey key, Path file, String password) throws Exception {
        byte[] encrypted = encrypt(key.getEncoded(), password);
        Files.write(file, encrypted, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    public static SecretKey loadKey(Path file, String password) throws Exception {
        byte[] raw = decrypt(Files.readAllBytes(file), password);
        return new SecretKeySpec(raw, SIMETRIC_ALGORITHM);
    }

    /**
     * Gera um par de chaves RSA.
     */
    public static KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ASSIMETRIC_ALGORITHM);
        kpg.initialize(keySize, new SecureRandom());
        return kpg.generateKeyPair();
    }

    /**
     * Grava a chave pública em plaintext (formato X.209/DER).
     */
    public static void savePublicKey(PublicKey publicKey, String filePath) throws IOException {
        if (!filePath.endsWith(".pub")) {
            filePath += ".pub";
        }
        Files.write(Paths.get(filePath), publicKey.getEncoded());
    }

    /**
     * Grava a chave privada (PLCS#8) cifrada com a password.
     */
    public static void savePrivateKey(PrivateKey privateKey, String filePath, String password) throws Exception {
        if (!filePath.endsWith(".priv")) {
            filePath += ".priv";
        }
        byte[] encrypted = encrypt(privateKey.getEncoded(), password);
        Files.write(Paths.get(filePath), encrypted);
    }

    /**
     * Lê a chave pública do ficheiro.
     */
    public static PublicKey loadPublicKey(String filePath) throws Exception {
        if (!filePath.endsWith(".pub")) {
            filePath += ".pub";
        }
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        KeyFactory kf = KeyFactory.getInstance(ASSIMETRIC_ALGORITHM);
        return kf.generatePublic(new X509EncodedKeySpec(bytes));
    }

    /**
     * Lê a chave privada do ficheiro, decifrando-a com a password.
     */
    public static PrivateKey loadPrivateKey(String filePath, String password) throws Exception {
        if (!filePath.endsWith(".priv")) {
            filePath += ".priv";
        }
        byte[] encrypted = Files.readAllBytes(Paths.get(filePath));
        byte[] decrypted = decrypt(encrypted, password);
        KeyFactory kf = KeyFactory.getInstance(ASSIMETRIC_ALGORITHM);
        return kf.generatePrivate(new PKCS8EncodedKeySpec(decrypted));
    }

    /////////////////////////////////////////////////////////
    //////  Código para a encriptação/desincriptação  //////
    ///////////////////////////////////////////////////////  
 
       /**
     * Length of the random salt used for key derivation, in bytes (128 bits).
     */
    private static final int SALT_LENGTH = 16;

    /**
     * Length of the GCM initialization vector, in bytes (96 bits, as
     * recommended for GCM).
     */
    private static final int IV_LENGTH = 12;

    /**
     * Length of the GCM authentication tag, in bits.
     */
    private static final int TAG_LENGTH_BITS = 128;

    /**
     * Length of the derived AES key, in bits.
     */
    private static final int KEY_LENGTH_BITS = 256;

    /**
     * PBKDF2 iteration count (OWASP recommendation for PBKDF2-HMAC-SHA256).
     */
    private static final int PBKDF2_ITERATIONS = 600_000;

    /**
     * Cryptographically strong random number generator for salts and IVs.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Encrypts the given data using a key derived from the given password.
     *
     * <p>
     * A fresh random salt and IV are generated on every call, so encrypting the
     * same data with the same password yields different outputs each time.</p>
     *
     * @param data the plaintext bytes to encrypt
     * @param password the password from which the encryption key is derived
     * @return the encrypted payload in the format
     * {@code salt || iv || ciphertext+tag}
     * @throws Exception if the cryptographic operation fails (e.g. algorithm
     * unavailable)
     */
    public static byte[] encrypt(byte[] data, String password) throws Exception {
        // Generate a fresh random salt and IV for this message
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        RANDOM.nextBytes(salt);
        RANDOM.nextBytes(iv);

        SecretKey key = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
        byte[] ciphertext = cipher.doFinal(data); // includes the GCM authentication tag

        // Pack salt, IV and ciphertext into a single array
        return ByteBuffer.allocate(salt.length + iv.length + ciphertext.length)
                .put(salt)
                .put(iv)
                .put(ciphertext)
                .array();
    }

    /**
     * Decrypts a payload previously produced by
     * {@link #encrypt(byte[], String)}.
     *
     * @param encrypted the encrypted payload in the format
     * {@code salt || iv || ciphertext+tag}
     * @param password the password used during encryption
     * @return the original plaintext bytes
     * @throws javax.crypto.AEADBadTagException if the password is wrong or the
     * data was tampered with or corrupted
     * @throws Exception if the payload is malformed or the cryptographic
     * operation fails
     */
    public static byte[] decrypt(byte[] encrypted, String password) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(encrypted);

        // Extract salt, IV and ciphertext from the payload
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        buffer.get(salt);
        buffer.get(iv);
        byte[] ciphertext = new byte[buffer.remaining()];
        buffer.get(ciphertext);

        SecretKey key = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
        return cipher.doFinal(ciphertext); // verifies the authentication tag before returning
    }

    /**
     * Derives a 256-bit AES key from a password and salt using
     * PBKDF2-HMAC-SHA256.
     *
     * <p>
     * Sensitive intermediate material (the password spec and raw key bytes) is
     * wiped from memory as soon as it is no longer needed.</p>
     *
     * @param password the password to derive the key from
     * @param salt the random salt (must be the same for encryption and
     * decryption)
     * @return the derived AES secret key
     * @throws Exception if the PBKDF2 algorithm is unavailable or key
     * derivation fails
     */
    private static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            try {
                // SecretKeySpec copies the array, so the original can be safely zeroed
                return new SecretKeySpec(keyBytes, "AES");
            } finally {
                Arrays.fill(keyBytes, (byte) 0);
            }
        } finally {
            spec.clearPassword();
        }
    }

    ///////////////////////////////////////////
    //////  Código para as assinaturas  //////
    /////////////////////////////////////////
    public static byte[] sign(byte[] data, PrivateKey pv) throws Exception {
        Signature sign = Signature.getInstance(SIGN_ALGORITHM);
        //inicializa a assinatura com a chave
        sign.initSign(pv);
        //assina os dados
        sign.update(data);
        //devolve a assinatura
        return sign.sign();
    }

    public static boolean verifySign(byte[] data, byte[] signature, PublicKey pk) throws Exception {
        Signature sign = Signature.getInstance(SIGN_ALGORITHM);
        //inicializa a validação da assinatura com a chave
        sign.initVerify(pk);
        //verifica se assinatura é valida para os dados e para a assinatura dada
        sign.update(data);
        return sign.verify(signature);
    }
}
