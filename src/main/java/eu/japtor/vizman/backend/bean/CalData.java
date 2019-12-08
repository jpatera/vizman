package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.Caly;
import eu.japtor.vizman.backend.entity.CalyTest;
import eu.japtor.vizman.backend.entity.Calym;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CalData {

    private static final List<CalyTest> CALY_LIST = createCalyList();
//    private static final List<CalTreeNode> CALYM_LIST = createCalymList();

    private static List<CalyTest> createCalyList() {
        List<CalyTest> calyList = new ArrayList<>();

        CalyTest caly1;
        caly1 = new CalyTest(2019, BigDecimal.valueOf(1119), BigDecimal.valueOf(1111));
        caly1.setId(1001L);
        calyList.add(caly1);

        CalyTest caly2 = new CalyTest(2020, BigDecimal.valueOf(1120), BigDecimal.valueOf(2222));
        caly2.setId(1002L);
        calyList.add(caly2);

        return calyList;
    }

    private static List<CalTreeNode> createCalymList() {
        List<CalTreeNode> calymList = new ArrayList<>();

//        calymList.add(
//                new Calym(YearMonth.of(2019, 01), BigDecimal.valueOf(111), BigDecimal.valueOf(11), (CalyTest)CALY_LIST.get(0))
//        );
//
//        calymList.add(
//                new Calym(YearMonth.of(2020, 01), BigDecimal.valueOf(222), BigDecimal.valueOf(22), (CalyTest)CALY_LIST.get(1))
//        );
//        calymList.add(
//                new Calym(YearMonth.of(2020, 02), BigDecimal.valueOf(333), BigDecimal.valueOf(33), (CalyTest)CALY_LIST.get(1))
//        );

        return calymList;
    }

    public List<CalyTest> getCalys() {
        return CALY_LIST;
    }

//    public List<CalTreeNode> getCalyms() {
//        return CALYM_LIST;
//    }

}
