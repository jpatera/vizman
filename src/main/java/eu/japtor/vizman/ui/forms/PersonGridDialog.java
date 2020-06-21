package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.ui.components.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class PersonGridDialog extends AbstractGridDialog<Person> implements HasLogger {

    public static final String DIALOG_WIDTH = "800px";
    public static final String DIALOG_HEIGHT = null;
    private static final String CLOSE_STR = "Zavřít";

    private Button closeButton;
    private HorizontalLayout personGridBar;

    private HorizontalLayout leftBarPart;
    private HorizontalLayout rightBarPart;

    private List<Person> currentItemList;
    private Role role;

    Grid<Person> personGrid;
    private FlexLayout titleComponent;

    private PersonService personService;


    public PersonGridDialog(
            PersonService personService
            , String mainTitle
    ) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setItemNames(ItemType.PERSON);

        if (null == mainTitle) {
            getMainTitle().setText("UŽIVATELÉ");
        } else {
            getMainTitle().setText(mainTitle);
        }

        this.personService = personService;

        getGridContainer().add(
                initPersonGridBar()
                , initPersonGrid()
        );
    }

    // Title for grid bar - not needed for person wages dialog
    // -------------------------------------------------------
    private Component initTitleComponent() {
        titleComponent = new FlexLayout(
                initTitle()
        );
        titleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        return titleComponent;
    }

    private Component initTitle() {
        H4 zakTitle = new H4();
        zakTitle.setText(ItemNames.getNomP(ItemType.PERSON));
        zakTitle.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-right", "1em");
        return zakTitle;
    }

    // -------------------------------------------------------


    public void openDialog(final Role role) {

        this.role = role;

        if (null == role || StringUtils.isEmpty(role.getName())) {
            getMainTitle().setText("UŽIVATELÉ role");
        } else {
            getMainTitle().setText("UŽIVATELÉ role " + role.getName());
        }

        initDataAndControls();

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        dateVystavField.setLocale(new Locale("cs", "CZ"));
//        dateDuzpField.setLocale(new Locale("cs", "CZ"));

//        initFaktDataAndControls(currentItem, currentOperation);
        this.open();
    }

    private void initDataAndControls() {
        deactivateListeners();
        this.currentItemList = role.getPersons();

        personGrid.setItems(currentItemList);
        initControlsOperability();
        activateListeners();
    }


    private void refreshHeaderMiddleBox(Zak zakItem) {
    }

    private void deactivateListeners() {
    }

    private void activateListeners() {
    }

    private void initControlsOperability() {
        closeButton.setEnabled(true);
    }

    private void adjustControlsOperability(final boolean hasChanges) {
        closeButton.setEnabled(true);
    }


    private Component initPersonGridBar() {
        personGridBar = new HorizontalLayout();
//        FlexLayout zakGridBar = new FlexLayout();
        personGridBar.setSpacing(false);
        personGridBar.setPadding(false);
        personGridBar.getStyle().set("margin-left", "-3em");
//        zakGridBar.setWidth("100%");
        personGridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        personGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        personGridBar.add(
                new Ribbon()
        );
        return personGridBar;
    }


    private Component initPersonGrid() {
        personGrid = new Grid<>();
        personGrid.setColumnReorderingAllowed(true);
        personGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        personGrid.setId("person-grid");
        personGrid.setClassName("vizman-simple-grid");
        personGrid.getStyle().set("marginTop", "0.5em");

        personGrid.addColumn(Person::getPrijmeni)
                .setHeader("Příjmení")
                .setWidth("12em")
                .setTextAlign(ColumnTextAlign.START)
//                .setFlexGrow(0)
                .setResizable(true)
        ;
        personGrid.addColumn(Person::getJmeno)
                .setHeader("Jméno")
                .setWidth("10em")
                .setTextAlign(ColumnTextAlign.START)
//                .setFlexGrow(0)
                .setResizable(true)
        ;
        personGrid.addColumn(Person::getUsername)
                .setHeader("Uživatel")
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.START)
//                .setFlexGrow(0)
                .setResizable(true)
        ;

        personGrid.getColumns().forEach(column -> column.setAutoWidth(true));

        return personGrid;
    }

// ------------------------------------------------------------

    @Override
    public Component initDialogButtonBar() {
        HorizontalLayout bar = new HorizontalLayout();

        closeButton = new Button(CLOSE_STR);
        closeButton.setAutofocus(true);
        closeButton.getElement().setAttribute("theme", "primary");
        closeButton.addClickListener(e -> closeClicked(true));

        leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);

        rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(
                closeButton
        );

//        buttonBar.getStyle().set("margin-top", "0.2em");
        bar.setClassName("buttons");
        bar.setSpacing(false);
        bar.setPadding(false);
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        bar.add(
                leftBarPart
                , rightBarPart
        );
        return bar;
    }


    private void closeClicked(boolean closeAfterSave) {
        if (closeAfterSave) {
            this.close();
        }
    }

//  --------------------------------------------

    @Override
    public List<Person> getCurrentItemList() {
        return currentItemList;
    }

}
