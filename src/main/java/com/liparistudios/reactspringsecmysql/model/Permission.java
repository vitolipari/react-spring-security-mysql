package com.liparistudios.reactspringsecmysql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( name = "permissions" )
public class Permission {
    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;
    private String name;
    private String description;
}
