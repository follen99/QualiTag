package it.unisannio.studenti.qualitag.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service class for JWT.
 */
@Service
public class JwtService {

  @Value("${token.secret.key}")
  String jwtSecretKey;

  @Value("${token.expiration}")
  Long jwtExpirationMs;

  /**
   * Extracts the username from the token.
   *
   * @param token the token
   * @return the username
   */
  public String extractUserName(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Generates a token for the user.
   *
   * @param userDetails the user details
   * @return the token
   */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /**
   * Generates a token for the user.
   *
   * @param extraClaims the extra claims
   * @param userDetails the user details
   * @return the token
   */
  private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Checks if the token is valid.
   *
   * @param token       the token
   * @param userDetails the user details
   * @return true if the token is valid, false otherwise
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String userName = extractUserName(token);
    return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  /**
   * Extracts a claim from the token.
   *
   * @param token           the token
   * @param claimsResolvers the claims resolver
   * @param <T>             the type of the claim
   * @return the claim
   */
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
    final Claims claims = extractAllClaims(token);
    return claimsResolvers.apply(claims);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
