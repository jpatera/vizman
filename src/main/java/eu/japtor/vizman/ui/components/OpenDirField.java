package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import eu.japtor.vizman.backend.utils.VmFileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class OpenDirField extends TextField {

    private static Icon warningIcon = new Icon(VaadinIcon.WARNING);
    private static Icon okIcon = new Icon(VaadinIcon.CHECK);
    static{
        warningIcon.setColor("red");
        okIcon.setColor("green");
    }

    private String docRoot;
    private String projRoot;
    private Button openDocDirBtn;
    private Button openProjDirBtn;


    public OpenDirField(
            final String label
            , final String placeholder
            , final String docRoot
            , final String projRoot
            , ValueChangeListener openDocDir
            , ValueChangeListener openProjDir
    ) {
        super();
        this.setLabel(label);
        this.setPlaceholder(placeholder);
        this.docRoot = docRoot;
        this.projRoot = projRoot;
        this.setSuffixComponent(initOpenDocDirBtn(event ->
                openDocDir(VmFileUtils.getKontDocPath(this.docRoot, this.getValue())))
        );
//        this.addClassName("view-toolbar__search-field");
//        this.setHeight("32px");
//        this.getStyle().set("theme", "small");
//        this.addValueChangeListener(openProjDir);
//        this.setValueChangeMode(ValueChangeMode.EAGER);

//        this.setPrefixComponent(warningIcon);
//        updateContent.valueChanged(new ValueChangeEvent<Object>() {
//        });
//        this.setValue(this.getValue());
    }

    public boolean verifyPath() {
        return true;
    }

    public void showDirNotFoundWarning(boolean showWarning) {
        this.setPrefixComponent(showWarning ? warningIcon : okIcon);
    }

    private Button initOpenDocDirBtn(
            ComponentEventListener openDocDirListener
    ) {
//        openDocDirBtn = new Button();
//        openDocDirBtn = new OpenDirBtn(event -> openDir(this.getValue()));
        openDocDirBtn = new OpenDirBtn(openDocDirListener);
//        openDocDirBtn.setText("");
//        clearBtn.setIcon(new Icon("lumo", "cross"));
//        Icon openDirIcon = new Icon(VaadinIcon.FOLDER_OPEN);
//        openDirIcon.setSize("20px");
//        openDocDirBtn.setIcon(openDirIcon);
//        this.addClassName("review__edit");
        openDocDirBtn.getStyle()
                .set("theme", "icon small primary")
                .set("margin", "0");
        openDocDirBtn.setHeight("25px");

//        clearBtn.getElement().setAttribute("theme", "small");
//        clearBtn.getElement().setAttribute("size", "");
        return openDocDirBtn;
    }

    private void openDocDir(Path docDirPath) {
        try {
            File docDir = docDirPath.toFile();
            if (!docDir.exists()) {
                new OkDialog().open(
                        "Dokumentový adresář"
                        , "Adresář \"" + docDirPath.toString() + "\" nenalezen"
                        , ""
                );
                return;
            } else if (!docDir.isDirectory()) {
                new OkDialog().open(
                        "Dokumentový adresář"
                        , "Cesta \"" + docDirPath.toString() + "\" neodkazuje na adresář ale na soubor"
                        , ""
                );
                return;
            }
            Runtime.getRuntime().exec("explorer.exe /select," + docDir.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
