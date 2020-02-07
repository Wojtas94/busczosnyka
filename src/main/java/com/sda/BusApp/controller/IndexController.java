package com.sda.BusApp.controller;

import com.sda.BusApp.model.IsPasswordChanged;
import com.sda.BusApp.model.controllerEnums.Menu;
import com.sda.BusApp.model.dto.UserCredentialsDto;
import com.sda.BusApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class IndexController {

    @Autowired
    private BusService busService;

    @Autowired
    private IndexService indexService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private TripService tripService;

    @Autowired
    private UserCredentialsService userCredentialsService;

    @Autowired
    private InformationService informationService;

    @RequestMapping(value ="/main")
    public String indexGet(Model model, Authentication authentication, HttpSession session) {
        UserCredentialsDto userCredentialsDto = userCredentialsService.getUserCredentialsByLogin(authentication.getName());
        if (userCredentialsDto.getIsPasswordChanged().equals(IsPasswordChanged.FALSE)) {
            session.setAttribute("user", userCredentialsDto);
            return "userchangepassword";
        }
        model.addAttribute("newestThreeChanges", informationService.getNewestThreeInformation());
        model.addAttribute("busesNeededInspection", busService.listOfBusDtosWithNeededInspection());
        model.addAttribute("driversWhoNeedExamination", driverService.listOfDriversWhoNeedExaminationIn45Days());
        model.addAttribute("tripsWhichStartInNext7Days", tripService.getTripsWhichStartInNext7Days());
        return "main";
    }

    @PostMapping("/main")
    public RedirectView indexPost(HttpServletRequest req) {
        return indexService.getRedirectViewMain(req);
    }
}
