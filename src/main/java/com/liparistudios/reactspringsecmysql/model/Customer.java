package com.liparistudios.reactspringsecmysql.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Entity
@AllArgsConstructor
@Table( name = "customers" )
public class Customer implements UserDetails {
    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;

    @Column(
        unique = true,
        nullable = false,
        length = 255
    )
    @Email
    @Length(min = 8, max = 255)
    private String email;

    @Column(
        nullable = false,
        length = 256,
        insertable = true,
        updatable = true,
        unique = false
    )
    @Length(min = 8, max = 524)
    private String password;

    @Column(
        nullable = false,
        length = 64,
        insertable = true,
        updatable = true,
        unique = false
    )
    @Length(min = 8, max = 64)
    private String phoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column( nullable = false )
    private LocalDate dob;




    public Customer() {

    }

    public Customer(Customer customer) {
        this.id = customer.getId();
        this.email = customer.getEmail();
        this.dob = customer.getDob();
        this.roles = customer.getRoles();
    }


    public Integer getAge() {
        return Period.between( this.dob, LocalDate.now() ).getYears();
    }

    @ManyToMany(
        fetch = FetchType.EAGER
//        cascade = CascadeType.ALL
    )
    @JoinTable(
        name = "customers_roles",
        joinColumns =           @JoinColumn( name = "customer_id",  referencedColumnName = "id"),
        inverseJoinColumns =    @JoinColumn( name = "role_id",      referencedColumnName = "id")
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
            put("DoB", dob.toString());
            put("age", getAge());
            put("phone", phoneNumber);
            put("roles", rolesList);
        }};
    }


    @PreUpdate
    public void preUpdate() {
        System.out.println("pre-update customer");
    }

    @PrePersist
    public void prePersist() {
        System.out.println("pre-save customer");
    }

    @PostPersist
    public void postPersist() {
        System.out.println("customer saved");
        System.out.println( toMap() );
        System.out.println("-----------");
    }

    @PostLoad
    public void postLoad() {
        System.out.println("customer loaded");
        System.out.println( toMap() );
    }

    @PostRemove
    public void postRemove() {
        System.out.println("customer removed");
    }

    @PostUpdate
    public void postUpdate() {
        System.out.println("customer updated");
    }

    @PreRemove
    public void preRemove() {
        System.out.println("pre remove customer");
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        getRoles()
            .stream()
            .map( role -> role.getName() )
            .forEach( roleName -> {
                authorities.add( new SimpleGrantedAuthority( roleName ));
            })
        ;

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

