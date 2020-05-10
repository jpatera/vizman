package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.VaadinSession;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.dataprovider.NabFiltPagDataProvider;
import eu.japtor.vizman.backend.dataprovider.spring.FilterablePageableDataProvider;
import eu.japtor.vizman.backend.entity.ItemNames;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.report.NabListReportBuilder;
import eu.japtor.vizman.backend.service.NabService;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.NabFormDialog;
import net.sf.jasperreports.engine.JRException;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_NAB_LIST, layout = MainView.class)
@PageTitle(PAGE_TITLE_NAB_LIST)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
        Perm.ZAK_BASIC_READ, Perm.ZAK_BASIC_MODIFY,
        Perm.ZAK_EXT_READ, Perm.ZAK_EXT_MODIFY
})
public class NabListView extends VerticalLayout {

    private final static String REPORT_FILE_NAME = "vzm-rep-nab";

    private List<Nab> nabList;
    private NabGrid nabGrid;
    private List<GridSortOrder<Nab>> initialSortOrder;
    private ReloadButton reloadButton;
    private Anchor expXlsAnchor;
    private ReportExporter<Nab> xlsReportExporter;

    private NabFormDialog nabFormDialog;
    private NewItemButton newItemButton;

    private DataProvider<Nab, NabService.NabFilter> gridDataProvider; // Second type  param  must not be Void
//    private ConfigurableFilterDataProvider<Nab, Void, NabService.NabFilter> filtPagGridDataProvider;
    private FilterablePageableDataProvider<Nab, NabService.NabFilter> filtPagGridDataProvider;

    @Autowired
    public NabService nabService;

    public NabListView() {
        xlsReportExporter = new ReportExporter((new NabListReportBuilder()).buildReport());
        initView();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
//        this.setAlignItems(Alignment.STRETCH);
//        setHeight("90%");
//        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        this.setPadding(false);
        this.setMargin(false);
        this.add(
                buildGridContainer()
        );
    }

    @PostConstruct
    public void postInit() {

//        // Person provider for grid
//        // -------------------------
//        gridDataProvider = DataProvider.fromFilteringCallbacks(
//                query -> {
//                    int off = query.getOffset();
//                    int lim = query.getLimit();
//
//                    return nabService
//                            .fetchByNabFilter(query.getFilter().orElse(null), query.getSortOrders(), )
//                            .stream();
//
////                    int offset = query.getOffset();
////                    int limit = query.getLimit();
////                    nabService.fetchBySearchFilter(filter)
////                    findByExample(repository, query.getFilter())
////                            .skip(query.getOffset())
////                            .take(query.getLimit())
////                    query.getFilter().orElse(null),
//                },
//                query -> {
//                    int off = query.getOffset();
//                    int lim = query.getLimit();
//                    return (int) nabService
//                            .countByNabFilter(query.getFilter().orElse(null));
//                }
//        );
//        filtPagGridDataProvider = gridDataProvider.withConfigurableFilter();
        filtPagGridDataProvider = new NabFiltPagDataProvider(nabService);
        nabGrid.setGridDataProvider(filtPagGridDataProvider);

        nabFormDialog = new NabFormDialog (
                this::saveItem
                , this::deleteItem
                , nabService
        );
        nabFormDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                finishNabEdit((NabFormDialog) event.getSource());
            }
        });

        loadInitialViewContent();
        // TODO: inital sort order markers
        //        nabGrid.sort(initialSortOrder);
        //        UI.getCurrent().getPage().executeJavaScript("document.querySelectorAll(\"vaadin-grid-sorter\")[1].click()");
    }

    private ComponentEventListener anchorExportListener = event -> {
        try {
            updateExpXlsAnchorResource(nabList);
        } catch (JRException e) {
            e.printStackTrace();
        }
    };

//    public void saveItem(Nab itemToSave, Operation operation) {
//        try {
//            currentItem = nabService.saveNab(itemToSave, operation);
//            lastOperationResult = OperationResult.ITEM_SAVED;
////            Notification.show(
////                    "Nabídka uložena", 2000, Notification.Position.MIDDLE);
////            return currentItem;
//        } catch(VzmServiceException e) {
//            lastOperationResult = OperationResult.NO_CHANGE;
//            throw(e);
//        }
//    }
//
//    private void deleteItem(final Nab itemToDelete) {
//        OperationResult lastOperResOrig = lastOperationResult;
//        try {
//            nabService.deleteNab(itemToDelete);
//            lastOperationResult = OperationResult.ITEM_DELETED;
////            return true;
//        } catch (VzmServiceException e) {
//            this.lastOperationResult = lastOperResOrig;
//            throw(e);
////            ConfirmDialog
////                    .createWarning()
////                    .withCaption("Zrušení nabidky.")
////                    .withMessage(String.format("Nabídku se nepodařilo zrušit."))
////                    .open()
////            ;
////            return false;
//        }
//    }


    public void saveItem(Nab itemToSave, Operation operation) {
        nabService.saveNab(itemToSave, operation);
    }

    private void deleteItem(final Nab itemToDelete) {
        nabService.deleteNab(itemToDelete);
    }


