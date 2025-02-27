package com.app.util;

import java.util.*;

public class TokenManager {
    private static final long EXPIRATION_TIME_MS = 10 * 60 * 1000; // 10분
    private static final Map<String, Long> tokenStore = new HashMap<>();

    /** ✅ 토큰 생성 메서드 */
    public static String generateToken() {
        String token = UUID.randomUUID().toString(); // 고유한 토큰 생성
        long expirationTime = System.currentTimeMillis() + EXPIRATION_TIME_MS; // 만료 시간 설정
        tokenStore.put(token, expirationTime); // 토큰 저장
        return token;
    }

    /** ✅ 토큰 검증 메서드 */
    public static boolean validateToken(String token) {
        Long expirationTime = tokenStore.get(token);
        if (expirationTime == null) {
            return false; // 토큰이 존재하지 않음
        }
        if (System.currentTimeMillis() > expirationTime) {
            tokenStore.remove(token); // 만료된 토큰 삭제
            return false; // 토큰이 만료됨
        }
        return true; // 토큰이 유효함
    }

    /** ✅ 토큰 삭제 메서드 (로그아웃 용도) */
    public static void revokeToken(String token) {
        tokenStore.remove(token);
    }

    public static void main(String[] args) throws InterruptedException {
        // 1️⃣ 토큰 생성
        String token = generateToken();
        System.out.println("생성된 토큰: " + token);

        // 2️⃣ 유효성 검사 (즉시 체크)
        System.out.println("토큰 유효한가? " + validateToken(token)); // true

        // 3️⃣ 만료 후 확인 (테스트를 위해 11초 대기)
        Thread.sleep(11 * 1000);
        System.out.println("11초 후 토큰 유효한가? " + validateToken(token)); // false
    }
}
