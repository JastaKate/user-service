package com.example.userservice1.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secretKey}")
    private  String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    // single claim from payload
    public <T> T extractClaim(String jwtToken, Function<Claims,T> claimsTFunction) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsTFunction.apply(claims);
    }

    public String generateJwt(UserDetails userDetails) {
        return generateJwt(new HashMap<>(), userDetails);
    }



    public String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        String s = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return s;
    }

    public String generateJwt(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshJwt(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    public boolean isValid(String jwtToken, UserDetails userDetails) {
        final String username = extractUsername(jwtToken);
        return (username.equals(userDetails.getUsername())) && !isExpired(jwtToken);
    }

    private boolean isExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
