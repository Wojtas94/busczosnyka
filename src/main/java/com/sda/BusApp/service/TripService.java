package com.sda.BusApp.service;

import com.sda.BusApp.exception.TripNotFoundException;
import com.sda.BusApp.model.TypeOfTrip;
import com.sda.BusApp.model.controllerEnums.Actions;
import com.sda.BusApp.model.dto.BusDto;
import com.sda.BusApp.model.dto.TripDto;
import com.sda.BusApp.model.entity.Information;
import com.sda.BusApp.model.entity.Trip;
import com.sda.BusApp.repository.TripRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private InformationService informationService;

    private ModelMapper modelMapper;

    public TripService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<TripDto> getAllTrips() {
        List<Trip> trips = tripRepository.findAll();
        return trips.stream()
                .map(m -> modelMapper.map(m, TripDto.class))
                .collect(Collectors.toList());
    }

    public Trip addTrip(TripDto tripDto) {
        Trip trip = modelMapper.map(tripDto, Trip.class);
        trip.setStartDateAndTime(LocalDateTime.of(tripDto.getStartDate(), tripDto.getStartTime()));
        trip.setFinishDateAndTime(LocalDateTime.of(tripDto.getFinishDate(), tripDto.getEstimatedFinishTime()));
        Trip saved = tripRepository.save(trip);
        Information information = informationService.createInformation
                ("Dodanie wycieczki","Cel wycieczki: " + trip.getFinishPlace() + ", data startu wycieczki: "
                        + trip.getStartDateAndTime() + ", zamawiający: " + trip.getName());
        informationService.addInformation(information);
        return saved;
    }

    public TripDto getTripById(Long id) throws TripNotFoundException {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException("Brak wycieczki o id: " + id));
        TripDto tripDto =  modelMapper.map(trip, TripDto.class);
        tripDto.setStartDate(trip.getStartDateAndTime().toLocalDate());
        tripDto.setFinishDate(trip.getFinishDateAndTime().toLocalDate());
        tripDto.setStartTime(trip.getStartDateAndTime().toLocalTime());
        tripDto.setEstimatedFinishTime(trip.getFinishDateAndTime().toLocalTime());
        return tripDto;
    }

    public List<TripDto> getTripsWhichStartInNext7Days() {
        LocalDateTime nowPlus7Days = LocalDateTime.now().plusDays(7);
        List<Trip> tripsWhichStartInNext7Days = tripRepository.findTripsWhichStartInNext7Days(nowPlus7Days);
        return tripsWhichStartInNext7Days.stream()
                .map(m -> modelMapper.map(m, TripDto.class))
                .collect(Collectors.toList());
    }

    public void deleteTrip(TripDto tripToDelete) {
        Trip trip = modelMapper.map(tripToDelete, Trip.class);
        tripRepository.delete(trip);
        Information information = informationService.createInformation
                ("Usunięcie wycieczki","Cel wycieczki: " + trip.getFinishPlace() + ", data startu wycieczki: "
                        + trip.getStartDateAndTime() + ", zamawiający: " + trip.getName());
        informationService.addInformation(information);
    }

    public List<TripDto> findTripsByDateAndTime(LocalDateTime start, LocalDateTime finish) {
        return tripRepository.findAllTripsByDateAndTime(start, finish)
                .stream()
                .map(m -> modelMapper.map(m, TripDto.class))
                .collect(Collectors.toList());
    }


    public Trip editTrip(TripDto tripDto) {
        Trip trip = modelMapper.map(tripDto, Trip.class);
        trip.setStartDateAndTime(LocalDateTime.of(tripDto.getStartDate(), tripDto.getStartTime()));
        trip.setFinishDateAndTime(LocalDateTime.of(tripDto.getFinishDate(), tripDto.getEstimatedFinishTime()));
        Trip edited = tripRepository.save(trip);
        Information information = informationService.createInformation
                ("Edycja wycieczki","Cel wycieczki: " + trip.getFinishPlace() + ", data startu wycieczki: "
                        + trip.getStartDateAndTime() + ", zamawiający: " + trip.getName());
        informationService.addInformation(information);
        return edited;
    }

    public List<TripDto> getTenNewestTrips() {
        Page<Trip> newest10Trips = tripRepository.findAll(PageRequest.of
                (0, 10, Sort.by(Sort.Direction.DESC, "id")));
        List<Trip> trips = newest10Trips.getContent();
        return trips.stream()
                .map(m -> modelMapper.map(m, TripDto.class))
                .collect(Collectors.toList());
    }

    public List<TripDto> getRegularLineByDateAndTime(LocalDateTime start, LocalDateTime finish) {
        return tripRepository.findAllTripsByDateAndTime(start, finish)
                .stream()
                .filter(f -> TypeOfTrip.LINIA_REGULARNA.equals(f.getTypeOfTrip()))
                .map(m -> modelMapper.map(m, TripDto.class))
                .collect(Collectors.toList());

    }

    public List<TripDto> getTripsWithoutRegularLineByDateAndTime(LocalDateTime start, LocalDateTime finish) {
        return tripRepository.findAllTripsByDateAndTime(start, finish)
                .stream()
                .filter(f -> TypeOfTrip.WYCIECZKA.equals(f.getTypeOfTrip()))
                .map(m -> modelMapper.map(m, TripDto.class))
                .collect(Collectors.toList());
    }

    public List<TripDto> getLastTenTripsByName(String name) {
        List<Trip> tripsByName = tripRepository.findTripsByNameOrderByIdDesc(name, PageRequest.of(0, 10));
        return tripsByName.stream()
                .map(m -> modelMapper.map(m, TripDto.class))
                .collect(Collectors.toList());
    }

    public RedirectView getRedirectViewTrips(HttpServletRequest req, HttpSession session) {
        Actions action = Actions.valueOf(req.getParameter("action"));
        session.setAttribute("id", Long.valueOf(req.getParameter("tripId")));
        String addressOfPage = "";
        switch (action) {
            case ADD:
                if (getTenNewestTrips().isEmpty()) {
                    return new RedirectView("emptytrips");
                }
                addressOfPage = "addtrip";
                break;
            case RETURN:
                addressOfPage = "index";
                break;
            case DELETE:
                addressOfPage = "deletetrip";
                break;
            case UPDATE:
                addressOfPage = "edittrip";
                break;
            case INFO:
                addressOfPage = "infotrips";
                break;
        }
        return new RedirectView(addressOfPage);
    }

    public int getPrice(HttpServletRequest req) {
        double amountOfKilometers = Double.parseDouble(req.getParameter("amountOfKilometers"));
        double moneyForDriver = Double.parseDouble(req.getParameter("moneyForDriver"));
        double fuel = Double.parseDouble(req.getParameter("fuel"));
        double combustion = Double.parseDouble(req.getParameter("combustion"));
        double profitPerKilometer = Double.parseDouble(req.getParameter("profitPerKilometer"));
        double additionalFees;
        if (req.getParameter("additionalFees").equals("")) {
            additionalFees = 0;
        } else additionalFees = Double.parseDouble(req.getParameter("additionalFees"));
        String amountOfHours = req.getParameter("amountOfHours");
        int price = (int) ((amountOfKilometers * 0.01 * fuel * combustion + profitPerKilometer * amountOfKilometers
                + moneyForDriver + additionalFees) * 1.08);
        if (profitPerKilometer * amountOfKilometers < 300 && amountOfHours.equals("powyżej 7 godzin")) {
            price =  (int) ((amountOfKilometers * 0.01 * fuel * combustion + 300
                    + moneyForDriver + additionalFees) * 1.08);
        } else if (profitPerKilometer * amountOfKilometers < 300 && amountOfHours.equals("między 4 a 7 godzin")) {
            price =  (int) ((amountOfKilometers * 0.01 * fuel * combustion + 300 * 0.5
                    + moneyForDriver + additionalFees) * 1.08);
        }else if (profitPerKilometer * amountOfKilometers < 300 && amountOfHours.equals("do 4 godzin")) {
            price =  (int) ((amountOfKilometers * 0.01 * fuel * combustion + 300 * 0.3
                    + moneyForDriver + additionalFees) * 1.08);
        }
        return price;
    }


}
