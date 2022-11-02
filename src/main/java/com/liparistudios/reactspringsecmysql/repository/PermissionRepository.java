package com.liparistudios.reactspringsecmysql.repository;

import com.liparistudios.reactspringsecmysql.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("SELECT p FROM Permission p WHERE p.name = ?1")
    Optional<Permission> findByName( String name );


}

