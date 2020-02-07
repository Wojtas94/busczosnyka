package com.sda.BusApp.model.dto;

import com.sda.BusApp.model.TypeOfTrip;
import com.sda.BusApp.model.entity.Bus;
import com.sda.BusApp.model.entity.Driver;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TripDto {

    private Long id;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime startDateAndTime;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime finishDateAndTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate finishDate;
//    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;
//    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime estimatedFinishTime;
    private String phoneNumber;
    private String startPlace;
    private String finishPlace;
    private Integer amountOfPassengers;
    private Integer price;
    private Integer kilometers;
    private String notes;
    private String name;
    private TypeOfTrip typeOfTrip;
    private List<Driver> drivers;
    private List<Bus> buses;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof TripDto)) {
            return false;
        }
        TripDto tripDto = (TripDto) obj;
        return tripDto.id.equals(id) &&
                tripDto.startPlace.equals(startPlace);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + startPlace.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Trip: " + startPlace + " " + finishPlace + " " + startDate + " " + finishDate;
    }
}
