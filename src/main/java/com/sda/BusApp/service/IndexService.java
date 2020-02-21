package com.sda.BusApp.service;

import com.sda.BusApp.model.controllerEnums.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Service
public class IndexService {

    @Autowired
    private BusService busService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private TripService tripService;

    @Autowired
    private UserService userService;

    public RedirectView getRedirectViewMain(HttpServletRequest req) {
        Menu menu = Menu.valueOf(req.getParameter("menu"));
        switch (menu) {
            case BUS:
                if (busService.getAllBuses().isEmpty()){
                    return new  RedirectView("emptybuses");
                }
                return new RedirectView("buses");
            case DRIVER:
                if (driverService.getAllDrivers().isEmpty()) {
                    return new RedirectView("emptydrivers");
                }
                return new RedirectView("drivers");
            case TRIP:
                if (tripService.getTenNewestTrips().isEmpty()) {
                    return new RedirectView("emptytrips");
                }
                return new RedirectView("trips");
            case FILTR:
                return new RedirectView("filters");
            case CENA:
                return new RedirectView("priceoftrip");
            case USER:
                if (userService.getAllUsers().isEmpty()) {
                    return new RedirectView("emptyusers");
                }
                return new RedirectView("users");
            case NEW_PASSWORD:
                return new RedirectView("userchangepassword");

        }
        throw new IllegalStateException("bad argument");
    }
}
