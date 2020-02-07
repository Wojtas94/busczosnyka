package com.sda.BusApp.service;

import com.sda.BusApp.model.controllerEnums.FiltersOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class FilterService {

    public LocalDateTime getLocalDateTime(HttpServletRequest req, String startDate, String startTime) {
        return LocalDateTime.of(LocalDate.parse(req.getParameter(startDate)), LocalTime.parse(req.getParameter(startTime)));
    }

    public RedirectView getRedirectView(FiltersOptions option) {
        switch (option) {
            case BUS_BY_NUMBER_OF_SEATS:
                return new RedirectView("/filterbusesbyseatsfirstpage");
            case TRIPS_BY_DATE:
                return new RedirectView("/filtertripsbydatefirstpage");
            case AVAILABLE_BUS_BY_DATE:
                return new RedirectView("filterbusesbydatefirstpage");
            case AVAILABLE_DRIVER_BY_DATE:
                return new RedirectView("/filterdriversbydatefirstpage");
            case TRIPS_BY_NAME:
                return new RedirectView("/filtertripsbyname");
            case CHANGE_BY_USER:
                return new RedirectView("/filterchangesbyuser");
            case CHANGE_BY_ACTION:
                return new RedirectView("/filterchangesbyaction");
        }
        throw new IllegalStateException("bad argument");
    }
}
