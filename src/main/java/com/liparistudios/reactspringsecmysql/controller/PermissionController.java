package com.liparistudios.reactspringsecmysql.controller;


import com.liparistudios.reactspringsecmysql.model.Permission;
import com.liparistudios.reactspringsecmysql.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok().body( permissionService.getAllPermissions() );
    }


}
