package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class KzFolderField extends TextField {

    private static Icon warningIcon = new Icon(VaadinIcon.WARNING);
    private static Icon okIcon = new Icon(VaadinIcon.CHECK);
    static{
        warningIcon.setColor("red");
        okIcon.setColor("green");
    }

    private String docRoot;
    private String projRoot;
    private String parentFolder;
    private Button openDocDirBtn;
    private Button openProjDirBtn;

    private static final OkDialog okDialog = new OkDialog();

    public KzFolderField(
            final String label
            , final String docRoot
            , final String projRoot
    ) {
        super();
        this.setLabel(label);
        this.setPlaceholder("Adresář není zadán");
        this.docRoot = docRoot;
        this.projRoot = projRoot;
        this.parentFolder = null;
        FlexLayout folderButtonsComponent = new FlexLayout();
        folderButtonsComponent.add(
                initOpenProjDirBtn()
                , initOpenDocDirBtn()
        );

        this.setSuffixComponent(folderButtonsComponent);
    }

    public void setParentFolder(final String parentFolder) {
        this.parentFolder = parentFolder;
    }

    public boolean verifyPath() {
        return true;
    }

    public void showDirNotFoundWarning(boolean showWarning) {
        this.setPrefixComponent(showWarning ? warningIcon : okIcon);
    }

    private Button initOpenDocDirBtn() {
        Icon docDirIco = new Icon(VaadinIcon.FOLDER_OPEN_O);
//        docDirIco.setColor("blue");
        openDocDirBtn = new OpenDirBtn(docDirIco, event -> {
            if (StringUtils.isEmpty(this.getValue())) {
                okDialog.open(
                        "Dokumentový adresář"
                        , "Složka není zadána, adresář nelze otevřít"
//                        , "Adresář \"" + docDirPath.toString() + "\" nenalezen"
                                , ""
                );
            } else {
                openDir("Dokumentový adresář"
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
            if (StringUtils.isEmpty(this.getValue())) {
                okDialog.open(
                        "Projektový adresář"
                        , "Složka není zadána, adresář nelze otevřít"
//                        , "Adresář \"" + docDirPath.toString() + "\" nenalezen"
                        , ""
                );
            } else {
                openDir("Projektový adresář"
                        , Paths.get(projRoot
                                , null == parentFolder ? "" : parentFolder
                                , this.getValue()
                        )
                );
            }
        });
        return openProjDirBtn;
    }

    private void openDir(final String msgDialogTitle, final Path dirPath) {
        try {
            File dir = dirPath.toFile();
            if (!dir.exists()) {
                okDialog.open(
                        msgDialogTitle
                        , "Adresář \"" + dirPath.toString() + "\" nenalezen"
                        , ""
                );
                return;
            } else if (!dir.isDirectory()) {
                new OkDialog().open(
                        msgDialogTitle
                        , "Cesta \"" + dirPath.toString() + "\" neodkazuje na adresář"
                        , ""
                );
                return;
            }
            Runtime.getRuntime().exec("explorer.exe /select," + dir.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
