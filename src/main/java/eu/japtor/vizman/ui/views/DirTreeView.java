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
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.bean.FileSystemDataProvider;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.utils.VzmFileUtils;
import eu.japtor.vizman.ui.components.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.io.File;
import java.util.Date;

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


    private TreeGrid<File> treeGrid;
    private TreeData<File> dirTreeData;
    private Button genMissingDirsButton;
    private Button reloadButton;
    private RadioButtonGroup<String> dirFilterRadio;


    @Autowired
    public CfgPropsCache cfgPropsCache;


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
//        <vaadin-treeGrid items="[[items]]" id="treeGrid" style="width: 100%;"></vaadin-treeGrid>
    }

    @PostConstruct
    public void postInit() {

        initGenMissingDirsButton();

        this.add(buildGridContainer());
        File rootFile = new File(cfgPropsCache.getDocRootServer());
        treeGrid.setDataProvider(new FileSystemDataProvider(rootFile));

        dirFilterRadio.addValueChangeListener(event -> updateViewContent());
        dirFilterRadio.setValue(RADIO_DIRS_EXISTING);
    }


    private VerticalLayout buildGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(buildGridToolBar());
        gridContainer.add(initDirTreeGrid());
        return gridContainer;
    }

    private Grid initDirTreeGrid() {

        treeGrid = new TreeGrid<>();

//        treeGrid.addColumn(iconTextValueProvider)
//            .setHeader("ČK/ČZ")
//            .setFlexGrow(0)
//            .setWidth("4em")
//            .setKey("file-icon")
//            .setId("file-icon-id")
//        ;
//        treeGrid.addHierarchyColumn(iconTextValueProvider);
        Grid.Column hierCol = treeGrid.addColumn(fileIconTextRenderer);
        hierCol
                .setHeader("Název")
                .setFlexGrow(1)
                .setWidth("30em")
                .setKey("file-name")
//                .setId("file-name-id")
                .setResizable(true)
//                .setFrozen(true)
//                .setKey("file-name")
//                .setId("file-name-id")
        ;
//        hierCol.setComparator(
//                ((a, b) -> compareMaybeComparables(valueProvider.apply(a),
//                        valueProvider.apply(b))));


//        treeGrid.addColumn(file -> {
//            String iconHtml;
//            if (file.isDirectory()) {
//                iconHtml = VaadinIcons.FOLDER_O.getHtml();
//            } else {
//                iconHtml = VaadinIcons.FILE_O.getHtml();
//            }
//            return iconHtml + " "
//                    + Jsoup.clean(file.getName(), Whitelist.simpleText());
//        }, new ComponentRenderer<>();

        treeGrid.addColumn(file -> file.isDirectory() ? "--" : file.length() + " bytes")
                .setHeader("Velikost")
                .setKey("file-size")
                .setId("file-size-id")
        ;

        treeGrid.addColumn(file -> new Date(file.lastModified()))
                .setHeader("Poslední změna")
                .setKey("file-last-modified")
                .setId("file-last-modified-id")
        ;

//        treeGrid.addColumn(file -> new Date(file.lastModified()),
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
        return treeGrid;
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

    TemplateRenderer fileIconTextRenderer = TemplateRenderer.<VzmFileUtils.VzmFile> of("<vaadin-grid-tree-toggle "
                            + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
//                            + "<img src='" + "[[item.icon]]" + "' alt=''>&nbsp;&nbsp;"
//                            + "<iron-icon style=\"padding-left: 1em; width: 0.8em; height: 0.8em;\" icon=\"vaadin:check\"></iron-icon>&nbsp;&nbsp;"
//                            + "<iron-icon style=\"padding-left: 1em; width: 0.8em; height: 0.8em;\" icon=\"vaadin:check\"></iron-icon>&nbsp;&nbsp;"

                            + "<iron-icon style=\"[[item.icon-style]]\" icon=\"[[item.icon-name]]\"></iron-icon>&nbsp;&nbsp;"
//                            + "<iron-icon style=\"padding-left: 1em; width: 0.8em; height: 0.8em;\" icon=\"[[item.icon]]\"></iron-icon>&nbsp;&nbsp;"

//                            + "<iron-icon style=\"padding-left: 1em; width: 0.8em; height: 0.8em;\" icon=\"vaadin:folder-o\"></iron-icon>&nbsp;&nbsp;"

                            + "[[item.name]]"
                            + "</vaadin-grid-tree-toggle>")
            .withProperty("leaf", file -> !treeGrid.getDataCommunicator().hasChildren(file))
            .withProperty("icon-name", file -> String.valueOf(vzmFileIconNameProvider.apply(file)))
            .withProperty("icon-style", file -> String.valueOf(vzmFileIconStyleProvider.apply(file)))
//            .withProperty("icon", icon -> "vaadin:folder-o")
            .withProperty("name", file -> file.getName())
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

//    private ComponentRenderer<Component, File> iconTextRenderer = new ComponentRenderer<>(file -> {
    private ValueProvider<File, IconTextField> iconTextValueProvider = file -> {
//        Icon icon;
//        if (file.isDirectory()) {
//            icon = VaadinIcon.FOLDER_O.create();
//        } else {
//            icon = VaadinIcon.FILE_O.create();
//        }
//
//        Component comp = new Composite<>();

        return new IconTextField(file);
        //            return iconHtml + " "
        //                    + Jsoup.clean(file.getName(), Whitelist.simpleText());
    };



    public class IconTextField extends Composite<Div> {
        private Icon icon;
        private Span text;

        public IconTextField(File file) {
            Icon icon;
            if (file.isDirectory()) {
                icon = VaadinIcon.FOLDER_O.create();
            } else {
                icon = VaadinIcon.FILE_O.create();
            }
            text = new Span(file.getName());
            getContent().add(icon, text);
        }
    }


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
                , initGenMissingDirsButton()
        );
        return viewToolBar;
    }

    Component initReloadButton() {
        reloadButton = new ReloadButton(event -> updateViewContent());
        return reloadButton;
    }

    private Component initGenMissingDirsButton() {
        genMissingDirsButton = new NewItemButton("Generuj...?"
                , event -> {
        });
        return genMissingDirsButton;
    }

    private void updateViewContent() {
        updateViewContent(null);
    }

    private void updateViewContent(final File itemToSelect) {
//        kzTreeData = loadKzTreeData(archFilterRadio.getValue());
//        inMemoryKzTreeProvider = new TreeDataProvider<>(kzTreeData);
//        assignDataProviderToGridAndSort(inMemoryKzTreeProvider);
//        inMemoryKzTreeProvider.refreshAll();
        treeGrid.getDataProvider().refreshAll();
        if (null != itemToSelect) {
            treeGrid.getSelectionModel().select(itemToSelect);
        }
    }
}
