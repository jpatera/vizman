package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Zakn;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.service.ZaknService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.AbstractGridDialog;
import eu.japtor.vizman.ui.components.Ribbon;
import eu.japtor.vizman.ui.views.ZakrListView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ZakNaklGridDialog extends AbstractGridDialog<Zakn> implements HasLogger {

    public static final String DIALOG_WIDTH = "1300px";
    public static final String DIALOG_HEIGHT = "800px";
//    public static final String DIALOG_HEIGHT = null;
    private static final String CLOSE_STR = "Zavřít";

    private HorizontalLayout leftBarPart;
    private HorizontalLayout rightBarPart;
    private Button closeButton;
    private Button zakNaklRepButton;
    private TextField rezieParamField;
    private Label rezieParamLabel;
    private TextField pojistParamField;
    private Label pojistParamLabel;
    private Binder<ZakrListView.ZakrParams> paramsBinder;

    private List<Zakn> currentItemList;
    private Zakr zakr;
    private Label zakInfoField;

    Grid<Zakn> grid;
    private FooterRow sumFooterRow;

    public static final String HODIN_COL_KEY = "zakn-bg-hodin";
    public static final String MZDA_COL_KEY = "zakn-bg-mzda";
    public static final String MZDA_POJ_COL_KEY = "zakn-bg-mzda-poj";

    private ZaknService zaknService;
    private ZakrListView.ZakrParams zakrParams;


    public ZakNaklGridDialog(ZaknService zaknService) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setItemNames(ItemType.UNKNOWN);
        getMainTitle().setText("NÁKLADY NA ZAKÁZKU");

        this.zaknService = zaknService;
        this.paramsBinder = new Binder<>();

        getGridInfoBox().add(
                initZakInfoField()
        );

        getGridContainer().add(
                buildGridBar()
                , initGrid()
        );
    }

    private Component initZakInfoField() {
        zakInfoField = new Label();
        zakInfoField.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em");
        return zakInfoField;
    }

    //    public void openDialog(LinkedList<PersonWage> personWages) {
    public void openDialog(Zakr zakr, ZakrListView.ZakrParams zakrParams) {

//        this.origItemList = Collections.unmodifiableList(new LinkedList<>(currentItemList));

//        this.itemsChanged = false;
        this.zakrParams = zakrParams;
        this.zakr = zakr;
        paramsBinder.setBean(zakrParams);
//        paramsBinder.addValueChangeListener(event -> calcButton.setIconDirty());
        initDataAndControls();
        this.open();
    }

    private void closeDialog() {
        this.close();
    }


    // Title for grid bar
    // -------------------------------------------------------
    private Component buildTitleComponent() {
        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
//        titleComponent.setAlignItems(FlexComponent.Alignment.CENTER);
        titleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        titleComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        titleComponent.add(
//                new GridTitle(ItemNames.getNomP(ItemType.ZAKR))
                initTitle()
                , new Ribbon()
//                , new ReloadButton(event -> {
//                    reloadViewContentPreserveFilters();
//                })
        );
        return titleComponent;
    }

    private Component buildGridBarControlsComponent() {
        HorizontalLayout controlsComponent = new HorizontalLayout();
        controlsComponent.setMargin(false);
        controlsComponent.setPadding(false);
        controlsComponent.setSpacing(false);
        controlsComponent.setAlignItems(FlexComponent.Alignment.CENTER);
        controlsComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        controlsComponent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        controlsComponent.add(
                initRezieLabel()
                , initRezieField()
                , new Ribbon("3em")
                , initPojistLabel()
                , initPojistField()
        );
        return controlsComponent;
    }

    private Component initRezieField() {
//        rezieParamField = new TextField("Koef. režie");
        rezieParamField = new TextField();
        rezieParamField.setWidth("5em");
        rezieParamField.setReadOnly(true);
        rezieParamField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        rezieParamField.setValueChangeMode(ValueChangeMode.EAGER);
        paramsBinder.forField(rezieParamField)
                .asRequired("Koeficient režie musí být zadán")
                .withConverter(VzmFormatUtils.bigDecimalPercent2Converter)
                .bind(ZakrListView.ZakrParams::getKoefRezie, ZakrListView.ZakrParams::setKoefRezie)
        ;
        return rezieParamField;
    }

    private Component initRezieLabel() {
        rezieParamLabel = new Label("Koef. režie");
        rezieParamLabel.setWidth("5em");
//        rezieParamLabel.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        return rezieParamLabel;
    }

    private Component initPojistField() {
//        pojistParamField = new TextField("Koef. poj.");
        pojistParamField = new TextField();
        pojistParamField.setWidth("5em");
        pojistParamField.setReadOnly(true);
        pojistParamField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        pojistParamField.setValueChangeMode(ValueChangeMode.EAGER);
        paramsBinder.forField(pojistParamField)
                .asRequired("Koeficient pojištění musí být zadán")
                .withConverter(VzmFormatUtils.bigDecimalPercent2Converter)
                .bind(ZakrListView.ZakrParams::getKoefPojist, ZakrListView.ZakrParams::setKoefPojist)
        ;
        return pojistParamField;
    }

    private Component initPojistLabel() {
        pojistParamLabel = new Label("Koef. poj.");
        pojistParamLabel.setWidth("5em");
//        rezieParamLabel.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        return pojistParamLabel;
    }

    private Component initTitle() {
        H4 zakTitle = new H4();
//        zakTitle.setText(ItemNames.getNomP(ItemType.WAGE));
        zakTitle.setText("NÁKLADŮ");
        zakTitle.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-right", "1em");
        return zakTitle;
    }
    // -------------------------------------------------------



    private Component initZakNaklRepButton() {
        zakNaklRepButton = new Button("Report"
                , event -> {
            openZakNaklRepDialog();
        });
//        this.addClassName("view-toolbar__button");
        zakNaklRepButton.getElement().setAttribute("theme", "small secondary");
        return zakNaklRepButton;
    }

    private void openZakNaklRepDialog() {
//        zakrParams.setRokZak(zakrGrid.getRokFilterValue());
//        zakrParams.setSkupina(zakrGrid.getSkupinaFilterValue());
        ReportZakNaklDialog repZakNaklDialog  = new ReportZakNaklDialog(zaknService, zakr, zakrParams);
        repZakNaklDialog.openDialog();
        repZakNaklDialog.generateAndShowReport();
    }


    private void initDataAndControls() {
        deactivateListeners();

        zakInfoField.setText(zakr.getCkzTextFull());

        this.currentItemList = zaknService.fetchByZakId(zakr.getId(), zakrParams);
        grid.setItems(currentItemList);
        updateFooterFields();
        initControlsOperability();

        activateListeners();
    }


