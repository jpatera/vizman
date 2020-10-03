/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.AbstractStreamResource;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.ItemNames;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.report.ZakListReportBuilder;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.service.ZakBasicService;
import eu.japtor.vizman.backend.service.ZaknService;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static eu.japtor.vizman.app.security.SecurityUtils.isNaklBasicAccessGranted;
import static eu.japtor.vizman.app.security.SecurityUtils.isNaklCompleteAccessGranted;
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
    private Anchor downloadAnchor;
    private ReportExporter<ZakBasic> xlsReportExporter;

    private final static String REPORT_FILE_NAME = "vzm-rep-zakb";
    private final static String ZAKB_DOWN_ANCHOR_ID = "zakb-down-anchor-id";

    @Autowired
    public ZakBasicRepo zakBasicRepo;

    @Autowired
    public ZakBasicService zakBasicService;

    @Autowired
    public ZaknService zaknService;

    @Autowired
    public CfgPropsCache cfgPropsCache;


    public ZakBasicListView() {
        xlsReportExporter = new ReportExporter();
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

    private ZakBasicFilter buildZakBasicFilterParams() {
        ZakBasicFilter zakBasicFilter = ZakBasicFilter.getEmpty();
        zakBasicFilter.setArch(zakGrid.getArchFilterValue());
        zakBasicFilter.setDigi(zakGrid.getDigiFilterValue());
        zakBasicFilter.setTyp(zakGrid.getTypFilterValue());
        zakBasicFilter.setCkz(zakGrid.getCkzFilterField());
        zakBasicFilter.setRokZak(zakGrid.getRokFilterValue());
        zakBasicFilter.setSkupina(zakGrid.getSkupinaFilterValue());
        return zakBasicFilter;
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
                , zaknService
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
                initDownloadAnchor()
                , buildTitleComponent()
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

    private String getReportFileName(ReportExporter.Format format) {
        return REPORT_FILE_NAME + "." + format.name().toLowerCase();
    }

    private SerializableSupplier<List<? extends ZakBasic>> zakBasiciListReportSupplier =
            () -> zakBasicService.fetchAndCalcByFiltersDescOrder(buildZakBasicFilterParams());

    private Component initDownloadAnchor() {
//        downloadAnchor = new ReportExpButtonAnchor(ReportExporter.Format.XLS, anchorExportListener);
        downloadAnchor = new Anchor();
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.setId(ZAKB_DOWN_ANCHOR_ID);
        downloadAnchor.setText("Invisible ZAK-BASIC download link");    // setVisible  also disables a server part - cannot be useed
        downloadAnchor.getStyle().set("display", "none");
        return downloadAnchor;
    }

//    private ComponentEventListener anchorExportListener = event -> {
////        try {
//            updateXlsRepResourceAndDownload(zakBasiciListReportSupplier);
////        } catch (JRException e) {
////            e.printStackTrace();
////        }
//    };

    private Component initXlsReportMenu() {
        Button menuBtn = new Button(new Image("img/xls_down_24b.png", ""));
        menuBtn.getElement().setAttribute("theme", "icon secondary small");

        ContextMenu menu = new ContextMenu();
        menu.addItem("Seznam zakázek", e -> updateXlsRepResourceAndDownload(zakBasiciListReportSupplier));
        menu.setOpenOnClick(true);

        menu.setTarget(menuBtn);
        return menuBtn;
    }

//    private void updateXlsRepResourceAndDownload(SerializableSupplier<List<? extends ZakBasic>> supplier) throws JRException {
    private void updateXlsRepResourceAndDownload(SerializableSupplier<List<? extends ZakBasic>> itemsSupplier) {
        ReportExporter.Format expFormat = ReportExporter.Format.XLS;
        final AbstractStreamResource xlsResource =
                xlsReportExporter.getStreamResource(
                        new ZakListReportBuilder(), getReportFileName(expFormat), itemsSupplier, expFormat, null
                );

        // Varianta 1
        downloadAnchor.setHref(xlsResource);
        Page page = UI.getCurrent().getPage();
        page.executeJs("$0.click();", downloadAnchor.getElement());
//      or:  page.executeJs("document.getElementById('" + ZAK_BASIC_REP_ID + "').click();");

        // Varianta 2 - browsers can have pop-up opening disabled
//        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(xlsResource);
//        Page page = UI.getCurrent().getPage();
//        page.executeJs("window.open($0, $1)", registration.getResourceUri().toString(), "_blank");

        // Varianta 3 - It is not clear how to activate source page again after download is finished
//        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(xlsResource);
//        Page page = UI.getCurrent().getPage();
//        page.setLocation(registration.getResourceUri());
    }


    private Component initReloadButton() {
        reloadButton = new ReloadButton(event -> zakGrid.reloadGridData());
        return reloadButton;
    }

    private Component initResetFiltersButton() {
        resetFiltersButton = new ResetFiltersButton(event -> zakGrid.resetFilterValues());
        return resetFiltersButton;
    }

    private void loadInitialViewContent() {
        zakList = zakBasicRepo.findAllByOrderByCkontDescCzakDesc();
        zakGrid.setItems(zakList);
        zakGrid.rebuildFilterFields(zakList);
        zakGrid.resetFilterValues();
        zakGrid.reloadGridData();

//        zakGrid.doFilter();
////        nabGrid.getDataProvider().refreshAll();
    }

    public static class ZakBasicFilter {

        String ckz;
        Integer rokZak;
        String skupina;
        Boolean arch;
        Boolean digi;
        ItemType typ;

        public ZakBasicFilter(
                String ckz
                , Integer rokZak
                , String skupina
                , Boolean arch
                , Boolean digi
                , ItemType typ
        ) {
            this.ckz = ckz;
            this.rokZak = rokZak;
            this.skupina = skupina;
            this.arch = arch;
            this.digi = digi;
            this.typ = typ;
        }

        public static final ZakBasicFilter getEmpty() {
            return new ZakBasicFilter(null, null, null, null, null, null);
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
    }
}
