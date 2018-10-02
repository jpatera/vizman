package eu.japtor.vizman.ui.components;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.function.SerializableSupplier;

import java.util.HashMap;
import java.util.Map;

//@SpringComponent
public class ExtendedPagedTabs extends Composite<HorizontalLayout> implements HasSize {

    protected Tabs tabs;
    protected final Map<Tab, SerializableSupplier<Component>> tabsToSuppliers = new HashMap<>();
    protected Component selected;

    public ExtendedPagedTabs() {
        init();
    }

//    @PostConstruct
    public void init() {
        this.getContent().setClassName("view-container");
        tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = tabs.getSelectedTab();
            select(selectedTab);
        });

        VerticalLayout sideMenu = new VerticalLayout();
        sideMenu.setWidth(null);
        sideMenu.add(new H4("KONFIGURACE"), tabs);
        getContent().add(sideMenu);
    }

    public void select(Tab tab) {
        SerializableSupplier<Component> supplier = tabsToSuppliers.get(tab);
        Component component = supplier.get();

//        VerticalLayout wrapper = new VerticalLayout(component);
//        wrapper.setMargin(false);

//        wrapper.setPadding(false);
//        wrapper.setSizeFull();

        if (selected == null) {
            getContent().add(component);
        } else {
            getContent().replace(selected, component);
        }

        tabs.setSelectedTab(tab);
        selected = component;
    }

//    public Tab add(Component component, String caption) {
//        return add(component, caption, false);
//    }
//
//    public Tab add(Component component, String caption, boolean closable) {
//        return add(() -> component, caption, closable);
//    }

    public void add(Component component, Tab tab) {
        add(() -> component, tab);
    }

//    public Tab add(SerializableSupplier<Component> componentSupplier, String caption) {
//        return add(componentSupplier, caption, false);
//    }

////    public Tab add(SerializableSupplier<Component> componentSupplier, String caption, boolean closable) {
//    public Tab add(SerializableSupplier<Component> componentSupplier, boolean closable) {
////        HorizontalLayout tabLayout = new HorizontalLayout(new Text(caption));
//        Div tabLayout = new Div();
////        tabLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
////        tabLayout.setSpacing(false);
//        Tab tab = new Tab(tabLayout);
//
//        if (closable) {
//            Span close = new Span(VaadinIcon.CLOSE_SMALL.create());
//            tabLayout.add(close);
//            close.addClickListener(e -> remove(tab));
//        }
//
//        add(componentSupplier, tab);
//        return tab;
//
//    }

    public void add(SerializableSupplier<Component> componentSupplier, Tab tab) {
        tabsToSuppliers.put(tab, componentSupplier);
        tabs.add(tab);

        if (tabsToSuppliers.size() == 1) {
            select(tab);
        }
    }

    public void remove(Tab tab) {
        tabs.remove(tab);
        tabsToSuppliers.remove(tab);
        select(tabs.getSelectedTab());
    }
}
