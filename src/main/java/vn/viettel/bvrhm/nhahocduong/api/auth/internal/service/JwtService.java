package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;

@Service
public class JwtService {

  private static final String JWT_SIGNING_KEY =
      "6A586E3272357538782F413F4428472D4B6150645367566B5970337336763979";

  // 1000 miliseconds x 60 seconds x 60 minutes x 24 hours
  private static final long TOKEN_EXP_TIME_MILLIS = (long) (1000L * 60 * 60 * 24 * 30);

  // 1000 miliseconds x 60 seconds x 5 min
  //  private static final long TOKEN_EXP_TIME_MILLIS = 1000L * 60 * 5;

  private Key getJwtSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(JWT_SIGNING_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUserId(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public List<RoleDTO> extractRoles(String token) {
    final Claims claims = extractAllClaims(token);

    if (claims.get("roles") == null) {
      return List.of();
    }

    ObjectMapper objectMapper = new ObjectMapper();

    return ((List<LinkedHashMap>) claims.get("roles"))
        .stream().map(entry -> objectMapper.convertValue(entry, RoleDTO.class)).toList();
  }

  public String extractUsername(String token) {
    final Claims claims = extractAllClaims(token);
    return claims.get("username", String.class);
  }

  public String makeToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXP_TIME_MILLIS))
        .signWith(getJwtSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String makeTokenWithUserIdAndRoles(Long userId, Collection<String> roles) {
    long currentEpoch = System.currentTimeMillis();
    Date currentDate = new Date(currentEpoch);
    Date expDate = new Date(currentEpoch + TOKEN_EXP_TIME_MILLIS);

    String token =
        Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuedAt(currentDate)
            .setNotBefore(currentDate)
            .setExpiration(expDate)
            .addClaims(Map.of("roles", String.join(",", roles)))
            .signWith(getJwtSigningKey(), SignatureAlgorithm.HS256)
            .compact();

    return token;
  }

  public String makeToken(Long userId, Map<String, Object> extraClaims) {
    long currentEpoch = System.currentTimeMillis();
    Date currentDate = new Date(currentEpoch);
    Date expDate = new Date(currentEpoch + TOKEN_EXP_TIME_MILLIS);

    String token =
        Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuedAt(currentDate)
            .setNotBefore(currentDate)
            .setExpiration(expDate)
            .addClaims(extraClaims)
            .signWith(getJwtSigningKey(), SignatureAlgorithm.HS256)
            .compact();

    return token;
  }

  public boolean isTokenValid(String token) {
    try {
      final String userId = extractUserId(token);
      return !isTokenExpired(token);
    } catch (JwtException e) {
      // Something is wrong with this token (invalid signature ...)
      return false;
    }
  }

  private boolean isTokenExpired(String token) {
    Date expirationDate = extractExpiration(token);
    Date now = new Date();

    return expirationDate.before(now);
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsSelector) {
    final Claims claims = extractAllClaims(token);
    return claimsSelector.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getJwtSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
