package com.app.repositories;

import com.app.entities.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionEntityRepository extends JpaRepository<PermissionEntity, Long> {
}
