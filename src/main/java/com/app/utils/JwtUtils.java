package com.app.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// Clase de utiliria para trabajar con JWT
@Component
public class JwtUtils {

    // Llave privada
    @Value("${security.jwt.key.private}")
    private String privateKey;

    // Un usuario generador del token
    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    // Metodos de utileria

    // Metodo encargado de crear el token
    // Se pasa un objeto de Authentication para extraer los datos del usuario
    public String createToken(Authentication authentication) {

        // Definimos cual va a hacer el algoritmo de encriptación
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        // Luego extraemos el usuario que se va a autenticar
        String username = authentication.getPrincipal().toString();

        // Tambien extraemos los authorities en un string separado por comas
        String authorities =
                authentication
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));

        // Generamos el token
        String jwtToken = JWT.create()
                .withIssuer(this.userGenerator) // Quien lo genera
                .withSubject(username) // A quien se lo genera
                .withClaim("authorities", authorities) // Generar los permisos en los claims
                .withIssuedAt(new Date()) // Fecha que se crea
                .withExpiresAt(new Date(System.currentTimeMillis() + 1800000)) // El momento actual en milisegundos sumado 30 minutos en milisegundos
                .withJWTId(UUID.randomUUID().toString()) // Asignando un id al token
                .withNotBefore(new Date(System.currentTimeMillis())) // Token valido a partir de este momento
                .sign(algorithm); // Firma del token, con el algoritmo de encriptacion

        return jwtToken; // Retornamos el token.
    }

    // Método para validar el token
    public DecodedJWT validateToken(String token) {
        try {
            // Algoritmo de encriptacion con la llave privada
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            // Verificador del token
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            // Verificamos el token
            DecodedJWT decodedJWT = verifier.verify(token);

            // Lo retornamos decodificado
            return decodedJWT;

        } catch (JWTVerificationException exception){
            // Excepcion que se ejecuta cuando el token es invalido
            throw new JWTVerificationException("Token invalid, not Authorized");
        }
    }

    // Extraer el usuario dentro del token decodificado
    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    // Extraer un claim en especifico
    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    // Devolver todos los claims del token
    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }
}
