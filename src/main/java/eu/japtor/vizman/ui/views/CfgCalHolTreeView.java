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
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.dataprovider.CfgCalHolTreeDataProvider;
import eu.japtor.vizman.backend.dataprovider.spring.PageableTreeDataProvider;
import eu.japtor.vizman.backend.entity.CalHolTreeNode;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.service.CalService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.NoSuchElementException;

@Permissions(
        {Perm.CAL_READ, Perm.CAL_MODIFY}
)
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class CfgCalHolTreeView extends VerticalLayout  implements HasLogger {

    private static final String ID_KEY = "cal-id-key";
    private static final String YR_KEY = "cal-yr-key";
    private static final String CAL_HOL_DATE_KEY = "cal-hol-date-key";
    private static final String CAL_HOL_TEXT_KEY = "cal-hol-text-key";

    private TreeGrid<CalHolTreeNode> calHolGrid;
    private Button newHolButton;
    private Button reloadButton;
    private HeaderRow filterRow;
    private Select<Integer> rokFilterField;

//    @Autowired
//    public CfgPropsCache cfgPropsCache;

//    @Autowired
//    public CalService calHolService;

    @Autowired
    public CalService calService;


    @Autowired
    public CfgCalHolTreeView() {
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


//    ConfigurableFilterDataProvider<CalTreeNode, Void, CalTreeNode> filteredCalHolDataProvider;
//    PageableTreeDataProvider<CalHolTreeNode, Void> filteredCalHolDataProvider;
    PageableTreeDataProvider<CalHolTreeNode, CalHolTreeNode> calHolDataProvider;

    @PostConstruct
    public void postInit() {
        initAddNewHolButton();
        this.add(buildGridContainer());

        calHolDataProvider = new CfgCalHolTreeDataProvider(calService);
//        PageableTreeDataProvider<CalHolTreeNode, Void> calHolDataProvider = new PageableTreeDataProvider(calService);
//        filteredCalHolDataProvider = calDataProvider.withConfigurableFilter();
        calHolGrid.setDataProvider(calHolDataProvider);
        calHolGrid.getDataProvider().refreshAll();
    }

    private VerticalLayout buildGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(buildGridToolBar());
        gridContainer.add(initCalymTreeGrid());

        return gridContainer;
    }

    private Grid initCalymTreeGrid() {

        calHolGrid = new TreeGrid<>();

        calHolGrid.addColumn(CalHolTreeNode::getId)
                .setHeader("ID")
                .setFlexGrow(0)
                .setWidth("9em")
                .setResizable(true)
                .setKey(ID_KEY)
//                .setId("file-size-id")
        ;
        calHolGrid.addHierarchyColumn(c -> null == c.getYr() ? "" : c.getYr())
                .setHeader("Rok")
                .setFlexGrow(0)
                .setWidth("9em")
                .setResizable(true)
                .setKey(YR_KEY)
                .setSortProperty("yr")
        ;
        calHolGrid.addColumn(c -> null == c.getHolDate() ?
                "" : VzmFormatUtils.basicDateFormatter.format(c.getHolDate()))
                .setHeader("Datum")
                .setKey(CAL_HOL_DATE_KEY)
        ;
        calHolGrid.addColumn(CalHolTreeNode::getHolText)
                .setHeader("Svátek" )
                .setKey(CAL_HOL_TEXT_KEY)
        ;

//        filterRow = calHolGrid.appendHeaderRow();
//        rokFilterField = buildSelectionFilterField();
//        rokFilterField.setItems(calService.fetchCalyYrList());
//        rokFilterField.addValueChangeListener(event -> {
//                Integer yrFilter = event.getValue();
//                CalTreeNode filter = Caly.getEmptyInstance();
//                filter.setYr(yrFilter);
//                filteredCalHolDataProvider.setFilter(filter);
//            }
//        );
//
//        filterRow.getCell(calHolGrid.getColumnByKey(YR_KEY))
//                .setComponent(rokFilterField);

        return calHolGrid;
    }

    private Button buildEditBtn(CalHolTreeNode node) {
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
                new GridTitle("SVÁTKY")
                , new Ribbon()
                , initReloadButton()
        );

        viewToolBar.add(
                titleComponent
                , new Ribbon()
                , initAddNewHolButton()
        );
        return viewToolBar;
    }

    private Component initReloadButton() {
        reloadButton = new ReloadButton(event -> updateCalHolViewContent());
        return reloadButton;
    }

    private Component initAddNewHolButton() {

        newHolButton = new NewItemButton("Svátek", event -> {
            int yrNew = 1 + calService.fetchCalyYrList().stream()
                    .mapToInt(y -> y)
                    .max().orElseThrow(NoSuchElementException::new);

//            ConfirmDialog.createQuestion()
//                    .withCaption("PRACOVNÍ FOND")
//                    .withMessage(
//                            String.format("Generovat pracovní fond pro rok %d ?", yrNew))
//                    .withYesButton(() -> {
//                        calService.generateAndSaveCalYearWorkFonds(yrNew);
//                        rokFilterField.setItems(calService.fetchCalyYrList());
//                        calHolGrid.getDataProvider().refreshAll();
//                    }, ButtonOption.focus(), ButtonOption.caption("GENEROVAT"))
//                    .withCancelButton(ButtonOption.caption("ZPĚT"))
//                    .open();
        });
        return newHolButton;
    }

    private void updateCalHolViewContent() {
        updateCalHolViewContent(null);
    }

    private void updateCalHolViewContent(final CalHolTreeNode itemToSelect) {
        calHolGrid.getDataProvider().refreshAll();
    }
}
