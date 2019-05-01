package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import eu.japtor.vizman.backend.entity.DochManual;
import eu.japtor.vizman.ui.components.AbstractSimpleEditorDialog;
import eu.japtor.vizman.ui.components.Operation;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DochFormDialog extends AbstractSimpleEditorDialog<DochManual> {

    private static final DateTimeFormatter longDochDateFormatter = DateTimeFormatter.ofPattern("EEEE, dd. MM. yyyy");

    private Paragraph dochDateComponent;
    private TimePicker fromTimePicker;
    private TimePicker toTimePicker;
    private TextField cinnostField;
    private TextField poznamkaField;

    private boolean canStampFutureTime;

    public DochFormDialog(BiConsumer<DochManual, Operation> itemSaver)
    {
        super(itemSaver);
        this.setWidth("750px");

        getFormLayout().add(
                initDochDateComponent()
                , initCinnostField()
                , initFromTimePicker()
                , initToTimePicker()
                , initPoznamkaField()
        );

        setupEventListeners();
    }

    public boolean hasChanges() {
        return getBinder().hasChanges();
//        return getBinder().hasChanges() || itemsEditor.hasChanges();
    }


    public void openDialog(DochManual dochManual, Operation operation, String dialogTitle,
                           boolean fromTimeIsEditable, boolean toTimeIsEditable)
    {
        dochDateComponent.setText(dochManual.getDochDate().format(longDochDateFormatter));
        canStampFutureTime = dochManual.getDochDate().compareTo(LocalDate.now()) != 0;

        // TODO: Because of a bug  we must always rebuild selection fields:
        getBinder().removeBinding(fromTimePicker);
        getFormLayout().remove(fromTimePicker);
        getFormLayout().addComponentAtIndex(1, initFromTimePicker());
        getBinder().removeBinding(toTimePicker);
        getFormLayout().remove(toTimePicker);
        getFormLayout().addComponentAtIndex(2, initToTimePicker());

        fromTimePicker.setReadOnly(!fromTimeIsEditable);
        toTimePicker.setReadOnly(!toTimeIsEditable);
        if (toTimeIsEditable) {
            toTimePicker.focus();
        }
        if (fromTimeIsEditable) {
            fromTimePicker.focus();
        }
        this.openInternal(
                dochManual
                , operation
                , dialogTitle
                , null
                , null
        );
    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    @Override
    protected void openSpecific() {
    }

    private Paragraph initDochDateComponent() {
        dochDateComponent = new Paragraph("Datum docházky...");
        dochDateComponent.getElement().setAttribute("colspan", "2");
        dochDateComponent.getStyle()
                .set("font-size","1.3em")
                .set("font-weight","bold")
//                .set("font-family", "monospace")
//                .set("font-family", "ariel")
//                .set("font-variant-numeric", "tabular-nums")
        ;
        return dochDateComponent;
    }

    private Component initFromTimePicker() {
        fromTimePicker = new TimePicker("Od");
        fromTimePicker.setStep(Duration.ofSeconds(300));
        getBinder().forField(fromTimePicker)
//                .asRequired("Čas musí být zadán")
                .withValidator(
                        fromTime -> fromTimePicker.isReadOnly() ? true : null != fromTime
                        , "Čas musí být zadán"
                )
                .withValidator(
                        fromTime -> fromTimePicker.isReadOnly() ? true : (canStampFutureTime) || (fromTime.compareTo(LocalTime.now()) < 0)
                        , "Nelze zadávat budoucí čas"
                )
                .withValidator(
                        fromTime -> fromTimePicker.isReadOnly() ? true : null == toTimePicker.getValue() || fromTime.isBefore(toTimePicker.getValue())
                        , "Čas OD musí být dříve než DO"
                )
                .bind(DochManual::getFromTime, DochManual::setFromTime)
        ;
        return fromTimePicker;
    }

    private Component initToTimePicker() {
        toTimePicker = new TimePicker("Do");
        toTimePicker.setStep(Duration.ofSeconds(300));
        getBinder().forField(toTimePicker)
//                .asRequired("Čas musí být zadán.")
                .withValidator(
                        toTime -> toTimePicker.isReadOnly() ? true : null != toTime
                        , "Čas musí být zadán"
                )
                .withValidator(
                        toTime -> toTimePicker.isReadOnly() ? true : (canStampFutureTime) || (toTime.compareTo(LocalTime.now()) < 0)
                        , "Nelze zadávat budoucí čas"
                )
                .withValidator(
                        toTime -> toTimePicker.isReadOnly() ? true : (null == fromTimePicker.getValue()) || toTime.isAfter(fromTimePicker.getValue())
                        , "Čas DO musí být později než čas OD."
                )
                .bind(DochManual::getToTime, DochManual::setToTime)
        ;
        return toTimePicker;
    }

    private Component initCinnostField() {
        cinnostField = new TextField("Činnost");
        cinnostField.getElement().setAttribute("colspan", "2");
        cinnostField.setReadOnly(true);
        getBinder().forField(cinnostField)
                .bind(DochManual::getCinnost, null);
        return cinnostField;
    }

    private Component initPoznamkaField() {
        poznamkaField = new TextField("Poznámka");
        poznamkaField.getElement().setAttribute("colspan", "2");
        getBinder().forField(poznamkaField)
                .bind(DochManual::getPoznamka, DochManual::setPoznamka);
        return poznamkaField;
    }
}
