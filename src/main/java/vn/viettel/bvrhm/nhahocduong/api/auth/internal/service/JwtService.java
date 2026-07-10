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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;

@Service
public class JwtService {

  /** JWT signing key — must be set via JWT_SIGNING_KEY env var (Base64-encoded 32-byte secret). */
  @Value("${JWT_SIGNING_KEY}")
  private String jwtSigningKey;

  /** Token expiry in milliseconds — set via JWT_EXPIRATION_MS env var. Default: 30 days. */
  @Value("${JWT_EXPIRATION_MS:2592000000}")
  private long tokenExpTimeMillis;

  private Key getJwtSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
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
        .setExpiration(new Date(System.currentTimeMillis() + tokenExpTimeMillis))
        .signWith(getJwtSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String makeTokenWithUserIdAndRoles(Long userId, Collection<String> roles) {
    long currentEpoch = System.currentTimeMillis();
    Date currentDate = new Date(currentEpoch);
    Date expDate = new Date(currentEpoch + tokenExpTimeMillis);

    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setIssuedAt(currentDate)
        .setNotBefore(currentDate)
        .setExpiration(expDate)
        .addClaims(Map.of("roles", String.join(",", roles)))
        .signWith(getJwtSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String makeToken(Long userId, Map<String, Object> extraClaims) {
    long currentEpoch = System.currentTimeMillis();
    Date currentDate = new Date(currentEpoch);
    Date expDate = new Date(currentEpoch + tokenExpTimeMillis);

    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setIssuedAt(currentDate)
        .setNotBefore(currentDate)
        .setExpiration(expDate)
        .addClaims(extraClaims)
        .signWith(getJwtSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean isTokenValid(String token) {
    try {
      extractUserId(token);
      return !isTokenExpired(token);
    } catch (JwtException e) {
      // Something is wrong with this token (invalid signature, expired, etc.)
      return false;
    }
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsSelector) {
    return claimsSelector.apply(extractAllClaims(token));
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getJwtSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
