package com.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder // Permite usar el patrón builder para construir objetos UserEntity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de usuario único para el login
    // En este caso para Angora, puede se el correo mi so
    @Column(unique = true)
    private String username;

    // Contraseña cifrada del usuario
    private String password;

    // Indica si el usuario está habilitado (activo o inactivo)
    @Column(name = "is_enabled")
    private Boolean isEnabled;

    // Indica si la cuenta no está expirada
    @Column(name = "account_no_expired")
    private Boolean accountNoExpired;

    // Indica si la cuenta no está bloqueada
    @Column(name = "account_no_locked")
    private Boolean accountNoLocked;

    // Indica si las credenciales (como la contraseña) no están expiradas
    @Column(name = "credential_no_expired")
    private Boolean credentialNoExpired;

    // Relación muchos a muchos entre usuarios y roles.
    // Carga los roles inmediatamente con el usuario (fetch = EAGER)
    // Las operaciones se propagan a los roles (cascade = ALL)
    // Usa una tabla intermedia llamada "users_roles" que enlaza usuarios con roles
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles", // Nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "user_id"), // Columna que hace referencia al usuario
            inverseJoinColumns = @JoinColumn(name = "role_id") // Columna que hace referencia al rol
    )
    private Set<RoleEntity> roles = new HashSet<>();
}
