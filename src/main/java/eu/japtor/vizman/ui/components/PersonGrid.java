package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.entity.ZakBasic;

import java.util.function.Consumer;
import java.util.function.Function;

public class PersonGrid extends Grid<Person> {


    // Person Grid field keys:
    public static final String KZCISLO_COL_KEY = "zak-bg-kzcislo";
    public static final String ROK_COL_KEY = "zak-bg-rok";
    public static final String SKUPINA_COL_KEY = "zak-bg-skupina";
    public static final String OBJEDNATEL_COL_KEY = "zak-bg-objednatel";
    public static final String KZTEXT_COL_KEY = "zak-bg-kztext";
    public static final String SEL_COL_KEY = "zak-bg-select";
    public static final String ARCH_COL_KEY = "zak-bg-arch";

    TextField kzCisloFilterField;
    Select<Boolean> archFilterField;
    Select<Integer> rokFilterField;
    Select<String> skupinaFilterField;
    TextField objednatelFilterField;
    TextField kzTextFilterField;

    private Boolean initFilterArchValue;
    private boolean archFieldVisible;
    private boolean selectFieldVisible;
    private Consumer<Integer> selectionChanger;
    private Function<ZakBasic, Boolean> checkBoxEnabler;

    private int selCount;

    HeaderRow filterRow;


    public PersonGrid() {

        this.getStyle().set("marginTop", "0.5em");
        this.setMultiSort(true);
        this.setSelectionMode(Grid.SelectionMode.SINGLE);
        this.setColumnReorderingAllowed(true);
        this.setId("person-grid");  // .. same ID as is used in shared-styles grid's dom module

    }
}
