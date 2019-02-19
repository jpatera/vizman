package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubFormDialog extends AbstractEditorDialog<Fakt> {
//public class ZakFormDialog extends AbstractEditorDialog<Zak> implements BeforeEnterObserver {

//    private final static String FAKT_EDIT_COL_KEY = "fakt-edit-col";
//    private final static String FAKT_VYSTAV_COL_KEY = "fakt-vystav-col";
//    private final static String FAKT_EXPORT_COL_KEY = "fakt-export-col";

//    final private ValueProvider<Fakt, String> castkaProvider;
//    final private ComponentRenderer<HtmlComponent, Fakt> faktCastkaCellRenderer;
//    final private ComponentRenderer<HtmlComponent, Fakt> faktZakladCellRenderer;

    private TextField zakEvidField;
    private TextField cfaktField;
    private TextField textField;
    private TextField castkaField;

    private Fakt faktOrig;
    private CfgPropsCache cfgPropsCache;

    public SubFormDialog(BiConsumer<Fakt, Operation> itemSaver,
                         Consumer<Fakt> itemDeleter
    ){
        super("800px", null, false, false, itemSaver, itemDeleter);


        getFormLayout().add(
                initZakEvidField()
                , initCfaktField()
                , initTextField()
                , initCastkaField()
        );
    }


//    public void openDialog(Zak item, final Operation operation
//                    , String titleItemNameText, Component zakFaktFlags, String titleEndText) {
//        this.parentKont = parentKont;
//        openDialog(item, parentKont, operation, titleItemNameText, zakFaktFlags, titleEndText);
//    }

    public void openDialog(
            Fakt fakt, Zak parentZak, Operation operation
            , String titleItemNameText, Component gap, String titleEndText
    ){
        setItemNames(fakt.getTyp());

        castkaField.setSuffixComponent(new Span(fakt.getMena().name()));

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));

//        this.kontFolder = zak.getKontFolder();
//        this.zakFolderOrig = zakFolderOrig;
//        this.faktOrig = fakt;

//        this.faktGrid.setItems(zak.getFakts());
//        this.docGrid.setItems(zak.getZakDocs());
//        this.zakFolderField.setParentFolder(kontFolder);

//        getLowerPane().setVisible(ItemType.SUB != zak.getTyp());
//        getUpperGridContainer().setVisible(ItemType.SUB != zak.getTyp());



//        fakt.setCastka(BigDecimal.ZERO);


        openInternal(fakt, operation, titleItemNameText, new Span(), titleEndText
        );
//        openInternal(fakt, operation, titleItemNameText
//                , VzmFormatUtils.buildAvizoComponent(zak.getBeforeTerms(), zak.getAfterTerms(), true)
//                , titleEndText
//        );
    }


    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {

    }

    @Override
    protected void confirmDelete() {
//        new OkDialog().open("Zrušení subdodávky", "Rušení subdodávek není implementováno", "");
        openConfirmDeleteDialog("Zrušení subdodávky", "Opravdu zrušit subdodávku?", "");
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
        cfaktField = new TextField("Číslo subdodávky");
        cfaktField.setReadOnly(true);
        cfaktField.setWidth("8em");
        getBinder().forField(cfaktField)
//                .withConverter(new StringToIntegerConverter("Neplatný formát čísla"))
                .withConverter(
                        Integer::valueOf,
                        String::valueOf,
                        // Text to use instead of the NumberFormatException message
                        "Neplatný formát čísla")
                .bind(Fakt::getCfakt, null);
        return cfaktField;
    }

    private Component initTextField() {
        textField = new TextField("Text subdodávky");
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
                .bind(Fakt::getText, Fakt::setText);
//        textField.setReadOnly(true);
        return textField;
    }

    private Component initCastkaField() {
        castkaField = new TextField("Částka subdodávky");
//        castkaField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        getBinder().forField(castkaField)
                .withNullRepresentation("")
                .withValidator(new StringLengthValidator(
                        "Částka subdodávky nesmí být prázdná",
                        1, null))
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .withValidator(castka -> null != castka && castka.compareTo(BigDecimal.ZERO) < 0
                        , "Částka subdodávky musí být záporná")
                .bind(Fakt::getCastka, Fakt::setCastka);
//        castkaField.setValueChangeMode(ValueChangeMode.EAGER);
        return castkaField;
    }

    // ----------------------------------------------

}
