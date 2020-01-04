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

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.communication.PushMode;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.ExtendedPagedTabs;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_TOOLS, layout = MainView.class)
@PageTitle(PAGE_TITLE_TOOLS)
//@Tag(TAG_CFG)
// Note: @SpringComponent  ..must not be defined, otherwise refresh (in Chrome) throws exception
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL})
public class ToolsTabsView extends VerticalLayout implements BeforeEnterObserver {

//    private final TextField searchField = new TextField("", "Hleadat uživatele");

    private final H3 cfgTabsHeader = new H3(TITLE_TOOLS);

    private VerticalLayout curr = new VerticalLayout();


    @Autowired
    ToolsMzdyView toolsMzdyView;

    @Autowired
    ToolsNaklView toolsNaklView;

    @Autowired
    ToolsDirTreeView toolsDirTreeView;



    @PostConstruct
    public void init() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        initTabs();
    }

    private void initTabs() {
//        VerticalLayout container = new VerticalLayout();
//        container.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        ExtendedPagedTabs toolsExtTabs = new ExtendedPagedTabs("NÁSTROJE");
        Tab tabMzdy = new Tab("Mzdy");
        Tab tabNakl = new Tab("Náklady");
        Tab tabDirs = new Tab("Adresáře");

        toolsExtTabs.add(toolsMzdyView, tabMzdy);
        toolsExtTabs.add(toolsNaklView, tabNakl);
        toolsExtTabs.add(toolsDirTreeView, tabDirs);
//        toolsExtTabs.add(curr, tabCurr);

//        MemoryBuffer buffer = new MemoryBuffer();
//        Upload upload = new Upload(buffer);
////        upload.addSucceededListener(event -> {
//////            Component component = createComponent(event.getMIMEType(),
////            Component component = createComponent(event.getMIMEType(),
////                    event.getFileName(), buffer.getInputStream());
////            showOutput(event.getFileName(), component, output);
////        });

        this.add(toolsExtTabs);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first goes here, then to the beforeEnter of MainView
        // System.out.println("###  ToolsTabsView.beforeEnter");
    }
}
