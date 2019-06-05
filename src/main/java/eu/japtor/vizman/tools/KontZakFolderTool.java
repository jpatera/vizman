package eu.japtor.vizman.tools;

import eu.japtor.vizman.backend.repository.KontRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


// @Service
public class KontZakFolderTool {


//    private KontService kontService;
//    private final KontRepo kontRepo;
//
//    @Autowired
//    public KontZakFolderTool(KontRepo kontRepo) {
//        super();
//        this.kontRepo = kontRepo;
//    }

//    @Autowired
//    public setKontService() {
//        kontService =
//    }

    @Autowired
    private ApplicationContext context;

//    public void fixKontFolders() {
//        KontRepo kontRepo = (KontRepo) new KontRepoCustomImpl();
//        KontService kontService = new KontServiceImpl(kontRepo);
//        List<Kont> konts = (List<Kont>) kontService.fetchAll();
//        for (Kont kont: konts) {
//            if (null == kont.getFolder()) {
//                System.out.println("Missing folder for KONT " + kont.getCkont() + "  " + kont.getText());
//            }
//        }
//    }

    public void printTest() {
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("**********************************************");

        context.getBean(KontRepo.class);
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("**********************************************");

    }
}
