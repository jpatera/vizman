package eu.japtor.vizman.fsdataprovider;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;

import java.io.File;
import java.util.Date;

public class FileSelect extends TextField {

    private String filesText = "files";

    private TreeGrid<File> treeGrid = new TreeGrid<>();
    private Component content;

    private File rootFile;
    private File selectedFile = null;
    private String filter = null;
    private FilesystemData root = null;

    private static int GIGA = 1024*1024*1024;
    private static int MEGA = 1024*1024;
    private static int KILO = 1024;

    /**
     * Constructor
     *
     * @param rootFile The root directory where to browse
     */
    public FileSelect(File rootFile) {
        this.rootFile = rootFile;
        content = createContent();
    }

    /**
     * Alternative constructor with filter
     *
     * @since 1.1.0
     *
     * @param rootFile The root directory where to browse
     * @param filter Set filter used for filename extension
     */
    public FileSelect(File rootFile, String filter) {
        this.rootFile = rootFile;
        this.filter = filter;
        content = createContent();
    }

//    @Override
//    protected Component initContent() {
//        return content;
//    }

    private Component createContent() {
        if (filter != null) {
            root = new FilesystemData(rootFile, filter, false);
        } else {
            root = new FilesystemData(rootFile, false);
        }
        FilesystemDataProvider fileSystem = new FilesystemDataProvider(root);
        treeGrid.setDataProvider(fileSystem);

//        treeGrid.setItemIconGenerator(file -> {
//            return FileTypeResolver.getIcon(file);
//        });
//
//        treeGrid.setItemDescriptionGenerator(file -> {
//            String desc = "";
//            if (!file.isDirectory()) {
//                Date date = new Date(file.lastModified());
//                long size = file.length();
//                String unit = "";
//                if (size > GIGA) {
//                    size = size / GIGA;
//                    unit = "GB";
//                }
//                else if (size > MEGA) {
//                    size = size / MEGA;
//                    unit = "MB";
//                }
//                else if (size > KILO) {
//                    size = size / KILO;
//                    unit = "KB";
//                } else {
//                    unit = "B";
//                }
//                desc = file.getName()+", "+date+", "+size+ " "+unit;
//            } else {
//                desc = root.getChildrenFromFilesystem(file).size()+" "+filesText;
//            }
//            return desc;
//        });

        treeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

//        treeGrid.addSelectionListener(event -> {
//            selectedFile = null;
//            event.getFirstSelectedItem().ifPresent(file -> {
//                selectedFile = file;
//                fireEvent(new ValueChangeEvent<File>(this, selectedFile, true));
//            });
//        });

        VerticalLayout panel = new VerticalLayout();
        panel.setSizeFull();
        panel.add(treeGrid);

        return panel;
    }

//    @Override
//    protected void doSetValue(File value) {
//        treeGrid.select(value);
//    }

    /**
     * Set String used for "files" text, for localization.
     *
     * @since 1.1.0
     *
     * @param filesText String for "files" text
     */
    public void setFilesText(String filesText) {
        this.filesText = filesText;
    }

    /**
     * Get the selected File
     *
     * @return The selected File, can be a file or directory
     */
//    @Override
//    public File getStringValue() {
//        return selectedFile;
//    }

}
