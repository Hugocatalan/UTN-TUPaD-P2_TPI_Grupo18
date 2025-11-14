package com.integradorfinal.programacion2.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utilidad simple para gestionar contraseñas:
 * - generar salt aleatorio
 * - hashear con SHA-256 (password + salt)
 * - validar password vs hash esperado
 *
 * Nota: Para producción suele recomendarse BCrypt/Argon2.
 * Para el integrador, SHA-256 + salt es suficiente.
 */
public final class PasswordUtil {

    private static final String HASH_ALGO = "SHA-256";
    private static final SecureRandom RNG = new SecureRandom();

    private PasswordUtil() {}

    /** Genera un salt aleatorio de N bytes, devuelto como hex. */
    public static String generateSalt(int numBytes) {
        
        byte[] salt = new byte[numBytes];
        RNG.nextBytes(salt);
        return toHex(salt);
    }

    /** Calcula el hash SHA-256 de (password + salt) y devuelve hex. */
    public static String hashPassword(String password, String saltHex) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGO);
            byte[] input = (password + saltHex).getBytes(StandardCharsets.UTF_8);
            byte[] digest = md.digest(input);
            return toHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // No debería ocurrir para SHA-256 en JRE moderno
            throw new IllegalStateException("Algoritmo de hash no disponible: " + HASH_ALGO, e);
        }
    }

    /** Valida password calculando hash con el salt y comparando con el hash esperado. */
    public static boolean validatePassword(String password, String saltHex, String expectedHashHex) {
        String calc = hashPassword(password, saltHex);
        return slowEquals(calc, expectedHashHex);
    }

    // ===== helpers =====

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    /** Comparación constante para evitar filtrado por tiempo. */
    private static boolean slowEquals(String a, String b) {
        if (a == null || b == null) return false;
        byte[] x = a.getBytes(StandardCharsets.UTF_8);
        byte[] y = b.getBytes(StandardCharsets.UTF_8);
        int diff = x.length ^ y.length;
        for (int i = 0; i < Math.min(x.length, y.length); i++) diff |= x[i] ^ y[i];
        return diff == 0;
    }
}
