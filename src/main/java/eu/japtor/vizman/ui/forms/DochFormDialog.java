package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import eu.japtor.vizman.backend.entity.DochManual;
import eu.japtor.vizman.ui.components.AbstractSimpleEditorDialog;
import eu.japtor.vizman.ui.components.Operation;

import java.util.function.BiConsumer;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DochFormDialog extends AbstractSimpleEditorDialog<DochManual> {

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
                , initFromTimeField()
                , initToTimeField()
                , initCinnostField()
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
                           boolean timeFromIsEditabele, boolean timeToIsEditable)
    {
        dochDateComponent.setText(dochManual.getdDochDate().toString());
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
        return dochDateComponent;
    }

    private Component initFromTimeField() {
        fromTimeField = new TimePicker("Od");
        getBinder().forField(fromTimeField)
                .asRequired("Čas musí být zadán")
                .withValidator(
                        fromTime -> null == toTimeField.getValue() || fromTime.isBefore(toTimeField.getValue())
                        , "Čas OD musí být dříve než DO"
                )
                .bind(DochManual::getFromTime, DochManual::setToTime)
        ;
        return fromTimeField;
    }

    private Component initToTimeField() {
        toTimeField = new TimePicker("Do");
        getBinder().forField(toTimeField)
                .asRequired("Čas musí být zadán.")
                .withValidator(
                        toTime -> null == fromTimeField.getValue() || toTime.isAfter(toTimeField.getValue())
                        , "Čas DO musí být později než čes DO."
                )
                .bind(DochManual::getFromTime, DochManual::setToTime)
        ;
        return toTimeField;
    }

    private Component initCinnostField() {
        cinnostField = new TextField("Činnost");
        cinnostField.setReadOnly(true);
        getBinder().forField(cinnostField)
                .bind(DochManual::getCinnost, null);
        return cinnostField;
    }

    private Component initPoznamkaField() {
        poznamkaField = new TextField("Poznámka");
        getBinder().forField(poznamkaField)
                .bind(DochManual::getPoznamka, DochManual::setPoznamka);
        return poznamkaField;
    }
}
