package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.service.ZaknService;
import eu.japtor.vizman.ui.components.*;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


public class ZakSelectDialog extends Dialog {

    private List<Long> pruhZakIdList = new ArrayList<>();
    private List<ZakBasic> zakBasicList;
    private ZakSimpleGrid zakGrid;

    private VerticalLayout mainPanel;
    private HorizontalLayout buttonBar;
    private Button addZaksButton;
    private Button cancelButton;

    private ZakBasicRepo zakBasicRepo;
    private Consumer<List<ZakBasic>> itemsAder;
    private ZaknService zaknService;
    private CfgPropsCache cfgPropsCache;


    public ZakSelectDialog(
            Consumer<List<ZakBasic>> itemAdder
            , ZakBasicRepo zakBasicRepo
            , ZaknService zaknService
            , CfgPropsCache cfgPropsCache
    ) {
        this.itemsAder = itemAdder;
        this.zakBasicRepo = zakBasicRepo;
        this.zaknService = zaknService;
        this.cfgPropsCache = cfgPropsCache;

        this.setWidth("1400px");
        this.setHeight("750px");

        zakBasicList = new ArrayList<>();
//        setupEventListeners();

        this.add(initView());
        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);
    }

    private Component initDialogButtonBar() {
        buttonBar = new HorizontalLayout();
        cancelButton = new Button("Zpět");
        cancelButton.addClickListener(e -> close());

        addZaksButton = new Button("Přidat zakázky");
        addZaksButton.getElement().setAttribute("theme", "primary");
        addZaksButton.addClickListener(e -> addZaksClicked());
        // TODO: Disable when opened, enabled when item selected
//        addZaksButton.setEnabled(false);

        HorizontalLayout leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);

        HorizontalLayout middleBarPart = new HorizontalLayout();
        middleBarPart.setSpacing(true);
        middleBarPart.add(addZaksButton, new Gap(), cancelButton);

        HorizontalLayout rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);

        buttonBar.setSpacing(false);
        buttonBar.setPadding(false);
        buttonBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttonBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        buttonBar.add(leftBarPart, middleBarPart, rightBarPart);
        buttonBar.setClassName("buttons");

        return buttonBar;
    }

    private Component initView() {
        mainPanel = new VerticalLayout();
        mainPanel.setWidth("100%");
        mainPanel.setHeight("100%");
        mainPanel.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        mainPanel.setPadding(false);
        mainPanel.setMargin(false);
        mainPanel.add(
                initGridContainer()
                , initDialogButtonBar()
        );
        return mainPanel;
    }


    private Component initGridContainer() {

        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(FlexComponent.Alignment.STRETCH);

        gridContainer.add(
                initGridToolBar()
                , initZakGrid(selCount -> addZaksButton.setEnabled(selCount > 0))
        );
        return gridContainer;
    }

    private Component initZakGrid(Consumer<Integer> selectionChanger) {
        zakGrid = new ZakSimpleGrid(
                true
                , checkBoxEnabler
                , selectionChanger
                , false
                , false
                , false
                , Boolean.FALSE
                , null
                , zaknService
                , cfgPropsCache
        );
        zakGrid.setMultiSort(true);
        zakGrid.setSelectionMode(Grid.SelectionMode.NONE);
        return zakGrid;
    }

    private Function<ZakBasic, Boolean> checkBoxEnabler = zakb -> !pruhZakIdList.contains(zakb.getId());


    private Component initGridToolBar() {
        HorizontalLayout gridToolBar = new HorizontalLayout();
        gridToolBar.setSpacing(false);
        gridToolBar.setAlignItems(FlexComponent.Alignment.END);
        gridToolBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(FlexComponent.Alignment.CENTER);
        titleComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        titleComponent.add(
                new GridTitle(ItemNames.getNomP(ItemType.ZAK))
                , new Ribbon()
                , new ReloadButton(event -> updateViewContent())
        );

        gridToolBar.add(
                titleComponent
                , new Ribbon()
                , new Ribbon()
                , new Span(""));
        return gridToolBar;
    }

    private void updateViewContent() {
        zakBasicList = zakBasicRepo.findAllByOrderByCkontDescCzakDesc();
        zakGrid.setItems(zakBasicList);

        zakGrid.rebuildFilterFields(zakBasicList);
        zakGrid.setInitialFilterValues();
        zakGrid.doFilter();
        zakGrid.setSelCount(0);
        addZaksButton.setEnabled(zakGrid.getSelCount() > 0);
        zakGrid.getDataProvider().refreshAll();
    }

    public void openDialog(List<Long> pruhZakIdList) {
        this.pruhZakIdList = pruhZakIdList;
        updateViewContent();
        this.open();
    }

    private void addZaksClicked() {
        List<ZakBasic> zakBasicItemsToAdd = new ArrayList<>();
        for(ZakBasic zakb : zakBasicList) {
            if (zakb.isSelectableForPruh() && !zakb.getArch() && zakb.isChecked() ) {
                zakBasicItemsToAdd.add(zakb);
            }
        }

        if (CollectionUtils.isEmpty(zakBasicItemsToAdd)) {
            ConfirmDialog.createInfo()
                    .withCaption("Přidání zakázek")
                    .withMessage("Nejsou vybrány žádné zakázky")
                    .open()
            ;
        } else {
            itemsAder.accept(zakBasicItemsToAdd);
            this.close();
        }
    }
}
