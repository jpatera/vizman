package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import eu.japtor.vizman.backend.bean.EvidKont;
import eu.japtor.vizman.backend.bean.EvidZak;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.backend.utils.VmFileUtils;
import eu.japtor.vizman.ui.components.AbstractEditorDialog;
import eu.japtor.vizman.ui.components.AbstractSimpleEditorDialog;

import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakEvidFormDialog extends AbstractSimpleEditorDialog<EvidZak> {

    private TextField czakField;
    private TextField textField;
    private TextField folderField;

    AbstractEditorDialog.Operation operation;
    private ZakService zakService;
    private Long kontId;


    public ZakEvidFormDialog (Consumer<EvidZak> itemSaver, ZakService zakService) {

        super(itemSaver);
        this.setWidth("750px");
        this.kontId = kontId;
        this.zakService = zakService;
        this.operation = operation;

        getFormLayout().add(
                initCkontField()
                , initTextField()
                , initFolderField()
                , initInfoField()
        );

        setupEventListeners();
    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    @Override
    protected void openSpecific() {
    }

    private Component initCkontField() {
        czakField = new TextField("Číslo zakázky");
        czakField.addValueChangeListener(event -> {
            folderField.setValue(
                VmFileUtils.NormalizeDirnamesAndJoin(event.getValue(), textField.getValue())
            );
        });
        czakField.setValueChangeMode(ValueChangeMode.EAGER);

        getBinder().forField(czakField)
                .withConverter(new StringToIntegerConverter(
                        "Číslo zakázky musí být celé číslo")
                )
//                .withValidator(new StringLengthValidator(
//                        "Číslo zakázky musí být celé číslo",
//                        3, 16)
//                )
                .withValidator(
                        czak -> {
                            return zakService.zakIdExistsInKont(kontId, czak);
                        },
                        "Toto číslo zakázky v rámci kontraktu již existuje, zvol jiné"
                )
                .bind(EvidZak::getCzak, EvidZak::setCzak);
        return czakField;
    }

    private Component initTextField() {
        textField = new TextField("Text zakázky");
        textField.getElement().setAttribute("colspan", "2");

        textField.addValueChangeListener(event -> {
            folderField.setValue(
                VmFileUtils.NormalizeDirnamesAndJoin(czakField.getValue(), event.getValue())
            );
        });
        textField.setValueChangeMode(ValueChangeMode.EAGER);

        getBinder().forField(textField)
                .bind(EvidZak::getText, EvidZak::setText);
        return textField;
    }


    private Component initFolderField() {
        folderField = new TextField("Složka zakázky");
        folderField.getElement().setAttribute("colspan", "2");
        getBinder().forField(folderField)
//                .withValidator(
//                        docdir -> kontService.getByDocdir(docdir) == null,
//                        "Adresář stejného jména již existuje, zadej jiné číslo kontraktu nebo text")
                .bind(EvidZak::getFolder, EvidZak::setFolder);
        folderField.setReadOnly(true);
        return folderField;
    }

    private Component initInfoField() {
        Div infoBox = new Div();
        Emphasis infoText = new Emphasis("Odpovídající projektové a dokumentové adresáře budou vytvořeny/přejmenovány až při uložení celé zakázky.");
        infoBox.getElement().setAttribute("colspan", "2");
        infoBox.add(infoText);
        return infoBox;
    }
}
