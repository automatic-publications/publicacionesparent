package com.userauth.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.userauth.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private SecretKey getSigningKey() {
        // ✅ opción 1: si tu clave es un string normal, convertirla a bytes
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // ✅ opción 2: si tu clave es Base64, usa esto en lugar de lo anterior:
        // return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userDetails.getUsername());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000L))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // ✅ forma correcta
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // ✅ forma nueva
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String resolveTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthenticationFromToken(String token) {
        String email = getEmailFromToken(token);
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // ✅ forma nueva
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("email");
    }
    
    public String getUsernameFromToken(String token) {
    return getEmailFromToken(token);
}
}