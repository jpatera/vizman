package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import eu.japtor.vizman.backend.entity.DochManual;
import eu.japtor.vizman.ui.components.AbstractSimpleEditorDialog;
import eu.japtor.vizman.ui.components.Operation;

import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DochFormDialog extends AbstractSimpleEditorDialog<DochManual> {

    private static final DateTimeFormatter longDochDateFormatter = DateTimeFormatter.ofPattern("EEEE, dd. MM. yyyy");

    private Paragraph dochDateComponent;
    private TimePicker fromTimeField;
    private TimePicker toTimeField;
    private TextField cinnostField;
    private TextField poznamkaField;

    public DochFormDialog(BiConsumer<DochManual, Operation> itemSaver)
    {
        super(itemSaver);
        this.setWidth("750px");

        getFormLayout().add(
                initDochDateComponent()
                , initCinnostField()
                , initFromTimeField()
                , initToTimeField()
                , initPoznamkaField()
        );

        setupEventListeners();
    }

    public boolean hasChanges() {
        return getBinder().hasChanges();
//        return getBinder().hasChanges() || itemsEditor.hasChanges();
    }


    public void openDialog(DochManual dochManual, Operation operation,
                           String dialogTitle,
                           boolean fromTimeIsEditable, boolean toTimeIsEditable)
    {
        dochDateComponent.setText(dochManual.getDochDate().format(longDochDateFormatter));
        fromTimeField.setReadOnly(!fromTimeIsEditable);
        toTimeField.setReadOnly(!toTimeIsEditable);
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
//        Emphasis infoText = new Emphasis("Odpovídající projektové a dokumentové adresáře...");
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

    private Component initFromTimeField() {
        fromTimeField = new TimePicker("Od");
        getBinder().forField(fromTimeField)
//                .asRequired("Čas musí být zadán")
                .withValidator(
                        fromTime -> fromTimeField.isReadOnly() ? true : null != fromTime
                        , "Čas musí být zadán\""
                )
                .withValidator(
                        fromTime -> fromTimeField.isReadOnly() ? true : null == toTimeField.getValue() || fromTime.isBefore(toTimeField.getValue())
                        , "Čas OD musí být dříve než DO"
                )
                .bind(DochManual::getFromTime, DochManual::setFromTime)
        ;
        return fromTimeField;
    }

    private Component initToTimeField() {
        toTimeField = new TimePicker("Do");
        getBinder().forField(toTimeField)
//                .asRequired("Čas musí být zadán.")
                .withValidator(
                        fromTime -> toTimeField.isReadOnly() ? true : null != fromTime
                        , "Čas musí být zadán\""
                )
                .withValidator(
                        toTime -> toTimeField.isReadOnly() ? true : null == fromTimeField.getValue() || toTime.isAfter(fromTimeField.getValue())
                        , "Čas DO musí být později než čas OD."
                )
                .bind(DochManual::getToTime, DochManual::setToTime)
        ;
        return toTimeField;
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
