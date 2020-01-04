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

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_ZAK_EVAL, layout = MainView.class)
@PageTitle(PAGE_TITLE_ZAK_EVAL)
//@Tag(TAG_ZAK)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
        Perm.ZAK_BASIC_READ, Perm.ZAK_BASIC_MODIFY,
        Perm.ZAK_EXT_READ, Perm.ZAK_EXT_MODIFY
})
//public class ZakEvalListView extends Div implements BeforeEnterObserver {
// ###***
public class ZakEvalListView extends VerticalLayout implements BeforeEnterObserver {

////    private final VerticalLayout mainViewContainer = new VerticalLayout();
//    private final H3 zakHeader = new H3(TITLE_ZAK_LIST);
//    private final Grid<Zak> zakGrid = new Grid<>();
//
//    @Autowired
//    public ZakRepo zakRepo;
//
//    @PostConstruct
//    public void init() {
////        setHeight("90%");
////        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
//        initView();
//        initGrid();
//        updateViewContent();
//    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
//        System.out.println("###  ZaklListView.beforeEnter");
    }

//    private void initView() {
//        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
//    }
//
//    private void initGrid() {
//        VerticalLayout container = new VerticalLayout();
//        container.setClassName("view-container");
//        container.setAlignItems(Alignment.STRETCH);
//
//
////        VerticalLayout gridContainer = new VerticalLayout();
////        gridContainer.setClassName("view-container");
////        gridContainer.setAlignItems(Alignment.CENTER);
////        gridContainer.setComponentAlignment();
////        gridContainer.setSizeFull();
////        gridContainer.setHeight("100%");
////        gridContainer.setHeight("90%");
////        gridContainer.setWidth("90%");
//
//        zakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
////        zakGrid.addColumn(TemplateRenderer.of("[[index]]")).setHeader("#").setWidth("4em")
////            .setFrozen(true);;
//        zakGrid.addColumn(Zak::getCkont).setHeader("ČK").setWidth("7em").setResizable(true)
//            .setFrozen(true);
//        zakGrid.addColumn(Zak::getCzak).setHeader("ČZ").setWidth("3em").setResizable(true)
//            .setFrozen(true);;
//        zakGrid.addColumn(Zak::getText).setHeader("Text").setWidth("15em").setResizable(true);
//
//        zakGrid.addColumn(Zak::getSkupina).setHeader("Skupina").setWidth("4em").setResizable(true);
//        zakGrid.addColumn(Zak::getHonorar).setHeader("Honorář").setWidth("4em").setResizable(true);
//        zakGrid.addColumn(Zak::getRm).setHeader("RM").setWidth("4em").setResizable(true);
//        zakGrid.addColumn(Zak::getR1).setHeader("R1").setWidth("4em").setResizable(true);
//        zakGrid.addColumn(Zak::getR2).setHeader("R2").setWidth("4em").setResizable(true);
//        zakGrid.addColumn(Zak::getR3).setHeader("R3").setWidth("4em").setResizable(true);
//        zakGrid.addColumn(Zak::getR4).setHeader("R4").setWidth("4em").setResizable(true);
//
//        container.add(zakHeader, zakGrid);
//        add(container);
//    }
//
////    private void openDir(String path) {
////        try {
//////            Runtime.getRuntime().exec("explorer.exe /select," + path);
////            ProcessBuilder pb = new ProcessBuilder("explorer.exe", "/select," + path);
////            pb.redirectError();
////            pb.start();
//////            Process proc = pb.start();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }
//
//    private void updateViewContent() {
//        List<Zak> zaks = zakRepo.findAll();
//        zakGrid.setItems(zaks);
//    }
}
