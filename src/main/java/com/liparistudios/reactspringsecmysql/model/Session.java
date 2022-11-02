package com.liparistudios.reactspringsecmysql.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.Period;


@Data
@Entity
@AllArgsConstructor
@Table( name = "sessions" )
public class Session {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;

    @Column(
            nullable = false,
            length = 64,
            insertable = true,
            updatable = false,
            unique = false
    )
    @Length(min = 64, max = 64)
    private String code;

    @Column( nullable = false )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime open;

    @Column( nullable = false )
    private Period startUpTime;

    @Column( nullable = false )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime exp;

    @Column( nullable = true )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime closed;

    /**
     * Molte sessioni appartengono ad un customer
     */
    @ManyToOne
    @JoinColumn( name = "customer_id", nullable = true )
    private Customer customer;



}
