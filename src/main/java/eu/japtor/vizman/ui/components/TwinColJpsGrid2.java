package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridNoneSelectionModel;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.shared.Registration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Modifiedd from https://vaadin.com/directory/component/twincolgrid-add-on/2.0.3
 */
public class TwinColJpsGrid2<T> extends VerticalLayout implements HasValue<HasValue.ValueChangeEvent<Set<T>>, Set<T>>, HasComponents, HasSize {
    protected final Grid<T> leftGrid;
    protected final Grid<T> rightGrid;
    protected ListDataProvider<T> leftGridDataProvider;
    protected ListDataProvider<T> rightGridDataProvider;
    private final Button addAllButton;
    private final Button addButton;
    private final Button removeButton;
    private final Button removeAllButton;
    private final VerticalLayout buttonContainer;
    private Grid<T> draggedGrid;
    private VerticalLayout leftVL;
    private VerticalLayout rightVL;
    private Label rightColumnLabel;
    private Label leftColumnLabel;
    private Label fakeButtonContainerLabel;

    List<Grid.Column> leftSortColumns;
    List<Grid.Column> rightSortColumns;

    public TwinColJpsGrid2() {
        this((ListDataProvider)DataProvider.ofCollection(new LinkedHashSet()), (String)null);
    }

    public TwinColJpsGrid2(ListDataProvider<T> poolDataProvider, String caption) {

        leftSortColumns = new LinkedList<>();
        rightSortColumns = new LinkedList<>();

        this.leftGrid = new Grid();
        this.rightGrid = new Grid();
        this.addAllButton = new Button();
        this.addButton = new Button();
        this.removeButton = new Button();
        this.removeAllButton = new Button();
        this.rightColumnLabel = new Label();
        this.leftColumnLabel = new Label();
        this.fakeButtonContainerLabel = new Label();
        this.setMargin(false);
        this.setPadding(false);
        if (caption != null) {
            this.add(new Component[]{new Label(caption)});
        }

        this.setPoolDataProvider(poolDataProvider);
        this.leftGrid.setSelectionMode(SelectionMode.MULTI);

        this.rightGridDataProvider = DataProvider.ofCollection(new LinkedHashSet());
        this.rightGrid.setDataProvider(this.rightGridDataProvider);
        this.rightGrid.setSelectionMode(SelectionMode.MULTI);

        this.addButton.setIcon(VaadinIcon.ANGLE_RIGHT.create());
        this.addButton.setWidth("3em");
        this.addAllButton.setIcon(VaadinIcon.ANGLE_DOUBLE_RIGHT.create());
        this.addAllButton.setWidth("3em");
        this.removeButton.setIcon(VaadinIcon.ANGLE_LEFT.create());
        this.removeButton.setWidth("3em");
        this.removeAllButton.setIcon(VaadinIcon.ANGLE_DOUBLE_LEFT.create());
        this.removeAllButton.setWidth("3em");
        this.fakeButtonContainerLabel.getElement().setProperty("innerHTML", "&nbsp;");
        this.fakeButtonContainerLabel.setVisible(false);
        this.buttonContainer = new VerticalLayout(new Component[]{this.fakeButtonContainerLabel, this.addAllButton, this.addButton, this.removeButton, this.removeAllButton});
        this.buttonContainer.setPadding(false);
        this.buttonContainer.setSpacing(false);
        this.buttonContainer.setSizeUndefined();

        this.leftGrid.setWidth("100%");
        this.rightGrid.setWidth("100%");

        this.addAllButton.addClickListener((e) -> {
            Stream var10000 = this.leftGridDataProvider.getItems().stream();
            GridSelectionModel var10001 = this.leftGrid.getSelectionModel();
            var10000.forEach(var10001::select);
            this.updateSelection(new LinkedHashSet(this.leftGrid.getSelectedItems()), new HashSet());
        });
        this.addButton.addClickListener((e) -> {
            this.updateSelection(new LinkedHashSet(this.leftGrid.getSelectedItems()), new HashSet());
        });
        this.removeButton.addClickListener((e) -> {
            this.updateSelection(new HashSet(), this.rightGrid.getSelectedItems());
        });
        this.removeAllButton.addClickListener((e) -> {
            Stream var10000 = this.rightGridDataProvider.getItems().stream();
            GridSelectionModel var10001 = this.rightGrid.getSelectionModel();
            var10000.forEach(var10001::select);
            this.updateSelection(new HashSet(), this.rightGrid.getSelectedItems());
        });
        this.getElement().getStyle().set("display", "flex");

        this.leftColumnLabel.setVisible(false);
        this.rightColumnLabel.setVisible(false);

        this.leftVL = new VerticalLayout(new Component[]{this.leftColumnLabel, this.leftGrid});
        this.rightVL = new VerticalLayout(new Component[]{this.rightColumnLabel, this.rightGrid});
        this.leftVL.setSizeFull();
        this.leftVL.setMargin(false);
        this.leftVL.setPadding(false);
        this.leftVL.setSpacing(false);
        this.rightVL.setSizeFull();
        this.rightVL.setMargin(false);
        this.rightVL.setPadding(false);
        this.rightVL.setSpacing(false);
//        HorizontalLayout hl = new HorizontalLayout(new Component[]{this.leftVL, this.buttonContainer, this.rightVL});
        HorizontalLayout hl = new HorizontalLayout(new Component[]{this.leftVL, this.buttonContainer, this.rightVL});
        hl.setMargin(false);
        hl.setSizeFull();
        this.add(new Component[]{hl});
        this.setSizeUndefined();
    }

