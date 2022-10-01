package com.liparistudios.reactspringsecmysql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( name = "customers" )
public class Customer {
    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;

    @Column( unique = true )
    private String email;

    private String password;

    @ManyToMany(
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL
    )
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn( name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn( name = "role_id", referencedColumnName = "id")
    )
    private Collection<Role> roles;
}

