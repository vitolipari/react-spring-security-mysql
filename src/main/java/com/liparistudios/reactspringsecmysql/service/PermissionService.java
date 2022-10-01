package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.Permission;
import com.liparistudios.reactspringsecmysql.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository repository;

    @Autowired
    public PermissionService( PermissionRepository repo ) {
        this.repository = repo;
    }


    public List<Permission> getAllPermissions() {
        return this.repository.findAll();
    }

    public void addPermission(Permission perm) {
        // check
        Optional<Permission> permissionOptional = this.repository.findByName( perm.getName() );
        if( permissionOptional.isPresent() ) {
            throw new IllegalStateException("Permesso già esistente");
        }
        this.repository.save( perm );
    }

    public Permission getByName( String name ) {
        return
            this.repository
                .findByName( name )
                .orElseThrow( () -> {
                    throw new IllegalStateException("Permission not Exists");
                })
            ;
    }

}
