package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.service.RoleService;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.TwinColGrid;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RoleEditorDialog extends AbstractComplexFormDialog<Role> {

    private TextField nameField; // = new TextField("Username");
    private TextField descriptionField; // = new TextField("Jméno");
    private TwinColGrid<Perm> twinPermsGrid;

    private RoleService roleService;
    private PersonService personService;
    private Set<Perm> permsPool;

    public RoleEditorDialog(BiConsumer<Role, Operation> itemSaver,
                            Consumer<Role> itemDeleter,
                            RoleService roleService,
                            List<Perm> allPerms)
    {
//        super(GrammarGender.FEMININE, "role", "role","roli", itemSaver, itemDeleter);
        super(itemSaver, itemDeleter);

        setWidth("900px");
//        setHeight("600px");

        this.roleService = roleService;
        this.personService = personService;
        this.permsPool = new HashSet<>(allPerms);

        nameField = new TextField("Název");
        descriptionField = new TextField("Popis");
        getFormLayout().add(initNameField());
        getFormLayout().add(initDescriptionField());
        getFormLayout().add(initPermsField(permsPool));

//        roleGridContainer = buildRoleGridContainer(roleTwinGrid);
    }


//    private VerticalLayout buildRoleGridContainer(Grid<Role> grid) {
//        VerticalLayout roleGridContainer = new VerticalLayout();
//        roleGridContainer.setClassName("view-container");
//        roleGridContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
//        roleGridContainer.add(grid);
//        return roleGridContainer;
//    }

//    private void initRoleGrid() {
////        roleTwinGrid.setId("person-grid");  // .. same ID as is used in shared-styles grid's dom module
//        roleTwinGrid.addColumn(Role::getName).setHeader("Název").setWidth("3em").setResizable(true);
//        roleTwinGrid.addColumn(Role::getDescription).setHeader("Popis").setWidth("8em").setResizable(true);
//    }

//    private void addStatusField() {
//        statusField.setDataProvider(DataProvider.ofItems(PersonStatus.values()));
//        getFormLayout().add(statusField);
//        getBinder().forField(statusField)
////                .withConverter(
////                        new StringToIntegerConverter("Must be a number"))
//                .bind(Person::getStatus, Person::setStatus);
//    }

    private TextField initNameField() {
        nameField = new TextField();
        getBinder().forField(nameField)
                .withConverter(String::trim, String::trim)
                // TODO: fix validator
//                .withValidator(new StringLengthValidator(
//                        "Role nesmí obsahovat mezery ani diakritiku",
//                        3, null))
                .withValidator(
                        name -> (currentOperation != Operation.ADD) ?
                            true : roleService.fetchRoleByName(name) == null,
                        "Role s tímto názvem již existuje, zvol jiný název")
                .bind(Role::getName, Role::setName);
        return nameField;
    }

    private TextField initDescriptionField() {
        descriptionField = new TextField();
        getBinder().forField(descriptionField)
                .bind(Role::getDescription, Role::setDescription);
        return descriptionField;
    }

    private Component initPermsField(final Set<Perm> allPerms) {
//        this.allRolesDataProvider = DataProvider.ofCollection(roleRepo.findAll());
//        this.personRoles = DataProvider.ofCollection(getCurrentItem().getRoles());

//        twinGrid = new Grid<>();
//        roleTwinGrid.setLeftDataProvider(personRoles);
//        initRoleGrid();
//        roleTwinGrid.addColumn(Role::getName).setHeader("Název").setWidth("3em").setResizable(true);
//        roleTwinGrid.addColumn(Role::getDesription).setHeader("Popis").setWidth("8em").setResizable(true);
//        this.add(roleGridContainer);

//        twinRolesGrid = new TwinColGrid<>(allRolesDataProvider)
//        twinRolesGrid = new TwinColGrid<>(roleRepo.findAll())
        twinPermsGrid = new TwinColGrid<>(allPerms)
                .addColumn(Perm::getAuthority, "Oprávnění")
//            .addColumn(Role::getDesription, "Popis")
//            .withLeftColumnCaption("Available books")
//            .withRightColumnCaption("Added books")
//            .showAddAllButton()
                .withSizeFull()
                .withHeight("200px")
//            .withRows(4)
//            .withRows(availableBooks.size() - 3)
//            .withDragAndDropSupport()
        ;
//        twinRolesGrid.setValue(getCurrentItem().getRoles());
//        FormLayout.FormItem formItem = getFormLayout().addFormItem(twinRolesGrid, "Label");
        twinPermsGrid.setId("twin-col-grid");
        twinPermsGrid.getElement().setAttribute("colspan", "2");
        twinPermsGrid.getContent().setAlignItems(FlexComponent.Alignment.STRETCH);
        twinPermsGrid.getContent().getStyle().set("padding-right", "0em");
        twinPermsGrid.getContent().getStyle().set("padding-left", "0em");
        twinPermsGrid.getContent().getStyle().set("padding-top", "2.5em");
        twinPermsGrid.getContent().getStyle().set("padding-bottom", "2.5em");
//        getBinder().forField(twinRolesGrid).bind(Person::getRoles, Person::setRoles);
        getBinder().bind(twinPermsGrid, Role::getPerms, Role::setPerms);
        return twinPermsGrid;
    }


    @Override
    protected void confirmDelete() {
        long roleCount = roleService.countAllRoles();
//        if (personCount > 0) {
            openConfirmDeleteDialog("Zrušit roli",
                    "Opravdu zrušit roli '" + getCurrentItem().getName() + "' ?",
                    "");
//        } else {
//            deleteKont(getCurrentItem());
//        }
    }
}
