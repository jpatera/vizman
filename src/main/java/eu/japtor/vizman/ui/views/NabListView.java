package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.VaadinSession;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.dataprovider.NabFiltPagDataProvider;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.report.NabListReportBuilder;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.NabFormDialog;
import net.sf.jasperreports.engine.JRException;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_NAB_LIST, layout = MainView.class)
@PageTitle(PAGE_TITLE_NAB_LIST)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
        Perm.ZAK_EXT_READ, Perm.ZAK_EXT_MODIFY
})
public class NabListView extends VerticalLayout {

    private final static String REPORT_FILE_NAME = "vzm-rep-nab";

    private NabGrid nabGrid;
//    private List<GridSortOrder<NabView>> initialSortOrder;
    private ReportExporter<NabView> xlsReportExporter;
    private NabFormDialog nabFormDialog;

    private ReloadButton reloadButton;;
    private ResetFiltersButton resetFiltersButton;
    private NewItemButton newItemButton;
    private Anchor expXlsAnchor;

//    private DataProvider<NabView, NabViewService.NabViewFilter> gridDataProvider; // Second type  param  must not be Void
//    private NabFiltPagDataProvider gridDataProvider; // Second type  param  must not be Void
//    private FilterablePageableDataProvider<NabView, NabViewService.NabViewFilter> filtPagGridDataProvider;
    private NabFiltPagDataProvider filtPagGridDataProvider;
//    private List<NabView> nabViewList = new ArrayList<>();  // Temporary placeholder for report

    @Autowired
    public NabViewService nabViewService;

    @Autowired
    public NabService nabService;

    @Autowired
    public KontService kontService;

    @Autowired
    public KlientService klientService;

    @Autowired
    public CfgPropsCache cfgPropsCache;

    public NabListView() {
    }

    @PostConstruct
    public void postInit() {

        xlsReportExporter = new ReportExporter((new NabListReportBuilder()).buildReport());
        initView();

//        // Person provider for grid
//        // -------------------------
//        gridDataProvider = DataProvider.fromFilteringCallbacks(
//                query -> {
//                    int off = query.getOffset();
//                    int lim = query.getLimit();
//
//                    return nabViewService
//                            .fetchByNabFilter(query.getFilter().orElse(null), query.getSortOrders(), )
//                            .stream();
//
////                    int offset = query.getOffset();
////                    int limit = query.getLimit();
////                    nabViewService.fetchBySearchFilter(filter)
////                    findByExample(repository, query.getFilter())
////                            .skip(query.getOffset())
////                            .take(query.getLimit())
////                    query.getFilter().orElse(null),
//                },
//                query -> {
//                    int off = query.getOffset();
//                    int lim = query.getLimit();
//                    return (int) nabViewService
//                            .countByNabFilter(query.getFilter().orElse(null));
//                }
//        );

//        filtPagGridDataProvider = gridDataProvider.withConfigurableFilter();
        filtPagGridDataProvider = new NabFiltPagDataProvider(nabViewService);
        nabGrid.setGridDataProvider(filtPagGridDataProvider);

        nabFormDialog = new NabFormDialog (
                this::saveItem
                , this::deleteItem
                , nabViewService
                , kontService
                , klientService
                , cfgPropsCache
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
                , this::openItem
                , nabViewService
        );
        nabGrid.setMultiSort(true);
        nabGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        return nabGrid;
    }

    private Component buildGridBarComponent() {
        HorizontalLayout gridBar = new HorizontalLayout();
        gridBar.setSpacing(false);
        gridBar.setAlignItems(Alignment.END);
        gridBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        gridBar.add(
                buildTitleComponent()
                , new Ribbon()
//                , buildGridBarControlsComponent()
//                , new Ribbon()
                , buildToolBarComponent()
        );
        return gridBar;
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
                , new Ribbon()
                , initResetFiltersButton()
        );
        return titleComponent;
    }

    private Component buildToolBarComponent() {
        HorizontalLayout toolBar = new HorizontalLayout();
        toolBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        toolBar.setSpacing(false);
        toolBar.add(
                initNewItemButton()
                , new Ribbon()
                , initReportXlsExpAnchor()
        );
        return toolBar;
    }

    private ComponentEventListener anchorExportListener = event -> {
//        try {
//            updateExpXlsAnchorResource(filtPagGridDataProvider.getFilteredList());
//        } catch (JRException e) {
//            e.printStackTrace();
//        }
    };

