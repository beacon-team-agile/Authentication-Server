package com.example.authenticationserver.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.*;


@Service
@PropertySource("classpath:application.properties")
public class JwtTokenGenerator {

    private String secret;
    private int jwtExpirationInMs;

    @Value("${security.jwt.secret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Value("${security.jwt.expirationDateInMs}")
    public void setJwtExpirationInMs(int jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public String generateToken(String tokenGenerateKey) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, tokenGenerateKey);
    }

    public boolean validateToken(String authToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            throw ex;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.getSubject();

    }

    public List<SimpleGrantedAuthority> getRolesFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

        List<SimpleGrantedAuthority> roles = null;

        Boolean isEmployee = claims.get("employee", Boolean.class);
        Boolean isHR = claims.get("hr", Boolean.class);
        Boolean isNonEmployee = claims.get("nonemployee", Boolean.class);

        if (isHR) {
            roles = Collections.singletonList(new SimpleGrantedAuthority("hr"));
        } else if (isEmployee) {
            roles = Collections.singletonList(new SimpleGrantedAuthority("employee"));
        } else if (isNonEmployee) {
            roles = Collections.singletonList(new SimpleGrantedAuthority("nonemployee"));
        }
        return roles;
    }
}
