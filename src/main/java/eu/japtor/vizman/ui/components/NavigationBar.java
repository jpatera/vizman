package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.views.UserListView;
import eu.japtor.vizman.ui.views.ZakListView;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@ParentLayout(MainView.class)
@SpringComponent
@UIScope
public class NavigationBar extends Div implements RouterLayout {

    public NavigationBar() {

        // Navigation:
        RouterLink homeLink = new RouterLink(null, MainView.class);
        Icon ICON_HOME = new Icon (VaadinIcon.HOME);
        homeLink.add(ICON_HOME, new Text(TITLE_HOME));
        homeLink.addClassName("main-layout__nav-item");

        RouterLink usrLink = new RouterLink(null, UserListView.class);
        Icon ICON_USERS = new Icon (VaadinIcon.USERS);
        ICON_USERS.setColor("blue");
        usrLink.add(ICON_USERS, new Text(TITLE_USR));
        usrLink.addClassName("main-layout__nav-item");

        RouterLink zakLink = new RouterLink(null, ZakListView.class);
        Icon ICON_ZAKS = new Icon (VaadinIcon.LIST);
        ICON_ZAKS.setColor("green");
        zakLink.add(ICON_ZAKS, new Text(TITLE_ZAK));
        zakLink.addClassName("main-layout__nav-item");

        this.add(homeLink, zakLink, usrLink);
        this.addClassName("main-layout__nav");
    }
}
