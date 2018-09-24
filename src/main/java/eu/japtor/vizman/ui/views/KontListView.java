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

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.repository.KontRepo;
import eu.japtor.vizman.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_KONT, layout = MainView.class)
@PageTitle(PAGE_TITLE_KONT)
@Tag(TAG_KONT)
@Permissions({Perm.VIEW_ALL, Perm.MANAGE_ALL,
        Perm.KONT_VIEW_BASIC_READ, Perm.KONT_VIEW_BASIC_MANAGE,
        Perm.KONT_VIEW_EXT_READ, Perm.KONT_VIEW_EXT_MANAGE
})
public class KontListView extends VerticalLayout implements BeforeEnterObserver {

    private final H3 kontListHeader = new H3(TITLE_KONT);
    private final Grid<Kont> kontGrid = new Grid<>();

    @Autowired
    public KontRepo kontRepo;

    @PostConstruct
    public void init() {
        initView();
        initGrid();
        updateViewContent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first goes here, then to the beforeEnter of MainView
        System.out.println("###  KontListView.beforeEnter");
    }

    private void initView() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setWidth("90vw");
        this.setHeight("90vh");

//        <vaadin-vertical-layout style="width: 100%; height: 100%;" theme="padding">
//        <vaadin-text-field style="width: 100%;" placeholder="ID" id="idSearchTextField"></vaadin-text-field>
//        <vaadin-kontGrid items="[[items]]" id="kontGrid" style="width: 100%;"></vaadin-kontGrid>
    }

    private void initGrid() {
        VerticalLayout container = new VerticalLayout();
        container.setClassName("view-container");
        container.setAlignItems(Alignment.STRETCH);

//        kontGrid.setSizeFull();

        kontGrid.addColumn(TemplateRenderer.of("[[index]]")).setHeader("#");
        kontGrid.addColumn(Kont::getCisloKontraktu).setHeader("Číslo kont.").setWidth("8em").setResizable(true);
        kontGrid.addColumn(Kont::getFirma).setHeader("Firma").setWidth("16em").setResizable(true);
        kontGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        kontGrid.setItemDetailsRenderer(new ComponentRenderer<>(kont -> {
            VerticalLayout layout = new VerticalLayout();
            layout.add(new Label("Text: " + kont.getText()));
            layout.add(new Label("Zadáno: " + kont.getDatumzad()));
            return layout;
        }));

        container.add(kontListHeader, kontGrid);
        add(container);

    }

    private void updateViewContent() {
        List<Kont> konts = kontRepo.findAll();
        kontGrid.setItems(konts);
    }
}
