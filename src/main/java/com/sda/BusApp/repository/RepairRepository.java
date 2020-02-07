package com.sda.BusApp.repository;

import com.sda.BusApp.model.entity.Bus;
import com.sda.BusApp.model.entity.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {

    @Query(value = "select r from Repair r where r.bus = :bus")
    List<Repair> findAllRepairsByBus(@Param("bus") Bus bus);

}
