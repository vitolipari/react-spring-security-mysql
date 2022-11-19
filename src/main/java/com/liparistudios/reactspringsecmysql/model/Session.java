package com.liparistudios.reactspringsecmysql.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.Period;


@Data
@Entity
@NoArgsConstructor
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
//    @Length(min = 64, max = 64)
    @NotNull( message = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855" )
    private String code;

    @Column( nullable = false )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime open;

    @Column( nullable = true )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime access;

    @Column( nullable = true )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime enabled;

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




    public String getStatus() {
        String status = "ghost";
        if( getOpen() != null )     status = "active";
        if( getAccess() != null )   status = "live";
        if( getEnabled() != null )  status = "live";
        if( getClosed() != null )   status = "closed";
        if( !LocalDateTime.now().isBefore( getExp() ) ) status = "expired";
        return status;
    }

    public Boolean isExpired() {
        return ( LocalDateTime.now().isBefore( getExp() ) );
    }

    public Boolean isClosed() {
        if( getClosed() == null ) {
            return false;
        }
        else {
            // la sessione ha una data di chiusura
            return true;
        }
    }

    // da ripensare
    public Boolean isActive() {
        return ( getStatus().equals("active") );
    }

    public Boolean isLive() {
        return ( /*getStatus().equals("active") ||*/ getStatus().equals("live") && (getOpen() != null));
    }

    public Boolean isEnabled() {
        return (getEnabled() != null);
    }

}
