package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.CalyHol;
import eu.japtor.vizman.backend.repository.CalyHolRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
public class CalyHolServiceImpl implements CalyHolService{

    @Autowired
    CalyHolRepo calyHolRepo;


    public CalyHol fetchCalyHol(LocalDate holDate) {
        if (null == holDate) {
            return null;
        }
        return calyHolRepo.findByHolDate(holDate);
    };

    public boolean calyHolÈxist(LocalDate holDate) {
        return null != fetchCalyHol(holDate);
    };

}
