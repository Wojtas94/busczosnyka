package com.sda.BusApp.BusTests;

import com.sda.BusApp.exception.BusNotFoundException;
import com.sda.BusApp.model.dto.BusDto;
import com.sda.BusApp.model.dto.RepairDto;
import com.sda.BusApp.model.dto.TripDto;
import com.sda.BusApp.model.entity.Bus;
import com.sda.BusApp.model.entity.Information;
import com.sda.BusApp.model.entity.Repair;
import com.sda.BusApp.repository.BusRepository;
import com.sda.BusApp.repository.RepairRepository;
import com.sda.BusApp.service.BusService;
import com.sda.BusApp.service.InformationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BusServiceTest {


    @Mock
    private BusRepository busRepository;

    @Mock
    private InformationService informationService;

    @Mock
    private RepairRepository repairRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private BusService busService = new BusService(modelMapper);

    private Bus bus;

    @BeforeEach
    public void init() {
        System.out.println("beforreeeeeeeeee");
        bus = Bus.builder().name("Volvo").numberOfSeats(77)
                .dateOfInspection(LocalDate.of(2019, 12, 25))
                .registrationNumber("kda3355").id(1L).build();
    }

    @Test
    public void testGetAllBuses_shouldReturnTrue() {
        List<Bus> allBuses = new ArrayList<>();

        allBuses.add(Bus.builder().name("Mercedes").registrationNumber("kda4455").id(1L).build());
        allBuses.add(Bus.builder().name("Volkswagen").registrationNumber("kda7755").id(2L).build());
        List<BusDto> allBusDtos = allBuses
                .stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());

        when(busRepository.findAllBuses()).thenReturn(allBuses);
        assertEquals(allBusDtos, busService.getAllBuses());
    }

    @Test
    public void testAddBus_shouldReturnTrue() {
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(informationService.addInformation(any())).thenReturn(true);
        BusDto busDto = modelMapper.map(bus, BusDto.class);

        assertEquals(bus, busService.addBus(busDto));
    }

    @Test
    public void testGetAllAvailableBuses_shouldReturnTrue() {
        List<Bus> allBuses = new ArrayList<>();

        allBuses.add(Bus.builder().name("Mercedes").registrationNumber("kda4455").id(1L).build());
        allBuses.add(Bus.builder().name("Volkswagen").registrationNumber("kda7755").id(2L).build());
        List<BusDto> allBusDtos = allBuses
                .stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());

        when(busRepository.findAllAvailableBuses()).thenReturn(allBuses);
        assertEquals(allBusDtos, busService.getAllAvailableBuses());
    }

    @Test
    public void testRemoveBus_shouldReturnTrue() {
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(informationService.addInformation(any(Information.class))).thenReturn(true);

        BusDto busDto = modelMapper.map(bus, BusDto.class);

        assertEquals(bus, busService.remove(busDto));
    }

    @Test
    public void testGetBus_shouldReturnTrue() throws BusNotFoundException {
        when(busRepository.findById(bus.getId())).thenReturn(java.util.Optional.of(bus));

        BusDto busDto = modelMapper.map(bus, BusDto.class);

        assertEquals(busDto, busService.getBus(bus.getId()));
    }

    @Test
    public void testEditBus_shouldReturnTrue() {
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        BusDto busDto = modelMapper.map(bus, BusDto.class);

        assertEquals(bus, busService.editBus(busDto));
    }

    @Test
    public void testSubtractList_shouldReturnTrue() {
        BusDto bus1 = BusDto.builder().name("Setra").numberOfSeats(44)
                .dateOfInspection(LocalDate.of(2019, 12, 25))
                .registrationNumber("kda4444").build();
        BusDto bus2 = BusDto.builder().name("Mercedes").numberOfSeats(12)
                .dateOfInspection(LocalDate.of(2020, 12, 25))
                .registrationNumber("kda8888").build();

        List<BusDto> list1 = Arrays.asList(bus1, bus2);
        List<BusDto> list2 = Collections.singletonList(bus1);
        List<BusDto> subtractedList = busService.subtractLists(list1, list2);

        assertEquals(1, subtractedList.size());

        assertEquals(bus2, subtractedList.get(0));

    }

    @Test
    public void testGetListOfAvailableBusBetweenDates_shouldReturnTrue() {
        BusDto bus1 = BusDto.builder().id(3L).name("Setra").numberOfSeats(44)
                .dateOfInspection(LocalDate.of(2019, 12, 25))
                .registrationNumber("kda4444").build();
        BusDto bus2 = BusDto.builder().id(2L).name("Mercedes").numberOfSeats(12)
                .dateOfInspection(LocalDate.of(2020, 12, 25))
                .registrationNumber("kda8888").build();
        BusDto bus3 = modelMapper.map(bus, BusDto.class);

        List<BusDto> allBuses = Arrays.asList(bus1, bus2, bus3);

        List<BusDto> availableBuses = Arrays.asList(bus1, bus2);

        BusService busService1 = Mockito.spy(busService);

        Mockito.doReturn(new ArrayList<>()).when(busService1)
                .getAllAvailableBuses();

        when(busRepository.findAllBusesBetweenDateAndTime(any(), any())).thenReturn(Collections.singletonList(bus));

        when(busRepository.findAllAvailableBuses()).thenReturn(availableBuses.stream().map(m -> modelMapper.map(m, Bus.class)).collect(Collectors.toList()));
//        Mockito.doReturn(availableBuses).when(busService1).subtractLists(anyList(), anyList());

        assertEquals(availableBuses, busService.getListOfAvailableBusBetweenDates
                (LocalDateTime.of(2019, 12, 25, 11, 15), LocalDateTime.of(2019, 12, 26, 11, 15)));
    }

    @Test
    public void testFindBusesBetweenSeats() {
        Bus bus1 = Bus.builder().name("Setra").numberOfSeats(44)
                .dateOfInspection(LocalDate.of(2019, 12, 25))
                .registrationNumber("kda4444").id(1L).build();
        Bus bus2 = Bus.builder().name("Mercedes").numberOfSeats(12)
                .dateOfInspection(LocalDate.of(2020, 12, 25))
                .registrationNumber("kda8888").id(2L).build();

        List<Bus> list1 = Arrays.asList(bus1, bus2);

        List<BusDto> busDtos = list1
                .stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());

        when(busRepository.findAllByNumberOfSeatsIsBetween(anyInt(), anyInt())).thenReturn(list1);

        assertEquals(busDtos, busService.findBusesBetweenSeats(1, 111));
    }

    @Test
    public void testGetRepairsById_shouldReturnTrue() {
        Repair repair1 = Repair.builder().info("Rozrząd").date(LocalDate.of(2017, 4, 22)).mileage(222111).build();
        Repair repair2 = Repair.builder().info("Wymiana klocków").date(LocalDate.of(2018, 7, 26)).mileage(255555).build();

        List<Repair> repairs = new ArrayList<>();
        repairs.add(repair1);
        repairs.add(repair2);

        bus.setRepairs(repairs);

        List<RepairDto> repairDtos = repairs
                .stream()
                .map(r -> modelMapper.map(r, RepairDto.class))
                .collect(Collectors.toList());

        when(busRepository.getOne(anyLong())).thenReturn(bus);
        when(repairRepository.findAllRepairsByBus(any(Bus.class))).thenReturn(repairs);

        assertEquals(repairDtos, busService.getRepairs(1L));
    }

    @Test
    public void testListOfBusDtosWithNeededInspection_shouldReturnTrue() {
        Bus bus2 = Bus.builder().name("Setra").numberOfSeats(44)
                .dateOfInspection(LocalDate.of(2019, 12, 25))
                .registrationNumber("kda4444").id(1L).build();
        Bus bus3 = Bus.builder().name("Mercedes").numberOfSeats(12)
                .dateOfInspection(LocalDate.of(2020, 12, 25))
                .registrationNumber("kda8888").id(2L).build();

        LocalDate nowPlusSevenDays = LocalDate.now().plusDays(7);

        List<Bus> buses = Arrays.asList(bus, bus2);

        List<BusDto> busDtos = buses
                .stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());

        when(busRepository.findListOfBusesWithNeededInspection(nowPlusSevenDays))
                .thenReturn(buses);

        assertEquals(busDtos, busService.listOfBusDtosWithNeededInspection());
    }

    @Test
    public void testAddRepair_shouldReturnRepair() {
        Repair repair1 = Repair.builder().info("Rozrząd").date(LocalDate.of(2017, 4, 22)).mileage(222111).build();
        repair1.setBus(bus);

        when(repairRepository.save(any(Repair.class))).thenReturn(repair1);
        when(informationService.addInformation(any(Information.class))).thenReturn(true);

        RepairDto repairDto = modelMapper.map(repair1, RepairDto.class);

        assertEquals(repair1, busService.addRepair(repairDto));
    }

    @Test
    public void testGetRepair_shouldReturnRepairDto() {
        Repair repair1 = Repair.builder().info("Rozrząd").date(LocalDate.of(2017, 4, 22)).mileage(222111).id(1L).build();

        when(repairRepository.findById(anyLong())).thenReturn(Optional.of(repair1));

        RepairDto repairDto = modelMapper.map(repair1, RepairDto.class);

        assertEquals(repairDto, busService.getRepair(repair1.getId()));
    }

    @Test
    public void testRemoveRepair_shouldReturnTrue() {
        Repair repair1 = Repair.builder().info("Rozrząd").date(LocalDate.of(2017, 4, 22)).mileage(222111).build();
        repair1.setBus(bus);
        RepairDto repairDto = modelMapper.map(repair1, RepairDto.class);


        doNothing().when(repairRepository).delete(any(Repair.class));
        when(informationService.addInformation(any(Information.class))).thenReturn(true);

        assertTrue(busService.removeRepair(repairDto));
    }

    @Test
    public void testEditRepair_shouldReturnTrue() {
        Repair repair1 = Repair.builder().info("Rozrząd").date(LocalDate.of(2017, 4, 22)).mileage(222111).build();
        repair1.setBus(bus);
        RepairDto repairDto = modelMapper.map(repair1, RepairDto.class);

        when(repairRepository.save(any(Repair.class))).thenReturn(repair1);
        when(informationService.addInformation(any(Information.class))).thenReturn(true);

        assertEquals(repair1, busService.editRepair(repairDto));
    }

    @Test
    public void testSubtractList() {
        List<BusDto> list1 = new ArrayList<>();
        List<BusDto> list2 = new ArrayList<>();
        BusDto bus1 = BusDto.builder().name("bus").id(1L).dateOfInspection(LocalDate.of(2015, 11, 4)).build();
        BusDto bus2 = BusDto.builder().name("microbus").id(2L).dateOfInspection(LocalDate.of(2012, 10, 3)).build();
        BusDto bus3 = BusDto.builder().name("autobus").id(3L).dateOfInspection(LocalDate.of(2017, 2, 3)).build();
        list1.add(bus1);
        list2.add(bus1);
        list1.add(bus2);
        list2.add(bus2);
        list1.add(bus3);

        List<BusDto> busDtos = busService.subtractLists(list1, list2);

        assertEquals(1, busDtos.size());

        assertEquals(bus3, busDtos.get(0));
    }

    @Test
    public void testGetAddressOfPageBuses_shouldReturnTrue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("busId")).thenReturn("1");
        when(request.getParameter("action")).thenReturn("ADD");

        String str = "/addbus";

        assertEquals(str, busService.getAddressOfPageBuses(session, request));
    }

    @Test
    public void testGetAddressOfPageRepairs_shouldReturnTrue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("repairId")).thenReturn("1");
        when(request.getParameter("action")).thenReturn("ADD");

        String str = "/addrepair";

        assertEquals(str, busService.getAddressOfPageRepairs(request, session));

    }

    @Test
    public void testGetBusesByIds_shouldReturnTrue() {
        List<Bus> buses = new ArrayList<>();
        buses.add(bus);

        List<BusDto> busDtos = buses
                .stream()
                .map(m -> modelMapper.map(m, BusDto.class))
                .collect(Collectors.toList());

        when(busRepository.findByIdIn(anyListOf(Long.class))).thenReturn(buses);

        assertEquals(busDtos, busService.getBusesByIds(Collections.singletonList(1L)));
    }

    @Test
    public void testAddRepairSetBus_shouldReturnTrue() throws BusNotFoundException {
        RepairDto repair1 = RepairDto.builder().info("Rozrząd")
                .date(LocalDate.of(2017, 4, 22)).mileage(222111).build();

        BusService busService1 = Mockito.spy(busService);

        BusDto busDto = modelMapper.map(bus, BusDto.class);

        Mockito.doReturn(busDto).when(busService1).getBus(anyLong());

        Bus busRepaired = busService1.addRepairSetBus(repair1, 5L);
        assertEquals(bus, busRepaired);
    }

