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
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.dataprovider.CfgCalHolTreeDataProvider;
import eu.japtor.vizman.backend.dataprovider.spring.PageableTreeDataProvider;
import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.CalyHolTreeNode;
import eu.japtor.vizman.backend.entity.CalyHol;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.service.CalService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.CalyHolFormDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

@Permissions(
        {Perm.CAL_READ, Perm.CAL_MODIFY}
)
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class CfgCalHolTreeView extends VerticalLayout  implements HasLogger {

    private static final String ID_KEY = "caly-hol-id-key";
    private static final String YR_KEY = "caly-hol-yr-key";
    private static final String CAL_HOL_DATE_KEY = "caly-hol-date-key";
    private static final String CAL_HOL_DAY_KEY = "caly-hol-day-key";
    private static final String CAL_HOL_TEXT_KEY = "caly-hol-text-key";

    private TreeGrid<CalyHolTreeNode> calyHolGrid;
    private Button newHolButton;
    private Button reloadButton;
    private HeaderRow filterRow;
    private Select<Integer> rokFilterField;

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


    PageableTreeDataProvider<CalyHolTreeNode, CalyHolTreeNode> calHolDataProvider;

    @PostConstruct
    public void postInit() {
        initNewHolButton();
        this.add(buildGridContainer());

        calyHolFormDialog = new CalyHolFormDialog(
                this::saveItem, this::deleteItem, calService
        );
//        calyHolFormDialog.addOpenedChangeListener(event -> {
////            System.out.println("OPEN-CHANGED: " + event.toString());
//            if (!event.isOpened()) {
//                finishCalyHolEdit((CalyHolFormDialog)event.getSource());
//            }
//        });
//        calyHolFormDialog.addDialogCloseActionListener(event -> {
//            calyHolFormDialog.close();
//        });

        calHolDataProvider = new CfgCalHolTreeDataProvider(calService);
//        PageableTreeDataProvider<CalyHolTreeNode, Void> calHolDataProvider = new PageableTreeDataProvider(calService);
//        filteredCalHolDataProvider = calDataProvider.withConfigurableFilter();
        calyHolGrid.setDataProvider(calHolDataProvider);
        calyHolGrid.getDataProvider().refreshAll();
    }

    private void saveItem(CalyHol itemToSave, Operation operation) {
        CalyHol itemSaved = calService.saveCalyHol(itemToSave);
        CalyHol itemOrig = calyHolFormDialog.getItemOrig();
        Notification.show(
                "Svátek uložen", 2000, Notification.Position.MIDDLE);
        if (Operation.ADD == operation) {
            updateGridAfterAdd(itemSaved, itemOrig);
        } else {
            updateGridAfterEdit(itemSaved, itemOrig);
        }
    }

    private void deleteItem(final CalyHol itemToDelete) {
//        int itemIndexOrig = klients.indexOf(calyHol);
//        int itemIndexNew = itemIndexOrig >= klients.size() - 1 ? itemIndexOrig - 1 : itemIndexOrig;
        calService.deleteCalyHol(itemToDelete);
        Notification.show(
                "Svátek zrušen.", 2000, Notification.Position.MIDDLE);
        updateGridAfterDelete(itemToDelete);
    }

    private void updateGridAfterAdd(CalyHolTreeNode itemSaved, CalyHolTreeNode itemOrig) {
        calyHolGrid.getDataCommunicator().getKeyMapper().removeAll();
        calyHolGrid.getDataProvider().refreshAll();
        calyHolGrid.getDataCommunicator().reset();  //  Otherwise after addibg a first node for the year expand does not show it
        CalyHolTreeNode newRootNode = calyHolGrid.getDataCommunicator().fetchFromProvider(0, 999999)
                .filter(htn -> null == htn.getHolDate() && htn.getYr().equals(itemSaved.getYr()))
                .findFirst().orElse(null);
        calyHolGrid.getDataCommunicator().expand(newRootNode);
        selectAndScroll(itemSaved);
    }

    private void updateGridAfterEdit(CalyHolTreeNode itemSaved, CalyHolTreeNode itemOrig) {

        // Useful commands:
        // ----------------
        //        calyHolGrid.getDataCommunicator().confirmUpdate(idxItem);
        //        Integer idxItem = calyHolGrid.getDataCommunicator().getIndex(itemSaved);
        //        Integer idxParent = calyHolGrid.getDataCommunicator().getParentIndex(itemSaved);
        //        Integer i3 = calyHolGrid.getDataCommunicator().getDataProviderSize();
        //        reloadCalyHolGridData();
        //        idxParent = calyHolGrid.getDataCommunicator().fetchFromProvider(FromProvider(itemSaved);
        //        scrollToIndex(calyHolGrid, item2);

        calyHolGrid.getDataCommunicator().getKeyMapper().remove(itemSaved); // Otherwise tree  grid remebers original entity Version

        if (Objects.equals(itemOrig.getHolDate(), itemSaved.getHolDate())) {
//            calyHolGrid.getDataProvider().refreshItem(parentItem);
            calyHolGrid.getDataProvider().refreshItem(itemSaved);
        } else {
            calyHolGrid.getDataProvider().refreshAll();
            CalyHolTreeNode newRootNode = calyHolGrid.getDataCommunicator().fetchFromProvider(0, 999999)
                    .filter(htn -> null == htn.getHolDate() && htn.getYr().equals(itemSaved.getYr()))
                    .findFirst().orElse(null);
//            calyHolGrid.getDataCommunicator().reset();save
            calyHolGrid.getDataCommunicator().collapse(calyHolGrid.getDataCommunicator().getParentItem(itemSaved));
            calyHolGrid.getDataCommunicator().expand(newRootNode);

            selectAndScroll(itemSaved);
        }
    }

    private CalyHolTreeNode getSelectedNode() {
        Set<CalyHolTreeNode> selectedItems = calyHolGrid.getSelectedItems();
        return selectedItems.iterator().hasNext() ? selectedItems.iterator().next() : null;
    }

    private void updateGridAfterDelete(CalyHolTreeNode itemDeleted) {
//        Set<CalyHolTreeNode> selectedItems = calyHolGrid.getSelectedItems();

        reloadCalyHolGridData();

//        CalyHolTreeNode selItem = selectedItems.iterator().hasNext() ? selectedItems.iterator().next() : null;
//        if (null != selItem) {
//            Object selId = calyHolGrid.getDataProvider().getId(selItem);
//            calyHolGrid.select(selItem);
//            if (null != selId) {
//                scrollToIndex(calyHolGrid, 30);
////                scrollToIndex(calyHolGrid, (Integer)selId);
//            }
//        }

//        List<CalyHolTreeNode> items1 = ((TreeDataProvider<CalyHolTreeNode>)calyHolGrid.getDataProvider()).getTreeData()....;
//        List<CalyHolTreeNode> items = ((HierarchicalDataCommunicator<CalyHolTreeNode>)calyHolGrid.getDataCommunicator())
//                .get .fetchFromProvider(0, 999999)
//                .collect(Collectors.toList());
////                .filter(htn -> htn.equals(selNode))
////                .map(ist::indexOf)
////                .findFirst();
//        int index = items.indexOf(selItem);
//        if (index >= 0) {
//            scrollToIndex(calyHolGrid, index);
//        }

//        calyHolGrid.getDataProvider().getId(itemDeleted)Communicator().getKeyMapper().removeAll();
//        calyHolGrid.getDataCommunicator().getKeyMapper().removeAll();
//        scrollToIndex(calyHolGrid, itemIndexOrig);
    }

    private void selectAndScroll(final CalyHolTreeNode itemToSelect) {
        if (null != itemToSelect) {
            calyHolGrid.select(itemToSelect);
            Integer idxSel = calyHolGrid.getDataCommunicator().getIndex(itemToSelect);
            if (null != idxSel) {
                scrollToIndex(calyHolGrid, idxSel);
            }
        }
    }

    private CalyHolTreeNode getItemFromTree(final CalyHolTreeNode item) {
        Integer itemIdx = getItemRowIdx(item);
        if (null == itemIdx) {
            return null;
        }
        return calyHolGrid.getDataCommunicator()
                .fetchFromProvider(itemIdx, 1)
                .findFirst().orElse(null);
    }

    private Integer getItemRowIdx(final CalyHolTreeNode item) {
        return calyHolGrid.getDataCommunicator().getIndex(item);
    }

    private void reloadCalyHolGridData() {
        calyHolGrid.getDataProvider().refreshAll();
    }

    private void scrollToIndex(TreeGrid<?> treeGrid, int index) {
        UI.getCurrent().getPage()
                .executeJavaScript(
                        "$0._scrollToIndex($1)", treeGrid, index
                );
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
        calyHolGrid = new TreeGrid<>();

//        calyHolGrid.addColumn(c -> null == c.getVersion() ? "" : c.getVersion())
//                .setHeader("Version")
//                .setFlexGrow(0)
//                .setWidth("9em")
//                .setKey("ver")
//        ;
        calyHolGrid.addHierarchyColumn(c -> null == c.getYr() ? "" : c.getYr())
                .setHeader("Rok")
                .setFlexGrow(0)
                .setWidth("9em")
                .setResizable(true)
                .setKey(YR_KEY)
                .setSortProperty("yr")
        ;
        calyHolGrid.addColumn(new ComponentRenderer<>(this::buildHolOpenBtn))
                .setHeader("Edit")
                .setFlexGrow(0)
                .setWidth("4em")
        ;
        calyHolGrid.addColumn(c -> null == c.getHolDate() ?
                "" : VzmFormatUtils.basicDateFormatter.format(c.getHolDate()))
                .setHeader("Datum")
                .setFlexGrow(0)
                .setWidth("12em")
                .setKey(CAL_HOL_DATE_KEY)
        ;
        calyHolGrid.addColumn(c -> null == c.getHolDate() ?
                "" : VzmFormatUtils.dayOfWeekLocalizedFormatter.format(c.getHolDate()))
                .setHeader("Den")
                .setFlexGrow(0)
                .setWidth("12em")
                .setKey(CAL_HOL_DAY_KEY)
        ;
        calyHolGrid.addColumn(CalyHolTreeNode::getHolText)
                .setHeader("Svátek" )
                .setKey(CAL_HOL_TEXT_KEY)
        ;
        return calyHolGrid;
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
                , initNewHolButton()
        );
        return viewToolBar;
    }

    private Component initReloadButton() {
        reloadButton = new ReloadButton(event -> updateCalHolViewContent());
        return reloadButton;
    }

    private Component initNewHolButton() {
        newHolButton = new NewItemButton("Svátek", event -> {
//            int yrNew = 1 + calService.fetchCalyYrList().stream()
//                    .mapToInt(y -> y)
//                    .max().orElseThrow(NoSuchElementException::new);
//            CalyHolTreeNode selItem = getSelectedNode();
            CalyHolTreeNode newItem = new CalyHol();
            calyHolFormDialog.openDialog(
                    (CalyHol) newItem, Operation.ADD, "SVÁTEK", "NOVÝ"
            );
        });
        return newHolButton;
    }

    private void updateCalHolViewContent() {
        updateCalHolViewContent(null);
    }

    private void updateCalHolViewContent(final CalyHolTreeNode itemToSelect) {
        calyHolGrid.getDataProvider().refreshAll();
    }

    private CalyHolFormDialog calyHolFormDialog;

    private Component buildHolOpenBtn(CalyHolTreeNode calyHolNode) {
        Button btn;
        if (null != calyHolNode.getHolDate()) {
            btn = new GridItemEditBtn(event -> {
                calyHolGrid.select(calyHolNode);
                calyHolFormDialog.openDialog(
                        (CalyHol) calyHolNode, Operation.EDIT, "SVÁTEK", "EDITACE"
                );
            });
            return btn;
        } else {
            Span span = new Span();
            return span;
        }
    }
}
