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
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.hierarchy.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.bean.Account;
import eu.japtor.vizman.backend.bean.TestCalService;
import eu.japtor.vizman.backend.dataprovider.CfgCalTreeDataProvider;
import eu.japtor.vizman.backend.dataprovider.TestDataProvider;
import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.Caly;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.service.CalService;
import eu.japtor.vizman.ui.components.*;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.YearMonth;
import java.util.NoSuchElementException;

@Permissions(
        {Perm.CAL_READ, Perm.CAL_MODIFY}
)
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class CfgCalTreeView extends VerticalLayout  implements HasLogger {

    private static final String ID_KEY = "cal-id-key";
    private static final String YR_KEY = "cal-yr-key";
    private static final String YM_KEY = "cal-ym-key";
    private static final String MONTH_KEY = "cal-month-key";
    private static final String YEAR_FOND_HOURS_KEY = "year-fond-hours";
    private static final String YEAR_FOND_DAYS_KEY = "year-fond-days";
    private static final String MONTH_FOND_HOURS_KEY = "month-fond-hours";
    private static final String MONTH_FOND_DAYS_KEY = "month-fond-days";

    private TreeGrid<CalTreeNode> calGrid;
    private Button genCalYearButton;
    private Button reloadButton;
    private Select<Integer> rokFilterField;

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


    ConfigurableFilterDataProvider<CalTreeNode, Void, CalTreeNode> filteredCalDataProvider;

    @PostConstruct
    public void postInit() {
        initGenCalYearButton();
        this.add(buildGridContainer());

        HierarchicalDataProvider<CalTreeNode, CalTreeNode> calDataProvider = new CfgCalTreeDataProvider(calService);
        filteredCalDataProvider = calDataProvider.withConfigurableFilter();
//        calGrid.setDataProvider(filteredCalymDataProvider);
        calGrid.setDataProvider(filteredCalDataProvider);
        calGrid.getDataProvider().refreshAll();


//        testGrid.getDataProvider().refreshAll();


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



    TestCalService testService;


    private VerticalLayout buildGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(buildGridToolBar());
        gridContainer.add(initCalymTreeGrid());


//        testGrid = new TreeGrid<>();
//        testGrid.addHierarchyColumn(cal -> cal.getYr().toString()).setHeader("CAL Yr");
//        testGrid.addColumn(cal -> cal.getYearFondDays().toString()).setHeader("Fond (hod/rok)");
//        testService = new TestCalService();
//        HierarchicalDataProvider testDataProvider =
//            new AbstractBackEndHierarchicalDataProvider<CalyTest, Void>() {
//
//                @Override
//                public int getChildCount(HierarchicalQuery<CalyTest, Void> query) {
//                    return (int) testService.getChildCount(query.getParent());
//                }
//
//                @Override
//                public boolean hasChildren(CalyTest item) {
//                    return testService.hasChildren(item);
//                }
//
//                @Override
//                protected Stream<CalyTest> fetchChildrenFromBackEnd(
//                        HierarchicalQuery<CalyTest, Void> query) {
//                    return testService.fetchChildren(query.getParent()).stream();
//                }
//            };
//
//
//
////        testDataProvider = new TestDataProvider(testService);
//        testGrid.setDataProvider(testDataProvider);
//        testGrid.setId("testgrid");



//        initialCalymSortOrder = Arrays.asList(new GridSortOrder(
//                calGrid.getColumnByKey(YM_KEY), SortDirection.DESCENDING)
//        );


        TreeGrid<Account> accGrid = new TreeGrid<>();
        accGrid.addHierarchyColumn(Account::toString).setHeader("Account Title");
        accGrid.addColumn(Account::getCode).setHeader("Code");

//        gridContainer.add(testGrid);
        return gridContainer;
    }



    TestDataProvider testDataProvider;
    HeaderRow filterRow;

    private Grid initCalymTreeGrid() {

        calGrid = new TreeGrid<>();

// Only for in memory:
//        calGrid.setItems(calService.fetchAllCalRootNodes());
//                departmentData::getChildDepartments);

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

//        calGrid.addColumn(CalTreeNode::getId)
//                .setHeader("ID")
//                .setFlexGrow(0)
//                .setWidth("9em")
//                .setResizable(true)
//                .setKey(ID_KEY)
////                .setId("file-size-id")
//        ;
        calGrid.addHierarchyColumn(c -> null == c.getYr() ? "" : c.getYr())
                .setHeader("Rok")
                .setFlexGrow(0)
                .setWidth("9em")
                .setResizable(true)
                .setKey(YR_KEY)
                .setSortProperty("yr")
        ;
        calGrid.addColumn(c -> null == c.getYearFondDays() ? "" : c.getYearFondDays())
                .setHeader("Fond [den/rok]")
                .setKey(YEAR_FOND_DAYS_KEY)
        ;
        calGrid.addColumn(c -> null == c.getYearFondHours() ? "" : c.getYearFondHours())
                .setHeader("Fond [hod/rok]" )
                .setKey(YEAR_FOND_HOURS_KEY)
        ;
        calGrid.addColumn(CalTreeNode::getYm)
                .setHeader("Rok-Měs")
                .setKey(YM_KEY)
        ;
        calGrid.addColumn(c -> null == c.getMonthLocal() ? "" : c.getMonthLocal())
                .setHeader("Měsíc")
                .setKey(MONTH_KEY)
        ;
        calGrid.addColumn(c -> null == c.getMonthFondDays() ? "" : c.getMonthFondDays())
                .setHeader("Fond [den/měs]")
                .setKey(MONTH_FOND_DAYS_KEY)
        ;
        calGrid.addColumn(c -> null == c.getMonthFondHours() ? "" : c.getMonthFondHours())
                .setHeader("Fond [hod/měs]" )
                .setKey(MONTH_FOND_HOURS_KEY)
        ;


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

        filterRow = calGrid.appendHeaderRow();
        rokFilterField = buildSelectionFilterField();
        rokFilterField.setItems(calService.fetchCalyYrList());
        rokFilterField.addValueChangeListener(event -> {
                Integer yrFilter = event.getValue();
                CalTreeNode filter = Caly.getEmptyInstance();
                filter.setYr(yrFilter);
                filteredCalDataProvider.setFilter(filter);
            }
        );

        filterRow.getCell(calGrid.getColumnByKey(YR_KEY))
                .setComponent(rokFilterField);

        return calGrid;
    }

    private <T> Select buildSelectionFilterField() {
        Select <T> selectFilterField = new Select<>();
        selectFilterField.setSizeFull();
        selectFilterField.setEmptySelectionCaption("Vše");
        selectFilterField.setEmptySelectionAllowed(true);
        return selectFilterField;
    }

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
        viewToolBar.setAlignItems(Alignment.END);
        viewToolBar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(Alignment.CENTER);
        titleComponent.setJustifyContentMode(JustifyContentMode.START);
        titleComponent.add(
                new GridTitle("PRACOVNÍ FOND")
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
        reloadButton = new ReloadButton(event -> updateCalViewContent());
        return reloadButton;
    }

    private Component initGenCalYearButton() {

        genCalYearButton = new ToolBarButton("Generovat fond/rok", event -> {
            int yrNew = 1 + calService.fetchCalyYrList().stream()
                    .mapToInt(y -> y)
                    .max().orElseThrow(NoSuchElementException::new);

            ConfirmDialog.createQuestion()
                    .withCaption("PRACOVNÍ FOND")
                    .withMessage(
                            String.format("Generovat pracovní fond pro rok %d ?", yrNew))
                    .withYesButton(() -> {
                        calService.generateAndSaveCalYearWorkFonds(yrNew);
                        rokFilterField.setItems(calService.fetchCalyYrList());
                        calGrid.getDataProvider().refreshAll();
                    }, ButtonOption.focus(), ButtonOption.caption("GENEROVAT"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open();
        });
        return genCalYearButton;
    }

    private void updateCalViewContent() {
        updateCalViewContent(null);
    }

    private void updateCalViewContent(final CalTreeNode itemToSelect) {
        calGrid.getDataProvider().refreshAll();
    }
}
