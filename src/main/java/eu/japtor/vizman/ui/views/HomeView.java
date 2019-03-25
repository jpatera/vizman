package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.ui.MainView;

import javax.annotation.PostConstruct;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

//@Route(value = ROUTE_HOME, layout = MainView.class)
@Route(value = "", layout = MainView.class)
//@PageTitle(PAGE_TITLE_HOME)
//@Tag(TAG_HOME)
// Note: @SpringComponent  ..must not be defined, otherwise refresh (in Chrome) throws exception
public class HomeView extends VerticalLayout implements BeforeEnterObserver, HasLogger {

    private final H1 homeText = new H1("Das ist das Haus von VizMan");

    @PostConstruct
    public void init() {
        this.setWidthFull();
        this.setHeightFull();
        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        Icon homeIcon = VaadinIcon.HOME.create();
        homeIcon.setColor("darkslateblue");
        homeIcon.getStyle().set("width","400px");
        homeIcon.getStyle().set("height","400px");
        add(new H1(""), homeIcon, homeText);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
        getLogger().info("###  HomeView.beforeEnter");
    }
}
