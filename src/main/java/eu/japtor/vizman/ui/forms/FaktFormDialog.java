package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.FaktService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.AbstractEditorDialog;
import eu.japtor.vizman.ui.components.Gap;
import eu.japtor.vizman.ui.components.OkDialog;
import eu.japtor.vizman.ui.components.Operation;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FaktFormDialog extends AbstractEditorDialog<Fakt> {

    private TextField zakEvidField;
    private TextField cfaktField;
    private TextField zakHonorarField;
    private TextField plneniField;
    private TextField zakladField;
    private TextField castkaField;
    private DatePicker dateDuzpField;
    private DatePicker dateFakturovanoField;
    private TextField dateTimeExportField;
    private TextField textField;

    private Button fakturovatButton;
    private Button stornoButton;

//    @Autowired
    private FaktService faktService;
    private Zak parentZak;


    public FaktFormDialog(BiConsumer<Fakt, Operation> itemSaver,
                          Consumer<Fakt> itemDeleter,
                          FaktService faktService
    ){
        super("900px", null, false, false, itemSaver, itemDeleter);

//        this.getElement().getStyle().set("padding", "0");
//        this.getElement().getStyle().set("margin", "0");

        this.getDialogLeftBarPart().add(initFakturovatButton(), initStornoButton());
        this.faktService = faktService;

        this.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                if (Operation.FAKTUROVAT == currentOperation) {
    //                    && canFakturovat(getCurrentItem())) {
                    fakturovatButton.click();
                } else if (Operation.STORNO == currentOperation) {
                    stornoButton.click();
                }
            }
        });

        getFormLayout().add(
                initZakEvidField()
                , initZakHonorarField()
                , initCfaktField()
                , initPlneniField()
                , initDateDuzpField()
                , initTextField()
//                , initDevider()
//                , initDevider()
                , initCastkaField()
                , initZakladField()
                , initDateFakturovanoField()
                , initDateTimeExportField()
        );
    }


    public void openDialog(
            Fakt fakt, Zak parentZak, Operation operation,
            String titleItemNameText, Component gap, String titleEndText
    ){
        // Mandatory, should be first
        setItemNames(fakt.getTyp());
//        Fakt faktModif = fakt;

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
        dateFakturovanoField.setLocale(new Locale("cs", "CZ"));
        dateDuzpField.setLocale(new Locale("cs", "CZ"));
        castkaField.setSuffixComponent(new Span(fakt.getMena().name()));
        zakladField.setSuffixComponent(new Span(fakt.getMena().name()));


//        this.faktOrig = fakt;
//        this.faktGrid.setItems(zak.getFakts());
//        this.docGrid.setItems(zak.getZakDocs());

        this.parentZak = parentZak;

//        boolean isFaktrurovano = null != fakt.getCastka() && fakt.getCastka().compareTo(BigDecimal.ZERO) > 0;
        if (Operation.EDIT == operation) {
            activateControls(fakt.isFakturovano());
        } else if (Operation.ADD == operation) {
            activateControls(false);
        } else if (Operation.FAKTUROVAT == operation) {
            activateControls(false);
//            dateFakturovanoField.setReadOnly(true);
        } else if (Operation.STORNO == operation) {
            activateControls(true);
//            dateFakturovanoField.setReadOnly(true);
        } else {
            close();
        }

//        if (Operation.FAKTUROVAT == operation) {
////            faktModif.setZaklad(fakt.getZakHonorar());
////            faktModif.setCastka(fakt.getZakHonorar().multiply(fakt.getPlneni().divide(BigDecimal.valueOf(100))));
////            faktModif.setDateVystav(LocalDate.now());
//            zakladField.setValue(fakt.getZakHonorar().toString());
//            castkaField.setValue(fakt.getZakHonorar().multiply(fakt.getPlneni().divide(BigDecimal.valueOf(100))).toString());
//            dateFakturovanoField.setValue(LocalDate.now());
//            dateFakturovanoField.setReadOnly(false);
//        } else {
//            dateFakturovanoField.setReadOnly(true);
//        }

        openInternal(fakt, operation, titleItemNameText, gap, titleEndText);
    }

