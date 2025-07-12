package com.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
// Anotación de Lombok que permite construir objetos con un patrón builder (estilo encadenado)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permissions")
public class PermissionEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mapea el campo 'name' a una columna en la tabla:
    // unique = true  → no se pueden repetir nombres
    // nullable = false → el campo no puede ser nulo
    // updatable = false → el valor no se puede modificar una vez creado
    @Column(unique = true, nullable = false, updatable = false)
    private String name;
}
