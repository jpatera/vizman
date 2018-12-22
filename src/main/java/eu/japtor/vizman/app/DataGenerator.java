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
        if (roleRepo.count() != 0L) {
            getLogger().info("Using existing database");
            return;
        }

        getLogger().info("Generating demo data");
        getLogger().info("... generating roles");

        Set<Perm> adminPerms = new HashSet<>();
        adminPerms.addAll(Arrays.asList(Perm.VIEW_ALL, Perm.MANAGE_ALL));
        createRoleIfNotFound(1L, "ROLE_ADMIN", adminPerms);

        Set<Perm> userPerms = new HashSet<>();
        userPerms.addAll(Arrays.asList(
                Perm.ZAK_VIEW_BASIC_READ,
                Perm.ZAK_VIEW_BASIC_MANAGE));
        createRoleIfNotFound(2L, "ROLE_USER", userPerms);


        getLogger().info("... generating persons");

        Person personAdmin = new Person();
        Role adminRole = roleRepo.findTopByName("ROLE_ADMIN");
        personAdmin.setId(1001L);
        personAdmin.setJmeno("Admin");
        personAdmin.setPrijmeni("Systemak");
        personAdmin.setUsername("admin");
        personAdmin.setPassword("admin");
        personAdmin.setRoles(Stream.of(adminRole).collect(Collectors.toSet()));
        personRepo.save(personAdmin);

        Person personUser = new Person();
        Role userRole = roleRepo.findTopByName("ROLE_USER");
        personUser.setId(1002L);
        personUser.setJmeno("User");
        personUser.setPrijmeni("Běžný");
        personUser.setUsername("user");
        personUser.setPassword("user");
        personUser.setRoles(Stream.of(userRole).collect(Collectors.toSet()));
        personRepo.save(personUser);

//        getLogger().info("... generating Zak");
//        zakRepo.save(createZak("2018.01", "Zakázka NULA JEDNA"));
//        zakRepo.save(createZak("2018.02", "Zakázka NULA DVA"));

        getLogger().info("Generated demo data");
    }

//    private Zak createZak(String cisloZakazky, String text) {
//        Zak zak = new Zak();
//        zak.setCisloZakazky(cisloZakazky);
//        zak.setText(text);
//        return zak;
//    }

    @Transactional
    public Role createRoleIfNotFound(Long id, String name, Set<Perm> perms) {

        Role role = roleRepo.findTopByName(name);
        if (role == null) {
            role = new Role();
            role.setId(id);
            role.setName(name);
            role.setDescription("Description of "+ name);
            role.setPerms(perms);
            roleRepo.save(role);
        }
        return role;
    }
}
