package com.sda.BusApp.service;

import com.sda.BusApp.model.dto.InformationDto;
import com.sda.BusApp.model.entity.Information;
import com.sda.BusApp.repository.InformationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InformationService {

    @Autowired
    private InformationRepository informationRepository;

    private ModelMapper modelMapper;

    public InformationService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public boolean addInformation(Information information) {
        informationRepository.save(information);
        return true;
    }

    public void deleteInformation(Information information) {
        informationRepository.delete(information);
    }

    public List<Information> getAllInformations() {
        return informationRepository.findAll();
    }

    public List<InformationDto> getNewestThreeInformation() {
        Page<Information> pageOfNewestThreeChanges = informationRepository
                .findAll(PageRequest.of(0, 3,  Sort.by(Sort.Direction.DESC, "id")));
        List<Information> listOfNewestThreeChanges = pageOfNewestThreeChanges.getContent();
        return listOfNewestThreeChanges
                .stream()
                .map(m -> modelMapper.map(m, InformationDto.class))
                .collect(Collectors.toList());
    }

    public List<InformationDto> getLastTenChangesByUser(String login) {
        List<Information> listOfLastTenChangesByUser = informationRepository
                .findAllByLoginOrderByIdDesc(login, PageRequest.of(0, 10));
        return listOfLastTenChangesByUser
                .stream()
                .map(m -> modelMapper.map(m, InformationDto.class))
                .collect(Collectors.toList());
    }

    public List<InformationDto> getLastTenChangesByAction(String action) {
        List<Information> listOfLastTenChangesByUser = informationRepository
                .findAllByActionOrderByIdDesc(action, PageRequest.of(0, 10));
        return listOfLastTenChangesByUser
                .stream()
                .map(m -> modelMapper.map(m, InformationDto.class))
                .collect(Collectors.toList());
    }

    public Information createInformation(String action, String moreInfo) {
        return Information
                .builder()
                .action(action)
                .dateOfChange(LocalDateTime.now())
                .login(SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName())
                .moreInfo(moreInfo)
                .build();
    }
}
