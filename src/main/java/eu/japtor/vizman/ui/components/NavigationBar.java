package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.SecurityUtils;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.views.*;

import javax.annotation.PostConstruct;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@ParentLayout(MainView.class)
@SpringComponent
@UIScope
public class NavigationBar extends Div implements RouterLayout {

//    private List<String> hrefs = new ArrayList<>();
//    private String logoutHref;
//    private String defaultHref;
//    private String currentHref;


    public NavigationBar() {

//        UI.getCurrent().navigate(href);
//        init();
        System.out.println("###  NavigationBar constructor");

    }

    @PostConstruct
    public void init() {

        System.out.println("###  NavigationBar init");
        this.removeAll();

        // Navigation:
        RouterLink homeLink = new RouterLink(null, HomeView.class);
        Icon ICON_HOME = new Icon (VaadinIcon.HOME);
        ICON_HOME.setColor("magenta");
        homeLink.add(ICON_HOME, new Text(TITLE_HOME));
        homeLink.addClassName("main-layout__nav-item");

        RouterLink dochLink = new RouterLink(null, DochForm.class);
        Icon ICON_DOCH = new Icon (VaadinIcon.BELL);
        ICON_DOCH.setColor("blue");
        dochLink.add(ICON_DOCH, new Text(TITLE_DOCH));
        dochLink.addClassName("main-layout__nav-item");

        RouterLink kontTreeLink = new RouterLink(null, KontTreeView.class);
        Icon ICON_KONT_TREE = new Icon (VaadinIcon.TREE_TABLE);
        ICON_KONT_TREE.setColor("brown");
        kontTreeLink.add(ICON_KONT_TREE, new Text(TITLE_KONT_TREE));
        kontTreeLink.addClassName("main-layout__nav-item");

        RouterLink kontListLink = new RouterLink(null, KontListView.class);
        Icon ICON_KONT_LIST = new Icon (VaadinIcon.LIST);
        ICON_KONT_LIST.setColor("orange");
        kontListLink.add(ICON_KONT_LIST, new Text(TITLE_KONT_LIST));
        kontListLink.addClassName("main-layout__nav-item");

        RouterLink zakLink = new RouterLink(null, ZakListView.class);
        Icon ICON_ZAK = new Icon (VaadinIcon.LIST);
        ICON_ZAK.setColor("green");
        zakLink.add(ICON_ZAK, new Text(TITLE_ZAK));
        zakLink.addClassName("main-layout__nav-item");


//        RouterLink personLink = new RouterLink(null, PersonListView.class);
//        Icon ICON_PERSON = new Icon (VaadinIcon.USERS);
//        ICON_PERSON.setColor("blue");
//        personLink.add(ICON_PERSON, new Text(TITLE_PERSON));
//        personLink.addClassName("main-layout__nav-item");

        RouterLink cfgLink = new RouterLink(null, CfgTabsView.class);
        Icon ICON_CFG = new Icon (VaadinIcon.COG);
//        Icon ICON_CFG = new Icon (VaadinIcon.TOOLS);
        ICON_CFG.setColor("coral");
        cfgLink.add(ICON_CFG, new Text(TITLE_CFG));
        cfgLink.addClassName("main-layout__nav-item");

//        Element logoutLink = ElementFactory.createRouterLink("logout", "LOGOUT_CTX");
//        RouterLink logoutLink = new RouterLink(null, Nav.class);
        Div logoutLink = new Div();
        logoutLink.addClickListener(e ->
                UI.getCurrent().getPage().executeJavaScript("location.assign('logout')")
        );
        Icon ICON_LOGOUT = new Icon (VaadinIcon.USER);
        ICON_LOGOUT.setColor("black");
        logoutLink.add(ICON_LOGOUT, new Text("Logout"));
        logoutLink.addClassName("main-layout__nav-item");

//        logoutLink.addEventListener("click", e -> {
//            Element response = ElementFactory.createDiv("Hello!");
//            getElement().appendChild(response);
//        });


        this.add(homeLink, dochLink, kontTreeLink, kontListLink, zakLink);
//        if (SecurityUtils.isAccessGranted(UserListView.class)) {
//            this.add(personLink);
//        }

        if (SecurityUtils.isAccessGranted(CfgTabsView.class)) {
            this.add(cfgLink);
        }
        this.add(logoutLink);
        this.addClassName("main-layout__nav");
    }
}
