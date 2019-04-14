package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.ui.components.*;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//@UIScope    // Without this annotation browser refresh throws exception
public class ZakFlatSelectDialog extends Dialog {

    private List<ZakBasic> zakList;
    private ZakBasicGrid zakGrid;

    VerticalLayout mainPanel;
    HorizontalLayout buttonBar;
    private Button selectButton;
    private Button cancelButton;

    public ZakBasicRepo zakBasicRepo;
    private Consumer<List<ZakBasic>> zakBasicSelector;


    public ZakFlatSelectDialog(Consumer<List<ZakBasic>> itemSelector, ZakBasicRepo zakBasicRepo) {
//        super(itemSaver);
        this.zakBasicSelector = itemSelector;
        this.zakBasicRepo = zakBasicRepo;

        this.setWidth("1400px");
        this.setHeight("750px");

        zakList = new ArrayList<>();
//        setupEventListeners();

        this.add(initView());
        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);
    }

    private Component initDialogButtonBar() {
        buttonBar = new HorizontalLayout();
        cancelButton = new Button("Zpět");
        cancelButton.addClickListener(e -> close());
//        cancelButton.setAutofocus(true);

        selectButton = new Button("Uložit");
        selectButton.getElement().setAttribute("theme", "primary");
        // TODO: Disable when opened, enabled when item selected
//        selectButton.setEnabled(false);

//        HorizontalLayout leftBarPart = new HorizontalLayout();
//        leftBarPart.setSpacing(true);
//        leftBarPart.add(selectButton);
//
//        HorizontalLayout rightBarPart = new HorizontalLayout();
//        rightBarPart.setSpacing(true);
//        rightBarPart.add(cancelButton);

        HorizontalLayout leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);

        HorizontalLayout middleBarPart = new HorizontalLayout();
        middleBarPart.setSpacing(true);
        middleBarPart.add(selectButton, new Gap(), cancelButton);

        HorizontalLayout rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);

//        buttonBar.getStyle().set("margin-top", "0.2em");
        buttonBar.setSpacing(false);
//        buttonBar.setSpacing(true);
        buttonBar.setPadding(false);
        buttonBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttonBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        buttonBar.add(leftBarPart, middleBarPart, rightBarPart);
        buttonBar.setClassName("buttons");

        return buttonBar;
    }


//    @PostConstruct
//    public void postInit() {
//
////        initKzTextRenderer();
////        initView();
////        initZakProvider();
//        reloadDataProvider();
//        inMemoryKzTreeProvider.refreshAll();
//    }

//    private ComponentRenderer initKzTextRenderer() {
//        kzTextRenderer = new ComponentRenderer<>(kontZak -> {
//            Paragraph kzText = new Paragraph(kontZak.getKzText());
//            kzText.getStyle().set("color", VzmFormatUtils.getItemTypeColorName(kontZak.getTyp()));
//            if (ItemType.KONT != kontZak.getTyp()) {
//                kzText.getStyle().set("text-indent", "1em");
//            }
//            return kzText;
//        });
//        return kzTextRenderer;
//    }

    private ValueProvider<KzTreeAware, String> klientValProv =
            kz -> kz.getTyp() == ItemType.KONT ? (null == kz.getKlient() ? null : kz.getKlient().getName()) : null;


    private Component initView() {
        mainPanel = new VerticalLayout();
        mainPanel.setWidth("100%");
        mainPanel.setHeight("100%");
        mainPanel.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        mainPanel.setPadding(false);
        mainPanel.setMargin(false);

        mainPanel.setAlignItems(FlexComponent.Alignment.STRETCH);
//        mainPanel.add(initDialogTitle(), new Hr());
//        mainPanel.add(new Paragraph());
//        mainPanel.add(buttonBar);

//        this.setWidth("100%");
//        this.setWidth("90vw");
//        this.setHeight("90vh");

//        <vaadin-vertical-layout style="width: 100%; height: 100%;" theme="padding">
//        <vaadin-text-field style="width: 100%;" placeholder="ID" id="idSearchTextField"></vaadin-text-field>
//        <vaadin-treeGrid items="[[items]]" id="treeGrid" style="width: 100%;"></vaadin-treeGrid>

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
                , initZakGrid()
        );
        return gridContainer;
    }

    private Component initZakGrid() {
        zakGrid = new ZakBasicGrid(true,false, Boolean.FALSE);
        zakGrid.setMultiSort(true);
        zakGrid.setSelectionMode(Grid.SelectionMode.NONE);
        return zakGrid;
    }

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
                new TitleGrid(ItemNames.getNomP(ItemType.ZAK))
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
        loadGridData();
        zakGrid.initFilters();
        zakGrid.doFilter();
        zakGrid.getDataProvider().refreshAll();
    }

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


//    private void reloadDataProvider() {
////        List<? super Zak> kzList;
////        zakList = zakBasicRepo.findAllByOrderByRokDescCkontDescCzakDes();
//        zakList = zakBasicRepo.findAll();
////        ValueProvider<KzTreeAware, Collection<KzTreeAware>> kzNodesProvider = KzTreeAware::getNodes;
////        ListDataProvider<Zak> zakProvider = new ListDataProvider(zakList);
////        zakProvider.set
//
////        zakGrid.setDataProvider(zakProvider);
//        zakGrid.setItems(zakList);
//        zakGrid.focus();
//    }



    public void openDialog() {
        selectButton.addClickListener(e -> saveClicked());
        selectButton.setText("Přidat zakázky");
        updateViewContent();
        this.open();
    }

    private void saveClicked() {

//        ConfirmDialog.createInfo()
//                .withCaption("VÝBĚR ZAKÁZEK")
//                .withMessage("Comming soon...")
//        ;
//        return;

        List<ZakBasic> zakBasicItems = new ArrayList<>();
        for(ZakBasic zakb :  zakList) {
            if (((zakb.getTyp() == ItemType.ZAK) || (zakb.getTyp() == ItemType.LEK) || (zakb.getTyp() == ItemType.REZ))
                    && !zakb.getArch() && zakb.isChecked()) {
                zakBasicItems.add(zakb);
            }
        }

        if (CollectionUtils.isEmpty(zakBasicItems)) {
            ConfirmDialog.createInfo()
                    .withCaption("Přidání zakázek")
                    .withMessage("Nejsou vybrány žádné zakázky")
                    .open()
            ;
        } else {
//            inMemoryKzTreeProvider.refreshAll();
            zakBasicSelector.accept(zakBasicItems);
            close();
        }
    }

}
