package com.sda.BusApp.model.entity;

import com.sda.BusApp.model.IsPasswordChanged;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "user_credentials")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentials  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String login;

    private String password;

    private String email;

    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_password_changed")
    private IsPasswordChanged isPasswordChanged;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    private User user;


}
