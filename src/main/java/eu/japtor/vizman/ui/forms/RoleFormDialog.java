package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.service.RoleService;
import eu.japtor.vizman.ui.components.Gap;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.OperationResult;
import eu.japtor.vizman.ui.components.TwinColJpsGrid2;
import org.claspina.confirmdialog.ConfirmDialog;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RoleFormDialog extends AbstractComplexFormDialog<Role> {

    private TextField nameField;
    private TextField descriptionField;
    private TwinColJpsGrid2<Perm> permsTwinGrid;

    private RoleService roleService;
    private PersonService personService;
//    private List<Perm> permsPool;
    private List<Perm> permsPool;

//    private Binder<Role> binder = new Binder<>();
    private Registration binderChangeListener = null;
//    private Registration permsTwinGridListener = null;
//    private Role curItem;
    private Role origItemCopy;
    private Operation currentOperation;
    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;
    private boolean readOnly;

    public RoleFormDialog(
            BiConsumer<Role, Operation> itemSaver
            , Consumer<Role> itemDeleter
            , RoleService roleService
            , PersonService personService
            , List<Perm> allPerms
    ){
        super("1200px", null
                , false
                , false
                , itemSaver
                , itemDeleter
                , true
        );

        this.roleService = roleService;
        this.personService = personService;
        this.permsPool = new LinkedList<>(allPerms);

        getFormLayout().add(
                initNameField()
                , initDescriptionField()
                , initPermsTwinColGrid(permsPool)
        );
    }

    public void openDialog(
            boolean readonly
            , Role role
            , Operation  operation
    ) {
        this.readOnly = readonly;
//        this.curItem = role;
        this.origItemCopy = Role.getNewInstance(role);
        this.currentOperation = operation;

        if (null != permsTwinGrid) {
            getFormLayout().remove(permsTwinGrid);  // ..otherwise one extra checkbox is always added
        }
        getFormLayout().add(
//                initPermsTwinColJpsGrid(permsPool)
                initPermsTwinColGrid(permsPool)
        );

        openInternal(role, operation, isRoleAdmin(role), !isRoleAdmin(role), new Gap(), null);

        // Fix combos if necessary
    }

    private boolean isRoleAdmin(final Role role) {
        return "ROLE_ADMIN".equals(role.getName());
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
        nameField = new TextField("Název role");
        nameField.getElement().setAttribute("colspan", "2");
        getBinder().forField(nameField)
                .withConverter(String::trim, String::trim)
                // TODO: fix validator
//                .withValidator(new StringLengthValidator(
//                        "Role nesmí obsahovat mezery ani diakritiku",
//                        3, null))
                .withValidator(
                        name -> (currentOperation != Operation.ADD) ?
                            true : roleService.fetchRoleByNameIgnoreCase(name) == null,
                        "Role s tímto názvem již existuje, zvol jiný název")
                .bind(Role::getName, Role::setName);
        nameField.setValueChangeMode(ValueChangeMode.LAZY);
        return nameField;
    }

    private TextField initDescriptionField() {
        descriptionField = new TextField("Popis role");
        descriptionField.getElement().setAttribute("colspan", "2");
        getBinder().forField(descriptionField)
                .bind(Role::getDescription, Role::setDescription);
        descriptionField.setValueChangeMode(ValueChangeMode.LAZY);
        return descriptionField;
    }


    private Component initPermsTwinColGrid(final List<Perm> allPerms) {
        permsTwinGrid = new TwinColJpsGrid2<>(allPerms, " ")
                .addSortableColumn(Perm::getAuthority, Comparator.comparing(Perm::getAuthority), "Oprávnění")
                .addColumn(Perm::getDescription, "Popis")
                .withLeftColumnCaption("Nepřidělená OPRÁVNĚNÍ")
                .withRightColumnCaption("Přidělená OPRÁVNĚNÍ")
                .withoutAddAllButton()
                .withoutRemoveAllButton()
                .withHeight("25em")
                .withColAutoWidth()
//                .withDragAndDropSupport()
        ;
        permsTwinGrid.setId("perms-twin-col-grid");
        permsTwinGrid.getElement().setAttribute("colspan", "2");
        permsTwinGrid.getLeftGrid().setClassName("vizman-simple-grid");
        permsTwinGrid.getRightGrid().setClassName("vizman-simple-grid");

        getBinder().bind(permsTwinGrid, Role::getPerms, Role::setPerms);
        permsTwinGrid.doInitialGridSorts(SortDirection.ASCENDING);
        return permsTwinGrid;
    }

    @Override
    protected void refreshHeaderMiddleBox(Role item) {

    }

    @Override
    protected void activateListeners() {
        if (null != getBinder()) {
            binderChangeListener = getBinder().addValueChangeListener(e -> {
                adjustControlsOperability(false, true, true, isDirty(),  getBinder().isValid());
            });
        }
//        permsTwinGridListener = permsTwinGrid.addValueChangeListener(e -> {
//            adjustControlsOperability(false, true, true, true,  getBinder().isValid());
//        });
    }

    @Override
    protected void deactivateListeners() {
        if (null != binderChangeListener) {
            try {
                binderChangeListener.remove();
            } catch (Exception e)  {
                // do nothing
            }
        }
//        if (null != permsTwinGridListener) {
//            try {
//                permsTwinGridListener.remove();
//            } catch (Exception e)  {
//                // do nothing
//            }
//        }
    }


    @Override
    public void initControlsOperability(final boolean readOnly, final boolean deleteAllowed, final boolean canDelete) {
        super.initControlsOperability(readOnly, deleteAllowed, canDelete);
        nameField.setReadOnly(readOnly);
        descriptionField.setReadOnly(readOnly);

        // FIXME: https://github.com/FlowingCode/TwinColGridAddon/issues/11
        //        Setting readonly to false adds an additional checkbox column
        // permsTwinGrid.setReadOnly(readOnly);
    }

    @Override
    public void adjustControlsOperability(
            final boolean readOnly
            , final boolean deleteAllowed
            , final boolean canDelete
            , final boolean hasChanges
            , final boolean isValid
    ) {
        super.adjustControlsOperability(readOnly, deleteAllowed, canDelete, hasChanges, isValid);
    }

    @Override
    protected void confirmDelete() {
        if (canDeleteItem(getCurrentItem())) {
            openConfirmDeleteDialog("Zrušení role",
                    "Opravdu zrušit roli '" + getCurrentItem().getName() + "' ?",
                    "");
        } else {
            ConfirmDialog
                    .createWarning()
                    .withCaption("Zrušení role")
                    .withMessage("Roli nelze zrušit, existují přiřazení uživatelé")
                    .open();
        }
    }

    private boolean canDeleteItem(final Role itemToDelete) {
        long ret = personService.countOfAssignedPerson(itemToDelete.getId());
        return ret <= 0L;
    }

    public Role getOrigItemCopy() {
        return origItemCopy;
    }
}
