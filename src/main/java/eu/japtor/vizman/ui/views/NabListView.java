package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.VaadinSession;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.ItemNames;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.report.NabListReportBuilder;
import eu.japtor.vizman.backend.repository.NabRepo;
import eu.japtor.vizman.backend.service.NabService;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_NAB_LIST, layout = MainView.class)
@PageTitle(PAGE_TITLE_NAB_LIST)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
        Perm.ZAK_BASIC_READ, Perm.ZAK_BASIC_MODIFY,
        Perm.ZAK_EXT_READ, Perm.ZAK_EXT_MODIFY
})
public class NabListView extends VerticalLayout {

    private List<Nab> nabList;
    private NabGrid nabGrid;
    private List<GridSortOrder<Nab>> initialSortOrder;
    private ReloadButton reloadButton;
    private Anchor expXlsAnchor;
    private ReportExporter<Nab> xlsReportExporter;

    private final static String REPORT_FILE_NAME = "vzm-rep-nab";


    @Autowired
    public NabRepo nabRepo;

    @Autowired
    public NabService nabService;


    private ComponentEventListener anchorExportListener = event -> {
        try {
            updateExpXlsAnchorResource(nabList);
        } catch (JRException e) {
            e.printStackTrace();
        }
    };

    private SerializableSupplier<List<? extends Nab>> itemsSupplier =
            () -> nabService.fetchByFiltersDescOrder(buildNabFilterParams());

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
        loadViewContent();
        // TODO: inital sort order markers
        //        nabGrid.sort(initialSortOrder);
        //        UI.getCurrent().getPage().executeJavaScript("document.querySelectorAll(\"vaadin-grid-sorter\")[1].click()");
    }

    private NabFilterParams buildNabFilterParams() {
        NabFilterParams nabFilterParams = new NabFilterParams();
        nabFilterParams.setRok(nabGrid.getRokFilterValue());
        nabFilterParams.setText(nabGrid.getTextFilterValue());
        return nabFilterParams;
    }

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
                xlsReportExporter.getStreamResource(getReportFileName(expFormat), itemsSupplier, expFormat);
        expXlsAnchor.setHref(xlsResource);

        // Varianta 1
//        UI.getCurrent().getPage().executeJs("$0.click();", expXlsAnchor.getElement());

        // Varianta 2
        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(xlsResource);
        UI.getCurrent().getPage().setLocation(registration.getResourceUri());
    }


    private Component initReloadButton() {
        return reloadButton = new ReloadButton(event -> loadViewContent());
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
        nabGrid = new NabGrid(false, null, null,true);
        nabGrid.setMultiSort(true);
        nabGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        return nabGrid;
    }

    private void loadViewContent() {
        loadGridDataAndRebuildFilterFields();
        nabGrid.setInitialFilterValues();
        nabGrid.doFilter();
        nabGrid.getDataProvider().refreshAll();
    }

    private void loadGridDataAndRebuildFilterFields() {
        nabList = nabRepo.findAllByOrderByRokDescTextAsc();
        nabGrid.setItems(nabList);
        nabGrid.setRokFilterItems(nabList.stream()
                .filter(z -> null != z.getRok())
                .map(Nab::getRok)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        nabGrid.setVzFilterItems(nabList.stream()
                .map(Nab::getVz)
                .filter(s -> null != s)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
    }


    public static class NabFilterParams {

        Integer rok;
        String text;

        public Integer getRok() {
            return rok;
        }
        public void setRok(Integer rok) {
            this.rok = rok;
        }

        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }
    }
}
