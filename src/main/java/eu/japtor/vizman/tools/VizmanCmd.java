package eu.japtor.vizman.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;


//@SpringBootApplication
public class VizmanCmd {
// public class VizmanCmd implements CommandLineRunner {

//    @Autowired
//    KontZakFolderTool tool;

    public static void main(String[] args) {
        SpringApplication.run(VizmanCmd.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
////        KontZakFolderTool tool = new KontZakFolderTool();
//        tool.printTest();
////        tool.fixKontFolders();
//    }
}

