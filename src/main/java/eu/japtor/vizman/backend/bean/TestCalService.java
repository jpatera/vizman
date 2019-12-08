package eu.japtor.vizman.backend.bean;

//import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.CalyTest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestCalService {

    private CalData calData = new CalData();
//    private List<CalTreeNode> calyList = calData.getCalys();
    private List<CalyTest> calyList = calData.getCalys();
//    private List<CalTreeNode> calymList = calData.getCalyms();

    public long getChildCount(CalyTest parent) {
//        return null == parent ? 2 : 0;
//        return calList.size();

        return (int)calyList.stream()
                .filter(cal -> Objects.equals(parent, cal.getParent()))
                .count();
    }

    public Boolean hasChildren(CalyTest parent) {

//        return null == parent;
        return calyList.stream()
                .anyMatch(cal -> Objects.equals(parent, cal.getParent()));
    }

//    public List<CalTreeNode> fetchChildren(CalTreeNode parent) {
//
////        return calList;
//        List<CalTreeNode> lst = calyList.stream()
//                .filter(cal -> Objects.equals(parent, cal.getParent())).collect(Collectors.toList());
//        return lst;
//    }

    public List<CalyTest> fetchChildren(CalyTest parent) {

//        return calList;
        List<CalyTest> lst = calyList.stream()
                .filter(cal -> Objects.equals(parent, cal.getParent())).collect(Collectors.toList());
        return lst;
    }
}
