package eu.japtor.vizman.ui.components;

import eu.japtor.vizman.backend.entity.GrammarGender;

/**
 * The operations supported by this dialog. Delete is enabled when editing
 * an already existing item.
 */
public enum Operation {

    ADD("Nový", "Nová", "Nové", "Přidat", "zadat", false),
    EDIT("Editace", "Editace", "Editace", "Změnit", "editovat", true),
    DELETE("Zrušení", "Zrušení", "Zrušení", "Odebrat", "zrušit", true),
    FAKTUROVAT("Vystavení", "Vystavení", "Vystavení", "Vystavit", "vystavit", true),
    STORNO("Storno", "Storno", "Storno", "Stornovat", "stornovat", true),
    EXPORT("Export", "Export", "Export", "Exportovat", "exportovat", false),
    STAMP_PRICH("Zadání", "Zadání", "Zadání", "Zadat", "zadat", false),
    STAMP_PRICH_MAN("Zadání", "Zadání", "Zadání", "Zadat", "zadat", false),
//    STAMP_PRICH_MAN_FIRST("Zadání", "Zadání", "Zadání", "Zadat", "zadat", false),
    STAMP_ODCH("Zadání", "Zadání", "Zadání", "Zadat", "zadat", false),
    STAMP_ODCH_MAN("Zadání", "Zadání", "Zadání", "Zadat", "zadat", false),
    STAMP_ODCH_MAN_LAST("Zadání", "Zadání", "Zadání", "Zadat", "zadat", false)
    ;

    private final String titleOperNameForMasculine;
    private final String titleOperNameForFeminine;
    private final String titleOperNameForNeuter;
    private final String titleOperNameForUnknown;
    private final String opNameInText;
    private final boolean deleteEnabled;

    Operation(String titleOperNameForMasculine, String titleOperNameForFeminine,
              String titleOperNameForNeuter, String titleOperNameForUnknown,
              String opNameInText, boolean deleteEnabled) {
        this.titleOperNameForMasculine = titleOperNameForMasculine;
        this.titleOperNameForFeminine = titleOperNameForFeminine;
        this.titleOperNameForNeuter = titleOperNameForNeuter;
        this.titleOperNameForUnknown = titleOperNameForUnknown;
        this.opNameInText = opNameInText;
        this.deleteEnabled = deleteEnabled;
    }

    public String getDialogTitle(final String itemName, final GrammarGender itemGender) {
        return getTitleOperName(itemGender) + " " + itemName.toUpperCase();
    }

    public String getTitleOperName(final GrammarGender gender) {
        switch (gender) {
            case MASCULINE : return titleOperNameForMasculine;
            case FEMININE : return titleOperNameForFeminine;
            case NEUTER : return titleOperNameForNeuter;
            default : return titleOperNameForUnknown;
        }
    }

    public boolean isDeleteEnabled() {
        return deleteEnabled;
    }
}
