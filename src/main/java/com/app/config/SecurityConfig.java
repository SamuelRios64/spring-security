package com.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import java.util.ArrayList;
import java.util.List;

@Configuration // Marca esta clase como una clase de configuración de Spring
@EnableWebSecurity // Activa la seguridad web en la aplicación
@EnableMethodSecurity // Permite usar anotaciones como @PreAuthorize o @Secured para proteger métodos
public class SecurityConfig {

    // Configura la cadena de filtros de seguridad (security filter chain)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                // Desactiva CSRF (protección contra ataques Cross-Site Request Forgery)
                .csrf(csrf -> csrf.disable())

                // Usa autenticación HTTP básica (usuario y contraseña en el encabezado Authorization)
                .httpBasic(Customizer.withDefaults())

                // Define que la aplicación no mantendrá sesiones de usuario (stateless)
                // Útil para APIs que usan tokens como JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Aquí podrías definir reglas de autorización si lo deseas
                .build();
    }

    // Define el AuthenticationManager (encargado de autenticar usuarios)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Retorna el AuthenticationManager configurado automáticamente por Spring con el UserDetailsService
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Define un AuthenticationProvider para autenticar usuarios desde base de datos
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        // Asigna el PasswordEncoder para verificar contraseñas encriptadas
        provider.setPasswordEncoder(passwordEncoder());

        // Asigna el servicio que carga los usuarios desde la base de datos
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }

    // Define el PasswordEncoder (en este caso, BCrypt)
    // Se usa para encriptar y comparar contraseñas de manera segura
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


