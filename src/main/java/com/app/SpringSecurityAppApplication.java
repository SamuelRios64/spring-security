package com.app;

import com.app.entities.PermissionEntity;
import com.app.entities.RoleEntity;
import com.app.entities.RoleEnum;
import com.app.entities.UserEntity;
import com.app.repositories.PermissionEntityRepository;
import com.app.repositories.RoleEntityRepository;
import com.app.repositories.UserEntityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SpringSecurityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAppApplication.class, args);
	}

	// Este método se ejecuta automáticamente al arrancar la aplicación
	// Sirve para inicializar datos (usuarios, roles, permisos)
	@Bean
	CommandLineRunner init(UserEntityRepository userRepository, RoleEntityRepository roleRepository, PermissionEntityRepository permissionRepository) {
		return args -> {

			// Limpia todas las tablas (usuarios, roles y permisos)
			userRepository.deleteAll();
			roleRepository.deleteAll();
			permissionRepository.deleteAll();

			// =========================
			// CREACIÓN DE PERMISOS
			// =========================

			PermissionEntity createPermission = PermissionEntity.builder()
					.name("CREATE")
					.build();

			PermissionEntity readPermission = PermissionEntity.builder()
					.name("READ")
					.build();

			PermissionEntity updatePermission = PermissionEntity.builder()
					.name("UPDATE")
					.build();

			PermissionEntity deletePermission = PermissionEntity.builder()
					.name("DELETE")
					.build();

			PermissionEntity refactorPermission = PermissionEntity.builder()
					.name("REFACTOR")
					.build();

			// =========================
			// CREACIÓN DE ROLES
			// =========================

			// ADMIN: puede crear, leer, actualizar y eliminar
			RoleEntity roleAdmin = RoleEntity.builder()
					.roleEnum(RoleEnum.ADMIN)
					.permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission))
					.build();

			// USER: solo puede crear
			RoleEntity roleUser = RoleEntity.builder()
					.roleEnum(RoleEnum.USER)
					.permissionList(Set.of(createPermission))
					.build();

			// INVITED: solo puede leer
			RoleEntity roleInvited = RoleEntity.builder()
					.roleEnum(RoleEnum.INVITED)
					.permissionList(Set.of(readPermission))
					.build();

			// DEVELOPER: puede hacer todo, incluyendo refactorizar
			RoleEntity roleDeveloper = RoleEntity.builder()
					.roleEnum(RoleEnum.DEVELOPER)
					.permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission, refactorPermission))
					.build();

			// =========================
			// CREACIÓN DE USUARIOS
			// =========================

			// Usuario administrador
			UserEntity userJohan = UserEntity.builder()
					.username("Johan")
					// Contraseña encriptada (por ejemplo: "1234")
					.password("$2a$10$eTJOArN7.FlqkFsBKCOcYOWZsh9.FYtsPThL9K3K7kpzDQqAkOT92")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleAdmin))
					.build();

			// Usuario con rol INVITED (solo lectura)
			UserEntity userKevin = UserEntity.builder()
					.username("Kevin")
					.password("$2a$10$eTJOArN7.FlqkFsBKCOcYOWZsh9.FYtsPThL9K3K7kpzDQqAkOT92")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleInvited))
					.build();

			// Usuario con rol INVITED y DEVELOPER (muchos permisos)
			UserEntity userSamuel = UserEntity.builder()
					.username("Samuel")
					.password("$2a$10$eTJOArN7.FlqkFsBKCOcYOWZsh9.FYtsPThL9K3K7kpzDQqAkOT92")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleInvited, roleDeveloper))
					.build();

			// Guarda todos los usuarios (y con ellos los roles y permisos debido a cascade)
			userRepository.saveAll(List.of(userJohan, userKevin, userSamuel));
		};
	}
}