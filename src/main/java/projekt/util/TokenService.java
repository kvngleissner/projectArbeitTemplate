package projekt.util;

import java.util.Date;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.core.Response;

import java.security.Key;

public class TokenService {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRATION_TIME = 1000 * 60 * 5; // 5 minutes

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("Hausverwaltung")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .requireIssuer("Hausverwaltung")
                    .build();
            parser.parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.err.println("Token expired");
            return false;
        } catch (io.jsonwebtoken.SignatureException e) {
            System.err.println("Invalid signature");
            return false;
        } catch (Exception e) {
            System.err.println("Token validation failed");
            return false;
        }
    }

    public static Response verifyAuthToken(String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Unauthorized\"}")
                    .build();
        }
        String token = authHeader.substring("Bearer ".length());
        if(!TokenService.validateToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid or expired token\"}")
                    .build();
        }
        return null;

    }

    public static void main(String[] args) {
        String token = generateToken("testUser");
        System.out.println(validateToken(token));
    }
}