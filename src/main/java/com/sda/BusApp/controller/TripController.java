package com.sda.BusApp.controller;

import com.sda.BusApp.exception.BusNotFoundException;
import com.sda.BusApp.exception.DriverNotFoundException;
import com.sda.BusApp.exception.TripNotFoundException;
import com.sda.BusApp.model.dto.BusDto;
import com.sda.BusApp.model.dto.DriverDto;
import com.sda.BusApp.model.dto.TripDto;
import com.sda.BusApp.model.entity.Bus;
import com.sda.BusApp.model.entity.Driver;
import com.sda.BusApp.service.BusService;
import com.sda.BusApp.service.DriverService;
import com.sda.BusApp.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private BusService busService;

    @GetMapping("/trips")
    public ModelAndView tripsGet() {
        return new ModelAndView("trips", "allTrips", tripService.getTenNewestTrips());
    }

    @PostMapping("/trips")
    public RedirectView tripsPost(HttpServletRequest req, HttpSession session) {
        return tripService.getRedirectViewTrips(req, session);
    }

    @GetMapping("/emptytrips")
    public ModelAndView emptyTripsGet() {
        return new ModelAndView("emptytrips");
    }

    @PostMapping("/emptytrips")
    public RedirectView emptyTripPost() {
        return new RedirectView("addtrip");
    }

    @GetMapping("/addtrip")
    public ModelAndView addTripGet() {
        return new ModelAndView("addtrip", "tripToAdd", new TripDto());
    }

    @PostMapping("/addtrip")
    public RedirectView addTripPost(@ModelAttribute TripDto trip, HttpSession session) {
        session.setAttribute("trip", trip);
        return new RedirectView("addtripsecondpage");
    }

    @GetMapping("/addtripsecondpage")
    public ModelAndView addTripSecondPageGet(HttpSession session) {
        TripDto tripDto = (TripDto) session.getAttribute("trip");
        List<BusDto> listOfAvailableBuses = busService.getBusDtosByDate(tripDto);
        return new ModelAndView("addtripsecondpage", "buses", listOfAvailableBuses);
    }

    @PostMapping("/addtripsecondpage")
    public RedirectView addTripSecondPagePost(HttpSession session, HttpServletRequest req) throws BusNotFoundException {
        TripDto tripDto = (TripDto) session.getAttribute("trip");
        busService.BusChoice(req, tripDto);
        return new RedirectView("addtripthirdpage");
    }

    @GetMapping("/addtripthirdpage")
    public ModelAndView addTripThirdPageGet(HttpSession session) {
        TripDto tripDto = (TripDto) session.getAttribute("trip");
        List<DriverDto> listOfAvailableDrivers = driverService.getDriverDtos(tripDto);
        return new ModelAndView("addtripthirdpage", "drivers", listOfAvailableDrivers);
    }


    @PostMapping("/addtripthirdpage")
    public RedirectView addTripThirdPagePost(HttpSession session, HttpServletRequest req) throws DriverNotFoundException {
        TripDto tripDto = (TripDto) session.getAttribute("trip");
        driverService.DriverChoice(req, tripDto);
        tripService.addTrip(tripDto);
        return new RedirectView("trips");
    }

    @GetMapping("tripaddsuccess")
    public ModelAndView tripAddSuccessGet() {
        return new ModelAndView("tripaddsuccess");
    }

    @GetMapping("/infotrips")
    public ModelAndView infoTripsGet(HttpSession session) throws TripNotFoundException {
        Long id = (Long) session.getAttribute("id");
        TripDto tripDto = tripService.getTripById(id);
        return new ModelAndView("infotrips", "tripDto", tripDto);
    }

    @GetMapping("/deletetrip")
    public ModelAndView deleteTripGet(HttpSession session) throws TripNotFoundException {
        Long id = (Long) session.getAttribute("id");
        TripDto tripToDelete = tripService.getTripById(id);
        return new ModelAndView("deletetrip", "tripToDelete", tripToDelete);
    }

    @PostMapping("/deletetrip")
    public RedirectView deleteTripPost(HttpSession session) throws TripNotFoundException {
        Long id = (Long) session.getAttribute("id");
        TripDto tripToDelete = tripService.getTripById(id);
        tripService.deleteTrip(tripToDelete);
        if (tripService.getAllTrips().isEmpty()) {
            return new RedirectView("emptytrips");
        }
        return new RedirectView("trips");
    }

    @GetMapping("/edittrip")
    public ModelAndView editTripGet(HttpSession session) throws TripNotFoundException {
        Long id = (Long) session.getAttribute("id");
        TripDto tripToEdit = tripService.getTripById(id);
        session.setAttribute("buses", tripToEdit.getBuses());
        session.setAttribute("drivers", tripToEdit.getDrivers());
        return new ModelAndView("edittrip", "tripToEdit", tripToEdit);
    }

    @PostMapping("/edittrip")
    public RedirectView editTripPost(@ModelAttribute TripDto tripDto, HttpSession session) {
        Long id = (Long) session.getAttribute("id");
        tripDto.setId(id);
        session.setAttribute("tripToEdit", tripDto);
        return new RedirectView("edittripsecondpage");
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/edittripsecondpage")
    public ModelAndView editTripSecondPageGet(HttpSession session) {
        TripDto tripDto = (TripDto) session.getAttribute("tripToEdit");
        List<BusDto> listOfAvailableBuses = busService.getBusDtosByDate(tripDto);
        List<Bus> listOfBusesFromCurrentTrip = (List<Bus>) session.getAttribute("buses");
        List<BusDto> listOfBusesDtoFromCurrentTrip = busService.getBusDtosEditTripSupportMethod(listOfAvailableBuses, listOfBusesFromCurrentTrip);
        return new ModelAndView("edittripsecondpage", "buses", listOfBusesDtoFromCurrentTrip);
    }

    @PostMapping("/edittripsecondpage")
    public RedirectView editTripSecondPagePost(HttpSession session, HttpServletRequest req) throws BusNotFoundException {
        TripDto tripDto = (TripDto) session.getAttribute("tripToEdit");
        busService.BusChoice(req, tripDto);
        return new RedirectView("edittripthirdpage");
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/edittripthirdpage")
    public ModelAndView editTripThirdPageGet(HttpSession session) {
        TripDto tripDto = (TripDto) session.getAttribute("tripToEdit");
        List<DriverDto> listOfAvailableDrivers = driverService.getDriverDtos(tripDto);
        List<Driver> listOfDriversFromCurrentTrip = (List<Driver>) session.getAttribute("drivers");
        List<DriverDto> listOfDriversDtoFromCurrentTrip = driverService.getDriverDtosEditTripSupportMethod(listOfAvailableDrivers, listOfDriversFromCurrentTrip);
        return new ModelAndView("edittripthirdpage", "drivers", listOfDriversDtoFromCurrentTrip);
    }


    @PostMapping("/edittripthirdpage")
    public RedirectView editTripThirdPagePost(HttpSession session, HttpServletRequest req) throws DriverNotFoundException {
        TripDto tripDto = (TripDto) session.getAttribute("tripToEdit");
        driverService.DriverChoice(req, tripDto);
        tripService.editTrip(tripDto);
        return new RedirectView("trips");
    }


}
