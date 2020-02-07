package com.sda.BusApp.model.dto;

import com.sda.BusApp.model.entity.Bus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RepairDto {

    private Long id;
    private String info;
    private Integer mileage;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    private Bus bus;

    @Override
    public String toString() {
        return "Naprawa busa: " +
                bus.getName() +
                ", informacje o naprawie: " +
                info +
                ", data: " +
                date.toString();
    }
}
