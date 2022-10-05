package com.liparistudios.reactspringsecmysql.model;

import com.liparistudios.reactspringsecmysql.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( name = "roles" )
public class Role {


    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;

    private String name;

    @ManyToMany(
        fetch = FetchType.EAGER
//        cascade = CascadeType.ALL
    )
    @JoinTable(
        name = "permissions_roles",
        joinColumns = @JoinColumn( name = "role_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn( name = "permission_id", referencedColumnName = "id")
    )
    private List<Permission> permissions;


    public Role( String name ) {
        this.name = name;
    }


//    public Map<String, Object> toMap() {}

}
