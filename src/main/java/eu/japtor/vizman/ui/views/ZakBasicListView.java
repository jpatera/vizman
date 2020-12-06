package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.AbstractStreamResource;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.ItemNames;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.report.ZakBasicReportBuilder;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.service.ZakBasicService;
import eu.japtor.vizman.backend.service.ZakNaklVwService;
import eu.japtor.vizman.backend.service.ZakrService;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

import static eu.japtor.vizman.app.security.SecurityUtils.isNaklBasicAccessGranted;
import static eu.japtor.vizman.app.security.SecurityUtils.isNaklCompleteAccessGranted;
import static eu.japtor.vizman.backend.utils.VzmFormatReport.RFNDF;
import static eu.japtor.vizman.ui.util.VizmanConst.*;


@Route(value = ROUTE_ZAK_LIST, layout = MainView.class)
@PageTitle(PAGE_TITLE_ZAK_LIST)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
        Perm.ZAK_BASIC_READ, Perm.ZAK_BASIC_MODIFY,
        Perm.ZAK_EXT_READ, Perm.ZAK_EXT_MODIFY
})
public class ZakBasicListView extends VerticalLayout {

    private List<ZakBasic> zakList;
    private ZakSimpleGrid zakGrid;
    private List<GridSortOrder<ZakBasic>> initialSortOrder;
    private ReloadButton reloadButton;
    private ResetFiltersButton resetFiltersButton;
    private Anchor expXlsAnchor;
    private ReportExporter<ZakBasic> reportExporter;

    private final static String REPORT_FILE_NAME = "vzm-rep-zakb";

    @Autowired
    public ZakBasicRepo zakBasicRepo;

    @Autowired
    public ZakBasicService zakBasicService;

    @Autowired
    public ZakNaklVwService zakNaklVwService;

    @Autowired
    public ZakrService zakrService;

    @Autowired
    public CfgPropsCache cfgPropsCache;


    public ZakBasicListView() {
        reportExporter = new ReportExporter();
//        initView();
    }

    @PostConstruct
    public void postInit() {
        initView();
        loadInitialViewContent();
        // TODO: inital sort order markers
        //        zakGrid.sort(initialSortOrder);
        //        UI.getCurrent().getPage().executeJavaScript("document.querySelectorAll(\"vaadin-grid-sorter\")[1].click()");
    }

