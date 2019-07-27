package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class VerticalScrollLayout  extends VerticalLayout {

    private static final String COMPONENT_HEIGHT = "750px";
    private static final String CONTENT_FIXED_HEIGHT = "720px";
    private VerticalLayout contentPane;

    public VerticalScrollLayout(){
        preparePanel();
    }

    public VerticalScrollLayout(Component... children){
        preparePanel();
        this.add(children);
    }

    private void preparePanel() {
        this.setHeight(COMPONENT_HEIGHT);
        this.setWidth("100%");
//        this.getElement().getStyle().set("padding", "0");
//        this.getElement().getStyle().set("margin", "0");
        this.getStyle()
                .set("display", "flex")
                .set("margin-top", "4px")
                .set("padding-bottom", "8px")
                .set("overflow", "auto")
                .set("border", "1px solid")
                .set("border-color", "darkGrey")
        ;
        this.setAlignItems(Alignment.STRETCH);

        contentPane = new VerticalLayout();
        contentPane.getStyle()
                .set("display", "block")
                .set("margin-top", "0")
        ;
        contentPane.setSizeUndefined();
        contentPane.setHeight(CONTENT_FIXED_HEIGHT);
        contentPane.setAlignItems(Alignment.CENTER);
        contentPane.setPadding(false);
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

    public void setFixedHeight() {
        contentPane.setHeight(CONTENT_FIXED_HEIGHT);
    }

    public void setUndefinedHeight() {
        contentPane.setHeight(null);
//        contentPane.setHeight("98%");
    }

    public void setMaxHeight() {
        contentPane.setHeight("98%");
    }

    public void setAlignCenter() {
        this.setAlignItems(Alignment.CENTER);
    }

    public void setAlignStretch() {
        this.setAlignItems(Alignment.STRETCH);
    }

}
