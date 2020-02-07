package com.sda.BusApp.repository;

import com.sda.BusApp.model.dto.TripDto;
import com.sda.BusApp.model.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {



    @Query(value = "select t from Trip t where t.finishDateAndTime >= :startDate and t.startDateAndTime  <= :finishDate")
    List<Trip> findAllTripsByDateAndTime(@Param("startDate") LocalDateTime startDate,
                                         @Param("finishDate") LocalDateTime finishDate);

    List<Trip> findTripsByNameOrderByIdDesc(@Param("name") String name, Pageable pageable);

    @Query(value = "select t from Trip t where t.startDateAndTime between current_date and :nowPlus7Days")
    List<Trip> findTripsWhichStartInNext7Days(@Param("nowPlus7Days") LocalDateTime nowPlus7Days);
}
