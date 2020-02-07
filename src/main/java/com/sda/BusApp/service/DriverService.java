package com.sda.BusApp.service;

import com.sda.BusApp.exception.DriverNotFoundException;
import com.sda.BusApp.model.IsAvailable;
import com.sda.BusApp.model.controllerEnums.Actions;
import com.sda.BusApp.model.dto.BusDto;
import com.sda.BusApp.model.dto.DriverDto;
import com.sda.BusApp.model.dto.TripDto;
import com.sda.BusApp.model.entity.Driver;
import com.sda.BusApp.model.entity.Information;
import com.sda.BusApp.repository.DriverRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private InformationService informationService;

    private ModelMapper modelMapper;

    public DriverService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<DriverDto> getAllDrivers() {
        List<Driver> listOfDrivers = driverRepository.findAllDrivers();
        return listOfDrivers.stream()
                .map(m -> modelMapper.map(m, DriverDto.class))
                .collect(Collectors.toList());
    }

    public List<DriverDto> getAllAvailableDrivers() {
        List<Driver> listOfDrivers = driverRepository.findAllAvailableDrivers();
        return listOfDrivers.stream()
                .map(m -> modelMapper.map(m, DriverDto.class))
                .collect(Collectors.toList());
    }

    public Driver addDriver(DriverDto driverDto) {
        Driver driver = modelMapper.map(driverDto, Driver.class);
        Driver saved = driverRepository.save(driver);
        Information information = informationService.createInformation
                ("Dodanie kierowcy","Imię i nazwisko: " + driver.getName() + " " + driver.getSurname());
        informationService.addInformation(information);
        return saved;
    }

    public DriverDto getDriver(Long id) throws DriverNotFoundException {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Brak kierowcy o id: " + id));
        return modelMapper.map(driver, DriverDto.class);
    }

    public Driver deleteDriver(DriverDto driverDto) {
        driverDto.setIsAvailable(IsAvailable.NIEDOSTĘPNY);
        driverDto.setDateOfRemove(LocalDate.now());
        Driver driver = modelMapper.map(driverDto, Driver.class);
        Driver removed = driverRepository.save(driver);
        Information information = informationService.createInformation
                ("Usunięcie kierowcy","Imię i nazwisko: " + driver.getName() + " " + driver.getSurname());
        informationService.addInformation(information);
        return removed;
    }

    public Driver editDriver(DriverDto driverDto) {
        Driver driver = modelMapper.map(driverDto, Driver.class);
        Driver edited = driverRepository.save(driver);
        Information information = informationService.createInformation
                ("Edycja kierowcy","Imię i nazwisko: " + driver.getName() + " " + driver.getSurname());
        informationService.addInformation(information);
        return edited;
    }

    public List<DriverDto> getListOfAvailableDrivers(LocalDateTime startDate, LocalDateTime finishDate) {
        List<DriverDto> drivers = driverRepository.findAllDriversByDateAndTime(startDate, finishDate)
                .stream()
                .map(m -> modelMapper.map(m, DriverDto.class))
                .collect(Collectors.toList());
        List<DriverDto> allDrivers = getAllAvailableDrivers();
        return subtractLists(allDrivers, drivers);
    }

    public List<DriverDto> listOfDriversWhoNeedExaminationIn45Days() {
        LocalDate nowPlus45Days = LocalDate.now().plusDays(45);
        List<Driver> listOfDriversWhoNeedExamination = driverRepository.findListOfDriversWhoNeedExaminationIn45Days(nowPlus45Days);
        return listOfDriversWhoNeedExamination.stream()
                .map(m -> modelMapper.map(m, DriverDto.class))
                .collect(Collectors.toList());

    }

    public List<DriverDto> subtractLists(List<DriverDto> first, List<DriverDto> second) {
        return first.stream()
                .filter(i -> !second.contains(i))
                .collect(Collectors.toList());
    }

    public RedirectView getRedirectViewDrivers(HttpSession session, HttpServletRequest req) {
        session.setAttribute("id", Long.valueOf(req.getParameter("driverId")));
        Actions action = Actions.valueOf(req.getParameter("action"));
        String addressOfPage = "";
        if (getAllDrivers().isEmpty()) {
            return new RedirectView("/emptydrivers");
        }
        switch (action) {
            case UPDATE:
                addressOfPage = "/editdriver";
                break;
            case DELETE:
                addressOfPage = "/deletedriver";
                break;
            case RETURN:
                addressOfPage = "/index";
                break;
            case ADD:
                addressOfPage = "/adddriver";
                break;
        }
        return new RedirectView(addressOfPage);
    }

    public void DriverChoice(HttpServletRequest req, TripDto tripDto) throws DriverNotFoundException {
        String[] checkedIds;
        if (req.getParameterValues("driverIds") != null){
            checkedIds = req.getParameterValues("driverIds");
        } else checkedIds = new String[]{};
        List<String> listOFCheckedIds = Arrays.asList(checkedIds);
        List<Long> listOfCheckedIdsLong = listOFCheckedIds
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        List<DriverDto> listOfDrivers = getDriversByIds(listOfCheckedIdsLong);

        List<Driver> drivers = listOfDrivers
                .stream()
                .map(m -> modelMapper.map(m, Driver.class))
                .collect(Collectors.toList());
        tripDto.setDrivers(drivers);
    }

    public List<DriverDto> getDriversByIds(List<Long> listOFCheckedIds) {
        List<Driver> listOfDrivers = driverRepository.findByIdIn(listOFCheckedIds);
        return listOfDrivers
                .stream()
                .map(m -> modelMapper.map(m, DriverDto.class))
                .collect(Collectors.toList());
    }

    public List<DriverDto> getDriverDtos(TripDto tripDto) {
        LocalDateTime startDateAndTime = LocalDateTime.of(tripDto.getStartDate(), tripDto.getStartTime());
        LocalDateTime finishDateAndTime = LocalDateTime.of(tripDto.getFinishDate(), tripDto.getEstimatedFinishTime());
        return getListOfAvailableDrivers(startDateAndTime, finishDateAndTime);
    }

    public List<DriverDto> getDriverDtosEditTripSupportMethod(List<DriverDto> listOfAvailableDrivers, List<Driver> listOfDriversFromCurrentTrip) {
        List<DriverDto> listOfDriversDtoFromCurrentTrip = listOfDriversFromCurrentTrip
                .stream()
                .map(m -> modelMapper.map(m, DriverDto.class))
                .collect(Collectors.toList());
        listOfDriversDtoFromCurrentTrip.forEach(b -> b.setChecked(true));
        for (DriverDto driverDto : listOfAvailableDrivers) {
            if (!listOfDriversDtoFromCurrentTrip.contains(driverDto)) {
                listOfDriversDtoFromCurrentTrip.add(driverDto);
            }
        }
        return listOfDriversDtoFromCurrentTrip;
    }
}
