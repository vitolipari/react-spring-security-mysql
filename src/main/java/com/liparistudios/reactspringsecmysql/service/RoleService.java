package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.role.Role;
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


    @Transactional
    public Role editRole( Role role ) {

    }

}
