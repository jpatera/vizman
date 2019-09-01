package eu.japtor.vizman.ui.components;


    /*-
 * #%L
 * TwinColGrid add-on
 * %%
 * Copyright (C) 2017 - 2018 FlowingCode S.A.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.shared.Registration;

import java.util.*;
import java.util.stream.Collectors;

//import com.vaadin.data.HasValue;
//import com.vaadin.data.ValueProvider;
//import com.vaadin.data.provider.DataProvider;
//import com.vaadin.data.provider.ListDataProvider;
//import com.vaadin.event.selection.SelectionListener;
//import com.vaadin.icons.VaadinIcons;
//import com.vaadin.shared.Registration;
//import com.vaadin.shared.ui.dnd.DropEffect;
//import com.vaadin.shared.ui.dnd.EffectAllowed;
//import com.vaadin.shared.ui.grid.DropMode;
//import com.vaadin.ui.Button;
//import com.vaadin.ui.CustomComponent;
//import com.vaadin.ui.Grid;
//import com.vaadin.ui.Grid.SelectionMode;
//import com.vaadin.ui.HorizontalLayout;
//import com.vaadin.ui.VerticalLayout;
//import com.vaadin.ui.components.grid.GridDragSource;
//import com.vaadin.ui.components.grid.GridDropTarget;
//import com.vaadin.ui.components.grid.NoSelectionModel;
//import com.vaadin.ui.renderers.TextRenderer;

//    public final class TwinColGrid<T> extends Component implements HasValue<Set<T>> {
//    public final class TwinColGrid<T> extends Composite implements HasValue<Set<T>> {


//public final class TwinColGrid<T> extends AbstractCompositeField<VerticalLayout> {
//public final class TwinColGrid<T> extends AbstractCompositeField<VerticalLayout> implements HasValue<Set<T>> {
public final class TwinColGrid<T> extends Composite<VerticalLayout> implements HasValue {

        private final Grid<T> leftGrid = new Grid<>();

        private final Grid<T> rightGrid = new Grid<>();

        private final Collection<T> itemPool;

        private ListDataProvider<T> leftGridDataProvider;

        private ListDataProvider<T> rightGridDataProvider;

        private final Button moveAllItemsToLeftButton = new Button();

        private final Button moveSelectedItemsToLeftButton = new Button();

        private final Button moveSelectedItemsToRightButton = new Button();

        private final Button moveAllItemsToRightButton = new Button();

        private final VerticalLayout buttonContainer;

//        private Grid<T> draggedGrid;


//        public TwinColGrid(final Collection<T> itemPool) {
//            this.itemPool = itemPool;
//            this(DataProvider.ofCollection(new LinkedHashSet<>(itemPool)));
//        }

//        /**
//         * Constructs a new TwinColGrid with caption and data provider for options.
//         *
//         * @param caption the caption to set, can be {@code null}
//         * @param dataProvider the data provider, not {@code null}
//         */
//        public TwinColGrid(final String caption, final ListDataProvider<T> dataProvider) {
//            this(dataProvider);
////            setCaption(caption);
//        }

//        /**
//         * Constructs a new TwinColGrid with caption and the given options.
//         *
//         * @param caption the caption to set, can be {@code null}
//         * @param options the options, cannot be {@code null}
//         */
//        public TwinColGrid(final String caption, final Collection<T> options) {
//            this(caption, DataProvider.ofCollection(new LinkedHashSet<>(options)));
//        }

        /**
         * Constructs a new TwinColGrid with data provider for options.
         */