    private ZakBasicFilter buildZakBasicFilter() {
        zakGrid.saveFilterFieldValues();
        ZakBasicFilter filter = ZakBasicFilter.getEmpty();
        filter.setArch(zakGrid.getArchFilterValue());
        filter.setDigi(zakGrid.getDigiFilterValue());
        filter.setTyp(zakGrid.getTypFilterValue());
        filter.setCkz(zakGrid.getCkzFilterField());
        filter.setRokZak(zakGrid.getRokFilterValue());
        filter.setSkupina(zakGrid.getSkupinaFilterValue());
        filter.setObjednatel(zakGrid.getObjednatelFilterValue());
        filter.setTextKont(zakGrid.getTextKontFilterValue());
        filter.setTextZak(zakGrid.getTextZakFilterValue());
        return filter;
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
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
                , initZakGrid()
        );
        return gridContainer;
    }

    private Component initZakGrid() {
        zakGrid = new ZakSimpleGrid(
                false
                , null
                , null
                ,true
                , true
                , isNaklBasicAccessGranted() || isNaklCompleteAccessGranted()
                , null
                , null
                , zakNaklVwService
                , zakrService
                , cfgPropsCache
        );
        zakGrid.setMultiSort(true);
        zakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        return zakGrid;
    }

    private Component buildGridBarComponent() {
        HorizontalLayout gridBar = new HorizontalLayout();
        gridBar.setSpacing(false);
        gridBar.setAlignItems(Alignment.END);
        gridBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        gridBar.add(
                initExpXlsAnchor()
                , buildTitleComponent()
                , new Ribbon()
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
                new GridTitle(ItemNames.getNomP(ItemType.ZAK))
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
                initXlsReportMenu()
        );
        return toolBar;
    }

    private String getZakListRepFileName(ReportXlsExporter.Format format) {
        return REPORT_FILE_NAME  + RFNDF.format(LocalDateTime.now()) + "." + format.name().toLowerCase();
    }

    private SerializableSupplier<List<? extends ZakBasic>> zakBasicRepAllSupplier =
            () -> {
                return zakBasicService.fetchAllDescOrder();
            };

    private SerializableSupplier<List<? extends ZakBasic>> zakBasicRepFilteredSupplier =
            () -> {
                return zakBasicService.fetchByFiltersDescOrder(buildZakBasicFilter());
            };

    private Component initExpXlsAnchor() {
        expXlsAnchor = new ExpXlsAnchor();
        return expXlsAnchor;
    }


    private Component initXlsReportMenu() {
        Button btn = new Button(new Image("img/xls_down_24b.png", ""));
        btn.getElement().setAttribute("theme", "icon secondary small");
        btn.getElement().setProperty("title", "Seznam zakázek - report");

        ContextMenu menu = new ContextMenu();
        menu.addItem("Zobrazené zakázky", e -> updateExpXlsAnchorResourceAndDownload(
                zakBasicRepFilteredSupplier
                , getZakBasicRepFilteredSubtitleText()
        ));
        menu.addItem("Všechny zakázky", e -> updateExpXlsAnchorResourceAndDownload(
                zakBasicRepAllSupplier
                , getZakBasicRepAllSubtitleText()
        ));

        menu.setOpenOnClick(true);
        menu.setTarget(btn);
        return btn;
    }

    private void updateExpXlsAnchorResourceAndDownload(
            SerializableSupplier<List<? extends ZakBasic>> itemsSupplier
            , final String subtitleText
    ) {
        final AbstractStreamResource xlsResource =
                reportExporter.getStreamResource(
                        new ZakBasicReportBuilder(
                                "SEZNAM ZAKÁZEK"
                                , subtitleText
                )
                , getZakListRepFileName(ReportXlsExporter.Format.XLS)
                , itemsSupplier
                , null
        );
        expXlsAnchor.setHref(xlsResource);

        // Varianta 1
        UI.getCurrent().getPage().executeJs("$0.click();", expXlsAnchor.getElement());
//      or:  page.executeJs("document.getElementById('" + ZAK_BASIC_REP_ID + "').click();");

        // Varianta 2 - browsers can have pop-up opening disabled
//        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(xlsResource);
//        UI.getCurrent().getPage().executeJs("window.open($0, $1)", registration.getResourceUri().toString(), "_blank");

        // Varianta 3 - It is not clear how to activate source page again after download is finished
//        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(xlsResource);
//        UI.getCurrent().getPage().setLocation(registration.getResourceUri());
    }

    public String getZakBasicRepFilteredSubtitleText() {
        ZakBasicFilter filter = buildZakBasicFilter();
        return
            "Parametry:" +
            "  Arch=" + (null == filter.getArch() ? "Vše" : filter.getArch().toString()) +
            "  ČK-ČZ=" + (null == filter.getCkz() ? "Vše" : filter.getCkz().toString()) +
            "  Rok zak.=" + (null == filter.getRokZak() ? "Vše" : filter.getRokZak().toString()) +
            "  Skupina=" + (null == filter.getSkupina() ? "Vše" : filter.getSkupina().toString()) +
            "  Text kont.=" + (null == filter.getTextKont() ? "Vše" : filter.getTextKont().toString()) +
            "  Text zak.=" + (null == filter.getTextZak() ? "Vše" : filter.getTextZak().toString()) +
            "  Objednatel=" + (null == filter.getObjednatel() ? "Vše" : filter.getObjednatel().toString())
        ;
    }

    public String getZakBasicRepAllSubtitleText() {
        return "Parametry: Filtr=vše";
    }

    private Component initReloadButton() {
        reloadButton = new ReloadButton(event -> zakGrid.reloadGridData());
        return reloadButton;
    }

    private Component initResetFiltersButton() {
        resetFiltersButton = new ResetFiltersButton(event -> zakGrid.initFilterFieldValues());
        return resetFiltersButton;
    }

    private void loadInitialViewContent() {
        zakList = zakBasicRepo.findAllByOrderByCkontDescCzakDesc();
        zakGrid.setItems(zakList);
        zakGrid.rebuildSelectableFilterFields(zakList);
        zakGrid.initFilterFieldValues();
        zakGrid.reloadGridData();
    }

    public static class ZakBasicFilter {

        String ckz;
        Integer rokZak;
        String skupina;
        Boolean arch;
        Boolean digi;
        ItemType typ;
        String objednatel;
        String textKont;
        String textZak;

        public static final ZakBasicFilter getEmpty() {
            ZakBasicFilter filter = new ZakBasicFilter();
            filter.setArch(null);
            filter.setDigi(null);
            filter.setTyp(null);
            filter.setCkz(null);
            filter.setRokZak(null);
            filter.setSkupina(null);
            filter.setObjednatel(null);
            filter.setTextKont(null);
            filter.setTextZak(null);
            return filter;
        }

        public Boolean getArch() {
            return arch;
        }
        public void setArch(Boolean arch) {
            this.arch = arch;
        }

        public Boolean getDigi() {
            return digi;
        }
        public void setDigi(Boolean digi) {
            this.digi = digi;
        }

        public ItemType getTyp() {
            return typ;
        }
        public void setTyp(ItemType typ) {
            this.typ = typ;
        }

        public String getCkz() {
            return ckz;
        }
        public void setCkz(String ckz) {
            this.ckz = ckz;
        }

        public Integer getRokZak() {
            return rokZak;
        }
        public void setRokZak(Integer rokZak) {
            this.rokZak = rokZak;
        }

        public String getSkupina() {
            return skupina;
        }
        public void setSkupina(String skupina) {
            this.skupina = skupina;
        }

        public String getObjednatel() {
            return objednatel;
        }
        public void setObjednatel(String objednatel) {
            this.objednatel = objednatel;
        }

        public String getTextKont() {
            return textKont;
        }
        public void setTextKont(String textKont) {
            this.textKont = textKont;
        }

        public String getTextZak() {
            return textZak;
        }
        public void setTextZak(String textZak) {
            this.textZak = textZak;
        }
    }
}
