package com.sda.BusApp.TripTest;

import com.sda.BusApp.exception.TripNotFoundException;
import com.sda.BusApp.model.TypeOfTrip;
import com.sda.BusApp.model.dto.TripDto;
import com.sda.BusApp.model.entity.Information;
import com.sda.BusApp.model.entity.Trip;
import com.sda.BusApp.repository.TripRepository;
import com.sda.BusApp.service.InformationService;
import com.sda.BusApp.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private InformationService informationService;

    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private TripService tripService = new TripService(modelMapper);

    private Trip trip;

    @BeforeEach
    public void init() {
        trip = Trip.builder().startPlace("Olesno").finishPlace("Krakow").price(122).notes("brak").kilometers(125)
                .id(1L).startDateAndTime(LocalDateTime.of(2018, 11, 12, 10, 26))
                .finishDateAndTime(LocalDateTime.of(2018, 12, 12, 10, 28))
                .amountOfPassengers(50).name("szkola").phoneNumber("23533225").typeOfTrip(TypeOfTrip.LINIA_REGULARNA).build();
    }

    @Test
    public void testGetAllTrips_shouldReturnTrue() {
        TripDto tripDto = modelMapper.map(trip, TripDto.class);

        List<TripDto> tripDtos = Collections.singletonList(tripDto);

        List<Trip> trips = Collections.singletonList(trip);

        when(tripRepository.findAll()).thenReturn(trips);

        assertEquals(tripDtos, tripService.getAllTrips());
    }

    @Test
    public void testAddTrip_shouldReturnTrue() {
        TripDto tripDto = modelMapper.map(trip, TripDto.class);
        tripDto.setStartDate(trip.getStartDateAndTime().toLocalDate());
        tripDto.setEstimatedFinishTime(trip.getFinishDateAndTime().toLocalTime());
        tripDto.setStartTime(trip.getStartDateAndTime().toLocalTime());
        tripDto.setFinishDate(trip.getFinishDateAndTime().toLocalDate());

        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        assertEquals(trip, tripService.addTrip(tripDto));
    }

    @Test
    public void testGetTripById_shouldReturnTrue() throws TripNotFoundException {
        TripDto tripDto = modelMapper.map(trip, TripDto.class);
        tripDto.setStartDate(trip.getStartDateAndTime().toLocalDate());
        tripDto.setEstimatedFinishTime(trip.getFinishDateAndTime().toLocalTime());
        tripDto.setStartTime(trip.getStartDateAndTime().toLocalTime());
        tripDto.setFinishDate(trip.getFinishDateAndTime().toLocalDate());

        when(tripRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(trip));

        assertEquals(tripDto, tripService.getTripById(1L));
    }

    @Test
    public void testFindTripsWhichStartInNext7Days() {
        List<TripDto> tripDtos = Collections.singletonList(modelMapper.map(trip, TripDto.class));
        List<Trip> trips = Collections.singletonList(trip);

        when(tripRepository.findTripsWhichStartInNext7Days(any(LocalDateTime.class))).thenReturn(trips);

        assertEquals(tripDtos, tripService.getTripsWhichStartInNext7Days());
    }

    @Test
    public void testDeleteTrip() {
        TripDto tripDto = modelMapper.map(trip, TripDto.class);

        doNothing().when(tripRepository).delete(any(Trip.class));
        when(informationService.addInformation(any(Information.class))).thenReturn(true);

        tripService.deleteTrip(tripDto);

        verify(tripRepository, times(1)).delete(trip);
    }

    @Test
    public void testFindTripsByDateAndTime_shouldReturnTrue() {
        List<Trip> trips = Collections.singletonList(trip);
        List<TripDto> tripDtos = Collections.singletonList(modelMapper.map(trip, TripDto.class));

        when(tripRepository.findAllTripsByDateAndTime(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(trips);

        assertEquals(tripDtos, tripService.findTripsByDateAndTime
                (LocalDateTime.of(2015, 11, 11, 10, 30),
                        LocalDateTime.of(2015, 12, 11, 15, 30)));
    }

    @Test
    public void testEditTrip_shouldReturnTrue() {
        TripDto tripDto = modelMapper.map(trip, TripDto.class);
        tripDto.setStartDate(trip.getStartDateAndTime().toLocalDate());
        tripDto.setEstimatedFinishTime(trip.getFinishDateAndTime().toLocalTime());
        tripDto.setStartTime(trip.getStartDateAndTime().toLocalTime());
        tripDto.setFinishDate(trip.getFinishDateAndTime().toLocalDate());

        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        assertEquals(trip, tripService.editTrip(tripDto));
    }

    @Test
    public void testGetTenNewestTrips_shouldReturnTrue() {
        List<Trip> trips = Collections.singletonList(trip);
        List<TripDto> tripDtos = Collections.singletonList(modelMapper.map(trip, TripDto.class));

        Page<Trip> pageTrips = new PageImpl<>(trips);

        when(tripRepository.findAll(org.mockito.Matchers.isA(Pageable.class))).thenReturn(pageTrips);

        assertEquals(tripDtos, tripService.getTenNewestTrips());
    }

    @Test
    public void testGetRegularLineByDateAndTime_shouldReturnTrue() {
        List<Trip> trips = Collections.singletonList(trip);
        List<TripDto> tripDtos = Collections.singletonList(modelMapper.map(trip, TripDto.class));

        when(tripRepository.findAllTripsByDateAndTime(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(trips);

        assertEquals(tripDtos, tripService.getRegularLineByDateAndTime
                (LocalDateTime.of(2015, 11, 11, 10, 30),
                LocalDateTime.of(2015, 12, 11, 15, 30)));
    }

    @Test
    public void testGetTripsWithoutRegularLineByDateAndTime_shouldReturnTrue() {
        trip.setTypeOfTrip(TypeOfTrip.WYCIECZKA);
        List<Trip> trips = Collections.singletonList(trip);
        List<TripDto> tripDtos = Collections.singletonList(modelMapper.map(trip, TripDto.class));

        when(tripRepository.findAllTripsByDateAndTime(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(trips);

        assertEquals(tripDtos, tripService.getTripsWithoutRegularLineByDateAndTime(
                LocalDateTime.of(2015, 11, 11, 10, 30),
                        LocalDateTime.of(2015, 12, 11, 15, 30)));
    }

    @Test
    public void testGetLastTenTripsByName_shouldReturnTrue() {
        List<Trip> trips = Collections.singletonList(trip);
        List<TripDto> tripDtos = Collections.singletonList(modelMapper.map(trip, TripDto.class));

        when(tripRepository.findTripsByNameOrderByIdDesc(anyString(), any(Pageable.class))).thenReturn(trips);

        assertEquals(tripDtos, tripService.getLastTenTripsByName("name"));
    }

    @Test
    public void testGetRedirectViewTrips_shouldReturnTrue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        List<Trip> trips = Collections.singletonList(trip);

        Page<Trip> pageTrips = new PageImpl<>(trips);

        when(tripRepository.findAll(org.mockito.Matchers.isA(Pageable.class))).thenReturn(pageTrips);
        when(request.getParameter("action")).thenReturn("ADD");
        when(request.getParameter("tripId")).thenReturn("1");

        String action = "addtrip";

        assertEquals(action, tripService.getRedirectViewTrips(request, session).getUrl());
    }
}
