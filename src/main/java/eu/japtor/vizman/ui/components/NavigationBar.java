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
import eu.japtor.vizman.ui.views.KontListView;
import eu.japtor.vizman.ui.views.UserListView;
import eu.japtor.vizman.ui.views.ZakListView;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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
        RouterLink homeLink = new RouterLink(null, MainView.class);
        Icon ICON_HOME = new Icon (VaadinIcon.HOME);
        homeLink. add(ICON_HOME, new Text(TITLE_HOME));
        homeLink.addClassName("main-layout__nav-item");

        RouterLink usrLink = new RouterLink(null, UserListView.class);
        Icon ICON_USR = new Icon (VaadinIcon.USERS);
        ICON_USR.setColor("blue");
        usrLink.add(ICON_USR, new Text(TITLE_USR));
        usrLink.addClassName("main-layout__nav-item");

        RouterLink kontLink = new RouterLink(null, KontListView.class);
        Icon ICON_KONT = new Icon (VaadinIcon.LIST);
        ICON_KONT.setColor("orange");
        kontLink.add(ICON_KONT, new Text(TITLE_KONT));
        kontLink.addClassName("main-layout__nav-item");

        RouterLink zakLink = new RouterLink(null, ZakListView.class);
        Icon ICON_ZAK = new Icon (VaadinIcon.LIST);
        ICON_ZAK.setColor("green");
        zakLink.add(ICON_ZAK, new Text(TITLE_ZAK));
        zakLink.addClassName("main-layout__nav-item");

        this.add(homeLink, kontLink, zakLink);
        if (SecurityUtils.isAccessGranted(UserListView.class)) {
            this.add(usrLink);
        }
        this.addClassName("main-layout__nav");
    }
}
