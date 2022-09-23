package com.liparistudios.reactspringsecmysql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Customer {
    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;
    private String email;
    private String password;
    @ManyToOne
    private Role role;
}

