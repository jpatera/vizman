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
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Podzak;
import eu.japtor.vizman.backend.repository.PodzakRepo;
import eu.japtor.vizman.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_PODZAK, layout = MainView.class)
@PageTitle(PAGE_TITLE_PODZAK)
//@Tag(TAG_PODZAK)
@Permissions({Perm.VIEW_ALL, Perm.MANAGE_ALL,
        Perm.PODZAK_VIEW_BASIC_READ, Perm.PODZAK_VIEW_BASIC_MANAGE,
        Perm.PODZAK_VIEW_EXT_READ, Perm.PODZAK_VIEW_EXT_MANAGE
})
//public class PodzakListView extends Div implements BeforeEnterObserver {
// ###***
public class PodzakListView extends VerticalLayout implements BeforeEnterObserver {

//    private final VerticalLayout mainViewContainer = new VerticalLayout();
    private final H3 podzakHeader = new H3(TITLE_PODZAK);
    private final Grid<Podzak> podzakGrid = new Grid<>();

    @Autowired
    public PodzakRepo podzakRepo;

    @PostConstruct
    public void init() {
//        setHeight("90%");
//        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        initView();
        initGrid();
        updateViewContent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
        System.out.println("###  ZaklListView.beforeEnter");
    }

    private void initView() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
    }

    private void initGrid() {
        VerticalLayout container = new VerticalLayout();
        container.setClassName("view-container");
        container.setAlignItems(Alignment.STRETCH);
//        VerticalLayout gridContainer = new VerticalLayout();
//        gridContainer.setClassName("view-container");
//        gridContainer.setAlignItems(Alignment.CENTER);
//        gridContainer.setComponentAlignment();
//        gridContainer.setSizeFull();
//        gridContainer.setHeight("100%");
//        gridContainer.setHeight("90%");
//        gridContainer.setWidth("90%");

        podzakGrid.addColumn(TemplateRenderer.of("[[index]]")).setHeader("#");
        podzakGrid.addColumn(Podzak::getCzak).setHeader("Číslo zak.").setWidth("8em").setResizable(true);
        podzakGrid.addColumn(Podzak::getText).setHeader("Text").setWidth("8em").setResizable(true);
        podzakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        container.add(podzakHeader, podzakGrid);
        add(container);
    }


    private void updateViewContent() {
        List<Podzak> podzaks = podzakRepo.findAll();
        podzakGrid.setItems(podzaks);
    }
}
