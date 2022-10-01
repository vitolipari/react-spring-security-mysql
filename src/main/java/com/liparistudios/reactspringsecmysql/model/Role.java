package com.liparistudios.reactspringsecmysql.model;

import com.liparistudios.reactspringsecmysql.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@Entity
@Table( name = "roles" )
public class Role {
    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;

    private String name;

    @ManyToMany(
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    @JoinTable(
        name = "permissions_roles",
        joinColumns = @JoinColumn( name = "role_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn( name = "permission_id", referencedColumnName = "id")
    )
    private List<Permission> permissions;
}
