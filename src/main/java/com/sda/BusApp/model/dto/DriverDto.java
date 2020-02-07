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
public class DriverDto {

    private Long id;
    private String name;
    private String surname;
    private IsAvailable isAvailable;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfExamination;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfRemove;
    public  boolean isChecked = false;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DriverDto)) {
            return false;
        }
        DriverDto driverDto = (DriverDto) obj;
        return driverDto.surname.equals(surname) &&
                driverDto.id.equals(id);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + surname.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "id: " + id + ", imię: " + name + ", nazwisko: " + surname;
    }
}
