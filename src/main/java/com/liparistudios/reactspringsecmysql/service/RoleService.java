package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.Role;
import com.liparistudios.reactspringsecmysql.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService( RoleRepository repo ) {
        this.roleRepository = repo;
    }

    public Role addRole( Role role ) {
        System.out.println("vado ad aggiungere il ruolo "+ role.getName());
        Optional<Role> roleToFind = roleRepository.findByName( role.getName() );
        if( roleToFind.isPresent() ) {
            return roleToFind.get();
        }
        else {
            return roleRepository.save( role );
        }
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

    public List<Role> findAll() {
        return this.roleRepository.findAll();
    }

    /*
    @Transactional
    public Role editRole( Role role ) {

    }
    */

}
