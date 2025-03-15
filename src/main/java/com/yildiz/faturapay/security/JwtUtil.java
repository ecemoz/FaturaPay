package com.yildiz.faturapay.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    // Gerçek projelerde secret key'inizi daha güvenli bir şekilde (örneğin, environment variable veya application.properties üzerinden Base64 kodlu) saklayın.
    private final String SECRET_KEY = "rabia_ecem_emre";

    /**
     * Belirtilen kullanıcı adı için JWT token üretir.
     */
    public String generateToken(String username) {
        logger.debug("Token oluşturuluyor. Kullanıcı adı: {}", username);
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 saatlik geçerlilik
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        logger.debug("Oluşturulan token: {}", token);
        return token;
    }

    /**
     * Token içerisinden kullanıcı adını (subject) çıkarır.
     */
    public String extractUsername(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            logger.debug("Token'dan çıkarılan kullanıcı adı: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Token'dan kullanıcı adı çıkarılırken hata oluştu. Token: {}", token, e);
            throw e;
        }
    }

    /**
     * Belirtilen fonksiyonu kullanarak token içerisindeki istediğin claim'i çıkarır.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        T claim = claimsResolver.apply(claims);
        logger.debug("Çıkarılan claim: {} - Token: {}", claim, token);
        return claim;
    }

    /**
     * Token içerisindeki tüm claim'leri çıkarır.
     */
    private Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            logger.debug("Token'dan çıkarılan tüm claim'ler: {}", claims);
            return claims;
        } catch (JwtException e) {
            logger.error("JWT parse hatası. Token: {}", token, e);
            throw e;
        } catch (Exception e) {
            logger.error("Claim'ler çıkarılırken beklenmedik hata. Token: {}", token, e);
            throw e;
        }
    }

    /**
     * Token'ın imzasını ve son kullanma tarihini kontrol eder.
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                logger.warn("Token süresi dolmuş. Token: {}", token);
                return false;
            }
            logger.debug("Token geçerli. Token: {}", token);
            return true;
        } catch (JwtException e) {
            logger.error("Geçersiz JWT token. Token: {}", token, e);
            return false;
        } catch (Exception e) {
            logger.error("Token doğrulaması yapılırken hata oluştu. Token: {}", token, e);
            return false;
        }
    }
}
