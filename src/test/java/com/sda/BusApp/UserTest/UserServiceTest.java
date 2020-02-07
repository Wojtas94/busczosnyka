package com.sda.BusApp.UserTest;

import com.sda.BusApp.model.dto.UserDto;
import com.sda.BusApp.model.entity.User;
import com.sda.BusApp.repository.UserRepository;
import com.sda.BusApp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private UserService userService = new UserService(modelMapper);

    private User user;

    @BeforeEach
    public void init() {
        user = User.builder().joinDate(LocalDate.now()).name("Wojtek").surname("Misiaszek").id(1L).build();
    }

    @Test
    public void testGetAllUsers_shouldReturnTrue() {
        List<User> users = Collections.singletonList(user);

        List<UserDto> userDtos = users
                .stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(users);

        assertEquals(userDtos, userService.getAllUsers());
    }

    @Test
    public void testGetUserById_shouldReturnTrue() {
        UserDto userDto = modelMapper.map(user, UserDto.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertEquals(userDto, userService.getUserById(1L));
    }

    @Test
    public void testGetUserDto_shouldReturnTrue() {
        UserDto userDto = modelMapper.map(user, UserDto.class);
        HttpSession session = mock(HttpSession.class);

        when(session.getAttribute(anyString())).thenReturn(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertEquals(userDto, userService.getUserDto(session));
    }




}
