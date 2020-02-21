package com.sda.BusApp.controller;

import com.sda.BusApp.exception.UserCredentialsNotFoundException;
import com.sda.BusApp.model.IsPasswordChanged;
import com.sda.BusApp.model.Role;
import com.sda.BusApp.model.controllerEnums.Actions;
import com.sda.BusApp.model.dto.UserCredentialsDto;
import com.sda.BusApp.model.dto.UserDto;
import com.sda.BusApp.model.entity.User;
import com.sda.BusApp.service.MailService;
import com.sda.BusApp.service.UserCredentialsService;
import com.sda.BusApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;

@Controller
public class UserController {

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserCredentialsService userCredentialsService;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ModelAndView usersGet() {
        return new ModelAndView("users", "allusers", userService.getAllUsers());
    }

    @PostMapping("/users")
    public RedirectView usersPost(HttpServletRequest req, HttpSession session) {
        return userCredentialsService.getRedirectViewUser(req, session);
    }

    @GetMapping("/addnewuser")
    public ModelAndView addNewUserGet() {
        return new ModelAndView("addnewuser", "newuser", new UserCredentialsDto());
    }

    @PostMapping("/addnewuser")
    public RedirectView addNewUserPost(@ModelAttribute @Valid UserCredentialsDto userCredentialsDto, BindingResult result, HttpServletRequest req) {
        userCredentialsService.addNewUserSupportMethod(userCredentialsDto, req);
        userCredentialsService.addUserCredentials(userCredentialsDto, result);
        return new RedirectView("users");
    }

    @GetMapping("/emptyusers")
    public ModelAndView emptyUsersGet() {
        return new ModelAndView("emptyusers");
    }

    @PostMapping("/emptyusers")
    public RedirectView emptyUsersPost() {
        return new RedirectView("addnewuser");
    }

    @GetMapping("/deleteuser")
    public ModelAndView deleteUserGet(HttpSession session) {
        UserDto userToDelete = userService.getUserDto(session);
        if (userCredentialsService.getUserCredentialsByUser(userToDelete).getRole().equals("ROLE_ADMIN")) {
            return new ModelAndView("cannotdeleteadmin");
        }

        return new ModelAndView("deleteuser", "userToDelete", userToDelete);
    }

    @PostMapping("/deleteuser")
    public RedirectView deleteUserPost(HttpSession session) {
        UserDto userToDelete = userService.getUserDto(session);
        userCredentialsService.deleteByUser(userToDelete);
        if (userService.getAllUsers().isEmpty()) {
            return new RedirectView("emptyusers");
        }
        return new RedirectView("users");
    }

    @GetMapping("/userchangepassword")
    public ModelAndView userChangePasswordGet() {
        return new ModelAndView("userchangepassword");
    }

    @PostMapping("/userchangepassword")
    public RedirectView userChangePasswordPost(HttpSession session, HttpServletRequest req, Authentication authentication) {
        UserCredentialsDto userCredentialsDto = userCredentialsService.getUserCredentialsByLogin(authentication.getName());
        userCredentialsDto.setPassword(bCryptPasswordEncoder.encode(req.getParameter("password")));
        userCredentialsService.changePassword(userCredentialsDto);
        return new RedirectView("main");
    }

    @GetMapping("/cannotdeleteadmin")
    public ModelAndView cannotDeleteAdmin() {
        return new ModelAndView("cannotdeleteadmin");
    }

    @GetMapping("retrievepassword")
        public ModelAndView retrievePasswordGet() {
        return new ModelAndView("retrievepassword");
    }

    @PostMapping("retrievepassword")
    public RedirectView retrievePasswordPost(HttpServletRequest req) throws MessagingException, UserCredentialsNotFoundException {
        String email = req.getParameter("email");
        UserCredentialsDto userCredentialsDto = userCredentialsService.getUserCredentialsByEmail(email);
        if (userCredentialsDto != null) {
            mailService.sendMail(userCredentialsDto.getEmail(),"Dokończ resetowanie hasła!",
                    "Link: " +"http://localhost:8080/userchangepassword", true  );
        }
        return new RedirectView("login");
    }

}