//    public void saveItem(NabView itemToSave, Operation operation) {
//        try {
//            currentItem = nabViewService.saveNab(itemToSave, operation);
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
//    private void deleteItem(final NabView itemToDelete) {
//        OperationResult lastOperResOrig = lastOperationResult;
//        try {
//            nabViewService.deleteNab(itemToDelete);
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


    public void openItem(NabView itemFromView, Operation operation) {
        nabGrid.select(itemFromView);
        nabFormDialog.openDialog(
                false
                , nabService.fetchOne(itemFromView.getId())
                , operation
        );
    }

    public void saveItem(Nab itemToSave, Operation operation) {
        nabService.saveNab(itemToSave, operation);
    }

    private void deleteItem(final Nab itemToDelete) {
        nabService.deleteNab(itemToDelete);
    }


//    private SerializableSupplier<List<? extends NabView>> reportItemsSupplier =
////            () -> nabGrid.getDataCommunicator().Service.fetchByNabFilter(buildNabFilterParams(), nabGrid.getDataProvider().fetch()getSortOrder());
//            () -> nabViewService.fetchByNabFilter(
//                    nabGrid.buildNabFilter()
//                    , nabGrid.getDataCommunicator().getBackEndSorting());
////            () -> nabViewService.fetchByFiltersDescOrder(buildNabFilterParams());

    private SerializableSupplier<List<? extends NabView>> reportItemsSupplier =
            () -> nabViewService.fetchAll();


    void finishNabEdit(NabFormDialog nabFormDialog) {
        OperationResult operResult = nabFormDialog.getLastOperationResult();
        Nab resultItem = nabFormDialog.getCurrentItem();  // Modified, just added or just deleted
//        Nab origItem = nabFormDialog.getOrigItem();

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
                    .withMessage(String.format("Nabídka %s zrušena.", nabFormDialog.getOrigItemCopy().getCnab()))
                    .open();
        }
    }

//    private void syncGridAfterEdit(NabView itemModified, Operation oper
//            , OperationResult operRes, NabView itemOrig
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

    private Component initNewItemButton() {
        newItemButton = new NewItemButton("Nová nabíka",
                event -> nabFormDialog.openDialog(false, new Nab(ItemType.NAB), Operation.ADD)
        );
        return  newItemButton;
    }

    private Component initReloadButton() {
        reloadButton = new ReloadButton(event -> nabGrid.reloadGridData());
        return reloadButton;
    }

    private Component initResetFiltersButton() {
        resetFiltersButton = new ResetFiltersButton(event -> nabGrid.resetFilterValues());
        return resetFiltersButton;
    }

    private Anchor initReportXlsExpAnchor() {
        expXlsAnchor = new ReportExpButtonAnchor(ReportExporter.Format.XLS, anchorExportListener);
        return expXlsAnchor;
    }

    private String getReportFileName(ReportExporter.Format format) {
        return REPORT_FILE_NAME + "." + format.name().toLowerCase();
    }

    private void updateExpXlsAnchorResource(List<NabView> items) throws JRException {
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


    private void loadInitialViewContent() {
        nabGrid.rebuildFilterFields();
        nabGrid.resetFilterValues();
//        nabGrid.doFilter(NabViewService.NabViewFilter.getEmpty());
        nabGrid.reloadGridData();
//        doFilter(NabViewService.NabViewFilter.getEmpty());
//        nabGrid.getDataProvider().refreshAll();
    }


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
