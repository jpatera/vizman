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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.Ribbon;
import eu.japtor.vizman.ui.components.ZakBasicGrid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_ZAK_LIST, layout = MainView.class)
@PageTitle(PAGE_TITLE_ZAK_LIST)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
        Perm.ZAK_BASIC_READ, Perm.ZAK_BASIC_MODIFY,
        Perm.ZAK_EXT_READ, Perm.ZAK_EXT_MODIFY
})
//public class ZakEvalListView extends Div implements BeforeEnterObserver {
// ###***
public class ZakListView extends VerticalLayout {

//    private final VerticalLayout mainViewContainer = new VerticalLayout();
//    private final H3 zakHeader = new H3(TITLE_ZAK_LIST);

    List<ZakBasic> zakList;
    private ZakBasicGrid zakGrid;
    private List<GridSortOrder<ZakBasic>> initialSortOrder;

//    @Autowired
//    public ZakService zakService;

    @Autowired
    public ZakBasicRepo zakBasicRepo;

    @Autowired
    public ZakListView() {
        initView();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setPadding(false);
        this.setMargin(false);
        this.add(initGridContainer());
    }

    @PostConstruct
    public void postInit() {
//        setHeight("90%");
//        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        updateViewContent(null, null);
        zakGrid.setMultiSort(true);
//        initialSortOrder = Arrays.asList(
//                new GridSortOrder(
//                        zakGrid.getColumnByKey(ZakBasicGrid.ROK_COL_KEY), SortDirection.DESCENDING)
//                , new GridSortOrder(
//                        zakGrid.getColumnByKey(ZakBasicGrid.KZCISLO_COL_KEY), SortDirection.DESCENDING)
//        );
//        zakGrid.sort(initialSortOrder);
//        UI.getCurrent().getPage().executeJavaScript("document.querySelectorAll(\"vaadin-grid-sorter\")[1].click()");
    }

    private static final String RADIO_KONT_ACTIVE = "Aktivní";
    private static final String RADIO_KONT_ARCH = "Archivované";
    private static final String RADIO_KONT_ALL = "Všechny";
    private RadioButtonGroup<String> archFilterRadio;


    private Component initGridToolBar() {

//        kzToolBar = new HorizontalLayout();
//        FlexLayout kzToolBar;
        HorizontalLayout kzToolBar = new HorizontalLayout();
        kzToolBar.setSpacing(false);
        kzToolBar.setAlignItems(Alignment.END);
        kzToolBar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Span archFilterLabel = new Span("Zakázky:");

        archFilterRadio = new RadioButtonGroup<>();
        archFilterRadio.setItems(RADIO_KONT_ACTIVE, RADIO_KONT_ARCH, RADIO_KONT_ALL);
//        buttonShowArchive.addValueChangeListener(event -> setArchiveFilter(event));
        archFilterRadio.getStyle().set("alignItems", "center");
        archFilterRadio.getStyle().set("theme", "small");
        archFilterRadio.addValueChangeListener(event -> {
                    updateViewContent(event.getValue(), null);
                }
        );

        HorizontalLayout archFilterComponent = new HorizontalLayout();
        archFilterComponent.setMargin(false);
        archFilterComponent.setPadding(false);
        archFilterComponent.setAlignItems(Alignment.CENTER);
        archFilterComponent.setJustifyContentMode(JustifyContentMode.END);
        archFilterComponent.add(archFilterLabel, archFilterRadio);

        H3 mainTitle = new H3("ZAKÁZKY");
        mainTitle.getStyle().set("margin-top", "0.2em");
//        kzToolBar.add(mainTitle, new Ribbon(), archFilterComponent, new Ribbon(), new Span(""));

        Button reloadButton = new Button("Reload");
        reloadButton.addClickListener(event -> updateViewContent(null, null));

        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(Alignment.CENTER);
        titleComponent.setJustifyContentMode(JustifyContentMode.START);
        titleComponent.add(mainTitle, new Ribbon(), reloadButton);

        kzToolBar.add(titleComponent, new Ribbon(), archFilterComponent, new Ribbon(), new Span(""));
        return kzToolBar;
    }


    private Component initGridContainer() {

        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(initGridToolBar());
        gridContainer.add(initZakGrid());
        return gridContainer;
    }


    private Component initZakGrid() {
        zakGrid = new ZakBasicGrid();
        return zakGrid;
    }

//    private void openDir(String path) {
//        try {
////            Runtime.getRuntime().exec("explorer.exe /select," + path);
//            ProcessBuilder pb = new ProcessBuilder("explorer.exe", "/select," + path);
//            pb.redirectError();
//            pb.start();
////            Process proc = pb.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void updateViewContent(final String archFlag, Integer year) {
        loadGridData(archFlag, year);
        zakGrid.initFilters();
        zakGrid.getDataProvider().refreshAll();
    }

    private void loadGridData(final String archFlag, Integer year) {
//        List<Zak> zaks = zakService.fetchAll();
//        zakList = zakBasicRepo.findAllByOrderByRokDescCkontDescCzakDes();
        zakList = zakBasicRepo.findAllByOrderByRokDescCkontDescCzakDesc();
        zakGrid.setItems(zakList);
    }
}
