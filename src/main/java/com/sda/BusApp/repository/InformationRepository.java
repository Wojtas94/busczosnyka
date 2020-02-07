package com.sda.BusApp.repository;

import com.sda.BusApp.model.entity.Information;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InformationRepository extends JpaRepository<Information, Long> {

    List<Information> findAllByActionOrderByIdDesc(String action, Pageable pageable);
    List<Information> findAllByLoginOrderByIdDesc(String login, Pageable pageable);
}
