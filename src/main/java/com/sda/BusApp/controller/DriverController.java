package com.sda.BusApp.controller;

import com.sda.BusApp.exception.DriverNotFoundException;
import com.sda.BusApp.model.IsAvailable;
import com.sda.BusApp.model.IsRemoved;
import com.sda.BusApp.model.dto.DriverDto;
import com.sda.BusApp.model.controllerEnums.Actions;
import com.sda.BusApp.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;

@Controller
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping("/emptydrivers")
    public ModelAndView emptyDriversGet(){
        return new ModelAndView("emptydrivers");
    }

    @PostMapping("/emptydrivers")
    public RedirectView emptyDriversPost(){
        return new RedirectView("adddriver");
    }

    @GetMapping("/drivers")
    public ModelAndView driversView(){
        return new ModelAndView("drivers", "allDrivers", driverService.getAllDrivers());
    }

    @PostMapping("/drivers")
    public RedirectView driversPost(HttpSession session, HttpServletRequest req){
        return driverService.getRedirectViewDrivers(session, req);

    }

    @GetMapping("/adddriver")
    public ModelAndView addDriverGet() {
        return new ModelAndView("adddriver", "driverToAdd", new DriverDto());
    }

    @PostMapping("/adddriver")
    public RedirectView addDriverPost(@ModelAttribute DriverDto driver) {
        driverService.addDriver(driver);
        return new RedirectView("driveraddsuccess");
    }

    @GetMapping("/driveraddsuccess")
    public ModelAndView driverAddSuccess() {
        return new ModelAndView("driveraddsuccess");
    }

    @GetMapping("/deletedriver")
    public ModelAndView deleteDriverGet(HttpSession session) throws DriverNotFoundException {
        Long id = (Long) session.getAttribute("id");
        DriverDto driverToDelete = driverService.getDriver(id);
        return new ModelAndView("deletedriver", "driverToDelete", driverToDelete);
    }

    @PostMapping("/deletedriver")
    public RedirectView deleteDriverPost(HttpSession session) throws DriverNotFoundException {
        Long id = (Long) session.getAttribute("id");
        DriverDto driverToDelete = driverService.getDriver(id);
        driverService.deleteDriver(driverToDelete);
        if (driverService.getAllDrivers().isEmpty()) {
            return new RedirectView("emptydrivers");
        }
        return new RedirectView("drivers");
    }

    @GetMapping("/editdriver")
    public ModelAndView editDriverGet(HttpSession session) throws DriverNotFoundException {
        Long id = (Long) session.getAttribute("id");
        DriverDto driverToEdit = driverService.getDriver(id);
        return new ModelAndView("editdriver", "driverToEdit", driverToEdit);
    }

    @PostMapping("/editdriver")
    public RedirectView editDriverPost(@ModelAttribute DriverDto driverDto, HttpSession session) {
        driverDto.setId((Long) session.getAttribute("id"));
        driverService.editDriver(driverDto);
        return new RedirectView("drivers");
    }


}
