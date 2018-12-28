package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.ui.MainView;

import javax.annotation.PostConstruct;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

//@Route(value = ROUTE_HOME, layout = MainView.class)
@Route(value = "", layout = MainView.class)
//@PageTitle(PAGE_TITLE_HOME)
//@Tag(TAG_HOME)
// Note: @SpringComponent  ..must not be defined, otherwise refresh (in Chrome) throws exception
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    private final H1 homeHeader = new H1(TITLE_HOME);

    @PostConstruct
    public void init() {
        add(homeHeader);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
        System.out.println("###  HomeView.beforeEnter");
    }
}
