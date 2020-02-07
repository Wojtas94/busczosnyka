package com.sda.BusApp.repository;

import com.sda.BusApp.model.entity.Bus;
import com.sda.BusApp.model.entity.Driver;
import com.sda.BusApp.model.entity.Repair;
import com.sda.BusApp.model.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    @Query(value = "select distinct b from Bus b inner join b.trips t " +
            "where t.finishDateAndTime >= :startDate and t.startDateAndTime  <= :finishDate " +
            "and b.isAvailable = com.sda.BusApp.model.IsAvailable.DOSTĘPNY")
    List<Bus> findAllBusesBetweenDateAndTime(@Param("startDate") LocalDateTime startDate, @Param("finishDate") LocalDateTime finishDate);

    List<Bus> findAllByNumberOfSeatsIsBetween(Integer from, Integer to);

    @Query(value = "select b from Bus b where b.isAvailable = com.sda.BusApp.model.IsAvailable.DOSTĘPNY")
    List<Bus> findAllAvailableBuses();

    @Query(value = "select b from Bus b where b.dateOfRemove is null ")
    List<Bus> findAllBuses();

    @Query(value = "select b from Bus b where b.dateOfInspection <= :nowPlusSevenDays and b.dateOfRemove is null")
    List<Bus> findListOfBusesWithNeededInspection(@Param("nowPlusSevenDays") LocalDate nowPlusSevenDays);

    List<Bus> findByIdIn(List<Long> ids);
}
