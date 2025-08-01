package com.app.services;

import com.app.controllers.dto.AuthCreateUserRequest;
import com.app.controllers.dto.AuthLoginRequest;
import com.app.controllers.dto.AuthResponse;
import com.app.entities.RoleEntity;
import com.app.entities.UserEntity;
import com.app.repositories.RoleEntityRepository;
import com.app.repositories.UserEntityRepository;
import com.app.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
* Este servicio es clave para la autenticación en Spring Security.
*  Toma un nombre de usuario, lo busca en la base de datos, valida su existencia
*  y construye un objeto UserDetails con sus roles, permisos y estado de la cuenta,
*  que luego Spring usa para permitir o denegar el acceso.*/

@Service // Marca esta clase como un componente de servicio en Spring (se detecta automáticamente como bean)
public class UserDetailService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired // Inyecta automáticamente el repositorio de usuarios
    private UserEntityRepository userRepository;

    @Autowired
    private RoleEntityRepository roleEntityRepository;

    @Autowired
    private JwtUtils jwtUtils;

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

    // Generamos el token de acceso
    public AuthResponse loginUser(AuthLoginRequest authLoginRequest){

        // Recuperamos el usuario y la contraseña
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        // Se encarga de que la credenciales sean correctas
        Authentication authentication = this.authenticate(username, password);

        // Se agrega al Security Context Holder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(username, "User loged successfuly", accessToken, true);

        return authResponse;

    }

    // Metodo que nos permite buscar el usaurio en la base de datos y verificar de que las credenciales sean correctas
    public Authentication authenticate(String username, String password){
        // Buscamos el usuario en la base de datos
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null){
            throw new BadCredentialsException("Invalid username or password");
        }

        // Si son diferentes, votamos un error
        if (!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }
        // Objeto de autenticacion
        return new UsernamePasswordAuthenticationToken(
                username,
                userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    // Método que nos permite crear un usuario en el sistema
    public AuthResponse createUser(AuthCreateUserRequest authCreateUserRequest){

        // Extraemos el usuario
        String username = authCreateUserRequest.username();

        // Extraemos la contraseña
        String password = authCreateUserRequest.password();

        // Necesitamos extraer la lista de roles
        List<String> roleRequest = authCreateUserRequest.roleRequest().roleListName();

        // Me trae los roles que coincidan con los que el usuario va a  tener
        Set<RoleEntity> roleEntitySet = roleEntityRepository.findRoleEntitiesByRoleEnumIn(roleRequest).stream().collect(Collectors.toSet());

        if (roleEntitySet.isEmpty()) {
            throw new IllegalArgumentException("The roles specified does not exists.");
        }

        // Construir el usuario
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roleEntitySet)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();
        
        // Guardamos en la base de datos
        UserEntity userCreated = userRepository.save(userEntity);
        
        // Generamos el token

        // Lista de los permisos que va a tener
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // Obtenemos cada rol y los metemos a la lista de arriba
        userCreated.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        // Obtenemos cada uno de los permisos de cada rol y tambien los añadimos al array de arriba
        userCreated.getRoles()
                .stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));


        SecurityContext context = SecurityContextHolder.getContext();

        // Objeto de autenticacion
        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(), authorityList);

        String accessToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(userCreated.getUsername(), "User created successfully", accessToken, true);

        return authResponse;
    }


}
