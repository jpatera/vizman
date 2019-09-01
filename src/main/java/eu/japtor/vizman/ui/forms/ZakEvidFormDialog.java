package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import eu.japtor.vizman.backend.bean.EvidZak;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.backend.utils.VzmFileUtils;
import eu.japtor.vizman.ui.components.AbstractSimpleEditorDialog;
import eu.japtor.vizman.ui.components.Operation;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiConsumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakEvidFormDialog extends AbstractSimpleEditorDialog<EvidZak> {

    private TextField czakField;
    private TextField textField;
    private TextField folderField;

    private Integer czakOrig;
    private String textOrig;
    private String folderOrig;

    private ZakService zakService;
    private String docRoot;
    private String projRoot;
    private String kontFolder;
    private Long kontId;


    public ZakEvidFormDialog (BiConsumer<EvidZak, Operation> itemSaver, ZakService zakService) {

        super(itemSaver);
        this.setWidth("750px");
        this.zakService = zakService;

        getFormLayout().add(
                initCzakField()
                , initTextField()
                , initFolderField()
                , initInfoField()
        );
        bindCzakField();

        setupEventListeners();
    }


    public void openDialog(EvidZak evidZak, Operation operation,
                            String dialogTitle,
                            String docRoot, String projRoot)
    {
        this.docRoot = docRoot;
        this.projRoot = projRoot;
        this.kontFolder = evidZak.getKontFolder();

        this.czakOrig = evidZak.getCzak();
        this.textOrig = evidZak.getText();
        this.folderOrig = evidZak.getFolder();
        this.kontId = evidZak.getKontId();

//        this.open();
        openInternal(
            evidZak
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

    protected void bindCzakField() {

//        if (Operation.ADD == getOperation()) {
//            getBinder().forField(czakField)
//                    .withValidator(czak -> czak.matches("^\\d{1,3}$")
//                            , "Číslo zakázky musí být mezi 1-9999")
//                    .withConverter(new StringToIntegerConverter("Neplatný formát čísla"))
//
////                    .withValidator(
////                            czak -> !StringUtils.isEmpty(czak)
////                            , "Číslo zakázky nesmí být prázdné"
////                    )
////                    .withValidator(new StringLengthValidator(
////                            "Číslo zakázky musí být zadáno",
////                            1, 9999)
////                    )
//                    .withValidator(czak ->
////                            ((Operation.ADD == getOperation()) &&
//                                    (!zakService.zakIdExistsInKont(kontId, czak))
////                            )
//                            , "Toto číslo zakázky již existuje, zvol jiné"
//                    )
//                    .bind(EvidZak::getCzak, EvidZak::setCzak);
//        } else {
            getBinder().forField(czakField)
                    .withValidator(new StringLengthValidator(
                            "Číslo zakázky musí mít 1-4 číslice",
                            1, 4)
                    )
                    .withConverter(new StringToIntegerConverter("Neplatný formát čísla"))
//                    .withValidator(
//                            ckont -> !StringUtils.isEmpty(ckont)
//                            , "Číslo zakázky nesmí být prázdné"
//                    )
                    .withValidator(czak ->
                            ((Operation.EDIT == getOperation())
                                    && (czak.equals(czakOrig) || (!zakService.zakIdExistsInKont(kontId, czak)))
                            )
                            ||
                            ((Operation.ADD == getOperation())
                                    && (!zakService.zakIdExistsInKont(kontId, czak))
                            )
                            , "Toto číslo zakázky již existuje, zvol jiné"
                    )
                    .bind(EvidZak::getCzak, EvidZak::setCzak);
//        }
    }

    private Component initCzakField() {
        czakField = new TextField("Číslo zakázky");
        czakField.setReadOnly(true);
//        czakField.addValueChangeListener(event -> {
//            folderField.setValue(
//                VzmFileUtils.NormalizeDirnamesAndJoin(event.getStringValue(), textField.getStringValue())
//            );
//        });
//        czakField.setValueChangeMode(ValueChangeMode.EAGER);


//        getBinder().forField(czakField)
//                .withValidator(
//                        czak -> !StringUtils.isEmpty(czak)
//                        , "Číslo zakázky nesmí být prázdné"
//                )
//                .withConverter(new StringToIntegerConverter(
//                        "Číslo zakázky musí být celé číslo")
//                )
////                .withValidator(new StringLengthValidator(
////                        "Číslo zakázky musí být celé číslo",
////                        3, 16)
////                )
//                .withValidator(czak ->
//                    ((Operation.ADD == getOperation())
//                            && (!zakService.zakIdExistsInKont(kontId, czak))
//                    )
//                    ||
//                    ((Operation.EDIT == getOperation())
//                            && ((czak.equals(czakOrig))
//                                || (!zakService.zakIdExistsInKont(kontId, czak))
//                        )
//                    )
////                        {
////                            return zakService.zakIdExistsInKont(kontId, czak);
////                        },
//                    , "Toto číslo zakázky v rámci zakázky již existuje, zvol jiné"
//                )
//                .bind(EvidZak::getCzak, EvidZak::setCzak);

        return czakField;
    }

    private Component initTextField() {
        textField = new TextField("Text zakázky");
        textField.getElement().setAttribute("colspan", "2");
        textField.addValueChangeListener(event -> {
            folderField.setValue(
                VzmFileUtils.NormalizeDirnamesAndJoin(czakField.getValue(), event.getValue())
            );
        });
        textField.setValueChangeMode(ValueChangeMode.EAGER);

        getBinder().forField(textField)
//                .withValidator(
//                        text -> !StringUtils.isEmpty(text)
//                        , "Text zakázky nesmí být prázdné"
//                )
                .withValidator(new StringLengthValidator(
                        "Text zakázky musí mít mezi 3-127 znaky",
                        3, 127)
                )
                .bind(EvidZak::getText, EvidZak::setText)
        ;
        return textField;
    }


    private Component initFolderField() {
        folderField = new TextField("Složka zakázky");
        folderField.getElement().setAttribute("colspan", "2");
        getBinder().forField(folderField)
                .withValidator(
                    folder ->
                        ((Operation.ADD == getOperation()) &&
                                !VzmFileUtils.zakDocRootExists(docRoot, kontFolder, folder))
                        ||
                        ((Operation.EDIT == getOperation()) &&
                                ((folder.equals(folderOrig)) || !VzmFileUtils.zakDocRootExists(docRoot
                                                                , kontFolder, folder))
                        )
                        , "Dokumentový adresář zakázky stejného jména již existuje, změň evidenci"
                )
                .withValidator(
                    folder ->
                        ((Operation.ADD == getOperation()) &&
                                !VzmFileUtils.zakProjRootExists(projRoot, kontFolder, folder))
                                ||
                                ((Operation.EDIT == getOperation()) &&
                                        ((folder.equals(folderOrig)) ||
                                                !VzmFileUtils.zakProjRootExists(projRoot, kontFolder, folder)
                                        )
                                )
                        , "Projektový adresář zakázky stejného jména již existuje, změň evidenci"
                )


//                .withValidator(
//                        docdir -> kontService.getByDocdir(docdir) == null,
//                        "Adresář stejného jména již existuje, zadej jiné číslo zakázky nebo text")
                .bind(EvidZak::getFolder, EvidZak::setFolder);
        folderField.setReadOnly(true);
        return folderField;
    }

    private Component initInfoField() {
        Div infoBox = new Div();
//        Emphasis infoText = new Emphasis("Odpovídající projektové a dokumentové adresáře ...");
        infoBox.getElement().setAttribute("colspan", "2");
//        infoBox.add(infoText);
        return infoBox;
    }
}
