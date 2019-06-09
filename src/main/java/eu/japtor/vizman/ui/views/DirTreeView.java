/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.bean.FileSystemDataProvider;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.VzmFolderType;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.backend.utils.VzmFileUtils;
import eu.japtor.vizman.ui.components.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static eu.japtor.vizman.backend.utils.VzmFileUtils.*;
import static eu.japtor.vizman.backend.utils.VzmFormatUtils.vzmFileIconNameProvider;
import static eu.japtor.vizman.backend.utils.VzmFormatUtils.vzmFileIconStyleProvider;

//@Route(value = ROUTE_PERSON, layout = MainView.class)
//@PageTitle(PAGE_TITLE_PERSON)
//@Tag(TAG_PERSON)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
})
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class DirTreeView extends VerticalLayout  implements HasLogger {

    private static final String RADIO_DIRS_EXISTING = "Existující";
    private static final String RADIO_DIRS_MISSING = "Chybějící";
    private static final String RADIO_DIRS_ALL = "Vše";


    private TreeGrid<VzmFileUtils.VzmFile> folderGrid;
    private Button genMissingFoldersButton;
    private Button reloadButton;
    private RadioButtonGroup<String> dirFilterRadio;

    private List<GridSortOrder<VzmFileUtils.VzmFile>> initialFolderSortOrder;

    @Autowired
    public CfgPropsCache cfgPropsCache;

    @Autowired
    public KontService kontService;

    @Autowired
    public ZakService zakService;


    @Autowired
    public DirTreeView() {
        initView();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setAlignItems(Alignment.STRETCH);
        this.setPadding(false);
        this.setMargin(false);

//        this.setWidth("100%");
//        this.setWidth("90vw");
//        this.setHeight("90vh");

//        <vaadin-vertical-layout style="width: 100%; height: 100%;" theme="padding">
//        <vaadin-text-field style="width: 100%;" placeholder="ID" id="idSearchTextField"></vaadin-text-field>
//        <vaadin-folderGrid items="[[items]]" id="folderGrid" style="width: 100%;"></vaadin-folderGrid>
    }

    @PostConstruct
    public void postInit() {

        initGenMissingFoldersButton();

        this.add(buildGridContainer());
        VzmFileUtils.VzmFile rootFile = new VzmFileUtils.VzmFile(cfgPropsCache.getDocRootServer(), true, VzmFolderType.ROOT, 0);
        folderGrid.setDataProvider(new FileSystemDataProvider(rootFile));

        dirFilterRadio.addValueChangeListener(event -> updateFolderViewContent());
        dirFilterRadio.setValue(RADIO_DIRS_EXISTING);
    }


    private VerticalLayout buildGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(buildGridToolBar());
        gridContainer.add(initDirTreeGrid());
        initialFolderSortOrder = Arrays.asList(new GridSortOrder(
                folderGrid.getColumnByKey(FOLDER_NAME_KEY), SortDirection.ASCENDING)
        );
        return gridContainer;
    }

    private static final String FOLDER_NAME_KEY = "flder-name-key";
    private static final String FOLDER_SIZE_KEY = "folder-size-key";
    private static final String FOLDER_LAST_MODIFIED_KEY = "folder-last-modified";

    private Grid initDirTreeGrid() {

        folderGrid = new TreeGrid<>();

//        folderGrid.addColumn(iconTextValueProvider)
//            .setHeader("ČK/ČZ")
//            .setFlexGrow(0)
//            .setWidth("4em")
//            .setKey("file-icon")
//            .setId("file-icon-id")
//        ;
//        folderGrid.addHierarchyColumn(iconTextValueProvider);
        Grid.Column hCol = folderGrid.addColumn(fileIconTextRenderer);
        hCol.setHeader("Název")
                .setFlexGrow(1)
                .setWidth("30em")
                .setKey("folder-name-key")
//                .setId("file-name-id")
                .setResizable(true)
//                .setFrozen(true)
//                .setKey("file-name")
//                .setId("file-name-id")
        ;
//        hierCol.setComparator(
//                ((a, b) -> compareMaybeComparables(valueProvider.apply(a),
//                        valueProvider.apply(b))));


//        folderGrid.addColumn(file -> {
//            String iconHtml;
//            if (file.isDirectory()) {
//                iconHtml = VaadinIcons.FOLDER_O.getHtml();
//            } else {
//                iconHtml = VaadinIcons.FILE_O.getHtml();
//            }
//            return iconHtml + " "
//                    + Jsoup.clean(file.getName(), Whitelist.simpleText());
//        }, new ComponentRenderer<>();

        folderGrid.addColumn(file -> file.isDirectory() ? "--" : file.length() + " bytes")
                .setHeader("Velikost")
                .setKey(FOLDER_SIZE_KEY)
//                .setId("file-size-id")
        ;

        folderGrid.addColumn(file -> new Date(file.lastModified()))
                .setHeader("Poslední změna")
                .setKey(FOLDER_LAST_MODIFIED_KEY)
//                .setId("file-last-modified-id")
        ;

//        folderGrid.addColumn(file -> new Date(file.lastModified()),
//                new DateRenderer()).setCaption("Last Modified")
//                .setId("file-last-modified");



//        dirTreeGrid = new Grid<>();
//        dirTreeGrid.setMultiSort(true);
//        dirTreeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
//        dirTreeGrid.setId("person-grid");  // .. same ID as is used in shared-styles grid's dom module
//
//        dirTreeGrid.addColumn(Person::getId)
//                .setTextAlign(ColumnTextAlign.END)
//                .setHeader("ID")
//                .setSortProperty("id")
//                .setWidth("5em")
//                .setResizable(true)
//                .setFrozen(true)
//        ;
//        dirTreeGrid.addColumn(Person::getState)
//                .setHeader("State")
//                .setSortProperty("state")
//                .setWidth("5em")
//                .setResizable(true)
//                .setFrozen(true)
//        ;
//        dirTreeGrid.addColumn(new ComponentRenderer<>(this::buildEditBtn))
//                .setFlexGrow(0)
//        ;
//        dirTreeGrid.addColumn(Person::getUsername)
//                .setHeader("Username")
//                .setSortProperty("username")
//                .setWidth("8em")
//                .setResizable(true)
//                .setFrozen(true)
//        ;
//        dirTreeGrid.addColumn(Person::getJmeno)
//                .setHeader("Jméno")
//                .setSortProperty("jmeno")
//                .setWidth("8em")
//                .setResizable(true)
//        ;
//        dirTreeGrid.addColumn(Person::getPrijmeni)
//                .setHeader("Příjmení")
//                .setSortProperty("prijmeni")
//                .setWidth("8em")
//                .setResizable(true)
//        ;
//        if (isWagesAccessGranted()) {
//            dirTreeGrid.addColumn(new NumberRenderer<>(Person::getSazba, VzmFormatUtils.moneyFormat))
//                    .setTextAlign(ColumnTextAlign.END)
//                    .setHeader("Sazba")
//                    .setWidth("8em")
//                    .setResizable(true)
//            ;
//        }
//        dirTreeGrid.addColumn(Person::getNastup)
//                .setHeader("Nástup")
//                .setWidth("8em")
//                .setResizable(true)
//        ;
//        dirTreeGrid.addColumn(Person::getVystup)
//                .setHeader("Ukončení")
//                .setWidth("8em")
//                .setResizable(true)
//        ;
        return folderGrid;
    }