//    private SerializableSupplier<List<? extends Nab>> reportItemsSupplier =
////            () -> nabGrid.getDataCommunicator().Service.fetchByNabFilter(buildNabFilterParams(), nabGrid.getDataProvider().fetch()getSortOrder());
//            () -> nabService.fetchByNabFilter(
//                    nabGrid.buildNabFilter()
//                    , nabGrid.getDataCommunicator().getBackEndSorting());
////            () -> nabService.fetchByFiltersDescOrder(buildNabFilterParams());

    private SerializableSupplier<List<? extends Nab>> reportItemsSupplier =
            () -> nabService.fetchAll();


    void finishNabEdit(NabFormDialog nabFormDialog) {
        OperationResult operResult = nabFormDialog.getLastOperationResult();
        Nab resultItem = nabFormDialog.getCurrentItem();  // Modified, just added or just deleted
        Nab origItem = nabFormDialog.getOrigItem();

//        syncGridAfterEdit(
//                resultItem
//                , nabFormDialog.getCurrentOperation()
//                , operResult
//                , origItem
//        );

        syncGridAfterEdit(nabFormDialog.getCurrentOperation());

        if (OperationResult.ITEM_SAVED == operResult) {
            Notification.show(String.format("Nabídka %s uložena", resultItem.getCnab())
                    , 2500, Notification.Position.TOP_CENTER);

        } else if (OperationResult.ITEM_DELETED == operResult) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Editace nabídky")
                    .withMessage(String.format("Nabídka %s zrušena.", origItem.getCnab()))
                    .open();
        }
    }

//    private void syncGridAfterEdit(Nab itemModified, Operation oper
//            , OperationResult operRes, Nab itemOrig
//    ) {
////        nabGrid.getDataCommunicator().getKeyMapper().removeAll();
////        nabGrid.getDataProvider().refreshAll();
//        nabGrid.doFilter();
//    }

    private void syncGridAfterEdit(Operation operation) {
        if (Operation.ADD == operation) {
            nabGrid.doFilter();
        } else {
            nabGrid.doFilter();
        }
    }

//    private NabFilterParams buildNabFilterParams() {
//        NabFilterParams nabFilterParams = new NabFilterParams();
//        nabFilterParams.setRok(nabGrid.getRokFilterValue());
//        nabFilterParams.setText(nabGrid.getTextFilterValue());
//        return nabFilterParams;
//    }


    private Component buildGridBarComponent() {
        HorizontalLayout gridToolBar = new HorizontalLayout();
        gridToolBar.setSpacing(false);
        gridToolBar.setAlignItems(Alignment.END);
        gridToolBar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        expXlsAnchor = initReportXlsExpAnchor();
        gridToolBar.add(
                buildTitleComponent()
                , new Ribbon()
//                , buildGridBarControlsComponent()
                , new Ribbon()
                , expXlsAnchor
        );
        return gridToolBar;
    }

    private Component buildTitleComponent() {
        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(Alignment.CENTER);
        titleComponent.setJustifyContentMode(JustifyContentMode.START);
        titleComponent.add(
                new GridTitle(ItemNames.getNomP(ItemType.NAB))
                , new Ribbon()
                , initReloadButton()
        );
        return titleComponent;
    }

    private Anchor initReportXlsExpAnchor() {
        expXlsAnchor = new ReportExpButtonAnchor(ReportExporter.Format.XLS, anchorExportListener);
        return expXlsAnchor;
    }

    private String getReportFileName(ReportExporter.Format format) {
        return REPORT_FILE_NAME + "." + format.name().toLowerCase();
    }

    private void updateExpXlsAnchorResource(List<Nab> items) throws JRException {
        ReportExporter.Format expFormat = ReportExporter.Format.XLS;
        AbstractStreamResource xlsResource =
                xlsReportExporter.getStreamResource(getReportFileName(expFormat), reportItemsSupplier, expFormat);
        expXlsAnchor.setHref(xlsResource);

        // Varianta 1
//        UI.getCurrent().getPage().executeJs("$0.click();", expXlsAnchor.getElement());

        // Varianta 2
        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(xlsResource);
        UI.getCurrent().getPage().setLocation(registration.getResourceUri());
    }


    private Component initReloadButton() {
        return reloadButton = new ReloadButton(event -> loadInitialViewContent());
    }

    private Component buildGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(
                buildGridBarComponent()
                , initNabGrid()
        );
        return gridContainer;
    }


    private Component initNabGrid() {
        nabGrid = new NabGrid(
                false
                , null
                , null
                ,true
                , (nab, operation) -> nabFormDialog.openDialog(
                        nab, operation, "aaaaa", "bbbbb")
        );
        nabGrid.setMultiSort(true);
        nabGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        return nabGrid;
    }

    private void loadInitialViewContent() {
//        loadGridDataAndRebuildFilterFields();
        nabGrid.setVzFilterItems(Arrays.asList(Boolean.FALSE, Boolean.TRUE));
        nabGrid.setInitialFilterValues();
        nabGrid.doFilter(NabService.NabFilter.getEmpty());
//        nabGrid.getDataProvider().refreshAll();
    }

//    private void loadGridDataAndRebuildFilterFields() {
//        nabList = nabService.fetchByFiltersDescOrder(buildNabFilterParams());
//        nabGrid.setItems(nabList);
//        nabGrid.setRokFilterItems(nabList.stream()
//                .filter(z -> null != z.getRok())
//                .map(Nab::getRok)
//                .distinct().collect(Collectors.toCollection(LinkedList::new))
//        );
//        nabGrid.setVzFilterItems(nabList.stream()
//                .map(Nab::getVz)
//                .filter(s -> null != s)
//                .distinct().collect(Collectors.toCollection(LinkedList::new))
//        );
//    }


//    public static class NabFilterParams {
//
//        Integer rok;
//        String text;
//
//        public Integer getRok() {
//            return rok;
//        }
//        public void setRok(Integer rok) {
//            this.rok = rok;
//        }
//
//        public String getText() {
//            return text;
//        }
//        public void setText(String text) {
//            this.text = text;
//        }
//    }
}
