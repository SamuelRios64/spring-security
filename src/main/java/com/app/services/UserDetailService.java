package com.app.services;

import com.app.entities.UserEntity;
import com.app.repositories.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/*
* Este servicio es clave para la autenticación en Spring Security.
*  Toma un nombre de usuario, lo busca en la base de datos, valida su existencia
*  y construye un objeto UserDetails con sus roles, permisos y estado de la cuenta,
*  que luego Spring usa para permitir o denegar el acceso.*/

@Service // Marca esta clase como un componente de servicio en Spring (se detecta automáticamente como bean)
public class UserDetailService implements UserDetailsService {

    @Autowired // Inyecta automáticamente el repositorio de usuarios
    private UserEntityRepository userRepository;

    /**
     * Este método se ejecuta automáticamente por Spring Security cuando alguien intenta iniciar sesión.
     * Se encarga de buscar el usuario por su nombre de usuario y construir un objeto UserDetails.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Busca el usuario en la base de datos por su nombre de usuario.
        // Si no lo encuentra, lanza una excepción específica de Spring Security.
        UserEntity user = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe"));

        // Lista donde se almacenarán las autoridades (roles y permisos) del usuario
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Agrega los roles del usuario como autoridades, con prefijo "ROLE_" (requisito de Spring Security)
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name())));
        });

        // Agrega los permisos asociados a cada rol como autoridades
        user.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream()) // Accede a todos los permisos de todos los roles
                .forEach(permission -> {
                    authorities.add(new SimpleGrantedAuthority(permission.getName()));
                });

        // Crea y retorna un objeto User (de Spring Security) con:
        // - nombre de usuario
        // - contraseña
        // - estado de habilitación
        // - si las credenciales están vigentes
        // - si la cuenta está bloqueada
        // - si la cuenta está expirada
        // - y la lista de autoridades
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getIsEnabled(),
                user.getCredentialNoExpired(),
                user.getAccountNoLocked(),
                user.getAccountNoExpired(),
                authorities
        );
    }
}
