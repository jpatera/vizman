package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class VerticalScrollLayout  extends VerticalLayout {

    private VerticalLayout contentPane;

    public VerticalScrollLayout(){
        preparePanel();
    }

    public VerticalScrollLayout(Component... children){
        preparePanel();
        this.add(children);
    }

    private void preparePanel() {
        this.setWidth("100%");
        setHeight("650px");
//        this.setHeight("400px");
        this.getStyle().set("overflow", "auto");
        this.getStyle().set("border", "1px solid");
        this.setAlignItems(FlexComponent.Alignment.STRETCH);

        contentPane = new VerticalLayout();
        contentPane.getStyle().set("display", "block");
        contentPane.setSizeUndefined();
        contentPane.setAlignItems(FlexComponent.Alignment.STRETCH);
//        contentPane.setWidth("100%");
//        contentPane.setWidth("100%");
        contentPane.setPadding(false);
//        super.add(contentPane);
        this.add(contentPane);
    }

    public VerticalLayout getContentPane(){
        return contentPane;
    }

    public void addContent(Component... components){
        contentPane.add(components);
    }

    public void removeContent(Component... components){
        contentPane.remove(components);
    }

    public void removeAllContent(){
        contentPane.removeAll();
    }

    public void addContentAsFirst(Component component) {
        contentPane.addComponentAtIndex(0, component);
    }
}