//    private ComponentRenderer<Component, KzTreeAware> kzArchRenderer = new ComponentRenderer<>(kz -> {
//        ArchIconBox archBox = new ArchIconBox();
//        archBox.showIcon(kz.getTyp(), kz.getArchState());
//        return archBox;
//    });



//    private Button buildEditBtn(Person person) {
//        Button editBtn = new GridItemEditBtn(event -> personEditForm.open(
//                person, Operation.EDIT, ""));
//        return editBtn;
//    }

//    private ComponentRenderer<Component, File> fileIconRenderer = new ComponentRenderer<>(file -> {
//        Icon icon;
//        if (file.isDirectory()) {
//            icon = VaadinIcon.FOLDER_O.create();
//        } else {
//            icon = VaadinIcon.FILE_O.create();
//        }
//        return icon;
//        //            return iconHtml + " "
//        //                    + Jsoup.clean(file.getName(), Whitelist.simpleText());
//    });

//    private static ValueProvider<File, String> vzmFileIconNameProvider = file -> {
//        String iconName;
//        Icon icon;
//        if (file.isDirectory()) {
////            icon = VaadinIcon.FOLDER_O.create();
////            iconName = VaadinIcon.FOLDER_O.create().toString();
//            iconName = "vaadin:folder-o";
//        } else {
////            iconName = VaadinIcon.FILE_O.create().toString();
//            iconName = "vaadin:file-o";
//        }
//        return iconName;
//    };
//
//    private static ValueProvider<File, String> vzmFileIconStyleProvider = file -> {
//        String iconStyle;
//        Icon icon;
//        if (file.isDirectory()) {
////            icon = VaadinIcon.FOLDER_O.create();
////            iconName = VaadinIcon.FOLDER_O.create().toString();
//            iconStyle = "padding-left: 1em; width: 0.8em; height: 0.8em; color: red;";
//        } else {
////            iconName = VaadinIcon.FILE_O.create().toString();
//            iconStyle = "padding-left: 1em; width: 0.8em; height: 0.8em; color: red;";
//        }
//        return iconStyle;
//    };

    private TemplateRenderer fileIconTextRenderer = TemplateRenderer.<VzmFileUtils.VzmFile> of("<vaadin-grid-tree-toggle "
                            + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
//                            + "<img src='" + "[[item.icon]]" + "' alt=''>&nbsp;&nbsp;"
//                            + "<iron-icon style=\"padding-left: 1em; width: 0.8em; height: 0.8em;\" icon=\"vaadin:check\"></iron-icon>&nbsp;&nbsp;"
//                            + "<iron-icon style=\"padding-left: 1em; width: 0.8em; height: 0.8em;\" icon=\"vaadin:check\"></iron-icon>&nbsp;&nbsp;"

                            + "<iron-icon style=\"[[item.icon-style]]\" icon=\"[[item.icon-name]]\"></iron-icon>&nbsp;&nbsp;"
//                            + "<iron-icon style=\"padding-left: 1em; width: 0.8em; height: 0.8em;\" icon=\"[[item.icon]]\"></iron-icon>&nbsp;&nbsp;"

//                            + "<iron-icon style=\"padding-left: 1em; width: 0.8em; height: 0.8em;\" icon=\"vaadin:folder-o\"></iron-icon>&nbsp;&nbsp;"

                            + "[[item.name]]"
                            + "</vaadin-grid-tree-toggle>")
            .withProperty("leaf", file -> !folderGrid.getDataCommunicator().hasChildren(file))
            .withProperty("icon-name", file -> String.valueOf(vzmFileIconNameProvider.apply(file)))
            .withProperty("icon-style", file -> String.valueOf(vzmFileIconStyleProvider.apply(file)))
