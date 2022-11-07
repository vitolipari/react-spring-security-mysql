package com.liparistudios.reactspringsecmysql.repository;

import com.liparistudios.reactspringsecmysql.model.Permission;
import com.liparistudios.reactspringsecmysql.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    @Query("SELECT p FROM Platform p WHERE p.name = ?1")
    Optional<Platform> findByName( String name );

    @Query("SELECT p FROM Platform p WHERE p.name LIKE %?1%")
    List<Platform> searchByName( String name );

//    @Query("SELECT p FROM Platform p WHERE p.name LIKE %?1%")
//    Optional<Platform> findBySessionId( Long id );

}
