package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.KzTreeAware;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.Gap;
import eu.japtor.vizman.ui.components.Ribbon;
import eu.japtor.vizman.ui.components.ZakSimpleGrid;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@UIScope    // Without this annotation browser refresh throws exception
//public class KzSelectFormDialog<KzTreeAware> extends Dialog {
public class ZakTreeSelectDialog extends Dialog {

    private static final String SEL_COL_KEY = "zak-sel-col";

    private Grid<ZakBasic> zakGrid;
    private List<ZakBasic> zakList;
    private ComponentRenderer<HtmlComponent, ZakBasic> kzTextRenderer;

    VerticalLayout mainPanel;
    HorizontalLayout buttonBar;
    private Button selectButton;
    private Button cancelButton;


    private Consumer<List<Zak>> zaksSelector;

//    @Autowired
//    public ZakService zakService;

    @Autowired
    public ZakBasicRepo zakBasicRepo;


    public ZakTreeSelectDialog()
    {
//        this.zaksSelector = itemSelector;
//        super(itemSaver);
        this.setWidth("1200px");
        this.setHeight("700px");
//        this.zakService = zakService;
//        setupEventListeners();

        initKzTextRenderer();

        zakList = new ArrayList<>();

        this.add(initView());
        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);
    }

    private Component initDialogButtonBar() {
        buttonBar = new HorizontalLayout();

        cancelButton = new Button("Zpět");
//        cancelButton.setAutofocus(true);
        cancelButton.addClickListener(e -> close());

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

    private ComponentRenderer initKzTextRenderer() {
        kzTextRenderer = new ComponentRenderer<>(kontZak -> {
            Paragraph kzText = new Paragraph(kontZak.getKzText());
            kzText.getStyle().set("color", VzmFormatUtils.getItemTypeColorName(kontZak.getTyp()));
            if (ItemType.KONT != kontZak.getTyp()) {
                kzText.getStyle().set("text-indent", "1em");
            }
            return kzText;
        });
        return kzTextRenderer;
    }

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

//        initSimplePersonTreeGrid();
//        gridContainer.add(personGrid);


//        initNodeTreeGrid();
//        gridContainer.add(treeGrid);

        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(FlexComponent.Alignment.STRETCH);

        gridContainer.add(initKzToolBar());
        zakGrid = new ZakSimpleGrid(true, null, null, false, false,  Boolean.FALSE, null);
        zakGrid.setMultiSort(true);
        zakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
//        gridContainer.add(initKzTreeGrid());
        gridContainer.add(zakGrid);

//        this.add();
//        this.add(gridContainer);
        return gridContainer;
    }

    private Component initKzToolBar() {

//        kzToolBar = new HorizontalLayout();
//        FlexLayout kzToolBar;
        HorizontalLayout kzToolBar = new HorizontalLayout();
        kzToolBar.setSpacing(false);
        kzToolBar.setAlignItems(FlexComponent.Alignment.END);
        kzToolBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//        Paragraph archFilterLabel = new Paragraph("Kontrakty:");
        H3 mainTitle = new H3("KONTRAKTY / ZAKÁZKY");
        mainTitle.getStyle().set("margin-top", "0.2em");
        kzToolBar.add(mainTitle, new Ribbon());
        return kzToolBar;
    }


    private static final String CKZ_COL_KEY = "ckz-col";
    private static final String TEXT_COL_KEY = "text-col";
    private static final String OBJEDNATEL_COL_KEY = "objednatel-col";
    private static final String SKUPINA_COL_KEY = "skupina-col";
    private static final String ROK_COL_KEY = "rok-col";

