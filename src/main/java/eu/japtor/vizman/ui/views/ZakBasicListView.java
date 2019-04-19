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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.ItemNames;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.ReloadButton;
import eu.japtor.vizman.ui.components.Ribbon;
import eu.japtor.vizman.ui.components.TitleGrid;
import eu.japtor.vizman.ui.components.ZakBasicGrid;
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
//public class ZakEvalListView extends Div implements BeforeEnterObserver {
// ###***
public class ZakBasicListView extends VerticalLayout {

    private List<ZakBasic> zakList;
    private ZakBasicGrid zakGrid;
    private List<GridSortOrder<ZakBasic>> initialSortOrder;

    private static final String RADIO_KONT_ACTIVE = "Aktivní";
    private static final String RADIO_KONT_ARCH = "Archivované";
    private static final String RADIO_KONT_ALL = "Všechny";
    private RadioButtonGroup<String> archFilterRadio;

    @Autowired
    public ZakBasicRepo zakBasicRepo;

    public ZakBasicListView() {
        initView();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
//        setHeight("90%");
//        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        this.setPadding(false);
        this.setMargin(false);
        this.add(
                initGridContainer()
        );
    }

    @PostConstruct
    public void postInit() {
        updateViewContent();
        // TODO: inital sort order markers
        //        zakGrid.sort(initialSortOrder);
        //        UI.getCurrent().getPage().executeJavaScript("document.querySelectorAll(\"vaadin-grid-sorter\")[1].click()");
    }


//    private Component buildInitinitGridToolBar() {
//        initialSortOrder = Arrays.asList(
//                new GridSortOrder(
//                        zakGrid.getColumnByKey(ZakBasicGrid.ROK_COL_KEY), SortDirection.DESCENDING)
//                , new GridSortOrder(
//                        zakGrid.getColumnByKey(ZakBasicGrid.KZCISLO_COL_KEY), SortDirection.DESCENDING)
//        );
//    }


    private Component initGridToolBar() {
        HorizontalLayout gridToolBar = new HorizontalLayout();
        gridToolBar.setSpacing(false);
        gridToolBar.setAlignItems(Alignment.END);
        gridToolBar.setJustifyContentMode(JustifyContentMode.BETWEEN);

//        Span archFilterLabel = new Span("Zakázky:");
//        HorizontalLayout archFilterComponent = new HorizontalLayout();
//        archFilterComponent.setMargin(false);
//        archFilterComponent.setPadding(false);
//        archFilterComponent.setAlignItems(Alignment.CENTER);
//        archFilterComponent.setJustifyContentMode(JustifyContentMode.END);
//        archFilterComponent.add(
//                archFilterLabel
//                , initArchFilterRadion()
//        );

//        Icon reloadIcon = VaadinIcon.REFRESH.create();
//        reloadIcon.getStyle()
//                .set("theme", "small")
//        ;
//        Button reloadButton = new ReloadButton(event -> updateViewContent());
//        reloadButton.setIcon(reloadIcon);
//        reloadButton.getStyle()
//                .set("theme", "icon")
//        ;
//        reloadButton.addClickListener(event -> updateViewContent());

        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(Alignment.CENTER);
        titleComponent.setJustifyContentMode(JustifyContentMode.START);
        titleComponent.add(
                new TitleGrid(ItemNames.getNomP(ItemType.ZAK))
                , new Ribbon()
                , new ReloadButton(event -> updateViewContent())
        );

        gridToolBar.add(
                titleComponent
                , new Ribbon()
//                , archFilterComponent
                , new Ribbon()
                , new Span(""));
        return gridToolBar;
    }

    private Component initArchFilterRadion() {
        archFilterRadio = new RadioButtonGroup<>();
        archFilterRadio.setItems(RADIO_KONT_ACTIVE, RADIO_KONT_ARCH, RADIO_KONT_ALL);
//        buttonShowArchive.addValueChangeListener(event -> setArchiveFilter(event));
        archFilterRadio.getStyle().set("alignItems", "center");
        archFilterRadio.getStyle().set("theme", "small");
        archFilterRadio.addValueChangeListener(event -> updateViewContent());
        return archFilterRadio;
    }

    private Component initGridContainer() {

        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(
                initGridToolBar()
                ,initZakGrid()
        );
        return gridContainer;
    }


    private Component initZakGrid() {
        zakGrid = new ZakBasicGrid(false,true, null);
        zakGrid.setMultiSort(true);
        zakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
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

    private void updateViewContent() {
        loadGridData();
        zakGrid.initFilters();
        zakGrid.doFilter();
        zakGrid.getDataProvider().refreshAll();
    }

//    private void loadGridData(final String archFlag, Integer year) {
    private void loadGridData() {
        zakList = zakBasicRepo.findAllByOrderByRokDescCkontDescCzakDesc();
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
    }
}