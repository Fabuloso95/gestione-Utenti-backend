package com.gestione_utenti.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtGenerator
{
    private static final String SECRET_STRING = "mySuperSecretKeyThatIsAtLeast64BytesLongForHS512Algorithm1234567890abcdef";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());
    
    private static final long JWT_EXPIRATION_TIME_MS = 86400000;

    public String generateToken(Authentication authentication) 
    {
        String codiceFiscale = authentication.getName(); 
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION_TIME_MS);

        String roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        String token = Jwts.builder()
            .setSubject(codiceFiscale)
            .claim("roles", roles) 
            .setIssuedAt(currentDate)
            .setExpiration(expireDate)
            .signWith(KEY, SignatureAlgorithm.HS512)
            .compact();
            
        return token;
    }

    public String getUsernameFromJwt(String token) 
    {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(KEY)
            .build()
            .parseClaimsJws(token)
            .getBody();
            
        return claims.getSubject();
    }

    public boolean validateToken(String token) 
    {
        try 
        {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        }
        catch (Exception ex) 
        {
            throw new AuthenticationCredentialsNotFoundException("JWT non valido o scaduto: " + ex.getMessage());
        }
    }
    
    public String generateRefreshToken(Authentication authentication) 
    {
        String codiceFiscale = authentication.getName(); 
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + 7 * 86400000);

        String roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        String token = Jwts.builder()
            .setSubject(codiceFiscale)
            .claim("roles", roles) 
            .claim("type", "refresh")
            .setIssuedAt(currentDate)
            .setExpiration(expireDate)
            .signWith(KEY, SignatureAlgorithm.HS512)
            .compact();
            
        return token;
    }
}