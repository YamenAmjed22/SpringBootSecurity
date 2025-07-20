package com.yamen.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static  final  String SECRET_KEY = "9d68835e9d74b526683c3df20f82b2f5c20b4966e5e71068694048f0c838ab75";

    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) { // this to extract single claims
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) //signing key it what we use to make signature in jwt part
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateTokenFromUserDetailesAndExctraCliams(Map<String, Object> extraClaims, UserDetails userDetails) {

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject( userDetails.getUsername() )
                .setIssuedAt(  new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public String generateTokenFromUserDetailes(UserDetails userDetails) {

        Map<String, Object> extraClaims = new HashMap<>();

        // ✅ Add roles as a list of strings
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return generateTokenFromUserDetailesAndExctraCliams(extraClaims, userDetails);
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String userEmail = extractUserEmail(token); 
        return ( userEmail.equals( userDetails.getUsername() ) && !isTokenExpired(token) );
    }

    private boolean isTokenExpired(String token) {
        return extractExprition(token).before(new Date());

    }

    private Date extractExprition(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