//    private boolean isFakturovano(final Fakt fakt) {
//        return null != fakt.getCastka() && fakt.getCastka().compareTo(BigDecimal.ZERO) > 0;
//    }

    private boolean canFakturovat(final Fakt fakt) {
//        return (null != fakt.getDateDuzp()
//                && (null != fakt.getPlneni() && fakt.getPlneni().compareTo(BigDecimal.ZERO) > 0)
//                && StringUtils.isNotBlank(fakt.getText()));
        return (null != dateDuzpField.getValue()
                && StringUtils.isNotBlank(plneniField.getValue())
                && StringUtils.isNotBlank(textField.getValue()));
    }

    private void activateControls(boolean isFakturovano) {
        textField.setReadOnly(isFakturovano);
        plneniField.setReadOnly(isFakturovano);
        dateDuzpField.setReadOnly(isFakturovano);
        dateFakturovanoField.setReadOnly(!isFakturovano);
        fakturovatButton.setEnabled(!isFakturovano);
        stornoButton.setEnabled(isFakturovano);
//        getSaveButton().setEnabled(!isFakturovano);
    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {
        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        dateDuzpField.setLocale(new Locale("cs", "CZ"));
//        dateDuzpField.setLocale(new Locale("cs", "CZ"));

//        castkaField.setSuffixComponent(new Span(getCurrentItem().getMena().name()));
    }

    private Component initFakturovatButton() {
        fakturovatButton = new Button("Fakturovat");
        fakturovatButton.addClickListener(event -> {

            if (!canFakturovat(getCurrentItem())) {
//            if (null == dateDuzpField.getValue() || StringUtils.isBlank(plneniField.getValue())
//                     || StringUtils.isBlank(textField.getValue())) {
                new OkDialog().open("Nelze fakturovat", "Některé položky předpisu fakturace nejsou zadány.", "");
                return;
            }

//            zakladField.setValue(VzmFormatUtils.moneyFormat.format(getCurrentItem().getZakHonorar()));
//            castkaField.setValue(VzmFormatUtils.moneyFormat.format(getCurrentItem().getZakHonorar()
//                    .multiply(getCurrentItem().getPlneni().divide(BigDecimal.valueOf(100)))));
//            dateFakturovanoField.setValue(LocalDate.now());

            boolean isValid = getBinder().writeBeanIfValid(getCurrentItem());
            if (isValid) {
                getCurrentItem().setZaklad(getCurrentItem().getZakHonorar());
                getCurrentItem().setCastka(getCurrentItem().getZakHonorar()
                        .multiply(getCurrentItem().getPlneni().divide(BigDecimal.valueOf(100))));
                getCurrentItem().setDateVystav(LocalDate.now());
                getBinder().readBean(getCurrentItem());
                activateControls(true);
                dateFakturovanoField.setReadOnly(false);
                getSaveButton().setEnabled(true);
            }
        });
        return fakturovatButton;
    }

    private Component initStornoButton() {
        stornoButton = new Button("Storno fakturace");
        stornoButton.addClickListener(event -> {

//            zakladField.clear();
//            castkaField.clear();
//            dateFakturovanoField.setValue(null);

            getCurrentItem().setZaklad(null);
            getCurrentItem().setCastka(null);
            getCurrentItem().setDateVystav(null);
            getBinder().readBean(getCurrentItem());

//            dateFakturovanoField.setReadOnly(true);
            activateControls(false);
            getSaveButton().setEnabled(true);
        });
        return stornoButton;
    }


    private Component initZakEvidField() {
        zakEvidField = new TextField("Ze zakázky");
        zakEvidField.getStyle()
                .set("padding-top", "0em");
        zakEvidField.setReadOnly(true);
        zakEvidField.getElement().setAttribute("colspan", "2");
        getBinder().forField(zakEvidField)
                .bind(Fakt::getZakEvid, null);
        return zakEvidField;
    }

    private Component initCfaktField() {
        cfaktField = new TextField("Číslo fakturace");
        cfaktField.setReadOnly(true);
        cfaktField.setWidth("8em");
        getBinder().forField(cfaktField)
                .withConverter(new StringToIntegerConverter("Neplatný formát čísla"))
                .bind(Fakt::getCfakt, null);
        return cfaktField;
    }

    private Component initZakHonorarField() {
        zakHonorarField = new TextField("Honorář zakázky");
        zakHonorarField.setReadOnly(true);
        zakHonorarField.setWidth("8em");
        getBinder().forField(zakHonorarField)
                .withNullRepresentation("")
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Fakt::getZakHonorar, null);
        return zakHonorarField;
    }

    private Component initPlneniField() {
        plneniField = new TextField("Plnění [%]"); // = new TextField("Jméno");
        plneniField.setPattern("^100(\\.(0{0,2})?)?$|^\\d{1,2}(\\.(\\d{0,2}))?$");
//        plneniField.setPreventInvalidInput(true);
        plneniField.setSuffixComponent(new Span("[%]"));
        plneniField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        getBinder().forField(plneniField)
                .withNullRepresentation("")
                .withConverter(VzmFormatUtils.bigDecimalPercentConverter)
                .bind(Fakt::getPlneni, Fakt::setPlneni);
        plneniField.setValueChangeMode(ValueChangeMode.EAGER);
        return plneniField;
    }


    private Component initDateDuzpField() {
        dateDuzpField = new DatePicker("DUZP");
        getBinder().forField(dateDuzpField)
//                .withConverter(String::trim, String::trim)
                .bind(Fakt::getDateDuzp, Fakt::setDateDuzp);
        return dateDuzpField;
    }

    private Component initTextField() {
        textField = new TextField("Text fakturace");
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
                .bind(Fakt::getText, Fakt::setText);
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        return textField;
    }

    private Component initDevider() {
        HtmlComponent gap = new Gap("1em");
//        gap.getElement().setAttribute("colspan", "2");
        return gap;
//        Hr hr = new Hr();
//        hr.setTitle("Fakturace");
//        hr.getElement().setAttribute("colspan", "2");
//        return hr;
    }

    private Component initCastkaField() {
        castkaField = new TextField("Fakturovaná částka");
        castkaField.setReadOnly(true);
//        castkaField.setSuffixComponent(new Span(faktMena.name()));
        getBinder().forField(castkaField)
                .withNullRepresentation("")
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Fakt::getCastka, Fakt::setCastka);
        return castkaField;
    }

    private Component initZakladField() {
        zakladField = new TextField("Ze základu");
        zakladField.setReadOnly(true);
//        castkaField.setSuffixComponent(new Span(faktMena.name()));
        getBinder().forField(zakladField)
                .withNullRepresentation("")
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Fakt::getZaklad, Fakt::setZaklad);
        return zakladField;
    }

    private Component initDateFakturovanoField() {
        dateFakturovanoField = new DatePicker("Fakturováno");
//        dateFakturovanoField.setReadOnly(true);
        getBinder().forField(dateFakturovanoField)
//                .withConverter(String::trim, String::trim)
                .bind(Fakt::getDateVystav, Fakt::setDateVystav);
        return dateFakturovanoField;
    }

//    private Component initDateFakturovanoField() {
//        dateFakturovanoField = new DatePicker("Vystaveno");
//        getBinder().forField(dateFakturovanoField)
////                .withConverter(String::trim, String::trim)
//                .bind(Fakt::getDateVystav, Fakt::setDateVystav);
//        return dateFakturovanoField;
//    }

    private Component initDateTimeExportField() {
//        dateTimeExportField.setValue(getCurrentItem().getDateTimeExport().toString());
        dateTimeExportField = new TextField("Exportováno");
        dateTimeExportField.setReadOnly(true);
        getBinder().forField(dateTimeExportField)
//                .withConverter(new LocalDateToDateConverter())
                .bind(Fakt::getDateTimeExportStr, null);
        return dateTimeExportField;
    }

    @Override
    protected void confirmDelete() {
        openConfirmDeleteDialog("Zrušení fakturačního záznamu"
                , "Opravdu zrušit fakturační záznam?"
                , ""
        );
    }
}
