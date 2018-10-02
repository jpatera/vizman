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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_ZAK, layout = MainView.class)
@PageTitle(PAGE_TITLE_ZAK)
//@Tag(TAG_ZAK)    // Kurvi layout
@Permissions({Perm.VIEW_ALL, Perm.MANAGE_ALL,
        Perm.ZAK_VIEW_BASIC_READ, Perm.ZAK_VIEW_BASIC_MANAGE,
        Perm.ZAK_VIEW_EXT_READ, Perm.ZAK_VIEW_EXT_MANAGE
})
public class ZakListView extends VerticalLayout implements BeforeEnterObserver {

    private final H3 zakListHeader = new H3(TITLE_ZAK);
    private final Grid<Zak> zakGrid = new Grid<>();

    @Autowired
    public ZakRepo zakRepo;

//    public ZakListView() {
//        initView();
//        initGrid();
//        updateViewContent();
//    }

    @PostConstruct
    public void init() {
        initView();
        initGrid();
        updateViewContent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first goes here, then to the beforeEnter of MainView
        System.out.println("###  ZakListView.beforeEnter");
    }

    private void initView() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

//        this.setWidth("100%");
//        this.setWidth("90vw");
//        this.setHeight("90vh");

//        <vaadin-vertical-layout style="width: 100%; height: 100%;" theme="padding">
//        <vaadin-text-field style="width: 100%;" placeholder="ID" id="idSearchTextField"></vaadin-text-field>
//        <vaadin-zakGrid items="[[items]]" id="zakGrid" style="width: 100%;"></vaadin-zakGrid>
    }

    private void initGrid() {
        VerticalLayout container = new VerticalLayout();
        container.setClassName("view-container");
        container.setAlignItems(Alignment.STRETCH);

        zakGrid.addColumn(TemplateRenderer.of("[[index]]")).setHeader("#");
        zakGrid.addColumn(Zak::getCzak).setHeader("Číslo zak.").setWidth("8em").setResizable(true);
        zakGrid.addColumn(Zak::getFirma).setHeader("Firma").setWidth("16em").setResizable(true);
        zakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        zakGrid.setItemDetailsRenderer(new ComponentRenderer<>(zak -> {
            VerticalLayout layout = new VerticalLayout();
            layout.add(new Label("Text: " + zak.getText()));
            layout.add(new Label("Zadáno: " + zak.getDatumzad()));
            return layout;
        }));

        container.add(zakListHeader, zakGrid);
        add(container);

    }

    private void updateViewContent() {
        List<Zak> zaks = zakRepo.findAll();
        zakGrid.setItems(zaks);
    }
}
