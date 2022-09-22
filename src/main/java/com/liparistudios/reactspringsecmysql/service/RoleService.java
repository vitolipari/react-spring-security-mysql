package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService( RoleRepository repo ) {
        this.roleRepository = repo;
    }

}