//        public TwinColGrid(final ListDataProvider<T> allItemsDataProvider) {
        public TwinColGrid(final Collection<T> itemPool) {
            this.itemPool = itemPool;
//            super();
//            itemPool = allItemsDataProvider.getItems();
//            initLeftItems(itemPool);

//            setPoolDataProvider(poolDataProvider);

            getContent().setAlignItems(FlexComponent.Alignment.STRETCH);
            getContent().setHeight(null);

            this.leftGridDataProvider = DataProvider.ofCollection(new LinkedHashSet<>());
            leftGrid.setDataProvider(this.leftGridDataProvider);
            this.rightGridDataProvider = DataProvider.ofCollection(new LinkedHashSet<>());
            rightGrid.setDataProvider(this.rightGridDataProvider);

            leftGrid.setSelectionMode(Grid.SelectionMode.MULTI);
            leftGrid.setId("left-grid");
            leftGrid.setClassName("vizman-simple-grid");
//            setPoolDataProvider(itemPool);

            rightGrid.setSelectionMode(Grid.SelectionMode.MULTI);
            rightGrid.setId("right-grid");
            rightGrid.setClassName("vizman-simple-grid");

            moveSelectedItemsToLeftButton.setIcon(new Icon(VaadinIcon.ANGLE_LEFT));
            moveSelectedItemsToLeftButton.setWidth("3em");
//            moveAllItemsToLeftButton.setIcon(new Icon(VaadinIcon.ANGLE_DOUBLE_LEFT));
//            moveAllItemsToLeftButton.setWidth("3em");
            moveSelectedItemsToRightButton.setIcon(new Icon(VaadinIcon.ANGLE_RIGHT));
            moveSelectedItemsToRightButton.setWidth("3em");
//            moveSelectedItemsToRightButton.setIcon(new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
//            moveAllItemsToRightButton.setWidth("3em");

            moveAllItemsToLeftButton.addClickListener(e -> {
                leftGridDataProvider.getItems().stream().forEach(leftGrid.getSelectionModel()::select);
//                updateLeftGridItems(new LinkedHashSet<>(leftGrid.getSelectedItems()), new HashSet<>());
                updateLeftGridItems(rightGrid.getSelectedItems());
            });

            moveSelectedItemsToLeftButton.addClickListener(e -> {
//                updateLeftGridItems(new LinkedHashSet<>(rightGrid.getSelectedItems()), new HashSet<>());
                updateLeftGridItems(rightGrid.getSelectedItems());
            });

            moveSelectedItemsToRightButton.addClickListener(e -> {
                updateRightGridItems(leftGrid.getSelectedItems());
            });

            moveAllItemsToRightButton.addClickListener(e -> {
                rightGridDataProvider.getItems().stream().forEach(rightGrid.getSelectionModel()::select);
                updateRightGridItems(leftGrid.getSelectedItems());
            });

            buttonContainer = new VerticalLayout(moveSelectedItemsToLeftButton, moveSelectedItemsToRightButton);
            buttonContainer.setSpacing(false);
            buttonContainer.setSizeUndefined();

            final HorizontalLayout container = new HorizontalLayout(leftGrid, buttonContainer, rightGrid);
//            container.setSizeFull();
//            container.setAlignItems(FlexComponent.Alignment.STRETCH);
//            leftGrid.setSizeFull();
//            rightGrid.setSizeFull();
//            container.setExpandRatio(leftGrid, 1f);
//            container.setExpandRatio(rightGrid, 1f);


            this.getContent().add(container);
//            setSizeUndefined();
        }

//    private void setPoolDataProvider(ListDataProvider<T> poolDataProvider) {
    private void setPoolDataProvider(Collection<T> itemPool) {
//        this.rightGridDataProvider = poolDataProvider;
        rightGridDataProvider = new ListDataProvider<>(new LinkedHashSet<>(itemPool));
        rightGrid.setDataProvider(rightGridDataProvider);
        if (leftGridDataProvider != null) {
            leftGridDataProvider.getItems().clear();
            leftGridDataProvider.refreshAll();
        }
    }


