//This class contains informations about changes in class Trip, Driver, Repair, User, UserCredentials like add, edit, delete.

package com.sda.BusApp.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "informations")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@Setter
@ToString
public class Information {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    @Column(name = "date_of_change")
    private LocalDateTime dateOfChange;

    private String action;

    @Column(name = "more_info")
    private String moreInfo;
}