//    private void refreshHeaderMiddleBox(Zak zakItem) {
//    // Do nothing
//    }

    private void deactivateListeners() {
//        if (null != binderChangeListener) {
//            try {
//                binderChangeListener.remove();
//            } catch (Exception e)  {
//                // do nothing
//            }
//        }
    }

    private void activateListeners() {
    }


    private void initControlsOperability() {
        closeButton.setEnabled(true);
    }

    private void adjustControlsOperability(final boolean hasChanges) {
        closeButton.setEnabled(true);
    }


    private Component buildGridBar() {
        HorizontalLayout gridBar = new HorizontalLayout();
        gridBar.setSpacing(false);
        gridBar.setPadding(false);
        gridBar.getStyle().set("marginTop", "0.2em");
//        gridBar.getStyle().set("margin-left", "-3em");
        gridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        gridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        gridBar.add(
//                buildTitleComponent()
//                , new Ribbon()
                buildGridBarControlsComponent()
                , new Ribbon()
                , initZakNaklRepButton()
        );
        return gridBar;
    }

    private Component initGrid() {
        grid = new Grid<>();
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setId("person-wage-grid");
        grid.setClassName("vizman-simple-grid");
        grid.getStyle().set("marginTop", "0.5em");

        grid.addColumn(zakn -> zakn.getPerson().getPrijmeni())
                .setHeader("Příjmení")
                .setResizable(true)
                .setWidth("10em")
                .setFlexGrow(0)
        ;
        grid.addColumn(Zakn::getDatePruh)
                .setHeader("Datum")
                .setFlexGrow(0)
        ;
//        grid.addColumn(Zakn::getYmPruh)
//                .setHeader("Rok-měs")
//                .setFlexGrow(0)
//        ;
        grid.addColumn(Zakn::getWorkPruh)
                .setHeader("Hodin")
                .setWidth("6em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setKey(HODIN_COL_KEY)
                .setResizable(true)
        ;
        grid.addColumn(zn -> {
                    BigDecimal mzda = zn.getNaklMzda();
                    return null == mzda ? "" : VzmFormatUtils.moneyNoFractFormat.format(mzda);
                })
                .setHeader("Mzda")
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setKey(MZDA_COL_KEY)
                .setResizable(true)
        ;
        grid.addColumn(zn -> {
                    BigDecimal mzdaPojist = zn.getNaklMzdaPojist();
                    return null == mzdaPojist ? "" : VzmFormatUtils.moneyNoFractFormat.format(mzdaPojist);
                })
                .setHeader("Mzda+Poj.")
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setKey(MZDA_POJ_COL_KEY)
                .setResizable(true)
        ;
        grid.addColumn(Zakn::getSazba)
                .setHeader("Sazba")
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setResizable(true)
        ;

        sumFooterRow = grid.appendFooterRow();
        updateFooterFields();

        return grid;
    }

    private void updateFooterFields() {
        updateHodinSumField();
        updateMzdaSumField();
        updateMzdaPojistSumField();
    }

//    private void updateRecCountField() {
//        sumFooterRow
//                .getCell(this.getColumnByKey(KZCISLO_COL_KEY))
//                .setText("Počet: " + (calcRecCount()));
//    }

    private void updateHodinSumField() {
        sumFooterRow
                .getCell(grid.getColumnByKey(HODIN_COL_KEY))
                .setText("" + (VzmFormatUtils.decHodFormat.format(calcHodinSum())));
    }

    private void updateMzdaSumField() {
        sumFooterRow
                .getCell(grid.getColumnByKey(MZDA_COL_KEY))
                .setText("" + (VzmFormatUtils.moneyNoFractFormat.format(calcMzdaSum())));
    }

    private void updateMzdaPojistSumField() {
        sumFooterRow
                .getCell(grid.getColumnByKey(MZDA_POJ_COL_KEY))
                .setText("" + (VzmFormatUtils.moneyNoFractFormat.format(calcMzdaPojistSum())));
    }

    private BigDecimal calcHodinSum() {
        return grid.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .filter(zn -> zn.getWorkPruh() != null)
//                .map(zakr -> EUR == zakr.getMena() ? zakr.getRpZbyva().multiply(zakrParams.getKurzEur()) : zakr.getRpZbyva())
                .map(zn -> zn.getWorkPruh())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcMzdaSum() {
        return grid.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .filter(zn -> zn.getNaklMzda() != null)
//                .map(zakr -> EUR == zakr.getMena() ? zakr.getRpZbyva().multiply(zakrParams.getKurzEur()) : zakr.getRpZbyva())
                .map(zn -> zn.getNaklMzda())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcMzdaPojistSum() {
        return grid.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .filter(zn -> zn.getNaklMzdaPojist() != null)
                .map(zn -> zn.getNaklMzdaPojist())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

// ------------------------------------------------------------

    @Override
    public Component initDialogButtonBar() {
        HorizontalLayout bar = new HorizontalLayout();

        closeButton = new Button(CLOSE_STR);
        closeButton.setAutofocus(true);
        closeButton.getElement().setAttribute("theme", "primary");
        closeButton.addClickListener(e -> closeClicked());

        leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);

        rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(
                closeButton
//                , revertAndCloseButton
        );

        bar.setClassName("buttons");
        bar.setSpacing(false);
        bar.setPadding(false);
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        bar.add(
                leftBarPart
                , rightBarPart
        );
        return bar;
    }

    private void closeClicked() {
        closeDialog();
    }
//  --------------------------------------------

    @Override
    public List<Zakn> getCurrentItemList() {
        return currentItemList;
    }
}
