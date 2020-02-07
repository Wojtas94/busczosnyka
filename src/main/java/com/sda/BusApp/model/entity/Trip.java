package com.sda.BusApp.model.entity;

import com.sda.BusApp.model.TypeOfTrip;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "trips")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@Setter
@ToString
public class Trip implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trip")
    private Long id;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Column(nullable = false, name = "start_date_and_time")
    private LocalDateTime startDateAndTime;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Column(nullable = false, name = "finish_date_and_time")
    private LocalDateTime finishDateAndTime;

    @Column(length = 50, nullable = false, name = "start_place")
    private String startPlace;

    @Column(length = 50, nullable = false, name = "finish_place")
    private String finishPlace;

    @Column(nullable = false, name = "amount_of_passengers")
    private Integer amountOfPassengers;

    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private Integer price;

    private Integer kilometers;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_trip")
    private TypeOfTrip typeOfTrip;

    private String notes;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(name = "trip_bus",
            joinColumns = {@JoinColumn(name = "trip_id", referencedColumnName = "id_trip")},
            inverseJoinColumns = {@JoinColumn(name = "bus_id", referencedColumnName = "id_bus")})
    private List<Bus> buses;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(name = "trip_driver",
            joinColumns = {@JoinColumn(name = "trip_id", referencedColumnName = "id_trip")},
            inverseJoinColumns = {@JoinColumn(name = "driver_id", referencedColumnName = "id_driver")})
    private List<Driver> drivers;
}
