package com.sda.BusApp.DriverTest;

import com.sda.BusApp.exception.DriverNotFoundException;
import com.sda.BusApp.model.IsAvailable;
import com.sda.BusApp.model.dto.DriverDto;
import com.sda.BusApp.model.dto.TripDto;
import com.sda.BusApp.model.entity.Driver;
import com.sda.BusApp.model.entity.Information;
import com.sda.BusApp.repository.DriverRepository;
import com.sda.BusApp.repository.InformationRepository;
import com.sda.BusApp.service.DriverService;
import com.sda.BusApp.service.InformationService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private InformationService informationService;

    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private DriverService driverService = new DriverService(modelMapper);

    private Driver driver;

    @BeforeEach
    public void init() {
         driver = Driver.builder().dateOfExamination(LocalDate.of(2011, 11, 11))
                .surname("Walek").name("Mirek").id(1L).isAvailable(IsAvailable.NIEDOSTĘPNY).build();
    }

    @Test
    public void findListOfAllDrivers() {
        List<Driver> drivers = Collections.singletonList(driver);

        when(driverRepository.findAllDrivers()).thenReturn(drivers);

        List<DriverDto> driverDtos = drivers
                .stream()
                .map(m -> modelMapper.map(m, DriverDto.class))
                .collect(Collectors.toList());

        assertEquals(driverDtos, driverService.getAllDrivers());
    }

    @Test
    public void findListOfAvailableDrivers() {
        List<Driver> drivers = Collections.singletonList(driver);

        when(driverRepository.findAllAvailableDrivers()).thenReturn(drivers);

        List<DriverDto> driverDtos = drivers
                .stream()
                .map(m -> modelMapper.map(m, DriverDto.class))
                .collect(Collectors.toList());

        assertEquals(driverDtos, driverService.getAllAvailableDrivers());
    }

    @Test
    public void testAddDriver() {
        DriverDto driverDto = modelMapper.map(driver, DriverDto.class);

        when(driverRepository.save(any(Driver.class))).thenReturn(driver);
        when(informationService.addInformation(any(Information.class))).thenReturn(true);

        assertEquals(driver, driverService.addDriver(driverDto));
    }

    @Test
    public void getDriverTest_shouldReturnTrue() throws DriverNotFoundException {
        DriverDto driverDto = modelMapper.map(driver, DriverDto.class);

        when(driverRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(driver));

        assertEquals(driverDto, driverService.getDriver(1L));
    }

    @Test
    public void testDeleteDriver_shouldReturnTrue() {
        DriverDto driverDto = modelMapper.map(driver, DriverDto.class);

        when(informationService.addInformation(any(Information.class))).thenReturn(true);
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        assertEquals(driver, driverService.deleteDriver(driverDto));
    }

    @Test
    public void testEditDriver_shouldReturnTrue() {
        DriverDto driverDto = modelMapper.map(driver, DriverDto.class);

        when(informationService.addInformation(any(Information.class))).thenReturn(true);
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        assertEquals(driver, driverService.editDriver(driverDto));
    }

    @Test
    public void testGetListOfAvailableDrivers_shouldReturnTrue() {
        Driver driver1 = Driver.builder().dateOfExamination(LocalDate.of(2013, 12, 11))
                .surname("Jacek").name("Placek").id(2L).isAvailable(IsAvailable.NIEDOSTĘPNY).build();

        List<Driver> drivers = Arrays.asList(driver1, driver);

        List<DriverDto> availableDriver = Collections.singletonList(modelMapper.map(driver, DriverDto.class));

        DriverService driverService1 = Mockito.spy(driverService);

        when(driverRepository.findAllDriversByDateAndTime(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Collections.singletonList(driver1));
        when(driverRepository.findAllAvailableDrivers()).thenReturn(drivers);

        when(driverService1.subtractLists(anyList(), anyList())).thenReturn(availableDriver);

        assertEquals(availableDriver, driverService.getListOfAvailableDrivers
                (LocalDateTime.of(2014, 11, 11, 11, 11),
                        LocalDateTime.of(2014, 12, 11, 11, 11)));

    }

    @Test
    public void testListOfDriversWhoNeedExaminationIn45Days_shouldReturnTrue() {
        List<Driver> drivers = Collections.singletonList(driver);

        List<DriverDto> driverDtos = drivers
                .stream()
                .map(m -> modelMapper.map(m, DriverDto.class))
                .collect(Collectors.toList());

        when(driverRepository.findListOfDriversWhoNeedExaminationIn45Days(any(LocalDate.class))).thenReturn(drivers);

        assertEquals(driverDtos, driverService.listOfDriversWhoNeedExaminationIn45Days());
    }

    @Test
    public void testSubtractLists_shouldReturnTrue() {
        DriverDto driver1 = modelMapper.map(driver, DriverDto.class);
        DriverDto driver2 = DriverDto.builder().dateOfExamination(LocalDate.of(2013, 12, 11))
                .surname("Jacek").name("Placek").id(2L).isAvailable(IsAvailable.NIEDOSTĘPNY).build();

        List<DriverDto> drivers1 = Arrays.asList(driver1, driver2);
        List<DriverDto> drivers2 = Collections.singletonList(driver1);

        assertEquals(1, driverService.subtractLists(drivers1, drivers2).size());
        assertEquals(driver2, driverService.subtractLists(drivers1, drivers2).get(0));
    }

    @Test
    public void testGetRedirectViewDrivers_shouldReturnTrue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        List<Driver> drivers = Collections.singletonList(driver);

        when(driverRepository.findAllDrivers()).thenReturn(drivers);
        when(request.getParameter("action")).thenReturn("ADD");
        when(request.getParameter("driverId")).thenReturn("1");

        String action = "/adddriver";

        assertEquals(action, driverService.getRedirectViewDrivers(session, request).getUrl());
    }

    @Test
    public void testGetDriversByIds() {
        List<Driver> drivers = Collections.singletonList(driver);

        List<DriverDto> driverDtos = drivers
                .stream()
                .map(m -> modelMapper.map(m, DriverDto.class))
                .collect(Collectors.toList());

        when(driverRepository.findByIdIn(anyList())).thenReturn(drivers);

        assertEquals(driverDtos, driverService.getDriversByIds(Arrays.asList(1L, 2L)));
    }

    @Test
    public void testGetDriverDtos() {
//        List<Driver> drivers = Collections.singletonList(driver);
//
//        List<DriverDto> driverDtos = drivers
//                .stream()
//                .map(m -> modelMapper.map(m, DriverDto.class))
//                .collect(Collectors.toList());
//
//        TripDto tripDto = TripDto.builder().amountOfPassengers(22).startDate(LocalDate.of(2000, 12, 12))
//                .startTime(LocalTime.of(11, 21)).finishDate(LocalDate.of(2000, 12, 17))
//                .estimatedFinishTime(LocalTime.of(12, 21)).build();
//
//        DriverService driverService1 = spy(driverService);
//
//        doReturn(driverDtos).when(driverService1).getListOfAvailableDrivers(any(LocalDateTime.class), any(LocalDateTime.class));
//
//        assertEquals(driverDtos, driverService.getDriverDtos(tripDto));
    }


}
