package eu.japtor.vizman.ui.components;

import eu.japtor.vizman.backend.entity.GrammarGender;

/**
 * The operations supported by this dialog. Delete is enabled when editing
 * an already existing item.
 */
public enum OperationResult {

    NO_CHANGE
    , ITEM_SAVED
    , ITEM_DELETED
    , ALERT_MODIF_SWITCHED
}
