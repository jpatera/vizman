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
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.report.ZakListReportBuilder;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.backend.service.ZakBasicService;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    private Anchor expXlsAnchor;
    private ReportExporter<ZakBasic> xlsReportExporter;

    private final static String REPORT_FILE_NAME = "vzm-rep-zak";


    @Autowired
    public ZakBasicRepo zakBasicRepo;

    @Autowired
    public ZakBasicService zakBasicService;


    private ComponentEventListener anchorExportListener = event -> {
        try {
            updateExpXlsAnchorResource(zakList);
        } catch (JRException e) {
            e.printStackTrace();
        }
    };

    private SerializableSupplier<List<? extends ZakBasic>> itemsSupplier =
            () -> zakBasicService.fetchAndCalcByFiltersDescOrder(buildZakBasicFilterParams());

    public ZakBasicListView() {
        xlsReportExporter = new ReportExporter((new ZakListReportBuilder()).buildReport());
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
        //        zakGrid.sort(initialSortOrder);
        //        UI.getCurrent().getPage().executeJavaScript("document.querySelectorAll(\"vaadin-grid-sorter\")[1].click()");
    }

    private ZakBasicFilterParams buildZakBasicFilterParams() {
        ZakBasicFilterParams zakBasicFilterParams = new ZakBasicFilterParams();
        zakBasicFilterParams.setArch(zakGrid.getArchFilterValue());
        zakBasicFilterParams.setDigi(zakGrid.getDigiFilterValue());
        zakBasicFilterParams.setCkz(zakGrid.getkzCisloFilterField());
        zakBasicFilterParams.setRokZak(zakGrid.getRokFilterValue());
        zakBasicFilterParams.setSkupina(zakGrid.getSkupinaFilterValue());
        return zakBasicFilterParams;
    }

    private Component buildGridBarComponent() {
        HorizontalLayout gridToolBar = new HorizontalLayout();
//        gridToolBar.setWidth("100%");
//        gridToolBar.setPadding(true);
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
                new GridTitle(ItemNames.getNomP(ItemType.ZAK))
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

    private void updateExpXlsAnchorResource(List<ZakBasic> items) throws JRException {
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
                ,initZakGrid()
        );
        return gridContainer;
    }


    private Component initZakGrid() {
        zakGrid = new ZakSimpleGrid(false, null, null,true, true, null, null);
        zakGrid.setMultiSort(true);
        zakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        return zakGrid;
    }

    private void loadViewContent() {
        loadGridDataAndRebuildFilterFields();
        zakGrid.setInitialFilterValues();
        zakGrid.doFilter();
        zakGrid.getDataProvider().refreshAll();
    }

    private void loadGridDataAndRebuildFilterFields() {
        zakList = zakBasicRepo.findAllByOrderByCkontDescCzakDesc();
        zakGrid.setItems(zakList);
        zakGrid.setRokFilterItems(zakList.stream()
                .filter(z -> null != z.getRok())
                .map(ZakBasic::getRok)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        zakGrid.setSkupinaFilterItems(zakList.stream()
                .map(ZakBasic::getSkupina)
                .filter(s -> null != s)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        zakGrid.setArchFilterItems(zakList.stream()
                .map(ZakBasic::getArch)
                .filter(a -> null != a)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        zakGrid.setDigiFilterItems(zakList.stream()
                .map(ZakBasic::getDigi)
                .filter(a -> null != a)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
    }


    public static class ZakBasicFilterParams {

        String ckz;
        Integer rokZak;
        String skupina;
        Boolean arch;
        Boolean digi;

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
