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

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.ExtendedPagedTabs;
import eu.japtor.vizman.ui.forms.CfgPropsFormView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static eu.japtor.vizman.ui.util.VizmanConst.PAGE_TITLE_CFG;
import static eu.japtor.vizman.ui.util.VizmanConst.ROUTE_CFG;


@Route(value = ROUTE_CFG, layout = MainView.class)
@PageTitle(PAGE_TITLE_CFG)
//@Tag(TAG_CFG)
// Note 1: @SpringComponent  ..must not be defined, otherwise refresh (in Chrome) throws exception
// Note 2: May be has to be combined with a UIScope for a correct functionality
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL})
public class CfgTabsView extends VerticalLayout {

    @Autowired
    CfgPersonListView personListView;

    @Autowired
    CfgRoleListView roleListView;

    @Autowired
    CfgCalTreeView calTreeView;

    @Autowired
    CfgCalHolTreeView calHolTreeView;

    @Autowired
    CfgCinListView cinListView;

    @Autowired
    CfgPropsFormView propsFormView;


    @PostConstruct
    public void init() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        initTabs();
    }

    private void initTabs() {
        ExtendedPagedTabs cfgExtTabs = new ExtendedPagedTabs("KONFIGURACE");
        Tab tabPerson = new Tab("Uživatelé");
        Tab tabRole = new Tab("Role");
        Tab tabCin = new Tab("Činnosti");
        Tab tabCal = new Tab("Prac. fond");
        Tab tabCalHol = new Tab("Svátky");
//        Tab tabCurr = new Tab("Měny");
        Tab tabAppCfg = new Tab("Aplikace");

        cfgExtTabs.add(personListView, tabPerson);
        cfgExtTabs.add(roleListView, tabRole);
        cfgExtTabs.add(cinListView, tabCin);
        cfgExtTabs.add(calTreeView, tabCal);
        cfgExtTabs.add(calHolTreeView, tabCalHol);
//        cfgExtTabs.add(curr, tabCurr);
        cfgExtTabs.add(propsFormView, tabAppCfg);

//        cfgExtTabs.select(tabCin);  // -> Tohle rozhazuje UI layout
//        cfgExtTabs.setOrientation(Tabs.Orientation.VERTICAL);
//        cfgExtTabs.setSelectedTab(tabCin);

        this.add(cfgExtTabs);
    }



// ==================================================================================

// Calendar related snippets, not currently used in VizMan
// -------------------------------------------------------

// import org.vaadin.stefan.fullcalendar.Entry;
// import org.vaadin.stefan.fullcalendar.FullCalendar;
// import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
// import org.vaadin.stefan.fullcalendar.*;

//    private FullCalendar cal = FullCalendarBuilder.create().build();

//        Entry entry = new Entry();
//        entry.setTitle("Svatýho Dyndy");
//        entry.setStart(LocalDateTime.now());
////        entry.setStart(LocalDate.now().withDayOfMonth(3).atTime(0, 0));
//        entry.setEnd(entry.getStart().plusDays(3));
////        entry.setColor("#ff3333");
//        entry.setColor(Color.YELLOW.toString());
//        entry.setAllDay(true);
//        entry.setRenderingMode(Entry.RenderingMode.BACKGROUND);
//        entry.setDescription("DESC-DESC");
//
//        Entry entry2 = new Entry();
//        entry2.setTitle("Svatýho Dyndy 222");
//        entry2.setStart(LocalDateTime.now().plusDays(1));
////        entry2.setStart(LocalDate.now().withDayOfMonth(3).atTime(0, 0));
//        entry2.setEnd(entry.getStart().plusDays(1));
////        entry.setColor("#ff3333");
//        entry2.setColor(Color.RED.toString());
//        entry2.setAllDay(true);
//        entry2.setRenderingMode(Entry.RenderingMode.NORMAL);
//        entry2.setDescription("DESC-DESC 222");
//
//        cal.setLocale(CalendarLocale.CZECH);
//        cal.setNowIndicatorShown(true);
//        cal.setTimeslotsSelectable(true);
//        cal.setWeekNumbersVisible(true);
//        cal.setBusinessHours(
//                new BusinessHours(LocalTime.of(9, 0), LocalTime.of(17, 0),BusinessHours.DEFAULT_BUSINESS_WEEK)
////                new BusinessHours(LocalTime.of(12, 0), LocalTime.of(15, 0), DayOfWeek.SATURDAY)
//        );
//        cal.addEntry(entry);
//        cal.addEntry(entry2);

//        MemoryBuffer buffer = new MemoryBuffer();
//        Upload upload = new Upload(buffer);
//        upload.addSucceededListener(event -> {
////            Component component = createComponent(event.getMIMEType(),
//            Component component = createComponent(event.getMIMEType(),
//                    event.getFileName(), buffer.getInputStream());
//            showOutput(event.getFileName(), component, output);
//        });

}
