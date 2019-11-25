package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.Caly;
import eu.japtor.vizman.backend.entity.CalyHol;
import eu.japtor.vizman.backend.entity.Calym;
import eu.japtor.vizman.backend.repository.CalyHolRepo;
import eu.japtor.vizman.backend.repository.CalyRepo;
import eu.japtor.vizman.backend.repository.CalymRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Component
public class CalServiceImpl implements CalService {

    @Autowired
    CalyRepo calyRepo;

    @Autowired
    CalymRepo calymRepo;

    @Autowired
    CalyHolRepo calyHolRepo;


    // Calendar - years
    // ----------------

    @Override
    public List<Caly> fetchAllCalys() {
        return calyRepo.findAll();
    }


    // Calendar - YMs
    // --------------
    @Override
    public List<Calym> fetchAllCalyms() {
        return calymRepo.findAll();
    }

    @Override
    public List<CalTreeNode> fetchCalymsByYear(Integer yr) {
        return (List<CalTreeNode>)(List<?>) calymRepo.findByYr(yr);
    }


    // Calendar - holiday
    // ------------------
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