//        public void initLeftItems(Set<T> leftItems, Collection<T> allItems) {
        public void initLeftItems(Set<T> leftItems) {
//            itemPool = new LinkedHashSet(allItems);
//            itemPool.removeAll(leftItems);
            rightGridDataProvider.getItems().clear();
            rightGridDataProvider.getItems().addAll(itemPool);
            leftGridDataProvider.getItems().clear();
            leftGridDataProvider.getItems().addAll(leftItems);
//            updateLeftGridItems(rightGrid.getSelectedItems());
//            rightGrid.setDataProvider(new ListDataProvider<>(itemPool));
//            updateLeftGridItems(new LinkedHashSet<>(items), new HashSet<>());
        }

        public TwinColGrid<T> withHeight(final String height) {
            leftGrid.setHeight(height);
            rightGrid.setHeight(height);
            return this;
        }

        /**
         * Sets the number of rows in the selects. If the number of rows is set to 0 or less, the actual number of displayed rows is determined implicitly by the
         * selects.
         * <p>
         * If a height is set (using {@link #withHeight(String)} or {@link #withHeight(float, Unit)}) it overrides the number of rows. Leave the height undefined to
         * use this method.
         *
         * @param rows the number of rows to set.
         */
        public TwinColGrid<T> withRows(int rows) {
            if (rows < 0) {
                rows = 0;
            }
//            leftGrid.setHeightByRows(rows);
//            rightGrid.setHeightByRows(rows);
            leftGrid.setHeightByRows(false);
            rightGrid.setHeightByRows(false);
//            markAsDirty();
            return this;
        }

//        /**
//         * Returns the number of rows in the selects.
//         *
//         * @return the number of rows visible
//         */
//        public int getRows() {
////            return (int) leftGrid.getHeightByRows();
//            return 10;
//        }


    /**
     * Sets the text shown above the right column. {@code null} clears the caption.
     *
     * @param rightColumnCaption The text to show, {@code null} to clear
     */
//        public TwinColGrid<T> withRightColumnCaption(final String rightColumnCaption) {
//            rightGrid.setCaption(rightColumnCaption);
//            markAsDirty();
//            return this;
//        }

        /**
         * Adds a new text column to this {@link Grid} with a value provider. The column will use a {@link TextRenderer}. The value is converted to a String using
         * {@link Object#toString()}. In-memory sorting will use the natural ordering of elements if they are mutually comparable and otherwise fall back to
         * comparing the string representations of the values.
         *
         * @param valueProvider the value provider
         *
         * @return the new column
         */
        public <V> TwinColGrid<T> addColumn(final ValueProvider<T, V> valueProvider, final String caption) {
            leftGrid.addColumn(valueProvider).setHeader(caption);
            rightGrid.addColumn(valueProvider).setHeader(caption);
            return this;
        }

        public TwinColGrid<T> showAddAllButton() {
//            buttonContainer.addComponent(moveAllItemsToLeftButton, 0);
            buttonContainer.add(moveAllItemsToLeftButton);
            return this;
        }

        public TwinColGrid<T> showRemoveAllButton() {
//            buttonContainer.addComponent(moveAllItemsToRightButton, buttonContainer.getComponentCount());
            buttonContainer.add(moveAllItemsToRightButton);
            return this;
        }

        public TwinColGrid<T> withSizeFull() {
//            super.setSizeFull();
            getContent().setSizeFull();
            return this;
        }

    private void setLeftItems(final Set<T> leftItems) {
        rightGridDataProvider.getItems().addAll(itemPool);
        rightGridDataProvider.getItems().removeAll(leftItems);
        leftGridDataProvider.refreshAll();

        leftGridDataProvider.getItems().clear();;
        leftGridDataProvider.getItems().addAll(leftItems);
        rightGridDataProvider.refreshAll();

        leftGrid.getSelectionModel().deselectAll();
        rightGrid.getSelectionModel().deselectAll();
    }

    private void updateLeftGridItems(final Set<T> selectedRightItems) {
        rightGridDataProvider.getItems().removeAll(selectedRightItems);
        rightGridDataProvider.refreshAll();

        leftGridDataProvider.getItems().addAll(selectedRightItems);
        leftGridDataProvider.refreshAll();

        leftGrid.getSelectionModel().deselectAll();
        rightGrid.getSelectionModel().deselectAll();
    }

    private void updateRightGridItems(final Set<T> selectedLeftItems) {
        leftGridDataProvider.getItems().removeAll(selectedLeftItems);
        leftGridDataProvider.refreshAll();

        rightGridDataProvider.getItems().addAll(selectedLeftItems);
        rightGridDataProvider.refreshAll();

        leftGrid.getSelectionModel().deselectAll();
        rightGrid.getSelectionModel().deselectAll();
    }



    @Override
