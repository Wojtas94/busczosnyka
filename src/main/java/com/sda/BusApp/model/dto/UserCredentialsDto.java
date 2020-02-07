package com.sda.BusApp.model.dto;

import com.sda.BusApp.model.IsPasswordChanged;
import com.sda.BusApp.model.Role;
import com.sda.BusApp.model.entity.User;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserCredentialsDto {

    private Long id;
    @Size(min = 3, max = 15, message = "login length should be between 3 and 20 characters")
    private String login;
    private String password;
    @Email(message = "email should be valid")
    private String email;
    @Enumerated(EnumType.STRING)
    private String role;
    private IsPasswordChanged isPasswordChanged;
    private User user;
}
