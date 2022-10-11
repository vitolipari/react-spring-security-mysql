package com.liparistudios.reactspringsecmysql.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
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

    @Column(
        unique = true,
        nullable = false,
        length = 255
    )
    private String email;

    @Column( nullable = false, length = 128 )
    private String password;

    private String phoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @Column( nullable = false )
    private LocalDate dob;


    public Integer getAge() {
        return Period.between( this.dob, LocalDate.now() ).getYears();
    }

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
            put("DoB", dob.toString());
            put("age", getAge());
            put("phone", phoneNumber);
            put("roles", rolesList);
        }};
    }


    @PreUpdate
    public void preUpdate() {

    }

    @PrePersist
    public void prePersist() {

    }

    @PostPersist
    public void postPersist() {

    }

    @PostLoad
    public void postLoad() {

    }

    @PostRemove
    public void postRemove() {

    }

    @PostUpdate
    public void postUpdate() {

    }

    @PreRemove
    public void preRemove() {

    }


}

