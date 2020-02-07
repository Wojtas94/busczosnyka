package com.sda.BusApp.controller;

import com.sda.BusApp.model.dto.BusDto;
import com.sda.BusApp.model.dto.DriverDto;
import com.sda.BusApp.model.dto.InformationDto;
import com.sda.BusApp.model.dto.TripDto;
import com.sda.BusApp.model.controllerEnums.FiltersOptions;
import com.sda.BusApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class FiltersController {

    @Autowired
    private BusService busService;

    @Autowired
    private TripService tripService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private FilterService filterService;

    @Autowired
    private InformationService informationService;

    @GetMapping("/filters")
    public ModelAndView filtersGet() {
        return new ModelAndView("filters");
    }

    @PostMapping("/filters")
    public RedirectView filtersPost(HttpServletRequest req) {
        FiltersOptions option = FiltersOptions.valueOf(req.getParameter("action"));
        return filterService.getRedirectView(option);
    }


    @GetMapping("/filterbusesbydatefirstpage")
    public ModelAndView filterBusesByDateGet() {
        return new ModelAndView("filterbusesbydatefirstpage");
    }

    @PostMapping("/filterbusesbydatefirstpage")
    public RedirectView filterBusesByDatePost(HttpSession session, HttpServletRequest req) {
        LocalDateTime from = filterService.getLocalDateTime(req, "startDate", "startTime");
        LocalDateTime to = filterService.getLocalDateTime(req, "finishDate", "finishTime");
        List<BusDto> filteredBuses = busService.getListOfAvailableBusBetweenDates(from, to);
        session.setAttribute("buses", filteredBuses);
        return new RedirectView("filterbusesbydatesecondpage");
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/filterbusesbydatesecondpage")
    public ModelAndView filterBusesByDateSecondPage(HttpSession session) {
        List<BusDto> filteredBuses = (List<BusDto>) session.getAttribute("buses");
        return new ModelAndView("filterbusesbydatesecondpage", "buses", filteredBuses);
    }

    @GetMapping("/filterdriversbydatefirstpage")
    public ModelAndView filterDriversByDateGet() {
        return new ModelAndView("filterdriversbydatefirstpage");
    }

    @PostMapping("/filterdriversbydatefirstpage")
    public RedirectView filterDriversByDatePost(HttpSession session, HttpServletRequest req) {
        LocalDateTime from = filterService.getLocalDateTime(req, "startDate", "startTime");
        LocalDateTime to = filterService.getLocalDateTime(req, "finishDate", "finishTime");
        List<DriverDto> filteredBuses = driverService.getListOfAvailableDrivers(from, to);
        session.setAttribute("drivers", filteredBuses);
        return new RedirectView("filterdriversbydatesecondpage");
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/filterdriversbydatesecondpage")
    public ModelAndView filterDriversByDateSecondPage(HttpSession session) {
        List<DriverDto> filteredDrivers = (List<DriverDto>) session.getAttribute("drivers");
        return new ModelAndView("filterdriversbydatesecondpage", "drivers", filteredDrivers);
    }

    @GetMapping("/filtertripsbydatefirstpage")
    public ModelAndView filterTripsByDateGet() {
        return new ModelAndView("filtertripsbydatefirstpage");
    }

    @PostMapping("/filtertripsbydatefirstpage")
    public RedirectView filterTripsByDatePost(HttpSession session, HttpServletRequest req) {
        LocalDateTime from = filterService.getLocalDateTime(req, "startDate", "startTime");
        LocalDateTime to = filterService.getLocalDateTime(req, "finishDate", "finishTime");
        String action = req.getParameter("action");
        if (action.equals("all")) {
            List<TripDto> filteredAllTrips = tripService.findTripsByDateAndTime(from, to);
            session.setAttribute("trips", filteredAllTrips);
        } else if (action.equals("trip")) {
            List<TripDto> filteredTrips = tripService.getTripsWithoutRegularLineByDateAndTime(from, to);
            session.setAttribute("trips", filteredTrips);
        } else if (action.equals("regular")) {
            List<TripDto> filteredRegularLine = tripService.getRegularLineByDateAndTime(from, to);
            session.setAttribute("trips", filteredRegularLine);
        }

        return new RedirectView("filtertripsbydatesecondpage");
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/filtertripsbydatesecondpage")
    public ModelAndView filterTripsByDateSecondPage(HttpSession session) {
        List<TripDto> filteredTrips = (List<TripDto>) session.getAttribute("trips");
        return new ModelAndView("filtertripsbydatesecondpage", "trips", filteredTrips);
    }

    @GetMapping("/filterbusesbyseatsfirstpage")
    public ModelAndView filterBusesBySeatsGet() {
        return new ModelAndView("filterbusesbyseatsfirstpage");
    }

    @PostMapping("/filterbusesbyseatsfirstpage")
    public RedirectView filterBusesBySeatsPost(HttpSession session, HttpServletRequest req) {
        List<BusDto> buses = busService.getBusDtosByAmountOfSeats(req);
        session.setAttribute("busesFilter", buses);
        return new RedirectView("filterbusesbyseatssecondpage");
    }


    @SuppressWarnings("unchecked")
    @GetMapping("/filterbusesbyseatssecondpage")
    public ModelAndView filterBusesBySeatsSecondPage(HttpSession session) {
        List<BusDto> filteredBuses = (List<BusDto>) session.getAttribute("busesFilter");
        return new ModelAndView("filterbusesbyseatssecondpage", "buses", filteredBuses);
    }

    @GetMapping("/filtertripsbyname")
    public ModelAndView filterTripsByNameGet() {
        return new ModelAndView("filtertripsbyname");
    }

    @PostMapping("/filtertripsbyname")
    public RedirectView filterTripsByNamePost(HttpSession session, HttpServletRequest req) {
        String name = req.getParameter("name");
        List<TripDto> trips = tripService.getLastTenTripsByName(name);
        session.setAttribute("trips", trips);
        return new RedirectView("filtertripsbynamesecondpage");
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/filtertripsbynamesecondpage")
    public ModelAndView filterTripsByNameSecondPage(HttpSession session) {
        List<TripDto> trips = (List<TripDto>) session.getAttribute("trips");
        return new ModelAndView("filtertripsbynamesecondpage", "trips", trips);
    }

    @GetMapping("/filterchangesbyuser")
    public ModelAndView filterChangesByUserGet() {
        return new ModelAndView("filterchangesbyuser");
    }

    @PostMapping("/filterchangesbyuser")
    public RedirectView filterChangesByUserPost(HttpServletRequest req, HttpSession session) {
        String login = req.getParameter("login");
        List<InformationDto> listOfChanges =  informationService.getLastTenChangesByUser(login);
        session.setAttribute("list", listOfChanges);
        return new RedirectView("filterchangesbyusersecondpage");
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/filterchangesbyusersecondpage")
    public ModelAndView filterChangesByUserSecondPage(HttpSession session) {
        List<InformationDto> listOfChanges = (List<InformationDto>) session.getAttribute("list");
        return new  ModelAndView("filterchangesbyusersecondpage", "listOfChanges", listOfChanges);
    }

    @GetMapping("/filterchangesbyaction")
    public ModelAndView filterChangesByActionGet() {
        return new ModelAndView("filterchangesbyaction");
    }

    @PostMapping("/filterchangesbyaction")
    public RedirectView filterChangesByActionPost(HttpServletRequest req, HttpSession session) {
        String operation = req.getParameter("operation");
        List<InformationDto> listOfChangesByOperation = informationService.getLastTenChangesByAction(operation);
        session.setAttribute("list", listOfChangesByOperation);
        return new RedirectView("filterchangesbyactionsecondpage");
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/filterchangesbyactionsecondpage")
    public ModelAndView filterChangesByActionSecondPage(HttpSession session) {
        List<InformationDto> listOfChangesByOperation = (List<InformationDto>) session.getAttribute("list");
        return new ModelAndView("filterchangesbyactionsecondpage", "listOfChanges", listOfChangesByOperation);
    }


}
