package eu.japtor.vizman.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.SecurityUtils;
import eu.japtor.vizman.ui.components.NavigationBar;
import eu.japtor.vizman.ui.views.ZakListView;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.router.Route;

//import static eu.japtor.vizman.ui.util.VizmanConst.ICON_ZAK_LIST;
import java.util.ArrayList;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.TITLE_ZAKS;

@HtmlImport("frontend://styles/shared-styles.html")
@Route("")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public class MainView extends Div implements RouterLayout, HasLogger {
// public class MainView extends Div implements RouterLayout, HasLogger, BeforeEnterObserver {

//    @Id("navigationBar")
    private NavigationBar navigationBar;

    public MainView(NavigationBar navigationBar) {
        this.navigationBar = navigationBar;
        init();
    }

//    @Id("appNavigation")
//    private AppNavigation appNavigation;
//
//    public MainView(NavigationBar navigationBar) {
//        this.confirmDialog = confirmDialog;
//
//        List<PageInfo> pages = new ArrayList<>();
//
//        pages.add(new PageInfo(PAGE_STOREFRONT, ICON_STOREFRONT, TITLE_STOREFRONT));
//        pages.add(new PageInfo(PAGE_DASHBOARD, ICON_DASHBOARD, TITLE_DASHBOARD));
//        if (SecurityUtils.isAccessGranted(UsersView.class)) {
//            pages.add(new PageInfo(PAGE_USERS, ICON_USERS, TITLE_USERS));
//        }
//        if (SecurityUtils.isAccessGranted(ProductsView.class)) {
//            pages.add(new PageInfo(PAGE_PRODUCTS, ICON_PRODUCTS, TITLE_PRODUCTS));
//        }
//        pages.add(new PageInfo(PAGE_LOGOUT, ICON_LOGOUT, TITLE_LOGOUT));
//
//        appNavigation.init(pages, PAGE_DEFAULT, PAGE_LOGOUT);
//
//        getElement().appendChild(confirmDialog.getElement());
//    }

//    public void beforeEnter(BeforeEnterEvent event) {
//        if (!SecurityUtils.isAccessGranted(event.getNavigationTarget())) {
//            event.rerouteToError(AccessDeniedException.class);
//        }
//    }

    private void init() {

        getLogger().info("INIT started");

//        RouterLink zakListLink = new RouterLink(null, ZakListView.class);
//        Icon ICON_ZAK_LIST = new Icon (VaadinIcon.LIST);
//        ICON_ZAK_LIST.setColor("green");
//        zakListLink.add(ICON_ZAK_LIST, new Text(TITLE_ZAKS));
//        zakListLink.addClassName("main-layout__nav-item");

//        Div navComoponent = new Div(zakListLink);
//        navComoponent.addClassName("main-layout__nav");


        add(buildHeader());

        addClassName("main-layout");

        getLogger().info("INIT finished");

    }


    private Component buildHeader() {
        H2 title = new H2("VizMan");
        title.addClassName("main-layout__title");
//        return new Div(title, buildNavComponent());
        return new Div(title, navigationBar);
    }

//    private Component buildNavComponent() {
//        Div navComponent = new Div();
//        navComponent.add(buildZakListRoute());
//        navComponent.addClassName("main-layout__nav");
//        return navComponent;
//    }

//    private Component buildZakListRoute() {
//        RouterLink zakListRoute = new RouterLink(null, ZakListView.class);
//        Icon ICON_ZAK_LIST = new Icon (VaadinIcon.LIST);
//        ICON_ZAK_LIST.setColor("green");
//        zakListRoute.add(ICON_ZAK_LIST, new Text(TITLE_ZAKS));
//        zakListRoute.addClassName("main-layout__nav-item");
//        return zakListRoute;
//    }


}
