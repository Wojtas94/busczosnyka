package com.sda.BusApp.service;

import com.sda.BusApp.model.dto.UserDto;
import com.sda.BusApp.model.entity.User;
import com.sda.BusApp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    public UserService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users
                .stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto getUserDto(HttpSession session) {
        Long id = (Long) session.getAttribute("id");
        return getUserById(id);
    }

}
