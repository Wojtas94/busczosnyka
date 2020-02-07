package com.sda.BusApp.service;

import com.sda.BusApp.exception.UserCredentialsNotFoundException;
import com.sda.BusApp.model.IsPasswordChanged;
import com.sda.BusApp.model.controllerEnums.Actions;
import com.sda.BusApp.model.dto.UserCredentialsDto;
import com.sda.BusApp.model.dto.UserDto;
import com.sda.BusApp.model.entity.Information;
import com.sda.BusApp.model.entity.User;
import com.sda.BusApp.model.entity.UserCredentials;
import com.sda.BusApp.repository.UserCredentialsRepository;
import com.sda.BusApp.validation.BindingValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCredentialsService {

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    private InformationService informationService;

    private ModelMapper modelMapper;

    private PasswordEncoder encoder;

    public UserCredentialsService(ModelMapper modelMapper, PasswordEncoder encoder) {
        this.modelMapper = modelMapper;
        this.encoder = encoder;
    }

    public UserCredentials addUserCredentials(UserCredentialsDto userCredentialsDto, BindingResult result) {
        BindingValidator.validate(result);
        validateUserLogin(userCredentialsDto);
        String hash = encoder.encode(userCredentialsDto.getPassword());
        UserCredentials user = modelMapper.map(userCredentialsDto, UserCredentials.class);
        user.setPassword(hash);
        UserCredentials saved = userCredentialsRepository.save(user);
        Information information = informationService.createInformation
                ("Dodanie użytkownika", "Login użytkownika: " + user.getLogin());
        informationService.addInformation(information);
        return saved;
    }

    public void validateUserLogin(UserCredentialsDto userCredentialsDto) {
        if (loginExist(userCredentialsDto)) {
            throw new RuntimeException("Login already exist in database");
        }
//        if(!checkAuthorities(userCredentialsDto)){
//            throw new RuntimeException("Wrong role");
//        }
    }

    private boolean loginExist(UserCredentialsDto userCredentialsDto) {
        return userCredentialsRepository.countByLogin(userCredentialsDto.getLogin()) > 0;
    }


    public List<UserCredentialsDto> getAllUsers() {
        List<UserCredentials> userCredentials = userCredentialsRepository.findAll();
        return userCredentials
                .stream()
                .map(u -> modelMapper.map(u, UserCredentialsDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteByUser(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        userCredentialsRepository.deleteByUser(user);
        Information information = informationService.createInformation
                ("Usunięcie użytkownika",  "Imie i nazwisko użytkownika: " + user.getName() + " " + user.getSurname());
        informationService.addInformation(information);
    }

    public UserCredentialsDto getUserCredentialsByUser(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        UserCredentials userCredentials = userCredentialsRepository.getByUser(user);
        return modelMapper.map(userCredentials, UserCredentialsDto.class);
    }

    private boolean checkAuthorities(UserCredentialsDto userCredentialsDto) {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .toArray()[0]
                .toString()
                .equals(userCredentialsDto.getRole());
    }

    public UserCredentialsDto getUserCredentialsByLogin(String login) {
        UserCredentials userCredentials = userCredentialsRepository.findByLogin(login);
        return modelMapper.map(userCredentials, UserCredentialsDto.class);
    }

    public void changePassword(UserCredentialsDto userCredentialsDto) {
        userCredentialsDto.setIsPasswordChanged(IsPasswordChanged.TRUE);
        UserCredentials userCredentials = modelMapper.map(userCredentialsDto, UserCredentials.class);
        userCredentialsRepository.save(userCredentials);
    }

    public RedirectView getRedirectViewUser(HttpServletRequest req, HttpSession session) {
        Actions action = Actions.valueOf(req.getParameter("action"));
        session.setAttribute("id", Long.valueOf(req.getParameter("userId")));
        String addressOfPage = "";
        switch (action) {
            case ADD:
                addressOfPage = "addnewuser";
                break;
            case DELETE:
                addressOfPage = "deleteuser";
                break;
        }
        return new RedirectView(addressOfPage);
    }

    public UserCredentialsDto getUserCredentialsByEmail(String email) throws UserCredentialsNotFoundException{
        UserCredentials userCredentials = userCredentialsRepository.getByEmail(email);
        if (userCredentials == null) {
            throw new UserCredentialsNotFoundException("Brak użytkownika o emailu: " + email);
        }
        return modelMapper.map(userCredentials, UserCredentialsDto.class);
    }

    public void addNewUserSupportMethod(@ModelAttribute @Valid UserCredentialsDto userCredentialsDto, HttpServletRequest req) {
        if (getAllUsers().isEmpty()) {
            userCredentialsDto.setRole("ROLE_ADMIN");
        } else userCredentialsDto.setRole("ROLE_USER");
        User user = User.builder().joinDate(LocalDate.now())
                .name(req.getParameter("name")).surname(req.getParameter("surname")).build();
        userCredentialsDto.setUser(user);
        userCredentialsDto.setIsPasswordChanged(IsPasswordChanged.FALSE);
    }
}
