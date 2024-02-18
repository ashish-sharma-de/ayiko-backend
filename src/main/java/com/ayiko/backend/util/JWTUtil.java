package com.ayiko.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


public class JWTUtil {

    private String jwtSecret = "0574584cb44173ad40a530f3b8bb7c6cd26cf1afe9c0bfca128db4532ed31371906ed016ed9f98cb4c6aa6957550a73f63a2af1a7c3667c060efe101b4f6484f";

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            logger.error("JWT validation error", e);
        }
        return false;
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
