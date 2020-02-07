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
@Table(name = "drivers")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@Setter
public class Driver implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_driver")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_available", nullable = false)
    private IsAvailable isAvailable;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "date_of_examination")
    private LocalDate dateOfExamination;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "date_of_remove")
    private LocalDate dateOfRemove;

    @ManyToMany(mappedBy = "drivers")
    private List<Trip> trips;

    @Override
    public String toString() {
        return "id: " + id + ", imię: " + name + ", nazwisko: " + surname;
    }



}
