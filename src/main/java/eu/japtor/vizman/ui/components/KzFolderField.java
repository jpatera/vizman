package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.ItemType;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ConfirmDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class KzFolderField extends TextField implements HasLogger {

//    private static Icon warningIcon = new Icon(VaadinIcon.WARNING);
//    private static Icon okIcon = new Icon(VaadinIcon.CHECK);
//    static{
//        warningIcon.setColor("red");
//        okIcon.setColor("green");
//    }

    private ItemType itemType;
    private String docRoot;
    private String projRoot;
    private String parentFolder;
    private Button openDocDirBtn;
    private Button openProjDirBtn;

    private static final OkDialog okDialog = new OkDialog();

    public KzFolderField(
            final String label
            , final ItemType itemType
            , final String docRoot
            , final String projRoot
    ) {
        super();
        this.setLabel(label);
//        this.setPlaceholder("Adresář není zadán");
        this.itemType = itemType;
        this.docRoot = docRoot;
        this.projRoot = projRoot;
        this.parentFolder = null;
        FlexLayout folderButtonsSuffix = new FlexLayout();
        folderButtonsSuffix.add(
//                initOpenProjDirBtn()
                initOpenDocDirBtn()
        );

        this.setSuffixComponent(folderButtonsSuffix);
    }

    public void setItemType(final ItemType itemType) {
        this.itemType = itemType;
    }

    public void setParentFolder(final String parentFolder) {
        this.parentFolder = parentFolder;
    }

    public boolean verifyPath() {
        return true;
    }

//    public void showDirNotFoundWarning(boolean showWarning) {
//        this.setPrefixComponent(showWarning ? warningIcon : okIcon);
//    }

    private Button initOpenDocDirBtn() {
        Icon docDirIco = new Icon(VaadinIcon.FOLDER_OPEN_O);
//        docDirIco.setColor("blue");
        openDocDirBtn = new OpenDirBtn(docDirIco, event -> {
            if (ItemType.KONT != itemType && StringUtils.isBlank(parentFolder)) {
                ConfirmDialog
                        .createWarning()
                        .withCaption("Dokumentový adresář zakázky")
                        .withMessage("Složka kontraktu není definována, nelze pracovat se zakázkovými adresáři")
                        .open();
//                return;
            } else if (StringUtils.isBlank(this.getValue())) {
                ConfirmDialog
                        .createWarning()
                        .withCaption("Dokumentový adresář " + (ItemType.KONT == itemType ? "kontraktu" : "zakázky"))
                        .withMessage("Složka " + (ItemType.KONT == itemType ? "kontraktu" : "zakázky") + " není definována")
                        .open();
//                return;
            } else {
                openDir("Dokumentový adresář " + (ItemType.KONT == itemType ? "kontraktu" : "zakázky")
                        , Paths.get(docRoot
                                , null == parentFolder ? "" : parentFolder
                                , this.getValue()
                        )
                );
            }
        });
        return openDocDirBtn;
    }

    private Button initOpenProjDirBtn() {
        Icon projDirIco = new Icon(VaadinIcon.FOLDER_OPEN_O);
        projDirIco.setColor("brown");
        openProjDirBtn = new OpenDirBtn(projDirIco, event -> {
            if (ItemType.KONT != itemType && StringUtils.isBlank(parentFolder)) {
                ConfirmDialog
                        .createWarning()
                        .withCaption("Projektový adresář zakázky")
                        .withMessage("Složka kontraktu není definována, nelze pracovat se zakázkovými adresáři")
                        .open();
//                return;
            } else if (StringUtils.isBlank(this.getValue())) {
                ConfirmDialog
                        .createWarning()
                        .withCaption("Projektový adresář " + (ItemType.KONT == itemType ? "kontraktu" : "zakázky"))
                        .withMessage("Složka " + (ItemType.KONT == itemType ? "kontraktu" : "zakázky") + " není definována")
                        .open();
//                return;
            } else {
                openDir("Projektový adresář " + (ItemType.KONT == itemType ? "kontraktu" : "zakázky")
                        , Paths.get(projRoot
                                , null == parentFolder ? "" : parentFolder
                                , this.getValue()
                        )
                );
            }
        });
        return openProjDirBtn;
    }

    private void openDir(final String warningDialogTitle, final Path dirPath) {
        try {
            getLogger().info("Trying to open directory {}", dirPath.toString());
            File dir = dirPath.toFile();
            if (!dir.exists()) {
                ConfirmDialog
                        .createWarning()
                        .withCaption(warningDialogTitle)
                        .withMessage("Adresář " + dirPath.toString() + " nenalezen")
                        .open();
                return;
            } else if (!dir.isDirectory()) {
                ConfirmDialog
                        .createWarning()
                        .withCaption(warningDialogTitle)
                        .withMessage("Cesta \"" + dirPath.toString() + "\" neodkazuje na adresář")
                        .open();
                return;
            }
//            Runtime.getRuntime().exec("explorer.exe /select," + dir.toString());
            ProcessBuilder pb = new ProcessBuilder("explorer.exe", "/select," + dir.toString());
            pb.redirectError();
            pb.start();
//            Process proc = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
