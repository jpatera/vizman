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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.backend.entity.Usr;
import eu.japtor.vizman.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_USERS, layout = MainView.class)
@PageTitle(PAGE_USERS_TITLE)
@Tag(PAGE_USERS_TAG)
public class UserListView extends VerticalLayout {

    //    private ZakService zakService;
    private final H2 header = new H2(TITLE_USERS);

    private final Grid<Usr> grid = new Grid<>();

    @Autowired
//    public UserListView(ZakService zakService) {
    public UserListView() {
//        this.zakService = zakService;   // Note: Field @Autowiring of zakService resulted to null
        initView();
        addContent();
        updateView();
    }

    private void initView() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
    }

    private void addContent() {
        VerticalLayout container = new VerticalLayout();
        container.setClassName("view-container");
        container.setAlignItems(Alignment.STRETCH);

        grid.addColumn(Usr::getUsername).setHeader("Username").setWidth("8em").setResizable(true);
        grid.addColumn(Usr::getPassword).setHeader("Password").setWidth("8em").setResizable(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        container.add(header, grid);
        add(container);
    }

    private void updateView() {
//        List<Zak> zaks = zakService.getAllZak();
//        grid.setItems(zaks);
    }
}
