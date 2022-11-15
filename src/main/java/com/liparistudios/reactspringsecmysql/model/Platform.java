package com.liparistudios.reactspringsecmysql.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.*;


@Data
@Entity
@NoArgsConstructor
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
    private String logoFilePath;

    @Column(
        nullable = true,
        length = 1024,
        insertable = true,
        updatable = true,
        unique = false
    )
    @Length(min = 16, max = 1024)
    private String logoFileUrl;

    @OneToMany( cascade = CascadeType.ALL )
    @JoinColumn( name = "platform_id", nullable = true )
    private List<Session> sessions;

    @Column( nullable = true )
    private Boolean enabled;

    @Transient
    private String logoFileContent;


    public Map<String, Object> toMap() {
        return new HashMap<String, Object>(){{
            put("id", id);
            put("name", name);
            put("open", open);
            put("exp", exp);
            put("logoFilePath", logoFilePath);
            put("logoFileUrl", logoFileUrl);
        }};
    }


    @PreUpdate
    public void preUpdate() {
        System.out.println("pre-update platform");
    }

    @PrePersist
    public void prePersist() {
        System.out.println("pre-save platform");
    }

    @PostPersist
    public void postPersist() {
        System.out.println("platform saved");
        System.out.println( toMap() );
        System.out.println("-----------");
    }

    @PostLoad
    public void postLoad() {
        System.out.println("platform loaded");
        System.out.println( toMap() );
    }

    @PostRemove
    public void postRemove() {
        System.out.println("platform removed");
    }

    @PostUpdate
    public void postUpdate() {
        System.out.println("platform updated");
    }

    @PreRemove
    public void preRemove() {
        System.out.println("pre remove platform");
    }


    public boolean isEnabled() {
        return true;
    }

}
