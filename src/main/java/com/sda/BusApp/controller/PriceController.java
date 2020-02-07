package com.sda.BusApp.controller;

import com.sda.BusApp.service.TripService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class PriceController {

    @Autowired
    private TripService tripService;

    @GetMapping("/priceoftrip")
    public ModelAndView calculatePriceOfTripGet() {
        return new ModelAndView("priceoftrip");
    }

    @PostMapping("priceoftrip")
    public RedirectView calculatePriceOfTripPost(HttpServletRequest req, HttpSession session) {
        int price = tripService.getPrice(req);
        session.setAttribute("price", price);
        return new RedirectView("score");
    }

    @GetMapping("score")
    public ModelAndView score(HttpSession session) {
        int priceOfTrip = (int) session.getAttribute("price");
        return new ModelAndView("score", "priceOfTrip", priceOfTrip);
    }
}
