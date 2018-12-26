package eu.japtor.vizman.app;

import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.repository.PersonRepo;
import eu.japtor.vizman.backend.repository.RoleRepo;
import eu.japtor.vizman.backend.repository.ZakRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringComponent
public class DataGenerator implements HasLogger {

    static private final String ROLE_ADMIN_NAME = "ROLE_ADMIN";
    static private final String ROLE_USER_NAME = "ROLE_USER";
    static private final String ROLE_MANAGER_NAME = "ROLE_MANAGER";

    static private final String PERSON_ADMIN_USERNAME = "admin";
    static private final String PERSON_USER_USERNAME = "user";
    static private final String PERSON_MANAGER_USERNAME = "manag";

    @Autowired
    private ZakRepo zakRepo;
    @Autowired
    private PersonRepo personRepo;
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    public DataGenerator(
            RoleRepo roleRepo
            , PersonRepo personRepo
            , ZakRepo zakRepo
    ) {
        this.roleRepo = roleRepo;
        this.personRepo = personRepo;
        this.zakRepo = zakRepo;
    }


    @PostConstruct
    public void loadData() {
//        if (roleRepo.count() != 0L) {
//            getLogger().info("Using existing database");
//            return;
//        }

        getLogger().info("START: Generating initial data");
        getLogger().info("(1) Generating roles");

        if (null == roleRepo.findTopByName(ROLE_ADMIN_NAME)) {
            getLogger().info("    ...generating " + ROLE_ADMIN_NAME);
            Set<Perm> adminPerms = new HashSet<>();
            adminPerms.addAll(Arrays.asList(
                    Perm.VIEW_ALL,
                    Perm.MODIFY_ALL));
            createRole(1L, ROLE_ADMIN_NAME, "Administrátor - všechna existující oprávnění", adminPerms);
        }

        if (null == roleRepo.findTopByName(ROLE_USER_NAME)) {
            getLogger().info("    ...generating " + ROLE_USER_NAME);
            Set<Perm> userPerms = new HashSet<>();
            userPerms.addAll(Arrays.asList(
                    Perm.DOCH_USE,
                    Perm.ZAK_BASIC_READ));
            createRole(2L, ROLE_USER_NAME, "Uživatel - běžná oprávnění", userPerms);
        }

        if (null == roleRepo.findTopByName(ROLE_MANAGER_NAME)) {
            getLogger().info("    ...generating " + ROLE_MANAGER_NAME);
            Set<Perm> userPerms = new HashSet<>();
            userPerms.addAll(Arrays.asList(
                    Perm.DOCH_USE,
                    Perm.ZAK_BASIC_READ,
                    Perm.ZAK_EXT_READ,
                    Perm.ZAK_BASIC_MODIFY,
                    Perm.ZAK_EXT_MODIFY));
            createRole(3L, ROLE_MANAGER_NAME, "Manager - jako ROLE_USER plus editace zakázek/honorářů a fakturace", userPerms);
        }




        getLogger().info("(2) Generating users");

        if (null == personRepo.findTopByUsernameIgnoreCase(PERSON_ADMIN_USERNAME)) {
            getLogger().info("    ...generating " + PERSON_ADMIN_USERNAME);
            Person personAdmin = new Person();
            personAdmin.setId(1001L);
            personAdmin.setJmeno("Admin");
            personAdmin.setPrijmeni("Systemak");
            personAdmin.setUsername(PERSON_ADMIN_USERNAME);
            personAdmin.setPassword(PERSON_ADMIN_USERNAME);
            personAdmin.setRoles(Stream.of(roleRepo.findTopByName(ROLE_ADMIN_NAME))
                    .collect(Collectors.toSet()));
            personRepo.save(personAdmin);
        }

        if (null == personRepo.findTopByUsernameIgnoreCase(PERSON_USER_USERNAME)) {
            getLogger().info("    ...generating " + PERSON_USER_USERNAME);
            Person personUser = new Person();
            personUser.setId(1002L);
            personUser.setJmeno("User");
            personUser.setPrijmeni("Běžný");
            personUser.setUsername(PERSON_USER_USERNAME);
            personUser.setPassword(PERSON_USER_USERNAME);
            personUser.setRoles(Stream.of(roleRepo.findTopByName(ROLE_USER_NAME))
                    .collect(Collectors.toSet()));
            personRepo.save(personUser);
        }

        if (null == personRepo.findTopByUsernameIgnoreCase(PERSON_MANAGER_USERNAME)) {
            getLogger().info("    ...generating " + PERSON_MANAGER_USERNAME);
            Person personUser = new Person();
            personUser.setId(1003L);
            personUser.setJmeno("Manager");
            personUser.setPrijmeni("Zkušený");
            personUser.setUsername(PERSON_MANAGER_USERNAME);
            personUser.setPassword(PERSON_MANAGER_USERNAME);
            personUser.setRoles(Stream.of(roleRepo.findTopByName(ROLE_MANAGER_NAME))
                    .collect(Collectors.toSet()));
            personRepo.save(personUser);
        }

        getLogger().info("END: Generating initial data");
    }

//    private Zak createZak(String cisloZakazky, String text) {
//        Zak zak = new Zak();
//        zak.setCisloZakazky(cisloZakazky);
//        zak.setText(text);
//        return zak;
//    }

    @Transactional
    public Role createRole(Long id, String name, String description, Set<Perm> perms) {

        Role role = new Role();
        role.setId(id);
        role.setName(name);
        role.setDescription(description);
        role.setPerms(perms);
        roleRepo.save(role);
        return role;
    }
}
