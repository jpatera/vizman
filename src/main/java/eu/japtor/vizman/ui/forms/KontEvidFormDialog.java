package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import eu.japtor.vizman.backend.bean.EvidKont;
import eu.japtor.vizman.backend.bean.EvidZak;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.utils.VzmFileUtils;
import eu.japtor.vizman.ui.components.AbstractSimpleEditorDialog;
import eu.japtor.vizman.ui.components.Operation;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiConsumer;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontEvidFormDialog extends AbstractSimpleEditorDialog<EvidKont> {

    private TextField ckontField;
    private TextField textField;
    private TextField folderField;

    private String ckontOrig;
    private String textOrig;
    private String folderOrig;

    private KontService kontService;
    private String docRoot;
    private String projRoot;


    public KontEvidFormDialog(BiConsumer<EvidKont, Operation> itemSaver, KontService kontService)
    {
        super(itemSaver);
        this.setWidth("750px");
        this.kontService = kontService;

        getFormLayout().add(
                initCkontField()
                , initTextField()
                , initFolderField()
                , initInfoField()
        );
        bindCkontField();

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


    public void openDialog(EvidKont evidKont, Operation operation,
                           String dialogTitle,
                           String docRoot, String projRoot)
    {
        this.docRoot = docRoot;
        this.projRoot = projRoot;

        ckontOrig = evidKont.getCkont();
        textOrig = evidKont.getText();
        folderOrig = evidKont.getFolder();

        this.openInternal(
                evidKont
                , operation
                , dialogTitle
                , null
                , null
//            , titleMainText
//            , Component titleMiddleComponent
//            , String titleEndText
        );
    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    @Override
    protected void openSpecific() {

    }

    protected void bindCkontField() {

//        if (Operation.ADD == getOperation()) {
//            getBinder().forField(ckontField)
////                .withValidator(
////                        ckont -> !StringUtils.isEmpty(ckont)
////                        , "Číslo kontraktu nesmí být prázdné"
////                )
//                .withValidator(new StringLengthValidator(
//                        "Číslo kontraktu musí mít mezi 3-16 znaky",
//                        3, 16)
//                )
//                .withValidator(ckont ->
////                        ((Operation.ADD == getOperation()) &&
//                                (kontService.getByCkont(ckont) == null)
////                        )
//                        , "Toto číslo kontraktu již existuje, zvol jiné"
//                )
//                .bind(EvidKont::getCkont, EvidKont::setCkont);
//        } else {
            getBinder().forField(ckontField)
//                .withValidator(
//                        ckont -> !StringUtils.isEmpty(ckont)
//                        , "Číslo kontraktu nesmí být prázdné"
//                )
//                .withValidator(new StringLengthValidator(
//                        "Číslo kontraktu musí mít 3-16 znaků",
//                        3, 16)
//                )
                .withValidator(ckont -> {return ckont.matches("^[0-9]{5}\\.[0-9]-[1-2]$"); }
                        , "Neplatný formát. Je očekáváno [XXXXX.X-1|2].")
                .withValidator(ckont ->
                        ((Operation.EDIT == getOperation())
                                && (ckont.equals(ckontOrig) || (kontService.getByCkont(ckont) == null))
                        )
                        ||
                        ((Operation.ADD == getOperation())
                                && (kontService.getByCkont(ckont) == null)
                        )
                        , "Toto číslo kontraktu již existuje, zvol jiné"
                )
                .bind(EvidKont::getCkont, EvidKont::setCkont);
//        }
    }

    private Component initCkontField() {
        ckontField = new TextField("Číslo kontraktu");
        ckontField.setRequiredIndicatorVisible(true);
        ckontField.setPlaceholder("XXXXX.X-[1|2]");
        ckontField.addValueChangeListener(event -> {
            folderField.setValue(
                VzmFileUtils.NormalizeDirnamesAndJoin(event.getValue(), textField.getValue())
            );
        });
        ckontField.setValueChangeMode(ValueChangeMode.EAGER);

//        getBinder().forField(ckontField)
//                .withValidator(ckont -> {return ckont.matches("^[0-9]{5}\\.[0-9]-[1-2]$"); }
//                        , "Je očekáván formát XXXXX.X-[1|2].")
//                .bind(Kont::getCkont, Kont::setCkont);

//        if (Operation.ADD == getOperation()) {
//            // TODO: mozna zbytecne? inicializuje se v openSpecific
//            getBinder().forField(ckontField)
//                    .withValidator(
//                            ckont -> !StringUtils.isEmpty(ckont)
//                            , "Číslo kontraktu nesmí být prázdné"
//                    )
//                    .withValidator(new StringLengthValidator(
//                            "Číslo kontraktu musí mít mezi 3-16 znaky",
//                            3, 16)
//                    )
////                    .withValidator(ckont ->
////                                    ((Operation.ADD == getOperation())
////                                            && (kontService.getByCkont(ckont) == null)
////                                    )
////                                            ||
////                                            ((Operation.EDIT == getOperation())
////                                                    && ((ckont.equals(ckontOrig))
////                                                    || (kontService.getByCkont(ckont) == null)
////                                            )
////                                            )
////                            , "Toto číslo kontraktu již existuje, zvol jiné"
////                    )
//                    .bind(EvidKont::getCkont, EvidKont::setCkont);
//        }

        return ckontField;
    }

    private Component initTextField() {
        textField = new TextField("Text kontraktu");
        textField.setRequiredIndicatorVisible(true);
        textField.getElement().setAttribute("colspan", "2");

        textField.addValueChangeListener(event -> {
            folderField.setValue(
                VzmFileUtils.NormalizeDirnamesAndJoin(ckontField.getValue(), event.getValue())
            );
        });
        textField.setValueChangeMode(ValueChangeMode.EAGER);

        getBinder().forField(textField)
                .withValidator(new StringLengthValidator(
                        "Text kontraktu musí mít mezi 3-127 znaky",
                        3, 127)
                )

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
                .withValidator(
                    folder ->
                        ((Operation.ADD == getOperation()) &&
                                !VzmFileUtils.kontDocRootExists(docRoot, folder))
                        ||
                        ((Operation.EDIT == getOperation()) &&
                                ((folder.equals(folderOrig)) || !VzmFileUtils.kontDocRootExists(docRoot, folder))
                        )
                    , "Dokumentový adresář kontraktu stejného jména již existuje, změň evidenci"
                )
                .withValidator(
                    folder ->
                        ((Operation.ADD == getOperation()) &&
                                !VzmFileUtils.kontProjRootExists(projRoot, folder))
                        ||
                        ((Operation.EDIT == getOperation()) &&
                                ((folder.equals(folderOrig)) ||
                                        !VzmFileUtils.kontProjRootExists(projRoot, folder)
                                )
                        )
                    , "Projektový adresář kontraktu stejného jména již existuje, změň evidenci"
                )
//                .withValidator(
//                        folder ->
//                                ((Operation.EDIT == getOperation()) &&
//                                    ((folder.equals(folderOrig)) ||
//                                     !VzmFileUtils.kontDocRootExists(docRoot, folder)
//                                    )
//                                )
//                        , "Dokumentový adresář kontrakltu stejného jména již existuje, změň evidenci")
//                .withValidator(
//                        folder ->
//                                ((Operation.EDIT == getOperation()) &&
//                                    ((folder.equals(folderOrig)) ||
//                                     !VzmFileUtils.kontProjRootExists(projRoot, folder)
//                                    )
//                                )
//                        , "Projektový adresář kontraktu stejného jména již existuje, změň evidenci")

//                        folder -> (Operation.EDIT == getOperation()) && !VzmFileUtils.kontDocRootExists(docRoot, folder)
//                        , "Dokumentový adresář stejného jména již existuje, změň evidenci")
//                .withValidator(
//                        folder -> (Operation.EDIT == getOperation()) && !VzmFileUtils.kontProjRootExists(projRoot, folder)
//                        , "Projektový adresář stejného jména již existuje, změň evidenci")
                .bind(EvidKont::getFolder, EvidKont::setFolder);
        folderField.setReadOnly(true);
        return folderField;
    }

    private Component initInfoField() {
        Div infoBox = new Div();
//        Emphasis infoText = new Emphasis("Odpovídající projektové a dokumentové adresáře...");
        infoBox.getElement().setAttribute("colspan", "2");
//        infoBox.add(infoText);
        return infoBox;
    }
}