    public void setPoolItems(Collection<T> poolItems) {
        this.setPoolDataProvider(DataProvider.ofCollection(poolItems));
    }

    public void setPoolItems(Stream<T> poolItems) {
        this.setPoolDataProvider(DataProvider.fromStream(poolItems));
    }

    private void setPoolDataProvider(ListDataProvider<T> poolDataProvider) {
        this.leftGridDataProvider = poolDataProvider;
        this.leftGrid.setDataProvider(poolDataProvider);
        this.leftGridDataProvider.refreshAll(); // TODO: is it necessary for column auto width?
        if (this.rightGridDataProvider != null) {
            this.rightGridDataProvider.getItems().clear();
            this.rightGridDataProvider.refreshAll();
        }
    }

    public TwinColJpsGrid2(Collection<T> poolData) {
        this((ListDataProvider)DataProvider.ofCollection(new LinkedHashSet(poolData)), (String)null);
    }

    public TwinColJpsGrid2(Collection<T> poolData, String caption) {
        this(DataProvider.ofCollection(new LinkedHashSet(poolData)), caption);
    }

    public TwinColJpsGrid2<T> withRightColumnCaption(String rightColumnCaption) {
        this.rightColumnLabel.setText(rightColumnCaption);
        this.rightColumnLabel.setVisible(true);
        this.fakeButtonContainerLabel.setVisible(true);
        return this;
    }

    public TwinColJpsGrid2<T> withLeftColumnCaption(String leftColumnCaption) {
        this.leftColumnLabel.setText(leftColumnCaption);
        this.leftColumnLabel.setVisible(true);
        this.fakeButtonContainerLabel.setVisible(true);
        return this;
    }

    public TwinColJpsGrid2<T> addColumn(ItemLabelGenerator<T> itemLabelGenerator, String header) {
        this.leftGrid.addColumn(new TextRenderer(itemLabelGenerator)).setHeader(header);
        this.rightGrid.addColumn(new TextRenderer(itemLabelGenerator)).setHeader(header);
        return this;
    }

    public TwinColJpsGrid2<T> addSortableColumn(
            ItemLabelGenerator<T> itemLabelGenerator
            , Comparator<T> comparator
            , String header
    ) {
        leftSortColumns.add(this.leftGrid.addColumn(new TextRenderer(itemLabelGenerator)).setHeader(header).setComparator(comparator).setSortable(true));
        rightSortColumns.add(this.rightGrid.addColumn(new TextRenderer(itemLabelGenerator)).setHeader(header).setComparator(comparator).setSortable(true));
        return this;
    }

