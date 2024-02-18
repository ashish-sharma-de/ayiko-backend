package com.ayiko.backend.config;

import com.ayiko.backend.dto.AuthRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

@RestController
public class AuthenticationController {

    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest) {
        // Authenticate the user (pseudo-code)
        // if(authService.authenticate(authRequest.getUsername(), authRequest.getPassword())) {

        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(authRequest.getUsername())
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + 900000)) // Token valid for 15 minutes
                .signWith(SignatureAlgorithm.HS256, "secretKey")
                .compact();
        // }
        // else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
}

