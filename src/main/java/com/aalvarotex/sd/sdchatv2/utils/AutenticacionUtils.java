/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Named;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author alvar
 */
@Named
public class AutenticacionUtils {

    /**
     * Deriva una clave (hash) PBKDF2 a partir de una contraseña y una sal.
     * Devuelve el resultado en Base64.
     */
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // bits

    private static byte[] generarSal(int longitudBytes) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[longitudBytes];
        random.nextBytes(salt);
        return salt;
    }

    private static String hashPBKDF2(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        char[] chars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Método "simple" que NO recibe salt.
     * Genera una salt interna y devuelve todo en un solo String:
     *   iteraciones:saltBase64:hashBase64
     *
     * Esto es lo que puedes guardar en la BD en una sola columna.
     */
    public static String hashPassword(String password) {
        try {
            byte[] salt = generarSal(16); // 16 bytes
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = hashPBKDF2(password, salt);

            return ITERATIONS + ":" + saltBase64 + ":" + hashBase64;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al generar hash PBKDF2", e);
        }
    }

    /**
     * Verificación a partir del String guardado en BD.
     */
    public static boolean verificarPassword(String password, String almacenado) {
        try {
            // formato: iteraciones:salt:hash
            String[] partes = almacenado.split(":");
            int iteraciones = Integer.parseInt(partes[0]);
            byte[] salt = Base64.getDecoder().decode(partes[1]);
            String hashEsperado = partes[2];

            // Recalcular hash con las mismas iteraciones y salt
            char[] chars = password.toCharArray();
            PBEKeySpec spec = new PBEKeySpec(chars, salt, iteraciones, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashBytes = skf.generateSecret(spec).getEncoded();
            String hashCalculado = Base64.getEncoder().encodeToString(hashBytes);

            return hashCalculado.equals(hashEsperado);
        } catch (Exception e) {
            return false;
        }
    }


}
