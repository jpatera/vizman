package eu.japtor.vizman.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.SecurityUtils;
import eu.japtor.vizman.ui.components.NavigationBar;

import com.vaadin.flow.component.dependency.HtmlImport;
import eu.japtor.vizman.ui.exceptions.AccessDeniedException;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Locale;


//@Theme(Lumo.class)    // Lumo is default

//@Route(value = ROUTE_ROOT)
//@PageTitle(PAGE_TITLE_ROOT)
//@Tag(TAG_ROOT)
//@Theme(Material.class)
//@Route(value = "")
//@PageTitle(PAGE_TITLE_HOME)
@Theme(Lumo.class)
@HtmlImport("frontend://styles/shared-styles.html")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")    // ###***
//@BodySize(height = "100vh", width = "100vw")
//@Push(PushMode.MANUAL)
@Push
public class MainView extends VerticalLayout implements RouterLayout, BeforeEnterObserver, HasLogger {

    //    @Id("navigationBar")
    @Autowired  // Must not be used in constructor, only in @PostConstruct
    private NavigationBar navigationBar;

    @Autowired
    public MainView() {

//        getLogger().info("###  MainView constructor");
        ConfirmDialog.setDialogSessionLanguage(Locale.getDefault());
//        addClassName("main-layout");

        this.setWidth("100%");
//        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setAlignItems(FlexComponent.Alignment.STRETCH);
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        this.setClassName("main-layout");
//        this.setHeight("100%");
//    public MainView(NavigationBar navigationBar) {
//        this.navigationBar = navigationBar;
//        this.navigationBar.postInit();
//        postInit();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        // user name can be stored to session after login
        String userName = (String) attachEvent.getSession().getAttribute("username");
//        getElement().setText("Hello " + userName + ", welcome back!");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SecurityUtils.isAccessGranted(event.getNavigationTarget())) {
//            UI.getCurrent().navigate(href);
//            event = null;
            event.rerouteToError(AccessDeniedException.class, "PŘÍSTUP NENÍ POVOLEN");
        }
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
//            pages.add(new PageInfo(PAGE_USERS, ICON_USERS, TITLE_PERSON));
//        }
//        if (SecurityUtils.isAccessGranted(ProductsView.class)) {
//            pages.add(new PageInfo(PAGE_PRODUCTS, ICON_PRODUCTS, TITLE_PRODUCTS));
//        }
//        pages.add(new PageInfo(PAGE_LOGOUT, ICON_LOGOUT, TITLE_LOGOUT));
//
//        appNavigation.postInit(pages, PAGE_DEFAULT, PAGE_LOGOUT);
//
//        getElement().appendChild(confirmDialog.getElement());
//    }

//    public void beforeEnter(BeforeEnterEvent event) {
//        if (!SecurityUtils.isAccessGranted(event.getNavigationTarget())) {
//            event.rerouteToError(AccessDeniedException.class);
//        }
//    }

    @PostConstruct
    public void setup() {

//        getLogger().info("###  MainView SETUP started");

//        this.navigationBar.postInit();

//        RouterLink zakListLink = new RouterLink(null, ZakBasicListView.class);
//        Icon ICON_ZAK_LIST = new Icon (VaadinIcon.LIST);
//        ICON_ZAK_LIST.setColor("green");
//        zakListLink.add(ICON_ZAK_LIST, new Text(TITLE_ZAK_LIST));
//        zakListLink.addClassName("main-layout__nav-item");

//        Div navComoponent = new Div(zakListLink);
//        navComoponent.addClassName("main-layout__nav");


        add(buildSiteHeader());

        addClassName("main-layout");    // ###***

//        getLogger().info("### INIT finished");

    }


    private Component buildSiteHeader() {
////        H2 title = new H2("VizMan");
//        Div title = new Div();
//        title.add(new Label("VizMan"));
////        title.addClassName("main-layout__title");

        Div siteHeader = new Div(navigationBar);
        siteHeader.addClassName("main-layout__header");
        return siteHeader;
    }


//    private void navigate() {
//        int idx = tabs.getSelectedIndex();
//        if (idx >= 0 && idx < hrefs.size()) {
//            String href = hrefs.get(idx);
//            if (href.equals(logoutHref)) {
//                // The logout button is a 'normal' URL, not Flow-managed but
//                // handled by Spring Security.
//                UI.getCurrent().getPage().executeJavaScript("location.assign('logout')");
//            } else if (!href.equals(currentHref)) {
//                UI.getCurrent().navigate(href);
//            }
//        }
//    }

//    private Component buildNavComponent() {
//        Div navComponent = new Div();
//        navComponent.add(buildZakListRoute());
//        navComponent.addClassName("main-layout__nav");
//        return navComponent;
//    }

//    private Component buildZakListRoute() {
//        RouterLink zakListRoute = new RouterLink(null, ZakBasicListView.class);
//        Icon ICON_ZAK_LIST = new Icon (VaadinIcon.LIST);
//        ICON_ZAK_LIST.setColor("green");
//        zakListRoute.add(ICON_ZAK_LIST, new Text(TITLE_ZAK_LIST));
//        zakListRoute.addClassName("main-layout__nav-item");
//        return zakListRoute;
//    }


}
