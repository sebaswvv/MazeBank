package w.mazebank.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import w.mazebank.models.requests.RefreshRequest;
import w.mazebank.models.responses.RefreshResponse;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "5367566B5970337336763979244226452948404D6251655468576D5A71347437";

    public String extractEmail(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String email = extractEmail(jwt);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        // extract all claims from the token
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(jwt)
            .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    // refresh token:
    public RefreshResponse refreshJwtToken(RefreshRequest refreshToken) {
        return new RefreshResponse(refreshJwtToken(new HashMap<>(), refreshToken.getRefreshToken()));
    }

    public String refreshJwtToken(Map<String, Object> extraClaims, String refreshToken) {
        // check if token is expired
        if (isTokenExpired(refreshToken)) return null;

        // generate new refresh token
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(extractEmail(refreshToken))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(new HashMap<>(), userDetails);
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        // 30 days in milliseconds
        long validityInMilliseconds = 30L * 24L * 60L * 60L * 1000L;

        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}