//    private Component initKzTreeGrid() {
//
//        zakGrid = new TreeGrid<>();
//        zakGrid.getStyle().set("marginTop", "0.5em");
//        zakGrid.setColumnReorderingAllowed(true);
//        zakGrid.setSelectionMode(Grid.SelectionMode.NONE);
//
////        zakGrid.getElement().addEventListener("keypress", e -> {
////            JsonObject eventData = e.getEventData();
////            String enterKey = eventData.getString("event.key");
////            if ("Enter".equals(enterKey)) {
////                new Notification("ENTER pressed - will open form", 1500).open();
////            }
////        })
////                .addEventData("event.key")
////        ;
//
////        DomEventListener gridKeyListener = new DomEventListener() {
////            @Override
////            public void handleEvent(DomEvent domEvent) {
////                JsonObject eventData = domEvent.getEventData();
////                String enterKey = eventData.getString("event.key");
////                if ("Enter".equals(enterKey)) {
////                    new Notification("ENTER pressed - will open form", 1500).open();
////                }
////
////            }
////        };
//
//
////        zakGrid.addColumn(TemplateRenderer.of("[[index]]"))
////                .setHeader("Řádek")
////                .setFlexGrow(0)
////        ;
//        zakGrid.addHierarchyColumn(ckzValProv)
//                .setHeader("ČK/ČZ")
//                .setFlexGrow(0)
//                .setWidth("9em")
//                .setResizable(true)
////                .setFrozen(true)
//                .setKey(CKZ_COL_KEY)
//        ;
//        zakGrid.addColumn(KzTreeAware::getRok)
//                .setHeader("Rok")
//                .setFlexGrow(0)
//                .setWidth("8em")
//                .setResizable(true)
////                .setFrozen(true)
//                .setKey(ROK_COL_KEY)
//        ;
//        zakGrid.addColumn(KzTreeAware::getSkupina).setHeader("Sk.")
//                .setFlexGrow(0)
//                .setWidth("4em")
//                .setResizable(true)
////                .setFrozen(true)
//                .setKey(SKUPINA_COL_KEY)
//        ;
//        zakGrid.addColumn(kzCheckRenderer)
//                .setHeader(("Přidat"))
//                .setFlexGrow(0)
//                .setWidth("5em")
//                .setResizable(true)
//                .setKey(SEL_COL_KEY)
////                .setFrozen(true)
////                .setId("arch-column")
//        ;
//        zakGrid.addColumn(kzTextRenderer)
//                .setHeader("Text")
//                .setFlexGrow(1)
//                .setKey(TEXT_COL_KEY)
//                .setResizable(true)
//        ;
//        zakGrid.addColumn(klientValProv)
//                .setHeader("Objednatel")
//                .setFlexGrow(0)
//                .setWidth("18em")
//                .setKey(OBJEDNATEL_COL_KEY)
//                .setResizable(true)
//        ;
//
//
////        HeaderRow filterRow = zakGrid.appendHeaderRow();
////        TextField textFilterField = new TextField();
////        ValueProvider<KzTreeAware, String> kzTextValueProvider
////                = KzTreeAware::getText;
////        textFilterField.addValueChangeListener(event -> {});
////        textFilterField.setValueChangeMode(ValueChangeMode.EAGER);
////        filterRow.getCell(zakGrid.getColumnByKey(TEXT_COL_KEY)).setComponent(textFilterField);
////        textFilterField.setSizeFull();
////        textFilterField.setPlaceholder("Filtr (rozbitý)");
//
//
////        for (Grid.Column col : zakGrid.getColumns()) {
////            setResizable(col);
////        }
//        zakGrid.getColumnByKey(CKZ_COL_KEY).setSortable(true);
//        zakGrid.getColumnByKey(OBJEDNATEL_COL_KEY).setSortable(true);
//
//        return zakGrid;
//    }


    private ValueProvider<KzTreeAware, String> ckzValProv =
            kz -> kz.getTyp() == ItemType.KONT ? kz.getCkont() : kz.getCzak().toString();

    private ComponentRenderer<Component, KzTreeAware> kzCheckRenderer = new ComponentRenderer<>(kz -> {
//        this.getElement().setAttribute("theme", "small icon secondary");

        Checkbox zakSelectBox = new Checkbox();
//        Icon icoZakArchived = new Icon(VaadinIcon.CHECK);
//        icoZakArchived.setSize("0.8em");
//        icoZakArchived.getStyle()
//                .set("theme", "small icon secondary")
//                .set("padding-left","1em");
//        ;
        zakSelectBox.addValueChangeListener(event -> {
            kz.setChecked(event.getValue());
        });

//        return (ItemType.ZAK == kz.getTyp()) || (ItemType.REZ == kz.getTyp()) || (ItemType.LEK == kz.getTyp()) ?
        if ((ItemType.ZAK == kz.getTyp()) || (ItemType.REZ == kz.getTyp()) || (ItemType.LEK == kz.getTyp())) {
            zakSelectBox.addValueChangeListener(event -> {
                kz.setChecked(event.getValue());
            });
            return zakSelectBox;
        } else {
            return new Span("");
        }
    });

//    private void setResizable(Grid.Column column) {
//        column.setResizable(true);
//        Element parent = column.getElement().getParent();
//        while (parent != null
//                && "vaadin-grid-column-group".equals(parent.getTag())) {
//            parent.setProperty("resizable", "true");
//            parent = parent.getParent();
//        }
//    }

    private void reloadDataProvider() {
//        List<? super Zak> kzList;
//        zakList = zakBasicRepo.findAllByOrderByRokDescCkontDescCzakDes();
        zakList = zakBasicRepo.findAll();
        zakGrid.setItems(zakList);

//        ValueProvider<KzTreeAware, Collection<KzTreeAware>> kzNodesProvider = KzTreeAware::getNodes;
//        ListDataProvider<Zak> zakProvider = new ListDataProvider(zakList);
//        zakProvider.set

//        zakGrid.setDataProvider(zakProvider);
        zakGrid.focus();
    }



    public void openDialog(String dialogTitle, Consumer<List<Zak>> itemSelecto) {
//        this.openInternal(
//                evidKont
//                , operation
//                , dialogTitle
//                , null
//                , null
//        );

//        if (registrationForSave != null) {
//            registrationForSave.remove();
//        }
//        registrationForSave = selectButton.addClickListener(e -> saveClicked());
        selectButton.addClickListener(e -> saveClicked());
        selectButton.setText("Přidat zakázky");
//        selectButton.setEnabled(false);

        reloadDataProvider();
//        inMemoryKzTreeProvider.refreshAll();

        this.open();
    }

    private void saveClicked() {

        ConfirmDialog.createInfo()
                .withCaption("VÝBĚR ZAKÁZEK")
                .withMessage("Comming soon...")
        ;
        return;

//        List<KzTreeAware> kzZakItems = new ArrayList<>();
//        for(KzTreeAware rootItem : inMemoryKzTreeProvider.getTreeData().getRootItems()) {
//            List<KzTreeAware> childItems = inMemoryKzTreeProvider.getTreeData().getChildren(rootItem);
//            for(KzTreeAware child :  childItems) {
//                if (((child.getTyp() == ItemType.ZAK) || (child.getTyp() == ItemType.LEK) || (child.getTyp() == ItemType.REZ))
//                        && !child.getArch() && child.isChecked()) {
//                    kzZakItems.add(child);
//                }
//            }
//        }
//
//        if (CollectionUtils.isEmpty(kzZakItems)) {
//            ConfirmDialog.createInfo()
//                    .withCaption("Přidání zakázek")
//                    .withMessage("Nejsou vybrány žádné zakázky")
//                    .open()
//            ;
//        } else {
////            inMemoryKzTreeProvider.refreshAll();
//            zaksSelector.accept(kzZakItems);
//            close();
//        }
    }

}
