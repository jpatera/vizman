package eu.japtor.vizman.app;

import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.entity.Usr;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.RoleRepo;
import eu.japtor.vizman.backend.repository.UsrRepo;
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
    private UsrRepo usrRepo;
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    public DataGenerator(
            RoleRepo roleRepo
            , UsrRepo usrRepo
            , ZakRepo zakRepo
    ) {
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

        getLogger().info("... generating roles");

        Set<Perm> adminPerms = new HashSet<>();
        adminPerms.addAll(Arrays.asList(Perm.VIEW_ALL, Perm.MANAGE_ALL));
        createRoleIfNotFound("ROLE_ADMIN", adminPerms);

        Set<Perm> userPerms = new HashSet<>();
        userPerms.addAll(Arrays.asList(
                Perm.ZAK_VIEW_BASIC_READ, Perm.KONT_VIEW_BASIC_READ,
                Perm.ZAK_VIEW_BASIC_MANAGE, Perm.KONT_VIEW_BASIC_MANAGE));
        createRoleIfNotFound("ROLE_USER", userPerms);


        getLogger().info("... generating users");

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
    public Role createRoleIfNotFound(String name, Set<Perm> perms) {

        Role role = roleRepo.findTopByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
            role.setDescription("Description of "+ name);
            role.setPerms(perms);
            roleRepo.save(role);
        }
        return role;
    }
}
