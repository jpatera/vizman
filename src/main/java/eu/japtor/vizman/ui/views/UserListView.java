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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Usr;
import eu.japtor.vizman.backend.service.UsrService;
import eu.japtor.vizman.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_USR, layout = MainView.class)
@PageTitle(PAGE_TITLE_USR)
@Tag(TAG_USR)
// Note: @SpringComponent  ..must not be defined, otherwise refresh (in Chrome) throws exception
@Permissions({Perm.VIEW_ALL, Perm.MANAGE_ALL
        , Perm.USR_VIEW_BASIC_READ, Perm.USR_VIEW_EXT_READ
})
public class UserListView extends VerticalLayout implements BeforeEnterObserver {

    private final UsrService usrService;
    private final H3 header = new H3(TITLE_USR);

    private final Grid<Usr> grid = new Grid<>();

    @Autowired
    public UserListView(UsrService usrService) {
//	public ProductsView(CrudEntityPresenter<Product> presenter) {
        this.usrService = usrService;   // Note: Field @Autowiring of usrService resulted to null
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        setupGrid();
        updateView();

//        super(EntityUtil.getName(Usr.class), form);
//        this.presenter = presenter;
//        form.setBinder(binder);
//        setupEventListeners();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
        System.out.println("###  ZaklListView.beforeEnter");
    }

    private void setupGrid() {
        VerticalLayout container = new VerticalLayout();
        container.setClassName("view-container");
        container.setAlignItems(Alignment.STRETCH);

        grid.addColumn(Usr::getUsername).setHeader("Username").setWidth("8em").setResizable(true);
        grid.addColumn(Usr::getPassword).setHeader("Password").setWidth("8em").setResizable(true);
        grid.addColumn(Usr::getFirstName).setHeader("Jméno").setWidth("8em").setResizable(true);
        grid.addColumn(Usr::getLastName).setHeader("Příjmení").setWidth("8em").setResizable(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
//        grid.setDataProvider(filteredDataProvider);
        container.add(header, grid);
        add(container);
    }


    private void updateView() {
        List<Usr> usrList = usrService.getAllUsr();
        grid.setItems(usrList);
    }
}
