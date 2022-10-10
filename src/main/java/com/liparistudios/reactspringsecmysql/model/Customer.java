package com.liparistudios.reactspringsecmysql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( name = "customers" )
public class Customer {
    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;

    @Column( unique = true, nullable = false, length = 255)
    private String email;

    @Column( nullable = false, length = 128 )
    private String password;

    @ManyToMany(
        fetch = FetchType.EAGER
//        cascade = CascadeType.ALL
    )
    @JoinTable(
        name = "customers_roles",
        joinColumns = @JoinColumn( name = "customer_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn( name = "role_id", referencedColumnName = "id")
    )
    private Collection<Role> roles;

    public Map<String, Object> toMap() {

        List<Map<String, Object>> rolesList = new ArrayList<Map<String, Object>>();
        roles
            .stream()
            .forEach( role -> {

                List<Map<String, Object>> permissionsList = new ArrayList<Map<String, Object>>();
                role.getPermissions()
                    .stream()
                    .forEach( perm -> {
                        permissionsList.add(
                            new HashMap<String, Object>(){{
                                put("id", perm.getId() );
                                put("name", perm.getName() );
                                put("description", perm.getDescription() );
                            }}
                        );
                    });
                ;

                rolesList.add(
                    new HashMap<String, Object>(){{
                        put("name", role.getName());
                        put("permissions", permissionsList );
                    }}
                );

            })

        ;

        return new HashMap<String, Object>(){{
            put("id", id);
            put("email", email);
            put("roles", rolesList);
        }};
    }


}

