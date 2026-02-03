package com.invoice.security.service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtService {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public JwtService() {
        this.publicKey = loadPublicKey();
        this.privateKey = loadPrivateKey();
    }

    // ===================== LOAD KEYS =====================

    private PublicKey loadPublicKey() {
        try {
            var inputStream = getClass().getClassLoader().getResourceAsStream("keys/public.key");
            if (inputStream == null)
                throw new RuntimeException("Public key file not found");

            String key = new String(inputStream.readAllBytes())
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = java.util.Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(spec);

        } catch (Exception e) {
            throw new RuntimeException("Error loading public key", e);
        }
    }

    private PrivateKey loadPrivateKey() {
        try {
            var inputStream = getClass().getClassLoader().getResourceAsStream("keys/private.key");
            if (inputStream == null)
                throw new RuntimeException("Private key file not found");

            String key = new String(inputStream.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = java.util.Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);

        } catch (Exception e) {
            throw new RuntimeException("Error loading private key", e);
        }
    }

    // ===================== CLAIM EXTRACTION =====================

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
//    public String extractRoles(String token)
//    {
//    	return 
//    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     *  CORRECT EXPIRY CHECK
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // treat parsing errors as expired/invalid
        }
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    // ===================== TOKEN GENERATION =====================

    public String generateAccesToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username,1000 * 60 * 5);
    }

    public String generateRefreshToken(String username) {
        return createToken(
                Map.of("type", "REFRESH"),
                username,
                1000L * 60 * 60 * 24 * 7
        );
    }
    
    public boolean validateRefreshToken(String token) {
        try {
            extractAllClaims(token);   // verifies signature + expiry
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        return "REFRESH".equals(claims.get("type"));
    }
    
    public String generateAccesToken2(String username, List<String> roles) {
        return createToken2(
                Map.of("type", "ACCESS"),
                username,roles,
                1000L * 60  * 24 * 7
        );
    }

    private String createToken2(Map<String, Object> claims, String username,List<String> roles,long expirationMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .claim("roles",roles)
                .setId(UUID.randomUUID().toString()) // jti
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
    
    
    private String createToken(Map<String, Object> claims, String username,long expirationMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setId(UUID.randomUUID().toString()) // jti
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}