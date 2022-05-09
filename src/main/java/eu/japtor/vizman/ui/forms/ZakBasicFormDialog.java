package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.OperationResult;
import org.claspina.confirmdialog.ConfirmDialog;


public class ZakBasicFormDialog extends AbstractSimpleFormDialog<ZakBasic> {

    private final static String REVERT_AND_CLOSE_STR = "Zpět";
    private final static String SAVE_AND_CLOSE_STR = "Uložit a zavřít";

    private Button saveAndCloseButton;
    private Button revertAndCloseButton;

    private HorizontalLayout leftBarPart;

    private TextField zakInfoField;
    private Checkbox archField;
    private Checkbox digiField;

    private boolean readonly;
    private ZakBasicService zakBasicService;

    private Binder<ZakBasic> binder = new Binder<>();
    private Registration binderChangeListener = null;

    private Operation currentOperation;
    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;


    public ZakBasicFormDialog(ZakBasicService zakBasicService) {
        super("600px", null);
        setItemNames(ItemType.ZAK);

        this.zakBasicService = zakBasicService;

        this.getFormLayout().setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("300", 2, FormLayout.ResponsiveStep.LabelsPosition.ASIDE)
        );

        getFormLayout().add(
                initZakInfoField()
        );
        zakInfoField.getElement().setAttribute("colspan", "2");

        getFormLayout().add (
                initArchField()
                , new Span("")
        );

        getFormLayout().add (
                initDigiField()
                , new Span("")
        );
    }

    public void openDialog(boolean readonly, ZakBasic zakBasic, Operation operation) {
        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        someDateField.setLocale(new Locale("cs", "CZ"));

        this.readonly = readonly;
        this.currentOperation = operation;
        setCurrentItem(zakBasic);
        initDataAndControls(getCurrentItem(), currentOperation);
        this.open();
    }

    public OperationResult getLastOperationResult()  {
        return lastOperationResult;
    }

    private TextField initZakInfoField() {
        zakInfoField = new TextField();
        binder.forField(zakInfoField)
                .bind(ZakBasic::getCkzTextFull, null);
        return zakInfoField;
    }

    private Checkbox initArchField() {
        archField = new Checkbox("Arch");
        binder.forField(archField)
                .bind(ZakBasic::getArch, ZakBasic::setArch);
        return archField;
    }

    private Checkbox initDigiField() {
        digiField = new Checkbox("Digi");
        binder.forField(digiField)
                .bind(ZakBasic::getDigi, ZakBasic::setDigi);
        return digiField;
    }

    @Override
    public Component initDialogButtonBar() {
        HorizontalLayout bar = new HorizontalLayout();

        saveAndCloseButton = new Button(SAVE_AND_CLOSE_STR);
        saveAndCloseButton.setAutofocus(true);
        saveAndCloseButton.getElement().setAttribute("theme", "primary");
        saveAndCloseButton.addClickListener(e -> saveClicked(true));

        revertAndCloseButton = new Button(REVERT_AND_CLOSE_STR);
        revertAndCloseButton.addClickListener(e -> revertClicked(true));

        leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);
//        leftBarPart.add(
//                revertButton
//                , deleteAndCloseButton
//        );

        HorizontalLayout rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(
                saveAndCloseButton
                , revertAndCloseButton
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


    @Override
    public Operation getCurrentOperation() {
        return currentOperation;
    }

// -----------------------------------------------------

    private void initDataAndControls(final ZakBasic item, final Operation operation) {

        deactivateListeners();

        binder.removeBean();
        binder.readBean(item);

//        refreshHeaderMiddleBox(item);
//        getHeaderEndBox().setText(getHeaderEndComponentValue(null));

        initControlsForItemAndOperation(item, operation);
        initControlsOperability();

        activateListeners();
    }

    private void initControlsForItemAndOperation(final ZakBasic item, final Operation operation) {
        getMainTitle().setText(getDialogTitle(operation, itemGender));
        getHeaderDevider().getStyle().set(
                "background-color", VzmFormatUtils.getItemTypeColorBrighter((item).getTyp()));
    }

    private void initControlsOperability() {
        saveAndCloseButton.setEnabled(false);
        revertAndCloseButton.setEnabled(true);

        archField.setReadOnly(readonly);
        digiField.setReadOnly(readonly);
    }

    private void adjustControlsOperability(final boolean hasChanges, final boolean isValid) {
        saveAndCloseButton.setEnabled(!readonly && hasChanges && isValid);
    }

    private void saveClicked(boolean closeAfterSave) {
        if (!isItemValid()) {
            return;
        }
        try {
            binder.writeBeanIfValid(getCurrentItem());
            saveItem(getCurrentItem());
            binder.readBean(getCurrentItem());
            if (closeAfterSave) {
                closeDialog();
            }
        } catch (VzmServiceException e) {
            showSaveErrMessage();
        }
    }

    private void revertClicked(boolean closeAfterRevert) {
        revertFormChanges();
        if (closeAfterRevert) {
            closeDialog();
        } else {
            initControlsOperability();
        }
    }

    private boolean isItemValid() {
        return true;
    }

    private ZakBasic saveItem(ZakBasic itemToSave) {
        try {
            setCurrentItem(zakBasicService.saveZakBasic(itemToSave, currentOperation));
            lastOperationResult = OperationResult.ITEM_SAVED;
            return getCurrentItem();
        } catch(VzmServiceException e) {
            lastOperationResult = OperationResult.NO_CHANGE;
            throw(e);
        }
    }


    private void showSaveErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Archivace/Digi")
                .withMessage("Nepodařilo se uložit změny.")
                .open();
    }

    private void revertFormChanges() {
        getBinder().removeBean();
        getBinder().readBean(getCurrentItem());
    }

    protected final Binder<ZakBasic> getBinder() {
        return binder;
    }


    private void closeDialog() {
        this.close();
    }

    private void deactivateListeners() {
        if (null != binderChangeListener) {
            try {
                binderChangeListener.remove();
            } catch (Exception e)  {
                // do nothing
            }
        }
    }

    private void activateListeners() {
        binderChangeListener = binder.addValueChangeListener(e -> {
            adjustControlsOperability(true, binder.isValid());
        });
    }

}