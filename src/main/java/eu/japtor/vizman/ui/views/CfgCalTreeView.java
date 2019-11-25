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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.service.CalService;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.ui.components.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static eu.japtor.vizman.backend.utils.VzmFileUtils.VzmFile;

@Permissions(
        {Perm.CAL_READ, Perm.CAL_MODIFY}
)
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class CfgCalTreeView extends VerticalLayout  implements HasLogger {

    private static final String YM_KEY = "cal_ym-key";
    private static final String MONTH_FOND_HOURS_KEY = "month-fond-hours";
    private static final String MONTH_FOND_DAYS_KEY = "month-fond-days";

    private TreeGrid<CalTreeNode> calymGrid;
    private Button genCalYearButton;
    private Button reloadButton;
//    private RadioButtonGroup<String> dirFilterRadio;

    private List<GridSortOrder<CalTreeNode>> initialCalymSortOrder;

//    @Autowired
//    public CfgPropsCache cfgPropsCache;

    @Autowired
    public CalService calHolService;

    @Autowired
    public CalService calService;


    @Autowired
    public CfgCalTreeView() {
        initView();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setAlignItems(Alignment.STRETCH);
        this.setPadding(false);
        this.setMargin(false);

//        this.setWidth("100%");
//        this.setWidth("90vw");
//        this.setHeight("90vh");

//        <vaadin-vertical-layout style="width: 100%; height: 100%;" theme="padding">
//        <vaadin-text-field style="width: 100%;" placeholder="ID" id="idSearchTextField"></vaadin-text-field>
//        <vaadin-folderGrid items="[[items]]" id="folderGrid" style="width: 100%;"></vaadin-folderGrid>
    }

    @PostConstruct
    public void postInit() {
        initGenCalYearButton();
        this.add(buildGridContainer());
//        VzmFileUtils.VzmFile rootFile = new VzmFileUtils.VzmFile(cfgPropsCache.getDocRootServer(), true, VzmFolderType.ROOT, 0);
        DataProvider calymDataProvider = new CalymTreeDataProvider(calService);

//        ConfigurableFilterDataProvider<CalTreeNode, Void, CalymFilter> filteredCalymDataProvider
//                = calymDataProvider.withConfigurableFilter();
//        calymGrid.setDataProvider(filteredCalymDataProvider);

        calymGrid.setDataProvider(calymDataProvider);

        HierarchicalDataProvider dp;
    }

//    class CalymTreeDataProvider<CalTreeNode, CalymFilter>
    public static class CalymTreeDataProvider
            extends AbstractBackEndHierarchicalDataProvider<CalTreeNode, Void> {
//            extends AbstractBackEndHierarchicalDataProvider<CalTreeNode, CalymFilter> {

        private final CalService calService;

//        public CalymTreeDataProvider(CalTreeNode rootNode) {
//        }
        public CalymTreeDataProvider(CalService calService) {
            this.calService = calService;
        }

        @Override
//        protected Stream<CalTreeNode> fetchChildrenFromBackEnd(HierarchicalQuery<CalTreeNode, CalymFilter> query) {
        protected Stream<CalTreeNode> fetchChildrenFromBackEnd(HierarchicalQuery<CalTreeNode, Void> hQuery) {
//            return null;

            Optional<CalTreeNode> calymParentOpt = hQuery.getParentOptional();

            if (calymParentOpt.isPresent()) {
                System.out.print("fetchChildrenFromBackEnd: " + calymParentOpt.get().toString());
            } else {
                System.out.print("fetchChildrenFromBackEnd: parentNode==null");
            }
            final CalTreeNode calymParent = hQuery.getParentOptional().orElse(null);

            if (calymParent == null) { // fetching all top level nodes (all these nodes have no parent by definition).

//                return calymParent.getNodes().stream()
//                        .skip(hQuery.getOffset()).limit(hQuery.getLimit());

                List<CalTreeNode> childNodes = calService.fetchCalymsByYear(Integer.valueOf(2019));
//                List<CalTreeNode> childNodes = new ArrayList<>();
                System.out.println(" number of top level nodes=" + childNodes.size());

                return childNodes.stream()
                        .skip(hQuery.getOffset())
                        .limit(hQuery.getLimit())
                ;

//
//                return childNodes.stream()
//                        .map(s -> {
//                            return (Caly) s;   // casting to super type for the Stream
//                        }
//                );
            } else {
                return null;
            }

        }

        @Override
        public int getChildCount(HierarchicalQuery<CalTreeNode, Void> query) {
//        public int getChildCount(HierarchicalQuery<CalTreeNode, CalymFilter> query) {

            List<CalTreeNode> childNodes = calService.fetchCalymsByYear(Integer.valueOf(2019));
            return childNodes.size();
        }

        @Override
        public boolean hasChildren(CalTreeNode node) {
            return false;
        }
    }

    class CalymFilter {
        Integer year;
        YearMonth ym;

        public CalymFilter(Integer year, YearMonth ym) {
            this.year = year;
            this.ym = ym;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public YearMonth getYm() {
            return ym;
        }

        public void setYm(YearMonth ym) {
            this.ym = ym;
        }
    }

    private VerticalLayout buildGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(buildGridToolBar());
        gridContainer.add(initCalymTreeGrid());
        initialCalymSortOrder = Arrays.asList(new GridSortOrder(
                calymGrid.getColumnByKey(YM_KEY), SortDirection.DESCENDING)
        );
        return gridContainer;
    }

    private Grid initCalymTreeGrid() {

        calymGrid = new TreeGrid<>();

//        folderGrid.addColumn(iconTextValueProvider)
//            .setHeader("ČK/ČZ")
//            .setFlexGrow(0)
//            .setWidth("4em")
//            .setKey("file-icon")
//            .setId("file-icon-id")
//        ;
//        folderGrid.addHierarchyColumn(iconTextValueProvider);

//        Grid.Column hCol = folderGrid.addColumn(fileIconTextRenderer);
//        hCol.setHeader("Název")
//                .setFlexGrow(1)
//                .setWidth("30em")
//                .setKey("folder-name-key")
////                .setId("file-name-id")
//                .setResizable(true)
////                .setFrozen(true)
////                .setKey("file-name")
////                .setId("file-name-id")
//        ;

//        hCol.setComparator(
//                ((a, b) -> compareMaybeComparables(valueProvider.apply(a),
//                        valueProvider.apply(b))));

//        folderGrid.addColumn(file -> {
//            String iconHtml;
//            if (file.isDirectory()) {
//                iconHtml = VaadinIcons.FOLDER_O.getHtml();
//            } else {
//                iconHtml = VaadinIcons.FILE_O.getHtml();
//            }
//            return iconHtml + " "
//                    + Jsoup.clean(file.getName(), Whitelist.simpleText());
//        }, new ComponentRenderer<>();

        calymGrid.addColumn(CalTreeNode::getYm)
                .setHeader("Rok-Měs")
                .setKey(YM_KEY)
//                .setId("file-size-id")
        ;

//        calymGrid.addColumn(calym -> new Date(file.lastModified()))
        calymGrid.addColumn(CalTreeNode::getFondDays)
                .setHeader("Fond [dní]")
                .setKey(MONTH_FOND_DAYS_KEY)
//                .setId("file-last-modified-id")
        ;

        calymGrid.addColumn(CalTreeNode::getFondHours)
                .setHeader("Fond [hodin]")
                .setKey(MONTH_FOND_HOURS_KEY)
        ;

//        folderGrid.addColumn(file -> new Date(file.lastModified()),
//                new DateRenderer()).setCaption("Last Modified")
//                .setId("file-last-modified");



//        dirTreeGrid = new Grid<>();
//        dirTreeGrid.setMultiSort(true);
//        dirTreeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
//        dirTreeGrid.setId("person-grid");  // .. same ID as is used in shared-styles grid's dom module
//
//        dirTreeGrid.addColumn(Person::getId)
//                .setTextAlign(ColumnTextAlign.END)
//                .setHeader("ID")
//                .setSortProperty("id")
//                .setWidth("5em")
//                .setResizable(true)
//                .setFrozen(true)
//        ;
//        dirTreeGrid.addColumn(Person::getState)
//                .setHeader("State")
//                .setSortProperty("state")
//                .setWidth("5em")
//                .setResizable(true)
//                .setFrozen(true)
//        ;
//        dirTreeGrid.addColumn(new ComponentRenderer<>(this::buildEditBtn))
//                .setFlexGrow(0)
//        ;
//        dirTreeGrid.addColumn(Person::getUsername)
//                .setHeader("Username")
//                .setSortProperty("username")
//                .setWidth("8em")
//                .setResizable(true)
//                .setFrozen(true)
//        ;
//        dirTreeGrid.addColumn(Person::getJmeno)
//                .setHeader("Jméno")
//                .setSortProperty("jmeno")
//                .setWidth("8em")
//                .setResizable(true)
//        ;
//        dirTreeGrid.addColumn(Person::getPrijmeni)
//                .setHeader("Příjmení")
//                .setSortProperty("prijmeni")
//                .setWidth("8em")
//                .setResizable(true)
//        ;
//        if (isWagesAccessGranted()) {
//            dirTreeGrid.addColumn(new NumberRenderer<>(Person::getSazba, VzmFormatUtils.moneyFormat))
//                    .setTextAlign(ColumnTextAlign.END)
//                    .setHeader("Sazba")
//                    .setWidth("8em")
//                    .setResizable(true)
//            ;
//        }
//        dirTreeGrid.addColumn(Person::getNastup)
//                .setHeader("Nástup")
//                .setWidth("8em")
//                .setResizable(true)
//        ;
//        dirTreeGrid.addColumn(Person::getVystup)
//                .setHeader("Ukončení")
//                .setWidth("8em")
//                .setResizable(true)
//        ;
        return calymGrid;
    }

//    private ComponentRenderer<Component, KzTreeAware> kzArchRenderer = new ComponentRenderer<>(kz -> {
//        ArchIconBox archBox = new ArchIconBox();
//        archBox.showIcon(kz.getTyp(), kz.getArchState());
//        return archBox;
//    });



    private Button buildEditBtn(CalTreeNode node) {
        Button editBtn = new GridItemEditBtn(event ->
                System.out.println("OPEN EDIT FORM")
//                calymEditForm.open(calym, Operation.EDIT, "")
        );
        return editBtn;
    }


    private Component buildGridToolBar() {

        HorizontalLayout viewToolBar = new HorizontalLayout();
        viewToolBar.setSpacing(false);
//        viewToolBar.setPadding(true);
//        viewToolBar.getStyle().set("padding-bottom", "5px");
//        viewToolBar.setWidth("100%");
        viewToolBar.setAlignItems(Alignment.END);
        viewToolBar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(Alignment.CENTER);
        titleComponent.setJustifyContentMode(JustifyContentMode.START);
        titleComponent.add(
                new GridTitle("KALENDÁŘ")
                , new Ribbon()
                , initReloadButton()
        );

        viewToolBar.add(
                titleComponent
                , new Ribbon()
                , initGenCalYearButton()
        );
        return viewToolBar;
    }

    private Component initReloadButton() {
        reloadButton = new ReloadButton(event -> updateFolderViewContent());
        return reloadButton;
    }

    private Component initGenCalYearButton() {
        genCalYearButton = new NewItemButton("Generuj rok"
                , event -> {
        });
        return genCalYearButton;
    }

    private void updateFolderViewContent() {
        updateFolderViewContent(null);
    }

    private void assignDataProviderToGridAndSort(TreeData<CalTreeNode> calymTreeData) {
        List<GridSortOrder<CalTreeNode>> sortOrderOrig = calymGrid.getSortOrder();
        calymGrid.setTreeData(calymTreeData);
        if (CollectionUtils.isEmpty(sortOrderOrig)) {
            calymGrid.sort(initialCalymSortOrder);
        } else {
            calymGrid.sort(sortOrderOrig);
        }
    }

    private void updateFolderViewContent(final VzmFile itemToSelect) {

        calymGrid.getDataProvider().refreshAll();

//        kzTreeData = loadKzTreeData(archFilterRadio.getStringValue());
//        inMemoryKzTreeProvider = new TreeDataProvider<>(kzTreeData);
//        assignDataProviderToGridAndSort(inMemoryKzTreeProvider);
//        inMemoryKzTreeProvider.refreshAll();

//        folderGrid.deselectAll();
//        TreeData<VzmFileUtils.VzmFile> folderTreeData
//                = VzmFileUtils.getExpectedKontFolderTree(cfgPropsCache.getDocRootServer(), null);
//
//        Path folderRootPath = getKontDocRootPath(cfgPropsCache.getDocRootServer(), currentItem.getFolder());
//        File folderRootDir = new File(folderRootPath.toString());
//        addFilesToExpectedVzmTreeData(folderTreeData, folderRootDir.listFiles(), null);
//
//        addNotExpectedKontSubDirs(folderTreeData
//                , new VzmFileUtils.VzmFile(folderRootPath, true)
//        );
//        addNotExpectedKontSubDirs(folderTreeData, new VzmFileUtils.VzmFile(getExpectedKontFolder(currentItem), true));
//
//        assignDataProviderToGridAndSort(kontDocTreeData);

//        if (null != itemToSelect) {
//            folderGrid.getSelectionModel().select(itemToSelect);
//        }
    }
}
