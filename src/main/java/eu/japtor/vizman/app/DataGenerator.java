package eu.japtor.vizman.app;

import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.ZakRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringComponent
public class DataGenerator implements HasLogger {

    private ZakRepository zakRepository;

    @Autowired
    public DataGenerator(ZakRepository zakRepository) {
        this.zakRepository = zakRepository;
    }

    @PostConstruct
    public void loadData() {
        if (zakRepository.count() != 0L) {
            getLogger().info("Using existing database");
            return;
        }

        getLogger().info("Generating demo data");

        getLogger().info("... generating Zak");
        zakRepository.save(createZak("2018.01", "Zakázka NULA JEDNA"));
        zakRepository.save(createZak("2018.02", "Zakázka NULA DVA"));

        getLogger().info("Generated demo data");
    }

    private Zak createZak(String zakNum, String zakTitle) {
        Zak zak = new Zak();
        zak.setZakNum(zakNum);
        zak.setZakTitle(zakTitle);
        return zak;
    }
}
