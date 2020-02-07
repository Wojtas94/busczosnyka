package com.sda.BusApp.service;

import com.sda.BusApp.exception.BusNotFoundException;
import com.sda.BusApp.model.IsAvailable;
import com.sda.BusApp.model.controllerEnums.Actions;
import com.sda.BusApp.model.dto.BusDto;
import com.sda.BusApp.model.dto.RepairDto;
import com.sda.BusApp.model.dto.TripDto;
import com.sda.BusApp.model.entity.Bus;
import com.sda.BusApp.model.entity.Information;
import com.sda.BusApp.model.entity.Repair;
import com.sda.BusApp.repository.BusRepository;
import com.sda.BusApp.repository.RepairRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private InformationService informationService;

    private ModelMapper modelMapper;

    public BusService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<BusDto> getAllBuses() {
        List<Bus> allBuses = busRepository.findAllBuses();
        return allBuses.stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());
    }

    public List<BusDto> getAllAvailableBuses() {
        List<Bus> allBuses = busRepository.findAllAvailableBuses();
        return allBuses.stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());
    }

    public Bus addBus(BusDto busDto) {
        Bus bus = modelMapper.map(busDto, Bus.class);
        Bus save = busRepository.save(bus);
        informationService.addInformation(informationService.createInformation
                ("Dodanie busa", "Nazwa busa: " + bus.getName()));
        return save;
    }

    public Bus remove(BusDto busDto) {
        busDto.setDateOfRemove(LocalDate.now());
        busDto.setIsAvailable(IsAvailable.NIEDOSTĘPNY);
        Bus bus = modelMapper.map(busDto, Bus.class);
        Bus removeBus = busRepository.save(bus);
        Information information = informationService.createInformation
                ("Usunięcie busa", "Nazwa busa: " + bus.getName());
        informationService.addInformation(information);
        return removeBus;
    }

    public BusDto getBus(Long id) throws BusNotFoundException {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new BusNotFoundException("Brak busa o id: " + id));
        return modelMapper.map(bus, BusDto.class);
    }

    public Bus editBus(BusDto busDto) {
        Bus bus = modelMapper.map(busDto, Bus.class);
        Bus editedBus = busRepository.save(bus);
        Information information = informationService.createInformation
                ("Edycja busa", "Nazwa busa: " + bus.getName());
        System.out.println(information);
        informationService.addInformation(information);
        return editedBus;
    }

    public List<BusDto> getListOfAvailableBusBetweenDates(LocalDateTime startDate, LocalDateTime finishDate) {
        List<BusDto> buses = busRepository.findAllBusesBetweenDateAndTime(startDate, finishDate)
                .stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());
        List<BusDto> allBuses = getAllAvailableBuses();
        return subtractLists(allBuses, buses); //uzyskanie busów dostępnych w
        // danym przedziale czasu poprzez wyjęcie z bazy wszystkich busów oraz busów
        // które są zajęte w tym okresie i odjęcie zajętych od wszystkich aby uzyskać dostępne
    }

    public List<BusDto> subtractLists(List<BusDto> first, List<BusDto> second) {
        return first.stream()
                .filter(i -> !second.contains(i))
                .collect(Collectors.toList());
    }

    public List<BusDto> findBusesBetweenSeats(Integer from, Integer to) {
        List<Bus> busesBetweenSeats = busRepository.findAllByNumberOfSeatsIsBetween(from, to);
        return busesBetweenSeats
                .stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());
    }

    public List<RepairDto> getRepairs(Long id) {
        List<Repair> repairs = repairRepository.findAllRepairsByBus(busRepository.getOne(id));
        return repairs
                .stream()
                .map(m -> modelMapper.map(m, RepairDto.class))
                .collect(Collectors.toList());
    }

    public List<BusDto> listOfBusDtosWithNeededInspection() {
        LocalDate nowPlusSevenDays = LocalDate.now().plusDays(7);
        List<Bus> listOfBusWithNeededInspection = busRepository.findListOfBusesWithNeededInspection(nowPlusSevenDays);
        return listOfBusWithNeededInspection
                .stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());
    }

    public Repair addRepair(RepairDto repairDto) {
        Repair repair = modelMapper.map(repairDto, Repair.class);
        Repair save = repairRepository.save(repair);
        informationService.addInformation(informationService.createInformation
                ("Dodanie naprawy", "Nazwa busa: " + repair.getBus().getName()
                        + " co było naprawiane: " + repair.getInfo()));
        return save;
    }

    public RepairDto getRepair(Long id) {
        Repair repair = repairRepository.findById(id).orElseThrow(RuntimeException::new);
        return modelMapper.map(repair, RepairDto.class);
    }

    public boolean removeRepair(RepairDto repairDto) {
        Repair repair = modelMapper.map(repairDto, Repair.class);
        repairRepository.delete(repair);
        informationService.addInformation(informationService.createInformation
                ("Usunięcie naprawy", "Nazwa busa: " + repair.getBus().getName()
                        + " co było naprawiane: " + repair.getInfo()));
        return true;
    }

    public Repair editRepair(RepairDto repairDto) {
        Repair repair = modelMapper.map(repairDto, Repair.class);
        Repair edit = repairRepository.save(repair);
        informationService.addInformation(informationService.createInformation
                ("Edycja naprawy", "Nazwa busa: " + repair.getBus().getName()
                        + " co było naprawiane: " + repair.getInfo()));
        return edit;
    }

    public String getAddressOfPageBuses(HttpSession session, HttpServletRequest req) {
        String addressOfPage = "";
        session.setAttribute("id", Long.valueOf(req.getParameter("busId")));
        Actions action = Actions.valueOf(req.getParameter("action"));
        switch (action) {
            case ADD:
                addressOfPage = "/addbus";
                break;
            case UPDATE:
                addressOfPage = "/editbus";
                break;
            case DELETE:
                addressOfPage = "/deletebus";
                break;
            case RETURN:
                addressOfPage = "/index";
                break;
            case REPAIR:
                addressOfPage = "/repairs";
                break;
        }
        return addressOfPage;
    }

    public String getAddressOfPageRepairs(HttpServletRequest req, HttpSession session) {
        String addressOfPage = "";
        Actions action = Actions.valueOf(req.getParameter("action"));
        session.setAttribute("repairId", Long.valueOf(req.getParameter("repairId")));
        switch (action) {
            case ADD:
                addressOfPage = "/addrepair";
                break;
            case UPDATE:
                addressOfPage = "/editrepair";
                break;
            case DELETE:
                addressOfPage = "/deleterepair";
                break;
            case RETURN:
                addressOfPage = "/index";
                break;
        }
        return addressOfPage;
    }

    public List<BusDto> getBusDtosByDate(TripDto tripDto) {
        LocalDateTime startDateAndTime = LocalDateTime.of(tripDto.getStartDate(), tripDto.getStartTime());
        LocalDateTime finishDateAndTime = LocalDateTime.of(tripDto.getFinishDate(), tripDto.getEstimatedFinishTime());
        return getListOfAvailableBusBetweenDates(startDateAndTime, finishDateAndTime);
    }

    public void BusChoice(HttpServletRequest req, TripDto tripDto) throws BusNotFoundException {
        String[] checkedIds;
        if (req.getParameterValues("busIds") != null) {
            checkedIds = req.getParameterValues("busIds");
        } else checkedIds = new String[]{};
        List<String> listOFCheckedIds = Arrays.asList(checkedIds);
        List<Long> listOfCheckedIdsLong = listOFCheckedIds
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        List<BusDto> listOfBuses = getBusesByIds(listOfCheckedIdsLong);
        List<Bus> buses = listOfBuses
                .stream()
                .map(m -> modelMapper.map(m, Bus.class))
                .collect(Collectors.toList());
        tripDto.setBuses(buses);
    }

    public List<BusDto> getBusesByIds(List<Long> listOfCheckedIdsLong) {
        List<Bus> listOfBuses = busRepository.findByIdIn(listOfCheckedIdsLong);
        return listOfBuses
                .stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());
    }

    public Bus addRepairSetBus(@ModelAttribute RepairDto repair, Long id) throws BusNotFoundException {
        BusDto busDto = getBus(id);
        Bus bus = modelMapper.map(busDto, Bus.class);
        repair.setBus(bus);
        return repair.getBus();
    }

    public List<BusDto> getBusDtosByAmountOfSeats(HttpServletRequest req) {
        Integer from = Integer.valueOf(req.getParameter("from"));
        Integer to = Integer.valueOf(req.getParameter("to"));
        List<BusDto> busDtos = findBusesBetweenSeats(from, to);
        return busDtos;
    }

    public List<BusDto> getBusDtosEditTripSupportMethod(List<BusDto> listOfAvailableBuses, List<Bus> listOfBusesFromCurrentTrip) {
        List<BusDto> listOfBusesDtoFromCurrentTrip = listOfBusesFromCurrentTrip
                .stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());
        listOfBusesDtoFromCurrentTrip.forEach(b -> b.setChecked(true));
        for (BusDto busDto : listOfAvailableBuses) {
            if (!listOfBusesDtoFromCurrentTrip.contains(busDto)) {
                listOfBusesDtoFromCurrentTrip.add(busDto);
            }
        }
        return listOfBusesDtoFromCurrentTrip;
    }


}
