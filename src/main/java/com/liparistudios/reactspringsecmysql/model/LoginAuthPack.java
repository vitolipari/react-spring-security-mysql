package com.liparistudios.reactspringsecmysql.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

// import javax.validation.constraints.Email;
// import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginAuthPack {

    @Email
    @NotNull
    @Length(min = 5, max = 255)
    private String email;

    @NotNull
    @Length(min = 8, max = 524)
    private String password;

}