//            .withProperty("icon", icon -> "vaadin:folder-o")
            .withProperty("name", File::getName)
//            .withProperty("name", value -> String.valueOf(valueProvider.apply(value)))
            ;

//    TemplateRenderer  iconTextRenderer = TemplateRenderer.<File> of("<vaadin-grid-tree-toggle "
//                           + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
//                           + "<img src='" + "[[item.icon]]" + "' alt=''>&nbsp;&nbsp;"
//                           + "[[item.name]]"
//                           + "</vaadin-grid-tree-toggle>")
//			.withProperty("leaf", item -> !outlineTree.getDataCommunicator().hasChildren(item))
//            .withProperty("icon", icon -> String.valueOf(iconProvider.apply(icon)))
//            .withProperty("name", value -> String.valueOf(valueProvider.apply(value))))
//    ;

////    private ComponentRenderer<Component, File> iconTextRenderer = new ComponentRenderer<>(file -> {
//    private ValueProvider<VzmFileUtils.VzmFile, IconTextField> iconTextValueProvider = file -> {
////        Icon icon;
////        if (file.isDirectory()) {
////            icon = VaadinIcon.FOLDER_O.create();
////        } else {
////            icon = VaadinIcon.FILE_O.create();
////        }
////
////        Component comp = new Composite<>();
//
//        return new IconTextField(file);
//        //            return iconHtml + " "
//        //                    + Jsoup.clean(file.getName(), Whitelist.simpleText());
//    };



