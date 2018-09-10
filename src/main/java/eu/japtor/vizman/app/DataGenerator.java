package eu.japtor.vizman.app;

import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.japtor.vizman.backend.entity.Privilege;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.entity.Usr;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.PrivilegeRepo;
import eu.japtor.vizman.backend.repository.RoleRepo;
import eu.japtor.vizman.backend.repository.UsrRepo;
import eu.japtor.vizman.backend.repository.ZakRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringComponent
public class DataGenerator implements HasLogger {

    @Autowired
    private ZakRepo zakRepo;
    @Autowired
    private UsrRepo usrRepo;
    @Autowired
    private RoleRepo roleRepo;
//    @Autowired
//    private PrivilegeRepo privilegeRepo;

    @Autowired
    public DataGenerator(
//            PrivilegeRepo privilegeRepo
            RoleRepo roleRepo
            , UsrRepo usrRepo
            , ZakRepo zakRepo
    ) {
//        this.privilegeRepo = privilegeRepo;
        this.roleRepo = roleRepo;
        this.usrRepo = usrRepo;
        this.zakRepo = zakRepo;
    }


    @PostConstruct
    public void loadData() {
        if (zakRepo.count() != 0L) {
            getLogger().info("Using existing database");
            return;
        }

        getLogger().info("Generating demo data");

//        getLogger().info("... generating Privilege");
//
//        createPrivilegeIfNotFound("VIEW_ALL");
//        createPrivilegeIfNotFound("ZAK_VIEW_BASIC_READ");
//        createPrivilegeIfNotFound("ZAK_VIEW_BASIC_MANAGE");
//        createPrivilegeIfNotFound("ZAK_VIEW_EXT_READ");
//        createPrivilegeIfNotFound("ZAK_VIEW_EXT_MANAGE");

        getLogger().info("... generating roles");

        Set<Privilege> adminPrivileges = new HashSet<>();
        adminPrivileges.addAll(Arrays.asList(Privilege.VIEW_ALL, Privilege.MANAGE_ALL));
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);

        Set<Privilege> userPrivileges = new HashSet<>();
        adminPrivileges.addAll(Arrays.asList(Privilege.ZAK_VIEW_BASIC_READ));
        createRoleIfNotFound("ROLE_USER", userPrivileges);


        getLogger().info("... generating users");

//        if (alreadySetup)
//            return;
//        Privilege readPrivilege
//                = createPrivilegeIfNotFound("READ_PRIVILEGE");
//        Privilege writePrivilege
//                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

//        List<Privilege> adminPrivileges = Arrays.asList(
//                readPrivilege, writePrivilege);
//        createRoleIfNotFound("ADMIN", adminPrivileges);
//        createRoleIfNotFound("USER", Arrays.asList(readPrivilege));

        Usr usrAdmin = new Usr();
        Role adminRole = roleRepo.findTopByName("ROLE_ADMIN");
        usrAdmin.setFirstName("Admin");
        usrAdmin.setLastName("Systemak");
        usrAdmin.setUsername("admin");
        usrAdmin.setPassword("admin");
//        user.setEmail("test@test.com");
        usrAdmin.setRoles(Stream.of(adminRole).collect(Collectors.toSet()));
//        usr.setEnabled(true);
        usrRepo.save(usrAdmin);

        Usr usrUser = new Usr();
        Role userRole = roleRepo.findTopByName("ROLE_USER");
        usrUser.setFirstName("User");
        usrUser.setLastName("Běžný");
        usrUser.setUsername("user");
        usrUser.setPassword("user");
        usrUser.setRoles(Stream.of(userRole).collect(Collectors.toSet()));
        usrRepo.save(usrUser);

        getLogger().info("... generating Zak");
        zakRepo.save(createZak("2018.01", "Zakázka NULA JEDNA"));
        zakRepo.save(createZak("2018.02", "Zakázka NULA DVA"));

        getLogger().info("Generated demo data");
    }

    private Zak createZak(String zakNum, String zakTitle) {
        Zak zak = new Zak();
        zak.setZakNum(zakNum);
        zak.setZakTitle(zakTitle);
        return zak;
    }

    private Usr createUsr(
            String username, String password, String firstName, String lastName,
            Set<Role> roles
    ) {
        Usr usr = new Usr();
        usr.setUsername(username);
        usr.setPassword(password);
        usr.setFirstName(firstName);
        usr.setLastName(lastName);
        usr.setRoles(roles);
        return usr;
    }

//    @Transactional
//    private Role createRoleIfNotFound(String name) {
    private Role createRoleIfNotFound(String name, Set<Privilege> privileges) {

        Role role = roleRepo.findTopByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
            role.setDescription("Description of "+ name);
            role.setPrivileges(privileges);
            roleRepo.save(role);
        }
        return role;
    }

//    private Privilege createPrivilegeIfNotFound(String name) {
//
//        Privilege privilege = privilegeRepo.findTopByName(name);
//        if (privilege == null) {
//            privilege = new Privilege();
//            privilege.setName(name);
//            privilege.setDescription("Description of "+ name);
//            privilegeRepo.save(privilege);
//        }
//        return privilege;
//    }

//    Role adminRole = roleRepo.findByName("ADMIN");
//    Usr user = new Usr();
//        user.setFirstName("Test");
//        user.setLastName("Test");
//        user.setPassword(passwordEncoder.encode("test"));
//        user.setEmail("test@test.com");
//        user.setRoles(Arrays.asList(adminRole));
//        user.setEnabled(true);
//        userRepository.save(user);
}