//    public void setValue(final Set<T> value) {
    public void setValue(Object value) {
        Objects.requireNonNull(value);
        final Set<T> valueItems = ((Set<T>)value).stream().map(Objects::requireNonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        setLeftItems(valueItems);
    }


//    @Override
//    public void setValue(final Set<T> value) {
//        Objects.requireNonNull(value);
//        final Set<T> newValues = value.stream().map(Objects::requireNonNull)
//                .collect(Collectors.toCollection(LinkedHashSet::new));
//        updateSelection(newValues, new LinkedHashSet<>(leftGrid.getSelectedItems()));
//    }

    /**
     * Returns the current value of this object which is an immutable set of the currently selected items.
     *
     * @return the current selection
     */
    @Override
    public Set<T> getValue() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(leftGridDataProvider.getItems()));
    }

//    @Override
//    protected void setPresentationValue(Object o) {
//
//    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener valueChangeListener) {
        return null;
    }

//    @Override
//    public Registration addValueChangeListener(ValueChangeListener<Set<T>> valueChangeListener) {
////    public Registration addValueChangeListener(final ValueChangeListener<Set<T>> listener) {
//        return rightGridDataProvider.addDataProviderListener(
//                e -> {
//                    listener.valueChange(new ValueChangeEvent<>(TwinColGrid.this, new LinkedHashSet<>(rightGridDataProvider.getItems()), true));
//                });
//    }

    @Override
    public boolean isReadOnly() {
//        return super.isReadOnly();
        return false;
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
//        return super.isRequiredIndicatorVisible();
        return false;
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
//        leftGrid.setSelectionMode(readOnly?SelectionMode.NONE:SelectionMode.MULTI);
//        rightGrid.setSelectionMode(readOnly?SelectionMode.NONE:SelectionMode.MULTI);
//        addButton.setEnabled(!readOnly);
//        removeButton.setEnabled(!readOnly);
//        addAllButton.setEnabled(!readOnly);
//        removeAllButton.setEnabled(!readOnly);
    }

    @Override
    public void setRequiredIndicatorVisible(final boolean visible) {
//        super.setRequiredIndicatorVisible(visible);
    }




//        /**
//         * Adds drag n drop support between grids.
//         *
//         * @return
//         */
//        public TwinColGrid<T> withDragAndDropSupport() {
//            configDragAndDrop(leftGrid, rightGrid);
//            configDragAndDrop(rightGrid, leftGrid);
//            return this;
//        }

        /**
         * Returns the text shown above the right column.
         *
         * @return The text shown or {@code null} if not set.
         */
//        public String getRightColumnCaption() {
//            return rightGrid.getCaption();
//        }

        /**
         * Sets the text shown above the left column. {@code null} clears the caption.
         *
         * @param leftColumnCaption The text to show, {@code null} to clear
         */
//        public TwinColGrid<T> withLeftColumnCaption(final String leftColumnCaption) {
//            leftGrid.setCaption(leftColumnCaption);
//            markAsDirty();
//            return this;
//        }

        /**
         * Returns the text shown above the left column.
         *
         * @return The text shown or {@code null} if not set.
         */
//        public String getLeftColumnCaption() {
//            return leftGrid.getCaption();
//        }


//    /**
//     * Returns the current value of this object which is an immutable set of the currently selected items.
//     *
//     * @return the current selection
//     */
//    @Override
//    public Set<T> getStringValue() {
//        return Collections.unmodifiableSet(new LinkedHashSet<>(rightGridDataProvider.getItems()));
//    }


//    public void setValue(final Set<T> value) {
//        Objects.requireNonNull(value);
//        final Set<T> newValues = value.stream().map(Objects::requireNonNull)
//                .collect(Collectors.toCollection(LinkedHashSet::new));
//        updateLeftGridItems(newValues, new LinkedHashSet<>(leftGrid.getSelectedItems()));
//        updateLeftGridItems(rightGrid.getSelectedItems());
//    }

//    @Override
//    public void setValue(Object o) {
//
//    }
//
//    /**
//     * Returns the current value of this object which is an immutable set of the currently selected items.
//     *
//     * @return the current selection
//     */
//    @Override
//    public Set<T> getStringValue() {
//        return Collections.unmodifiableSet(new LinkedHashSet<>(rightGridDataProvider.getItems()));
//    }

