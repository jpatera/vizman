package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
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
    private Long kontId;


    public ZakEvidFormDialog (BiConsumer<EvidZak, Operation> itemSaver,
                              ZakService zakService) {

        super(itemSaver);
        this.setWidth("750px");
        this.zakService = zakService;

        getFormLayout().add(
                initCzakField()
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
        this.czakOrig = getCurrentItem().getCzak();
        this.textOrig = getCurrentItem().getText();
        this.folderOrig = getCurrentItem().getFolder();
        this.kontId = getCurrentItem().getKontId();
    }

    private Component initCzakField() {
        czakField = new TextField("Číslo zakázky");
        czakField.addValueChangeListener(event -> {
            folderField.setValue(
                VzmFileUtils.NormalizeDirnamesAndJoin(event.getValue(), textField.getValue())
            );
        });
        czakField.setValueChangeMode(ValueChangeMode.EAGER);

        getBinder().forField(czakField)
                .withValidator(
                        czak -> !StringUtils.isEmpty(czak)
                        , "Číslo zakázky nesmí být prázdné"
                )
                .withConverter(new StringToIntegerConverter(
                        "Číslo zakázky musí být celé číslo")
                )
//                .withValidator(new StringLengthValidator(
//                        "Číslo zakázky musí být celé číslo",
//                        3, 16)
//                )
                .withValidator(czak ->
                    ((Operation.ADD == getOperation())
                            && (!zakService.zakIdExistsInKont(kontId, czak))
                    )
                    ||
                    ((Operation.EDIT == getOperation())
                            && ((czak.equals(czakOrig))
                                || (!zakService.zakIdExistsInKont(kontId, czak))
                        )
                    )
//                        {
//                            return zakService.zakIdExistsInKont(kontId, czak);
//                        },
                    , "Toto číslo zakázky v rámci kontraktu již existuje, zvol jiné"
                )
                .bind(EvidZak::getCzak, EvidZak::setCzak);
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
//        Emphasis infoText = new Emphasis("Odpovídající projektové a dokumentové adresáře budou vytvořeny/přejmenovány až při uložení celé zakázky.");
        infoBox.getElement().setAttribute("colspan", "2");
//        infoBox.add(infoText);
        return infoBox;
    }
}
