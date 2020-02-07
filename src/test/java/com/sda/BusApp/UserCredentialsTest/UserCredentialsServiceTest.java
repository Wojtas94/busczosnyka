package com.sda.BusApp.UserCredentialsTest;

import com.sda.BusApp.exception.UserCredentialsNotFoundException;
import com.sda.BusApp.model.IsPasswordChanged;
import com.sda.BusApp.model.dto.UserCredentialsDto;
import com.sda.BusApp.model.dto.UserDto;
import com.sda.BusApp.model.entity.Information;
import com.sda.BusApp.model.entity.User;
import com.sda.BusApp.model.entity.UserCredentials;
import com.sda.BusApp.repository.UserCredentialsRepository;
import com.sda.BusApp.service.InformationService;
import com.sda.BusApp.service.UserCredentialsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserCredentialsServiceTest {

    @Mock
    private UserCredentialsRepository userCredentialsRepository;

    @Mock
    private InformationService informationService;

    private ModelMapper modelMapper = new ModelMapper();
    private PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder();

    @InjectMocks
    private UserCredentialsService userCredentialsService = new UserCredentialsService(modelMapper, passwordEncoder);

    private UserCredentials userCredentials;

    @BeforeEach
    public void init() {
        userCredentials = new UserCredentials(1L, "wojtas", "aa",
                "wojtas@wp.pl", "USER", IsPasswordChanged.TRUE,
                new User(1L, "Wojtek", "M", LocalDate.of(2017, 11, 11)));
    }

    @Test
    public void testAddUserCredentials_shouldReturnTrue() {
        UserCredentialsDto userCredentialsDto = modelMapper.map(userCredentials, UserCredentialsDto.class);

        BindingResult bindingResult = mock(BindingResult.class);

        UserCredentialsService userCredentialsService1 = Mockito.spy(userCredentialsService);

        doNothing().when(userCredentialsService1).validateUserLogin(userCredentialsDto);
        when(informationService.addInformation(any(Information.class))).thenReturn(true);
        when(userCredentialsRepository.save(any(UserCredentials.class))).thenReturn(userCredentials);

        assertEquals(userCredentials, userCredentialsService.addUserCredentials(userCredentialsDto, bindingResult));
    }

    @Test
    public void testGetAllUsers_shouldReturnTrue() {
        List<UserCredentials> users = Collections.singletonList(userCredentials);
        List<UserCredentialsDto> userDtos = users
                .stream()
                .map(u -> modelMapper.map(u, UserCredentialsDto.class))
                .collect(Collectors.toList());

        when(userCredentialsRepository.findAll()).thenReturn(users);

        assertEquals(userDtos, userCredentialsService.getAllUsers());
    }

    @Test
    public void testDeleteUserCredentials() {
        UserDto userDto = UserDto.builder().id(1L).surname("Kowalski").name("Wojtek")
                .joinDate(LocalDate.of(2017, 11, 11)).build();
        User user = modelMapper.map(userDto, User.class);

        doNothing().when(userCredentialsRepository).deleteByUser(user);
        when(informationService.addInformation(any(Information.class))).thenReturn(true);

        userCredentialsService.deleteByUser(userDto);

        verify(userCredentialsRepository, times(1)).deleteByUser(user);
    }

    @Test
    public void testGetUserCredentialsByUser_shouldReturnTrue() {
        UserCredentialsDto userCredentialsDto = modelMapper.map(userCredentials, UserCredentialsDto.class);
        UserDto userDto = UserDto.builder().id(1L).surname("Kowalski").name("Wojtek")
                .joinDate(LocalDate.of(2017, 11, 11)).build();
        User user = modelMapper.map(userDto, User.class);

        when(userCredentialsRepository.getByUser(user)).thenReturn(userCredentials);

        assertEquals(userCredentialsDto, userCredentialsService.getUserCredentialsByUser(userDto));
    }

    @Test
    public void testGetUserCredentialsByLogin_shouldReturnTrue() {
        UserCredentialsDto userCredentialsDto = modelMapper.map(userCredentials, UserCredentialsDto.class);
        String login = "Bartek";

        when(userCredentialsRepository.findByLogin(login)).thenReturn(userCredentials);

        assertEquals(userCredentialsDto, userCredentialsService.getUserCredentialsByLogin(login));
    }

    @Test
    public void testChangePassword_shouldReturnTrue() {
        UserCredentialsDto userCredentialsDto = modelMapper.map(userCredentials, UserCredentialsDto.class);

        when(userCredentialsRepository.save(any())).thenReturn(any());

        userCredentialsService.changePassword(userCredentialsDto);

        verify(userCredentialsRepository, times(1)).save(any(UserCredentials.class));
    }

    @Test
    public void testGetUserCredentialsByEmail() throws UserCredentialsNotFoundException {
        UserCredentialsDto userCredentialsDto = modelMapper.map(userCredentials, UserCredentialsDto.class);

        String email = "wojtek@.pl";

        when(userCredentialsRepository.getByEmail(email)).thenReturn(userCredentials);

        assertEquals(userCredentialsDto, userCredentialsService.getUserCredentialsByEmail(email));
    }

    @Test
    public void testGetRedirectViewUser_shouldReturnTrue() {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("action")).thenReturn("ADD");
        when(request.getParameter("userId")).thenReturn("1");

        assertEquals("addnewuser", userCredentialsService.getRedirectViewUser(request, session).getUrl());
    }
}
