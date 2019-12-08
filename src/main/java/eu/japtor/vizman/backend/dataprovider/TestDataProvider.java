package eu.japtor.vizman.backend.dataprovider;

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import eu.japtor.vizman.backend.bean.TestCalService;
import eu.japtor.vizman.backend.entity.CalyTest;

import java.util.stream.Stream;

public class TestDataProvider extends
        AbstractBackEndHierarchicalDataProvider<CalyTest, Void> {
//        AbstractBackEndHierarchicalDataProvider<CalyTreeNode, Void> {

    TestCalService testService;

    public TestDataProvider(TestCalService testService) {
        this.testService = testService;
    }

    @Override
    public int getChildCount(HierarchicalQuery<CalyTest, Void> query) {
        return (int) testService.getChildCount(query.getParent());
    }

    @Override
    public boolean hasChildren(CalyTest item) {
        return testService.hasChildren(item);
    }

    @Override
    protected Stream<CalyTest> fetchChildrenFromBackEnd(
            HierarchicalQuery<CalyTest, Void> query) {
        return testService.fetchChildren(query.getParent()).stream();
    }

//    public TestDataProvider(TestCalService testService) {
//        this.testService = testService;
//    }
//
//    @Override
//    public int getChildCount(HierarchicalQuery<CalTreeNode, Void> query) {
//        return (int) testService.getChildCount(query.getParent());
//    }
//
//    @Override
//    public boolean hasChildren(CalTreeNode item) {
//        return testService.hasChildren(item);
//    }
//
//    @Override
//    protected Stream<CalTreeNode> fetchChildrenFromBackEnd(
//            HierarchicalQuery<CalTreeNode, Void> query) {
//        return testService.fetchChildren(query.getParent()).stream();
//    }

}
