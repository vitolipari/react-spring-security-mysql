package com.liparistudios.reactspringsecmysql.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Entity
@AllArgsConstructor
@Table( name = "platforms" )
public class Platform {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;

    @Column(
        unique = true,
        nullable = false,
        length = 255
    )
    @Length(min = 4, max = 255)
    private String name;


    @Column( nullable = true )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd"
    )
    private LocalDateTime exp;

    @Column( nullable = false )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd"
    )
    private LocalDateTime open;


    @Column(
        nullable = true,
        length = 512,
        insertable = true,
        updatable = true,
        unique = false
    )
    @Length(min = 1, max = 512)
    private String LogoFilePath;

    @Column(
        nullable = true,
        length = 1024,
        insertable = true,
        updatable = true,
        unique = false
    )
    @Length(min = 16, max = 1024)
    private String LogoFileUrl;

    @OneToMany
    @JoinColumn( name = "platform_id", nullable = true )
    private List<Session> sessions;

    @Transient
    private String LogoFileContent;



}
