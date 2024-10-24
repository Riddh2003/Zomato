package com.utility;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtility {
	 private String SECRET_KEY = "mysecretkey";

	    // Extract username from JWT token
	    public String extractUsername(String token) {
	        return extractClaim(token, Claims::getSubject);
	    }

	    // Extract specific claims
	    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = extractAllClaims(token);
	        return claimsResolver.apply(claims);
	    }

	    // Extract all claims from JWT
	    private Claims extractAllClaims(String token) {
	        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	    }

	    // Check if token is expired
	    public Boolean isTokenExpired(String token) {
	        return extractExpiration(token).before(new java.util.Date());
	    }

	    // Extract expiration date from JWT token
	    public Date extractExpiration(String token) {
	        return (Date)extractClaim(token, Claims::getExpiration);
	    }

	    // Generate JWT token
	    public String generateToken(String username) {
	        Map<String, Object> claims = new HashMap<>();
	        return createToken(claims, username);
	    }

	    // Create JWT token
	    private String createToken(Map<String, Object> claims, String subject) {
	        return Jwts.builder()
	                .setClaims(claims)
	                .setSubject(subject)
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
	                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
	                .compact();
	    }

	    // Validate JWT token
	    public Boolean validateToken(String token, String username) {
	        final String extractedUsername = extractUsername(token);
	        return (extractedUsername.equals(username) && !isTokenExpired(token));
	    }
}
