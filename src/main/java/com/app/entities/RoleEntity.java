package com.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mapea el campo 'roleEnum' a la columna 'role_name' con un máximo de 50 caracteres.
    // Se almacenará como una cadena de texto (nombre del enum) en la base de datos.
    @Column(name = "role_name", length=50)
    @Enumerated(EnumType.STRING)
    private RoleEnum roleEnum;

    // Relación muchos a muchos entre roles y permisos.
    // 'fetch = EAGER' indica que los permisos se cargan inmediatamente junto con el rol.
    // 'cascade = ALL' propaga todas las operaciones (persist, merge, remove, etc.) al conjunto de permisos.
    // Se utiliza una tabla intermedia llamada 'roles_permissions' para mapear esta relación.
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "roles_permissions", // nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "role_id"), // columna que referencia al rol
            inverseJoinColumns = @JoinColumn(name = "permission_id") // columna que referencia al permiso
    )
    private Set<PermissionEntity> permissionList;
}
