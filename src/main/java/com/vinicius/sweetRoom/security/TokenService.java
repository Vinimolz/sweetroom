package com.vinicius.sweetRoom.security;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.vinicius.sweetRoom.model.User;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API SweetRoom")
                    .withSubject(user.getEmail())
                    .withExpiresAt(expirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
            throw new RuntimeException("Error generating token: " + ex);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("API SweetRoom")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTCreationException ex) {
            throw new RuntimeException("JWT token is invalid or expired");
        }
    }

    private Instant expirationDate() {
        return Instant.now().plus(java.time.Duration.ofHours(2));
    }
}
