package com.sda.BusApp.repository;

import com.sda.BusApp.model.entity.Bus;
import com.sda.BusApp.model.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {


    @Query(value = "select distinct d from Driver d inner join d.trips t " +
            "where t.finishDateAndTime >= :startDate and t.startDateAndTime  <= :finishDate " +
            "and d.isAvailable = com.sda.BusApp.model.IsAvailable.DOSTĘPNY")
    List<Driver> findAllDriversByDateAndTime(@Param("startDate") LocalDateTime startDate, @Param("finishDate") LocalDateTime finishDate);

    @Query(value = "select d from Driver d where d.isAvailable = com.sda.BusApp.model.IsAvailable.DOSTĘPNY")
    List<Driver> findAllAvailableDrivers();

    @Query(value = "select d from Driver d where d.dateOfRemove = null")
    List<Driver> findAllDrivers();

    @Query(value = "select d from Driver d where d.dateOfExamination <= :nowPlus45Days and d.dateOfRemove = null")
    List<Driver> findListOfDriversWhoNeedExaminationIn45Days(@Param("nowPlus45Days") LocalDate nowPlus45Days);

    List<Driver> findByIdIn(List<Long> ids);
}
