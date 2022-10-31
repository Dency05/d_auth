package com.example.sm.common.utils;

import com.example.sm.common.model.JWTUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenUtil {

    /*@Value("${jwt.secret}")*/
    private final String secret = "dev_generation_jwt_token_users_with_id_role";

    /*@Value("${jwt.validity}")*/
    private final String validity= "86400";

    //retrieve username from jwt token
    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {

        return getClaimFromToken(token, Claims::getExpiration);
    }
    public <T> T getClaimFromToken(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(Utils.encodeBase64(secret)).parseClaimsJws(token).getBody();
    }

    // Get JWTUser From Token
    public JWTUser getJwtUserFromToken(String token){
        return JWTUser.fromClaim(getAllClaimsFromToken(token));
    }

    //check if the token has expired
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(JWTUser userDetails) {
        return doGenerateToken(userDetails.toClaim(), userDetails.getId());
    }

    //while creating the token -
//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
//2. Sign the JWT using the HS512 algorithm and secret key.
//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
//   compaction of the JWT to a URL-safe string

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        log.info("Validity :{}", validity);
        long JWT_TOKEN_VALIDITY = Long.parseLong(validity);
        log.info("Secret Key  :"+secret);
        Map<String,Object> header = new HashMap<>();
        header.put("typ","JWT");
        log.info("header :{}", header);
        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS256,Utils.encodeBase64(secret)).compact();
    }
    //validate token
    public Boolean validateToken(String token, JWTUser userDetails) {
        final String userId = getUserIdFromToken(token);
        return (userId.equals(userDetails.getId()) && !isTokenExpired(token));
    }
}