package com.sda.BusApp.controller;

import com.sda.BusApp.exception.BusNotFoundException;
import com.sda.BusApp.model.dto.BusDto;
import com.sda.BusApp.model.dto.RepairDto;
import com.sda.BusApp.model.entity.Repair;
import com.sda.BusApp.service.BusService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class BusController {

    private static final Logger LOGGER = Logger.getLogger(BusController.class);

    @Autowired
    private BusService busService;

    @RequestMapping("/buses")
    public ModelAndView busView() {
        LOGGER.info("user: " + Authentication.class.getName() + ", Bus view - get");
        return new ModelAndView("buses", "allbuses", busService.getAllBuses());
    }

    @PostMapping("/buses")
    public RedirectView postBuses(HttpSession session, HttpServletRequest req) {
        String addressOfPage = busService.getAddressOfPageBuses(session, req);
        LOGGER.info("user: " + Authentication.class.getName() + ", Bus view - post, redirect to: " + addressOfPage);
        return new RedirectView(addressOfPage);

    }

    @GetMapping("/emptybuses")
    public ModelAndView emptyBusGet() {
        LOGGER.info("user: " + Authentication.class.getName() + ", EmptyBuses - get");
        return new ModelAndView("emptybuses");
    }

    @PostMapping("/emptybuses")
    public RedirectView emptyBusPost() {
        LOGGER.info("user: " + Authentication.class.getName() + "EmptyBuses - post, redirect to /addbus");
        return new RedirectView("addbus");
    }

    @RequestMapping("/addbus")
    public ModelAndView createNewBus() {
        LOGGER.info("user: " + Authentication.class.getName() + ", AddBus - get");
        return new ModelAndView("addbus", "busToAdd", new BusDto());
    }

    @PostMapping("/addbus")
    public RedirectView addNewBus(@ModelAttribute BusDto bus) {
        busService.addBus(bus);
        LOGGER.info("user: " + Authentication.class.getName() + "AddBus - post, added new bus: " + bus.getName() + ", redirect to /busaddsuccess");
        return new RedirectView("busaddsuccess");
    }

    @GetMapping("/busaddsuccess")
    public ModelAndView busAddSuccess() {
        LOGGER.info("user: " + Authentication.class.getName() + ", BusAddSuccess - get");
        return new ModelAndView("busaddsuccess");
    }

    @RequestMapping("/deletebus")
    public ModelAndView deleteBus(HttpSession session) throws BusNotFoundException {
        Long id = (Long) session.getAttribute("id");
        BusDto busToRemove = busService.getBus(id);
        LOGGER.info("user: " + Authentication.class.getName() + ", DeleteBus - get, bus to delete: " + busToRemove.getName());
        return new ModelAndView("deletebus", "busToRemove", busToRemove);
    }

    @PostMapping("/deletebus")
    public RedirectView removeBus(HttpSession session) throws BusNotFoundException {
        Long id = (Long) session.getAttribute("id");
        BusDto busToRemove = busService.getBus(id);
        busService.remove(busToRemove);
        if (busService.getAllBuses().isEmpty()) {
            return new RedirectView("/emptybuses");
        }
        LOGGER.info("user: " + Authentication.class.getName() + "DeleteBus - post, id deleted bus: " + busToRemove.getName() + ", redirect to /buses");
        return new RedirectView("/buses");
    }

    @GetMapping("/editbus")
    public ModelAndView editBus(HttpSession session) throws BusNotFoundException {
        Long id = (Long) session.getAttribute("id");
        BusDto busToEdit = busService.getBus(id);
        LOGGER.info("user: " + Authentication.class.getName() + ", EditBus - get, bus to edit: " + busToEdit.getName());
        return new ModelAndView("editbus", "busToEdit", busToEdit);
    }

    @PostMapping("/editbus")
    public RedirectView editedBus(@ModelAttribute BusDto bus, HttpSession session) {
        Long id = (Long) session.getAttribute("id");
        bus.setId(id);
        busService.editBus(bus);
        LOGGER.info("user: " + Authentication.class.getName() + "EditBus - post, edited bus: " + bus.getName() + ", redirect to /buses");
        return new RedirectView("buses");
    }

    @GetMapping("/repairs")
    public ModelAndView repairsGet(HttpSession session) throws BusNotFoundException {
        Long id = (Long) session.getAttribute("id");
        if (busService.getRepairs(id).isEmpty()) {
            return new ModelAndView("emptyrepairs");
        }
        LOGGER.info("user: " + Authentication.class.getName() + ", Repairs - get, bus name:  " + busService.getBus(id).getName());
        return new ModelAndView("repairs", "allRepairs", busService.getRepairs(id));
    }

    @PostMapping("/repairs")
    public RedirectView repairsPost(HttpServletRequest req, HttpSession session) {
        String addressOfPage = busService.getAddressOfPageRepairs(req, session);
        LOGGER.info("user: " + Authentication.class.getName() + ", Repairs - post, redirect to: " + addressOfPage);
        return new RedirectView(addressOfPage);
    }

    @GetMapping("/emptyrepairs")
    public ModelAndView emptyRepairsGet(HttpSession session) throws BusNotFoundException {
        LOGGER.info("user: " + Authentication.class.getName() + ", EmptyRepairs - get, bus name:  " + busService.getBus((Long) session.getAttribute("id")).getName());
        return new ModelAndView("emptyrepairs");
    }

    @PostMapping("/emptyrepairs")
    public RedirectView emptyRepairsPost() {
        LOGGER.info("user: " + Authentication.class.getName() + ", Repairs - post, redirect to : /addrepair");
        return new RedirectView("addrepair");
    }

    @GetMapping("/addrepair")
    public ModelAndView addRepairGet(HttpSession session) throws BusNotFoundException {
        LOGGER.info("user: " + Authentication.class.getName() + ", AddRepair - get, bus name:  " + busService.getBus((Long) session.getAttribute("id")).getName());
        return new ModelAndView("addrepair", "repairToAdd", new Repair());
    }

    @PostMapping("/addrepair")
    public RedirectView addRepairPost(@ModelAttribute RepairDto repair, HttpSession session) throws BusNotFoundException {
        Long id = (Long) session.getAttribute("id");
        busService.addRepairSetBus(repair, id);
        busService.addRepair(repair);
        LOGGER.info("user: " + Authentication.class.getName() + ", AddRepair - post, bus name:  " + busService.getBus((Long) session.getAttribute("id")).getName() + ", redirect to : /addrepair");
        return new RedirectView("addrepairsuccess");
    }

    @GetMapping("addrepairsuccess")
    public ModelAndView addRepairSuccess(HttpSession session) throws BusNotFoundException {
        LOGGER.info("user: " + Authentication.class.getName() + ", AddRepairSuccess - get, bus name:  " + busService.getBus((Long) session.getAttribute("id")).getName());
        return new ModelAndView("addrepairsuccess");
    }

    @GetMapping("/deleterepair")
    public ModelAndView deleteRepairGet(HttpSession session) throws BusNotFoundException {
        Long id = (Long) session.getAttribute("repairId");
        RepairDto repairToDelete = busService.getRepair(id);
        LOGGER.info("user: " + Authentication.class.getName() + ", DeleteRepair - get, repair: " + repairToDelete.getInfo() + ", bus name: " + busService.getBus((Long) session.getAttribute("id")).getName());
        return new ModelAndView("deleterepair", "repairToDelete", repairToDelete);
    }

    @PostMapping("/deleterepair")
    public RedirectView deleteRepairPost(HttpSession session) throws BusNotFoundException {
        Long id = (Long) session.getAttribute("repairId");
        RepairDto repairToDelete = busService.getRepair(id);
        busService.removeRepair(repairToDelete);
        LOGGER.info("user: " + Authentication.class.getName() + ", DeleteRepair - post, repair: " + repairToDelete.getInfo() + ", bus name: " + busService.getBus((Long) session.getAttribute("id")).getName());
        return new RedirectView("repairs");
    }

    @GetMapping("/editrepair")
    public ModelAndView editRepairGet(HttpSession session) throws BusNotFoundException {
        Long id = (Long) session.getAttribute("repairId");
        RepairDto repairToEdit = busService.getRepair(id);
        LOGGER.info("user: " + Authentication.class.getName() + ", EditRepair - get, repair: " + repairToEdit.getInfo() + ", bus name: " + busService.getBus((Long) session.getAttribute("id")).getName());
        return new ModelAndView("editrepair", "repairToEdit", repairToEdit);
    }

    @PostMapping("/editrepair")
    public RedirectView editRepairPost(@ModelAttribute RepairDto repair, HttpSession session) throws BusNotFoundException {
        Long id = (Long) session.getAttribute("id");
        Long idRepair = (Long) session.getAttribute("repairId");
        busService.addRepairSetBus(repair, id);
        repair.setId(idRepair);
        busService.editRepair(repair);
        LOGGER.info("user: " + Authentication.class.getName() + ", EditRepair - get, repair: " + repair.getInfo() + ", bus name: " + busService.getBus((Long) session.getAttribute("id")).getName());
        return new RedirectView("repairs");
    }
}
