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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.ui.components.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

//@Route(value = ROUTE_PERSON, layout = MainView.class)
//@PageTitle(PAGE_TITLE_PERSON)
//@Tag(TAG_PERSON)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
})
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class ToolsMzdyView extends VerticalLayout  implements HasLogger {

    private static final String RADIO_DIRS_EXISTING = "Existující";
    private static final String RADIO_DIRS_MISSING = "Chybějící";
    private static final String RADIO_DIRS_ALL = "Vše";

//    private Button mzdyRecalcButton;
    private Button mzdyRecalcButton;
    private Button reloadButton;
//    private RadioButtonGroup<String> dirFilterRadio;

    ProgressBar recalcProgressBar;


    @Autowired
    public CfgPropsCache cfgPropsCache;

    @Autowired
    public DochsumZakService dochsumZakService;

    @Autowired
    public WageService wageService;

    @Autowired
    public PersonService personService;



    @Autowired
    public ToolsMzdyView() {
        initView();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setAlignItems(Alignment.STRETCH);
        this.setPadding(false);
        this.setMargin(false);
    }

    @PostConstruct
    public void postInit() {
        this.add(buildMonitorContainer());
    }

    private VerticalLayout buildMonitorContainer() {
        VerticalLayout monitorContainer = new VerticalLayout();
        monitorContainer.setClassName("control-container");
        monitorContainer.getStyle().set("marginTop", "0.5em");
        monitorContainer.setAlignItems(Alignment.STRETCH);

        monitorContainer.add(buildControlToolBar());
        monitorContainer.add(initRecalcProgressBar());
        return monitorContainer;
    }

    private Component initRecalcProgressBar() {
        recalcProgressBar = new ProgressBar(0, 100, 0);
        recalcProgressBar.setHeight("30px");
        recalcProgressBar.setWidth("300px");
        recalcProgressBar.setId("mzdy-recalc-progress-bar");
        return recalcProgressBar;
    }


    private Component buildControlToolBar() {

        HorizontalLayout controlToolBar = new HorizontalLayout();
        controlToolBar.setSpacing(false);
//        viewToolBar.setPadding(true);
//        viewToolBar.getStyle().set("padding-bottom", "5px");
//        viewToolBar.setWidth("100%");
        controlToolBar.setAlignItems(Alignment.END);
        controlToolBar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(Alignment.CENTER);
        titleComponent.setJustifyContentMode(JustifyContentMode.START);
        titleComponent.add(
                new GridTitle("PŘEPOČET MEZD")
                , new Ribbon()
                , initReloadButton()
        );

        controlToolBar.add(
                titleComponent
                , new Ribbon()
                , initMzdyRecalcButton()
        );
        return controlToolBar;
    }

    private Component initReloadButton() {
//        reloadButton = new ReloadButton(event -> mzdyInfoViewContent());
        reloadButton = new ReloadButton(null);
        return reloadButton;
    }

//    private void mzdyInfoViewContent() {
//        mzdyInfoViewContent(null);
//    }

//    private Component initRecalcMzdyButton() {
//        mzdyRecalcButton = new NewItemButton("Generuj...?"
//                , event -> {
//        });
//        return mzdyRecalcButton;
//    }

    private Component initMzdyRecalcButton() {
        mzdyRecalcButton = new Button("Přepočítat mzdy dle mzdových tabulek", event -> {
            recalcMzdyForPersons();
        });
        return mzdyRecalcButton;
    }

    private void recalcMzdyForPersons() {
        List<Person> persons = personService.fetchAll();
        int personCount = persons.size();
        int personNum = 1;
        double barMax = recalcProgressBar.getMax();
        for (Person person : persons) {
            recalcProgressBar.setValue(
                    Math.min(barMax, personNum * barMax / personCount));
            this.getUI().ifPresent(ui -> ui.push());
            dochsumZakService.recalcMzdyForPerson(person.getId());
            personNum++;
        }
    }
}