    public void doInitialGridSorts (SortDirection sortDirection) {
        List<GridSortOrder<T>> initialLeftSortOrder = new LinkedList<>();
        for (Grid.Column sortLeftCol : leftSortColumns) {
            initialLeftSortOrder.add(new GridSortOrder(sortLeftCol, sortDirection));
        }
        this.leftGrid.sort(initialLeftSortOrder);

        List<GridSortOrder<T>> initialRightSortOrder = new LinkedList<>();
        for (Grid.Column sortRightCol : rightSortColumns) {
            initialRightSortOrder.add(new GridSortOrder(sortRightCol, sortDirection));
        }
        this.rightGrid.sort(initialRightSortOrder);
    }

    public TwinColJpsGrid2<T> withoutAddAllButton() {
        this.addAllButton.setVisible(false);
        this.checkContainerVisibility();
        return this;
    }

    public TwinColJpsGrid2<T> withoutRemoveAllButton() {
        this.removeAllButton.setVisible(false);
        this.checkContainerVisibility();
        return this;
    }

    public TwinColJpsGrid2<T> withoutAddButton() {
        this.addButton.setVisible(false);
        this.checkContainerVisibility();
        return this;
    }

    public TwinColJpsGrid2<T> withoutRemoveButton() {
        this.removeButton.setVisible(false);
        this.checkContainerVisibility();
        return this;
    }

    private void checkContainerVisibility() {
        boolean atLeastOneIsVisible = this.removeButton.isVisible() || this.addButton.isVisible() || this.removeAllButton.isVisible() || this.addAllButton.isVisible();
        this.buttonContainer.setVisible(atLeastOneIsVisible);
    }

    public TwinColJpsGrid2<T> withSizeFull() {
        this.setSizeFull();
        return this;
    }

    public TwinColJpsGrid2<T> withHeight(String height) {
        this.setHeight(height);
        return this;
    }

    public TwinColJpsGrid2<T> withColAutoWidth() {
        leftGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        rightGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        return this;
    }


    public TwinColJpsGrid2<T> withDragAndDropSupport() {
        this.configDragAndDrop(this.leftGrid, this.rightGrid);
        this.configDragAndDrop(this.rightGrid, this.leftGrid);
        return this;
    }

    public Grid getLeftGrid() {
        return this.leftGrid;
    }

    public Grid getRightGrid() {
        return this.rightGrid;
    }

    public String getRightColumnCaption() {
        return this.rightColumnLabel.getText();
    }

    public String getLeftColumnCaption() {
        return this.leftColumnLabel.getText();
    }

    public void setValue(Set<T> value) {
        Objects.requireNonNull(value);
        Set<T> newValues = (Set)value.stream().map(Objects::requireNonNull).collect(Collectors.toCollection(LinkedHashSet::new));
        this.updateSelection(newValues, new LinkedHashSet(this.leftGrid.getSelectedItems()));
    }

