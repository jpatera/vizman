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
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.DochsumZak;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.ui.components.GridTitle;
import eu.japtor.vizman.ui.components.ReloadButton;
import eu.japtor.vizman.ui.components.Ribbon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
})
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class ToolsNaklView extends VerticalLayout  implements HasLogger {

    private Button naklP8RecalcButton;
    private Button reloadButton;

    ProgressBar recalcProgressBar;
    TextField messageField;


    @Autowired
    public CfgPropsCache cfgPropsCache;

    @Autowired
    public DochsumZakService dochsumZakService;

    @Autowired
    public DochService dochService;

    @Autowired
    public PersonService personService;



    @Autowired
    public ToolsNaklView() {
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
        monitorContainer.setClassName("monitor-container");
        monitorContainer.getStyle().set("marginTop", "0.5em");
        monitorContainer.setAlignItems(Alignment.STRETCH);

        monitorContainer.add(buildControlToolBar());
        monitorContainer.add(
                initNaklP8RecalcButton()
                , initRecalcProgressBar()
                , initMessageField()

        );
        return monitorContainer;
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
                new GridTitle("PŘEPOČET NÁKLADŮ")
                , new Ribbon()
//                , initReloadButton()
        );

        controlToolBar.add(
                titleComponent
//                , new Ribbon()
//                , initMzdyRecalcButton()
        );
        return controlToolBar;
    }

//    private Component initReloadButton() {
////        reloadButton = new ReloadButton(event -> mzdyInfoViewContent());
//        reloadButton = new ReloadButton(null);
//        return reloadButton;
//    }

//    private void mzdyInfoViewContent() {
//        mzdyInfoViewContent(null);
//    }

//    private Component initRecalcMzdyButton() {
//        mzdyRecalcButton = new NewItemButton("Generuj...?"
//                , event -> {
//        });
//        return mzdyRecalcButton;
//    }

    private Component initNaklP8RecalcButton() {
        naklP8RecalcButton = new Button("Přepočítat náklady P8 dle docházky a proužků", event -> {
            recalcNaklP8ForPersons();
        });
        naklP8RecalcButton.getElement().setAttribute("theme", "primary");
        naklP8RecalcButton.setWidth("500px");
        return naklP8RecalcButton;
    }

    private Component initRecalcProgressBar() {
        recalcProgressBar = new ProgressBar(0, 100, 0);
        recalcProgressBar.setHeight("30px");
        recalcProgressBar.setWidth("400px");
        recalcProgressBar.setId("nakl-recalc-progress-bar");
        recalcProgressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
        return recalcProgressBar;
    }

    private Component initMessageField() {
        messageField = new TextField("Přepočítává se: ");
        messageField.setWidth("400px");
        messageField.setId("nakl-recalc-message-field");
        return messageField;
    }

    private void recalcNaklP8ForPersons() {
        List<Person> persons = personService.fetchAll();
        int personCount = persons.size();
        int personNum = 1;
        double barMax = recalcProgressBar.getMax();
        naklP8RecalcButton.setEnabled(false);
        for (Person person : persons) {
            messageField.setValue(person.getPrijmeni());
            recalcProgressBar.setValue(
                    Math.min(barMax, personNum * barMax / personCount));
            this.getUI().ifPresent(ui -> ui.push());


            for (int year = 2000; year <= LocalDate.now().getYear(); year++) {
                for (int month = 1; month <= 12; month++) {
                    List<DochsumZak> dsZaksDb = dochsumZakService.fetchDochsumZaksForPersonAndYm(
                            person.getId(), YearMonth.of(year, month)
                    );
                    if (CollectionUtils.isEmpty(dsZaksDb)) {
                        continue;
                    }
                    BigDecimal koefP8 = dochService.calcKoefP8(person.getId(), YearMonth.of(year, month));
                    for (DochsumZak dsZakDb : dsZaksDb) {
                        dsZakDb.setDszKoefP8(koefP8);
                        if (null != koefP8 && null != dsZakDb.getDszWorkPruh()) {
                            dsZakDb.setDszWorkP8(dsZakDb.getDszWorkPruh().multiply(koefP8));
                            dsZakDb.setDszMzdaP8(dsZakDb.getDszWorkP8().multiply(koefP8).multiply(dsZakDb.getSazba()));
                        } else {
                            dsZakDb.setDszWorkP8(null);
                            dsZakDb.setDszMzdaP8(null);
                        }
                        dochsumZakService.store(dsZakDb);
                    }
//                    DochsumZak dsZakDb = dsZaksDb.stream()
//                            .filter(zakDb -> zakDb.getZakId().equals(pzakZakId)
//                                    && zakDb.getDsDate().equals(pzakDate))
//                            .findFirst().orElse(null);
//
//                    dsZaksDb.stream()
//                            .forEach(dsZak -> {
//                                dsZak
//                            });
//                    dochsumZakService.updateDochsumZaksForPersonAndMonth(
//                            person.getId()
//                            , YearMonth.of(year, month)
//                            , pruhDayMax
//                            , List<PruhZak> pruhZaks
//                    );
                }
            }

            personNum++;
        }
        recalcProgressBar.setValue(0);
        messageField.setValue("PŘEPOČET DOKONČEN");
        naklP8RecalcButton.setEnabled(true);
    }
}
