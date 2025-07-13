package com.app.config.filter;

import com.app.utils.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

// Filtro que va a validar si el token es válido
// OncePerRequestFilter quiere decir que cada vez que se haga una solicitud, se ejecuta el filtro
public class JwtTokenValidator extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Obtenemos el header que viene de la solicitud
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Si el token es diferente a null
        if (jwtToken != null){

            // Lo primero que tenemos que hacer es quitarle un pedazo de string al token
            // porque este viene asi, por ejemplo "Bearer onidwqniodinowqdionqwdnoiqwoindqoinwd"
            // por ende, necesitamos quitarle ese "Bearer"
            // Existen varias formas de hacerlo por:
            jwtToken = jwtToken.substring(7);// extrae el index a partir del indice 7
            //jwtToken = jwtToken.replace("Bearer ", ""); // reemplaza ese el contenido

            // Validamos el token
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

            // Si el token es valido, vamos a conceder la autorizacion de acceso

            // Obtenemos el usuario
            String username = jwtUtils.extractUsername(decodedJWT);

            // Obtenemos los authorities (permisos) en formato string
            String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

            // Ahora toca setear el usuario en el Security Context Holder

            // Dame los permisos "READ,CREATE,DELETE..." en una lista de permisos GrantedAuthority
            // Que es como Spring Security entiende los permisos
            Collection<? extends GrantedAuthority> authorities =
                    AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities); // Obtenemos los permisos

            // Seteamos al contexto
            // Instancia del contexto
            SecurityContext context = SecurityContextHolder.getContext();

            // Objeto authentication
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

            // Seteamos
            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);

            // Con esto le damos acceso al usuario si el token es valido
        }

        // continua con el siguiente filtro
        // Si no hago nada con el token,
        // la autenticacion va a fallar
        filterChain.doFilter(request, response);
    }
}
