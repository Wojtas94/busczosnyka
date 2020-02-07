package com.sda.BusApp.model.dto;
import com.sda.BusApp.model.IsAvailable;
import com.sda.BusApp.model.IsRemoved;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusDto {

    private Long id;
    private String name;
    private String registrationNumber;
    private Integer numberOfSeats;
    private IsAvailable isAvailable;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfInspection;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfRemove;
    public  boolean isChecked = false;


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof BusDto)) {
            return false;
        }
        BusDto busDto = (BusDto) obj;
        return busDto.name.equals(name) &&
                busDto.id.equals(id);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "id: " + id + ", nazwa: " + name + ", numer rejestracyjny: " + registrationNumber;
    }
}
