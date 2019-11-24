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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.repository.CinRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;


@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.CIN_READ
})
@SpringComponent
@UIScope
public class CfgCinListView extends VerticalLayout {

    private Grid<Cin> cinGrid;
    private final VerticalLayout gridContainer;

    @Autowired
    public CinRepo cinRepo;

    @Autowired
    public CfgCinListView() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        setAlignItems(Alignment.STRETCH);

        cinGrid = buildCinGrid();
        gridContainer = buildGridContainer(cinGrid);
        add(gridContainer);
    }

    @PostConstruct
    public void init() {
        cinGrid.setDataProvider(new ListDataProvider<>(cinRepo.findAll()));
    }

    private VerticalLayout buildGridContainer(Grid<Cin> cinGrid) {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.setAlignItems(Alignment.STRETCH);
        gridContainer.add(cinGrid);
        return gridContainer;
    }

    private Grid<Cin> buildCinGrid() {
        Grid<Cin> grid = new Grid<>();
        grid.setMultiSort(false);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        // TODO: ID -> CSS ?
        grid.setId("cin-grid");  // .. same ID as is used in shared-styles grid's dom module
        grid.addColumn(Cin::getPoradi).setHeader("Pořadí").setWidth("3em").setResizable(true)
                .setSortProperty("poradi");
        grid.addColumn(Cin::getAkceTyp).setHeader("A-typ").setWidth("3em").setResizable(true);
        grid.addColumn(Cin::getAkce).setHeader("Akce").setWidth("4em").setResizable(true);
        grid.addColumn(Cin::getCinKod).setHeader("C-kod").setWidth("3em").setResizable(true);
        grid.addColumn(Cin::getCinnost).setHeader("Činnost").setWidth("4em").setResizable(true);
        grid.addColumn(Cin::getCalcprac).setHeader("Kalk.prac.").setWidth("4em").setResizable(true);
        return grid;
    }
}
