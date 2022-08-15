package com.example.authenticationserver.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:application.properties")
public class JwtProvider {

    @Value("${security.jwt.token.key}")
    private String key;


    // parse the token -> use the information in the token to create a userDetail object
    public Optional<AuthUserDetail> resolveToken(HttpServletRequest request){
        String prefixedToken = request.getHeader("Authorization"); // extract


        //jwt token not given
        if (prefixedToken == null) {
            return Optional.empty();
        }

        System.out.println("Prefixed Token " + prefixedToken);

        String token = prefixedToken.substring(7);

        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody(); // decode

        String username = claims.getSubject();
        List<LinkedHashMap<String, String>> permissions = (List<LinkedHashMap<String, String>>) claims.get("permissions");

        List<GrantedAuthority> authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.get("authority")))
                .collect(Collectors.toList());

        return Optional.of(AuthUserDetail.builder()
                .username(username)
                .authorities(authorities)
                .build());
    }

    // create jwt from a UserDetail
    public String createToken(UserDetails userDetails){
        System.out.println(userDetails);
        Claims claims1 = Jwts.claims().setSubject(userDetails.getUsername()); // user identifier
        Claims claims2 = Jwts.claims().setSubject(userDetails.getPassword()); // user identifier
        claims1.put("permissions", userDetails.getAuthorities()); // user permission
        claims2.put("permissions", userDetails.getAuthorities());
        return Jwts.builder()
                .setClaims(claims1)
                .setClaims(claims2)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

}
