package com.sda.BusApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginUserController {

    @GetMapping(value = {"/login", ""})
    public String loginGet() {
        return "login";
    }

}
