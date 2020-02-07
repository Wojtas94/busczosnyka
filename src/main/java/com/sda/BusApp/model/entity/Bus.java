package com.sda.BusApp.model.entity;

import com.sda.BusApp.model.IsAvailable;
import com.sda.BusApp.model.IsRemoved;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "buses")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@Setter
public class Bus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bus")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "registration_number", unique = true, length = 10)
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_available", nullable = false)
    private IsAvailable isAvailable;

    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "date_of_inspection")
    private LocalDate dateOfInspection;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "date_of_remove")
    private LocalDate dateOfRemove;

    @Transient
    @OneToMany(mappedBy = "bus")
    private List<Repair> repairs;

    @ManyToMany(mappedBy = "buses")
    private List<Trip> trips;


    @Override
    public String toString() {
        return "Id: " + id + ", nazwa busa: " + name + ", numer rejestracyjny: " + registrationNumber;
    }
}