//    @Test
//    public void testGetBusDtosByDate_shouldReturnTrue() {
//        BusDto bus1 = BusDto.builder().name("bus").id(1L).dateOfInspection(LocalDate.of(2015, 11, 4)).build();
//        BusDto bus2 = BusDto.builder().name("microbus").id(2L).dateOfInspection(LocalDate.of(2012, 10, 3)).build();
//        BusDto bus3 = BusDto.builder().name("autobus").id(3L).dateOfInspection(LocalDate.of(2017, 2, 3)).build();
//
//        List<BusDto> buses = Arrays.asList(bus1, bus2, bus3);
//
//        TripDto tripDto = TripDto.builder().amountOfPassengers(22).startDate(LocalDate.of(2000, 12, 12))
//                .startTime(LocalTime.of(11, 21)).finishDate(LocalDate.of(2000, 12, 17))
//                .estimatedFinishTime(LocalTime.of(12, 21)).build();
//
//        BusService busService1 = Mockito.spy(busService);
//
////        when(busRepository.findAllBusesBetweenDateAndTime(any(), any())).thenReturn(new ArrayList<>());
////
////        Mockito.doReturn(new ArrayList<>()).when(busService1.getAllAvailableBuses());
////
////        Mockito.doReturn(buses).when(busService1.subtractLists(anyList(), anyList()));
//
//        Mockito.doReturn(buses).when(busService1).getListOfAvailableBusBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class));
//
//        assertEquals(buses, busService.getBusDtosByDate(tripDto));
//    }

    @Test
    public void testGetBusDtosByAmountOfSeats_shouldReturnTrue() {
        BusDto bus1 = BusDto.builder().name("bus").id(1L).dateOfInspection(LocalDate.of(2015, 11, 4)).build();
        BusDto bus2 = BusDto.builder().name("microbus").id(2L).dateOfInspection(LocalDate.of(2012, 10, 3)).build();
        BusDto bus3 = BusDto.builder().name("autobus").id(3L).dateOfInspection(LocalDate.of(2017, 2, 3)).build();

        HttpServletRequest request = mock(HttpServletRequest.class);

        List<BusDto> buses = Arrays.asList(bus1, bus2, bus3);

        when(busRepository.findAllByNumberOfSeatsIsBetween(anyInt(), anyInt()))
                .thenReturn(buses.stream().map(m -> modelMapper.map(m, Bus.class)).collect(Collectors.toList()));

        when(request.getParameter(anyString())).thenReturn("1");

        assertEquals(buses, busService.getBusDtosByAmountOfSeats(request));
    }
}
