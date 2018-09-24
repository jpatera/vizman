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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TemplateRenderer;
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

@Route(value = ROUTE_ZAK, layout = MainView.class)
@PageTitle(PAGE_TITLE_ZAK)
@Tag(TAG_ZAK)
@Permissions({Perm.VIEW_ALL, Perm.MANAGE_ALL,
        Perm.ZAK_VIEW_BASIC_READ, Perm.ZAK_VIEW_BASIC_MANAGE,
        Perm.ZAK_VIEW_EXT_READ, Perm.ZAK_VIEW_EXT_MANAGE
})
public class ZakListView extends VerticalLayout implements BeforeEnterObserver {

    private final H3 header = new H3(TITLE_ZAK);
    private final Grid<Zak> grid = new Grid<>();

    @Autowired
    public ZakRepo zakRepo;

    @PostConstruct
    public void init() {
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

        grid.addColumn(TemplateRenderer.of("[[index]]")).setHeader("#");
        grid.addColumn(Zak::getCisloZakazky).setHeader("Číslo zak.").setWidth("8em").setResizable(true);
        grid.addColumn(Zak::getText).setHeader("Text").setWidth("8em").setResizable(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        container.add(header, grid);
        add(container);
    }

    private void updateViewContent() {
        List<Zak> zaks = zakRepo.findAll();
        grid.setItems(zaks);
    }
}
