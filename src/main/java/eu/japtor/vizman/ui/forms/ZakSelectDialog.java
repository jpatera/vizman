package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.ui.components.*;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//@UIScope    // Without this annotation browser refresh throws exception
public class ZakSelectDialog extends Dialog {

    private List<Long> pruhZakIdList = new ArrayList<>();
    private List<ZakBasic> zakBasicList;
    private ZakBasicGrid zakGrid;

    VerticalLayout mainPanel;
    HorizontalLayout buttonBar;
    private Button addZaksButton;
    private Button cancelButton;

    public ZakBasicRepo zakBasicRepo;
    private Consumer<List<ZakBasic>> itemsAder;


    public ZakSelectDialog(Consumer<List<ZakBasic>> itemAdder, ZakBasicRepo zakBasicRepo) {
//        super(itemSaver);
        this.itemsAder = itemAdder;
        this.zakBasicRepo = zakBasicRepo;

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
        zakGrid = new ZakBasicGrid(true, checkBoxEnabler, selectionChanger, false, Boolean.FALSE);
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
        zakGrid.populateGridDataAndRebuildFilterFields(zakBasicList);
        zakGrid.initFilterValues();
        zakGrid.doFilter();
        zakGrid.setSelCount(0);
        addZaksButton.setEnabled(zakGrid.getSelCount() > 0);
        zakGrid.getDataProvider().refreshAll();
    }

//    private void loadGridDataAndRebuildFilterFields() {
//        zakBasicList = zakBasicRepo.findAllByOrderByRokDescCkontDescCzakDesc();
//        zakGrid.setItems(zakBasicList);
//        zakGrid.setRokFilterItems(zakBasicList.stream()
//                .filter(z -> null != z.getRok())
//                .map(ZakBasic::getRok)
//                .distinct().collect(Collectors.toCollection(LinkedList::new))
//        );
//        zakGrid.setSkupinaFilterItems(zakBasicList.stream()
//                .map(ZakBasic::getSkupina)
//                .filter(s -> null != s)
//                .distinct().collect(Collectors.toCollection(LinkedList::new))
//        );
//        zakGrid.setArchFilterItems(zakBasicList.stream()
//                .map(ZakBasic::getArch)
//                .filter(a -> null != a)
//                .distinct().collect(Collectors.toCollection(LinkedList::new))
//        );
//    }


//    private void reloadDataProvider() {
////        List<? super Zak> kzList;
////        zakBasicList = zakBasicRepo.findAllByOrderByRokDescCkontDescCzakDes();
//        zakBasicList = zakBasicRepo.findAll();
////        ValueProvider<KzTreeAware, Collection<KzTreeAware>> kzNodesProvider = KzTreeAware::getNodes;
////        ListDataProvider<Zak> zakProvider = new ListDataProvider(zakBasicList);
////        zakProvider.set
//
////        zakGrid.setDataProvider(zakProvider);
//        zakGrid.setItems(zakBasicList);
//        zakGrid.focus();
//    }



    public void openDialog(List<Long> pruhZakIdList) {
        this.pruhZakIdList = pruhZakIdList;
        updateViewContent();
        this.open();
    }

    private void addZaksClicked() {
        List<ZakBasic> zakBasicItemsToAdd = new ArrayList<>();
        for(ZakBasic zakb : zakBasicList) {
            if (((zakb.getTyp() == ItemType.ZAK) || (zakb.getTyp() == ItemType.LEK) || (zakb.getTyp() == ItemType.REZ))
                    && !zakb.getArch() && zakb.isChecked()) {
                zakBasicItemsToAdd.add(zakb);
            }
        }

        if (CollectionUtils.isEmpty(zakBasicItemsToAdd)) {
//            Notification.show(String.format("Nejsou vybrányPočet přidaných zakázek: %s", i)
//                    , 2500, Notification.Position.TOP_CENTER);
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