//    public class IconTextField extends Composite<Div> {
//        private Icon icon;
//        private Span text;
//
//        IconTextField(VzmFileUtils.VzmFile file) {
//            Icon icon;
//            if (file.isDirectory()) {
//                icon = VaadinIcon.FOLDER_O.create();
//            } else {
//                icon = VaadinIcon.FILE_O.create();
//            }
//            text = new Span(file.getNainitgrme());
//            getContent().add(icon, text);
//        }
//    }


    private Component buildGridToolBar() {

        HorizontalLayout viewToolBar = new HorizontalLayout();
        viewToolBar.setSpacing(false);
//        viewToolBar.setPadding(true);
//        viewToolBar.getStyle().set("padding-bottom", "5px");
//        viewToolBar.setWidth("100%");
        viewToolBar.setAlignItems(Alignment.END);
        viewToolBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(Alignment.CENTER);
        titleComponent.setJustifyContentMode(JustifyContentMode.START);
        titleComponent.add(
                new GridTitle("DOKUMENTOVÉ ADRESÁŘE")
                , new Ribbon()
                , initReloadButton()
        );

        Span dirFilterLabel = new Span("Adresáře:");

        dirFilterRadio = new RadioButtonGroup<>();
        dirFilterRadio.setItems(RADIO_DIRS_EXISTING, RADIO_DIRS_MISSING, RADIO_DIRS_ALL);
//        buttonShowArchive.addValueChangeListener(event -> setArchiveFilter(event));
        dirFilterRadio.getStyle().set("alignItems", "center");
        dirFilterRadio.getStyle().set("theme", "small");

        HorizontalLayout archFilterComponent = new HorizontalLayout();
        archFilterComponent.setMargin(false);
        archFilterComponent.setPadding(false);
        archFilterComponent.setAlignItems(Alignment.CENTER);
        archFilterComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        archFilterComponent.add(dirFilterLabel, dirFilterRadio);


        viewToolBar.add(
                titleComponent
                , new Ribbon()
                , archFilterComponent
                , new Ribbon()
                , initFixFoldersInDbButton()
                , initGenMissingFoldersButton()
        );
        return viewToolBar;
    }

    private Component initReloadButton() {
        reloadButton = new ReloadButton(event -> updateFolderViewContent());
        return reloadButton;
    }

    private Component initGenMissingFoldersButton() {
        genMissingFoldersButton = new NewItemButton("Generuj...?"
                , event -> {
        });
        return genMissingFoldersButton;
    }

    private Component initFixFoldersInDbButton() {
        genMissingFoldersButton = new NewItemButton("Opravit adresare v DB/FS", event -> {
            fixAllKontZakFoldersInDbAndFs();
        });
        return genMissingFoldersButton;
    }

    private void fixAllKontZakFoldersInDbAndFs() {
        List<Kont> konts = kontService.fetchAll();
        for (Kont kont : konts) {
            fixKontZakDocFoldersInDb(kont);
        }
        for (Kont kont : konts) {
            fixKontZakDocFoldersInFs(cfgPropsCache.getDocRootServer(), kont);
//            fixKontZakProjFoldersInFs(cfgPropsCache.getProjRootServer(), kont);
        }
    }

    private void fixKontZakDocFoldersInDb(Kont kont) {
        String expectedKontFolder = VzmFileUtils.getExpectedKontFolder(kont);
        kont.setFolder(expectedKontFolder);
        kontService.saveKont(kont, Operation.EDIT);
        for (Zak  zak : kont.getZaks()) {
            String expectedZakFolder = VzmFileUtils.getExpectedZakFolder(zak);
            zak.setFolder(expectedZakFolder);
            zakService.saveZak(zak, Operation.EDIT);
        }
    }

    private void fixKontZakDocFoldersInFs(String docRoot, Kont kont) {
        String kontFolder = kont.getFolder();
        if (!VzmFileUtils.kontDocRootExists(docRoot, kontFolder)) {
            VzmFileUtils.createKontDocDirs(docRoot, kontFolder);
        }
        for (Zak  zak : kont.getZaks()) {
            String zakFolder = zak.getFolder();
            if (!VzmFileUtils.zakDocRootExists(docRoot, kontFolder, zakFolder)) {
                VzmFileUtils.createZakDocDirs(docRoot, kontFolder, zakFolder);
            }
        }
    }

    private void fixKontZakProjFoldersInFs(String projRoot, Kont kont) {
        String kontFolder = kont.getFolder();
        if (!VzmFileUtils.kontProjRootExists(projRoot, kontFolder)) {
            VzmFileUtils.createKontProjDirs(projRoot, kontFolder);
        }
        for (Zak  zak : kont.getZaks()) {
            String zakFolder = zak.getFolder();
            if (!VzmFileUtils.zakProjRootExists(projRoot, kontFolder, zakFolder)) {
                VzmFileUtils.createZakProjDirs(projRoot, kontFolder, zakFolder);
            }
        }
    }

    private void updateFolderViewContent() {
        updateFolderViewContent(null);
    }

    private void assignDataProviderToGridAndSort(TreeData<VzmFileUtils.VzmFile> folderTreeData) {
        List<GridSortOrder<VzmFile>> sortOrderOrig = folderGrid.getSortOrder();
        folderGrid.setTreeData(folderTreeData);
        if (CollectionUtils.isEmpty(sortOrderOrig)) {
            folderGrid.sort(initialFolderSortOrder);
        } else {
            folderGrid.sort(sortOrderOrig);
        }
    }

    private void updateFolderViewContent(final VzmFileUtils.VzmFile itemToSelect) {
//        kzTreeData = loadKzTreeData(archFilterRadio.getValue());
//        inMemoryKzTreeProvider = new TreeDataProvider<>(kzTreeData);
//        assignDataProviderToGridAndSort(inMemoryKzTreeProvider);
//        inMemoryKzTreeProvider.refreshAll();

//        folderGrid.deselectAll();
//        TreeData<VzmFileUtils.VzmFile> folderTreeData
//                = VzmFileUtils.getExpectedKontFolderTree(cfgPropsCache.getDocRootServer(), null);
//
//        Path folderRootPath = getKontDocRootPath(cfgPropsCache.getDocRootServer(), currentItem.getFolder());
//        File folderRootDir = new File(folderRootPath.toString());
//        addFilesToExpectedVzmTreeData(folderTreeData, folderRootDir.listFiles(), null);
//
//        addNotExpectedKontSubDirs(folderTreeData
//                , new VzmFileUtils.VzmFile(folderRootPath, true)
//        );
//        addNotExpectedKontSubDirs(folderTreeData, new VzmFileUtils.VzmFile(getExpectedKontFolder(currentItem), true));
//
//        assignDataProviderToGridAndSort(kontDocTreeData);

        folderGrid.getDataProvider().refreshAll();
//        if (null != itemToSelect) {
//            folderGrid.getSelectionModel().select(itemToSelect);
//        }
    }
}
