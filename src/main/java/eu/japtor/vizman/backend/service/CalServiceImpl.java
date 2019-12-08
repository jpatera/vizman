package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.SortDirection;
import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.Caly;
import eu.japtor.vizman.backend.entity.CalyHol;
import eu.japtor.vizman.backend.entity.Calym;
import eu.japtor.vizman.backend.repository.CalyHolRepo;
import eu.japtor.vizman.backend.repository.CalyRepo;
import eu.japtor.vizman.backend.repository.CalymRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

//    @Override
//    public List<CalTreeNode> fetchAllCalRootNodes() {
//        Sort sort = Sort.by(
//                Sort.Order.desc("yr")
////                , Sort.Order.desc("ym")
////                Sort.Direction.DESC
////                , "yr"
////                qSortDirection == SortDirection.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC
////                , qSortProp
//        );
//        Pageable pageable =  PageRequest.of(0, 1000, sort);
//
//
////        List<? extends  Object> list = calyRepo.findAll(Sort.by("yr").descending());
//        Page<? extends  Object> page = calyRepo.findAll((Pageable)null);
//        return (List<CalTreeNode>)page.getContent();
////        List<? extends  Object> list = calyRepo.findAll(pageable);
////        return (List<CalTreeNode>)list;
//    }

    @Override
    public List<Caly> fetchAllCalys(CalTreeNode probe, Pageable pageable) {
//        return calyRepo.findAll(Sort.by("yr").descending());

//        Sort sort = Sort.by(
//                Sort.Order.desc("yr")
////                , Sort.Order.desc("ym")
////                Sort.Direction.DESC
////                , "yr"
////                qSortDirection == SortDirection.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC
////                , qSortProp
//        );
//        Pageable pageable =  PageRequest.of(0, 8, sort);

        Page<Caly> page;
        if (null == probe) {
            page = calyRepo.findAll(pageable);
        } else  {
            page = calyRepo.findAll(Example.of((Caly)probe), pageable);
        }

//        Page<Caly> page = calyRepo.findAll(pageable);
        return page.getContent();
    }

    @Override
    public long countAllCalys(CalTreeNode probe, Pageable pageable) {
//        Sort sort = Sort.by(
//                Sort.Order.desc("yr")
////                , Sort.Order.desc("ym")
////                Sort.Direction.DESC
////                , "yr"
////                qSortDirection == SortDirection.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC
////                , qSortProp
//        );
//        Pageable pageable =  PageRequest.of(0, 8, sort);

        Page<Caly> page;
        if (null == probe) {
            page = calyRepo.findAll(pageable);
        } else {
            page = calyRepo.findAll(Example.of((Caly)probe), pageable);
        }
        return page.getContent().size();
//        return calyRepo.count(pageable);
    }

    // Calendar - YMs
    // --------------
    @Override
    public List<Calym> fetchAllCalyms() {
        return calymRepo.findAll();
    }

    @Override
    public List<CalTreeNode> fetchCalymNodesByYear(Integer yr) {
        List<? extends Object> list = calymRepo.findByYr(yr);
        return (List<CalTreeNode>)list;
    }

    @Override
    public List<Calym> fetchCalymsByYear(Integer yr) {
        List<Calym> list = calymRepo.findByYr(yr);
        return list;
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
