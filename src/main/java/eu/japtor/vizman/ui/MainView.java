package eu.japtor.vizman.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import eu.japtor.vizman.ui.views.ZakListView;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.router.Route;

import static eu.japtor.vizman.ui.util.VizmanConst.ICON_ZAK_LIST;
import static eu.japtor.vizman.ui.util.VizmanConst.TITLE_ZAKS;

@HtmlImport("frontend://styles/shared-styles.html")
@Route("")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public class MainView extends Div implements RouterLayout {

    public MainView() {
        init();
    }

    private void init() {
        RouterLink zakListLink = new RouterLink(null, ZakListView.class);
        zakListLink.add(ICON_ZAK_LIST, new Text(TITLE_ZAKS));
        zakListLink.addClassName("main-layout__nav-item");

        H2 title = new H2("VizMan");
        title.addClassName("main-layout__title");

        Div navigation = new Div(zakListLink);
        navigation.addClassName("main-layout__nav");

        Div header = new Div(title, navigation);
        header.addClassName("main-layout__header");
        add(header);

        addClassName("main-layout");
    }

}
