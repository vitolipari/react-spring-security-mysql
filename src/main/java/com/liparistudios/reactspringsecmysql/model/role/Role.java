package com.liparistudios.reactspringsecmysql.model.role;

import com.liparistudios.reactspringsecmysql.model.permission.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Role {
    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "role_permissions")
    private List<Permission> permissions;
}
