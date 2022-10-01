package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.Role;
import com.liparistudios.reactspringsecmysql.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService( RoleRepository repo ) {
        this.roleRepository = repo;
    }

    public void addRole( Role role ) {
        roleRepository.save( role );
    }

    public Role findByName( String roleName ) {
        return
            this.roleRepository
                .findByName( roleName )
                .orElseThrow( () -> {
                    throw new IllegalStateException("Role not Exists");
                })
            ;

    }

    /*
    @Transactional
    public Role editRole( Role role ) {

    }
    */

}
