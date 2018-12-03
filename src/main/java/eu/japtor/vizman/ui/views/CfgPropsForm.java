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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.CfgProp;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.repository.CfgPropRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Locale;


@Permissions({Perm.VIEW_ALL, Perm.MANAGE_ALL
})
@SpringComponent
@UIScope
public class CfgPropsForm extends VerticalLayout {

    private TextField appLocaleFld;
    private TextField projRootFld;
    private TextField docRootFld;

    private Binder<CfgProp> binder = new Binder<>();

    @Autowired
    public CfgPropRepo cfgPropRepo;

    @Autowired
    public CfgPropsForm() {
        buildPropsContainer();
    }

    @PostConstruct
    public void init() {
//        appLocaleFld.setValue(cfgPropRepo.findByName(CfgProp.CfgPropName.APP_LOCALE.getName()).getValue());
        appLocaleFld.setValue( Locale.getDefault().toString());
        projRootFld.setValue(cfgPropRepo.findByName(CfgProp.CfgPropName.PROJECT_ROOT.getName()).getValue());
        docRootFld.setValue(cfgPropRepo.findByName(CfgProp.CfgPropName.DOCUMENT_ROOT.getName()).getValue());

//        cinGrid.setDataProvider(new ListDataProvider<>(cinRepo.findAll()));
//        container.add(usernameField);
//        getBinder().forField(usernameField)
//                .withConverter(String::trim, String::trim)
//                .withValidator(new StringLengthValidator(
//                        "Uživatelské jméno musí obsahovat aspoň 3 znamky",
//                        3, null))
//                .withValidator(
//                        username -> (currentOperation != Operation.ADD) ?
//                                true : personService.getByUsername(username) == null,
//                        "Uživatel s tímto jménem již existuje, zvol jiné jméno")
//                .bind(Person::getUsername, Person::setUsername);
    }

    private void buildPropsContainer() {

//        systemCfg.add(new TextField("Application locale",Locale.getDefault().toString()));
//        systemCfg.add(new TextField("Project root", "P:\\projects"));
//        systemCfg.add(new TextField("Document root", "L:\\documents"));
//        VerticalLayout gridContainer = new VerticalLayout();

        setClassName("view-container");
        setAlignItems(Alignment.STRETCH);

        Component propsHeader = new H4("SYSTÉM");

        appLocaleFld = new TextField(CfgProp.CfgPropName.APP_LOCALE.getDescription(), Locale.getDefault().toString());
        appLocaleFld.setReadOnly(true);
        projRootFld = new TextField(CfgProp.CfgPropName.PROJECT_ROOT.getDescription(), "project-root-dir");
        docRootFld = new TextField(CfgProp.CfgPropName.DOCUMENT_ROOT.getDescription(), "project-root-dir");
        VerticalLayout propContainer = new VerticalLayout();
        propContainer.add(appLocaleFld);
        propContainer.add(projRootFld);
        propContainer.add(docRootFld);

        HorizontalLayout propsFooter = new HorizontalLayout();
        propsFooter.add(new Button("Uložit"));
        propsFooter.add(new Button("Vrátit zpět"));

        this.add(propsHeader, propContainer, propsFooter);
    }

//    private Grid<Cin> buildCinGrid() {
//        Grid<Cin> grid = new Grid<>();
//        grid.setMultiSort(false);
//        grid.setSelectionMode(Grid.SelectionMode.NONE);
//
//        // TODO: ID -> CSS ?
//        grid.setId("cin-grid");  // .. same ID as is used in shared-styles grid's dom module
//        grid.addColumn(Cin::getPoradi).setHeader("Pořadí").setWidth("3em").setResizable(true)
//                .setSortProperty("poradi");
//        grid.addColumn(Cin::getCinT1).setHeader("T1").setWidth("3em").setResizable(true);
//        grid.addColumn(Cin::getCinT2).setHeader("T2").setWidth("3em").setResizable(true);
//        grid.addColumn(Cin::getAkce).setHeader("Akce").setWidth("4em").setResizable(true);
//        grid.addColumn(Cin::getCinnost).setHeader("Činnost").setWidth("4em").setResizable(true);
//        grid.addColumn(Cin::getCalcprac).setHeader("Kalk.prac.").setWidth("4em").setResizable(true);
//        return grid;
//    }
}
