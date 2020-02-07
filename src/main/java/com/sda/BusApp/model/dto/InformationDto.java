package com.sda.BusApp.model.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class InformationDto {

    private Long id;
    private String login;
    private LocalDateTime dateOfChange;
    private String action;
    private String moreInfo;
}
