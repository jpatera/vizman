package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import eu.japtor.vizman.backend.bean.EvidKont;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.utils.VmFileUtils;
import eu.japtor.vizman.ui.components.AbstractEditorDialog;
import eu.japtor.vizman.ui.components.AbstractSimpleEditorDialog;

import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontEvidFormDialog extends AbstractSimpleEditorDialog<EvidKont> {

    private TextField ckontField;
    private TextField textField;
    private TextField folderField;

    AbstractEditorDialog.Operation operation;
    private KontService kontService;


    public KontEvidFormDialog(Consumer<EvidKont> itemSaver, KontService kontService) {

        super(itemSaver);
        this.setWidth("750px");
        this.kontService = kontService;
        this.operation = operation;

        getFormLayout().add(initCkontField());
        getFormLayout().add(initTextField());
        getFormLayout().add(initFolderField());
        getFormLayout().add(initInfoField());

        setupEventListeners();
    }

//    public boolean hasChanges() {
//        return binder.hasChanges() || itemsEditor.hasChanges();
//}
//
//    public void clear() {
//        orderDetails.setDirty(false);
//        orderEditor.clear();
//    }
//
//    void setDialogElementsVisibility(boolean editing) {
//        dialog.add(editing ? orderEditor : orderDetails);
//        orderEditor.setVisible(editing);
//        orderDetails.setVisible(!editing);
//    }

//    public void setupEventListeners() {
////        getGrid().addSelectionListener(e -> {
////            e.getFirstSelectedItem().ifPresent(entity -> {
////                navigateToEntity(entity.getId().toString());
////                getGrid().deselectAll();
////            });
////        });
////
////        getForm().getButtons().addSaveListener(e -> getPresenter().save());
////        getForm().getButtons().addCancelListener(e -> getPresenter().cancel());
////
////        getDialog().getElement().addEventListener("opened-changed", e -> {
////            if (!getDialog().isOpened()) {
////                getPresenter().cancel();
////            }
////        });
////
////        getForm().getButtons().addDeleteListener(e -> getPresenter().delete());
////
////        getSearchBar().addActionClickListener(e -> getPresenter().createNew());
////        getSearchBar()
////                .addFilterChangeListener(e -> getPresenter().filter(getSearchBar().getFilter()));
////
////        getSearchBar().setActionText("New " + entityName);
////        getBinder().addValueChangeListener(e -> getPresenter().onValueChange(isDirty()));
//
//        getBinder().addValueChangeListener(e -> onValueChange(isDirty()));
//    }

////    @Override
//    public boolean isDirty() {
//        return getBinder().hasChanges();
//    }

//    public void onValueChange(boolean isDirty) {
//        setSaveDisabled(!isDirty);
//    }
    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    @Override
    protected void openSpecific() {
    }

    private Component initCkontField() {
        ckontField = new TextField("Číslo kontraktu");
        ckontField.addValueChangeListener(event -> {
            folderField.setValue(
                VmFileUtils.NormalizeDirnamesAndJoin(event.getValue(), textField.getValue())
            );
        });
        ckontField.setValueChangeMode(ValueChangeMode.EAGER);

        getBinder().forField(ckontField)
                .withValidator(new StringLengthValidator(
                        "Číslo kontraktu musí mít mezi 3-16 znaky",
                        3, 16)
                )
                .withValidator(
                        ckont -> kontService.getByCkont(ckont) == null,
                        "Toto číslo kontraktu již existuje, zvol jiné"
                )
                .bind(EvidKont::getCkont, EvidKont::setCkont);
        return ckontField;
    }

    private Component initTextField() {
        textField = new TextField("Text kontraktu");
        textField.getElement().setAttribute("colspan", "2");

        textField.addValueChangeListener(event -> {
            folderField.setValue(
                VmFileUtils.NormalizeDirnamesAndJoin(ckontField.getValue(), event.getValue())
            );
        });
        textField.setValueChangeMode(ValueChangeMode.EAGER);

        getBinder().forField(textField)
//                .withValidator(new StringLengthValidator(
//                        "Text kontraktu musí mít alespoň jeden znak",
//                        1, null))
//                .withValidator(
//                        text -> kontService.getByText(text) == null,
//                        "Kontrakt se stejným textem již existuje, zadej jiný text")
                .bind(EvidKont::getText, EvidKont::setText);
        return textField;
    }


    private Component initFolderField() {
        folderField = new TextField("Složka kontraktu");
        folderField.getElement().setAttribute("colspan", "2");
        getBinder().forField(folderField)
//                .withValidator(
//                        docdir -> kontService.getByDocdir(docdir) == null,
//                        "Adresář stejného jména již existuje, zadej jiné číslo kontraktu nebo text")
                .bind(EvidKont::getFolder, EvidKont::setFolder);
        folderField.setReadOnly(true);
        return folderField;
    }

    private Component initInfoField() {
        Div infoBox = new Div();
        Emphasis infoText = new Emphasis("Odpovídající projektové a dokumentové adresáře budou vytvořeny/přejmenovány až při uložení celého kontraktu.");
        infoBox.getElement().setAttribute("colspan", "2");
        infoBox.add(infoText);
        return infoBox;
    }
}
