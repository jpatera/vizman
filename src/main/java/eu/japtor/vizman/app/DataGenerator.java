package eu.japtor.vizman.app;

import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.japtor.vizman.backend.entity.Usr;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.UsrRepo;
import eu.japtor.vizman.backend.repository.ZakRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringComponent
public class DataGenerator implements HasLogger {

    private ZakRepo zakRepo;
    private UsrRepo usrRepo;

    @Autowired
    public DataGenerator(final UsrRepo usrRepo, final ZakRepo zakRepo) {
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

        getLogger().info("... generating Usr");
        usrRepo.save(createUsr("admin", "admin", "Administrator", "Dlouhán"));
        usrRepo.save(createUsr("user", "user", "User", "Userovatej"));

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

    private Usr createUsr(String username, String password, String firstName, String lastName) {
        Usr usr = new Usr();
        usr.setUsername(username);
        usr.setPassword(password);
        usr.setFirstName(firstName);
        usr.setLastName(lastName);
        return usr;
    }
}