    public Set<T> getValue() {
        return Collections.unmodifiableSet(new LinkedHashSet(this.rightGridDataProvider.getItems()));
    }

    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<Set<T>>> listener) {
        return this.rightGridDataProvider.addDataProviderListener((e) -> {
            ComponentValueChangeEvent<TwinColJpsGrid2<T>, Set<T>> e2 = new ComponentValueChangeEvent(this, this, (Object)null, true);
            listener.valueChanged(e2);
        });
    }

    public boolean isReadOnly() {
        return this.isReadOnly();
    }

    public boolean isRequiredIndicatorVisible() {
        return this.isRequiredIndicatorVisible();
    }

    public void setReadOnly(boolean readOnly) {
        this.leftGrid.setSelectionMode(readOnly ? SelectionMode.NONE : SelectionMode.MULTI);
        this.rightGrid.setSelectionMode(readOnly ? SelectionMode.NONE : SelectionMode.MULTI);
        this.addButton.setEnabled(!readOnly);
        this.removeButton.setEnabled(!readOnly);
        this.addAllButton.setEnabled(!readOnly);
        this.removeAllButton.setEnabled(!readOnly);
    }

    public void setRequiredIndicatorVisible(boolean visible) {
        this.setRequiredIndicatorVisible(visible);
    }

    private void updateSelection(Set<T> addedItems, Set<T> removedItems) {
        this.leftGridDataProvider.getItems().addAll(removedItems);
        this.leftGridDataProvider.getItems().removeAll(addedItems);
//        this.leftGrid.getDataCommunicator().getKeyMapper().removeAll();
//        this.leftGrid.getDataCommunicator().reset();
//        recalculateLeftColWidths();
        this.leftGridDataProvider.refreshAll();

        this.rightGridDataProvider.getItems().addAll(addedItems);
        this.rightGridDataProvider.getItems().removeAll(removedItems);
//        this.rightGrid.getDataCommunicator().getKeyMapper().removeAll();
//        this.rightGrid.getDataCommunicator().reset();
//        recalculateRightColWidths();
        this.rightGridDataProvider.refreshAll();

        this.leftGrid.getSelectionModel().deselectAll();
        this.rightGrid.getSelectionModel().deselectAll();
        recalculateLeftColWidths();
        recalculateRightColWidths();
    }

    public void recalculateLeftColWidths()  {
        // Recalculate column auto widths:
//        leftGrid.recalculateColumnWidths();
//        rightGrid.recalculateColumnWidths();
        // TODO: workaround until an issue https://github.com/vaadin/vaadin-grid-flow/issues/855 is fixed
        leftGrid.getElement().executeJs("setTimeout(() => { this.recalculateColumnWidths() }, 0)");
    }

    public void recalculateRightColWidths()  {
        // Recalculate column auto widths:
//        leftGrid.recalculateColumnWidths();
//        rightGrid.recalculateColumnWidths();
        // TODO: workaround until an issue https://github.com/vaadin/vaadin-grid-flow/issues/855 is fixed
        rightGrid.getElement().executeJs("setTimeout(() => { this.recalculateColumnWidths() }, 0)");
    }

    private void configDragAndDrop(Grid<T> sourceGrid, Grid<T> targetGrid) {
        Set<T> draggedItems = new LinkedHashSet();
        sourceGrid.setRowsDraggable(true);
        sourceGrid.addDragStartListener((event) -> {
            this.draggedGrid = sourceGrid;
            if (!(this.draggedGrid.getSelectionModel() instanceof GridNoneSelectionModel)) {
                draggedItems.addAll(event.getDraggedItems());
            }

            targetGrid.setDropMode(GridDropMode.ON_GRID);
        });
        sourceGrid.addDragEndListener((event) -> {
            if (this.draggedGrid == null) {
                draggedItems.clear();
            } else {
                ListDataProvider<T> dragGridSourceDataProvider = (ListDataProvider)this.draggedGrid.getDataProvider();
                dragGridSourceDataProvider.getItems().removeAll(draggedItems);
                dragGridSourceDataProvider.refreshAll();
                draggedItems.clear();
                this.draggedGrid.deselectAll();
                this.draggedGrid = null;
            }
        });
        targetGrid.addDropListener((event) -> {
            ListDataProvider<T> dragGridTargetDataProvider = (ListDataProvider)((Grid)event.getSource()).getDataProvider();
            dragGridTargetDataProvider.getItems().addAll(draggedItems);
            dragGridTargetDataProvider.refreshAll();
        });
    }

    public void addLeftGridSelectionListener(SelectionListener<Grid<T>, T> listener) {
        this.leftGrid.addSelectionListener(listener);
    }

    public void addRightGridSelectionListener(SelectionListener<Grid<T>, T> listener) {
        this.rightGrid.addSelectionListener(listener);
    }

}