//    @Override
//    public Registration addValueChangeListener(ValueChangeListener valueChangeListener) {
//        return null;
//    }

////    @Override
////    public Registration addValueChangeListener(final ValueChangeListener<Set<T>> listener) {
////        return rightGridDataProvider.addDataProviderListener(
////                e -> {
////                    listener.valueChange(new ValueChangeEvent<>(TwinColGrid.this, new LinkedHashSet<>(rightGridDataProvider.getItems()), true));
////                });
////    }
//
//        @Override
//        public boolean isReadOnly() {
////            return super.isReadOnly();
//            return true;
//        }
//
//        @Override
//        public boolean isRequiredIndicatorVisible() {
////            return super.isRequiredIndicatorVisible();
//            return true;
//        }
//
//        @Override
//        public void setReadOnly(final boolean readOnly) {
//            leftGrid.setSelectionMode(readOnly? Grid.SelectionMode.NONE: Grid.SelectionMode.MULTI);
//            rightGrid.setSelectionMode(readOnly? Grid.SelectionMode.NONE: Grid.SelectionMode.MULTI);
//            moveSelectedItemsToLeftButton.setEnabled(!readOnly);
//            moveSelectedItemsToRightButton.setEnabled(!readOnly);
//            moveAllItemsToLeftButton.setEnabled(!readOnly);
//            moveAllItemsToRightButton.setEnabled(!readOnly);
//        }
//
//        @Override
//        public void setRequiredIndicatorVisible(final boolean visible) {
////            super.setRequiredIndicatorVisible(visible);
//        }


//        @SuppressWarnings("unchecked")
//        private void configDragAndDrop(final Grid<T> sourceGrid, final Grid<T> targetGrid) {
//            final GridDragSource<T> dragSource = new GridDragSource<>(sourceGrid);
//            dragSource.setEffectAllowed(EffectAllowed.MOVE);
//            dragSource.setDragImage(VaadinIcons.COPY);
//
//            final Set<T> draggedItems = new LinkedHashSet<>();
//
//            dragSource.addGridDragStartListener(event -> {
//                draggedGrid = sourceGrid;
//                if (!(draggedGrid.getSelectionModel() instanceof NoSelectionModel)) {
//                    if (event.getComponent().getSelectedItems().isEmpty()) {
//                        draggedItems.addAll(event.getDraggedItems());
//                    } else {
//                        draggedItems.addAll(event.getComponent().getSelectedItems());
//                    }
//                }
//            });
//
//            dragSource.addGridDragEndListener(event -> {
//                if (event.getDropEffect() == DropEffect.MOVE) {
//                    if (draggedGrid == null) {
//                        draggedItems.clear();
//                        return;
//                    }
//                    final ListDataProvider<T> dragGridSourceDataProvider = (ListDataProvider<T>) draggedGrid.getDataProvider();
//                    dragGridSourceDataProvider.getItems().removeAll(draggedItems);
//                    dragGridSourceDataProvider.refreshAll();
//
//                    draggedItems.clear();
//
//                    draggedGrid.deselectAll();
//                    draggedGrid = null;
//                }
//            });
//
//            final GridDropTarget<T> dropTarget = new GridDropTarget<>(targetGrid, DropMode.ON_TOP);
//            dropTarget.setDropEffect(DropEffect.MOVE);
//            dropTarget.addGridDropListener(event -> {
//                event.getDragSourceExtension().ifPresent(source -> {
//                    if (source instanceof GridDragSource && draggedGrid != event.getComponent()) {
//                        final ListDataProvider<T> dragGridTargetDataProvider = (ListDataProvider<T>) event.getComponent().getDataProvider();
//                        dragGridTargetDataProvider.getItems().addAll(draggedItems);
//                        dragGridTargetDataProvider.refreshAll();
//                    } else {
//                        draggedGrid = null;
//                    }
//                });
//            });
//        }

//        public Registration addLeftGridSelectionListener(final SelectionListener<T> listener) {
//            return leftGrid.addSelectionListener(listener);
//        }
//
//        public Registration addRightGridSelectionListener(final SelectionListener<T> listener) {
//            return rightGrid.addSelectionListener(listener);
//        }

    }
