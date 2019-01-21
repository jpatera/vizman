package eu.japtor.vizman.backend.utils;

import eu.japtor.vizman.backend.service.CfgPropsCache;
import org.apache.commons.io.FileUtils;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class VzmFileUtils {

    /**
     * Replace illegal characters in a filename with "_"
     * illegal characters :
     *           : \ / * ? | < > "
     * @param name
     * @return
     */
    public static String sanitizeFilename(String name) {
        return name.replaceAll("[:\\\\/*\"?|<>]", "_");
    }

    public static void validatePathname(String pathname) {
        Assert.notNull(pathname, "Cesta k adresáři/souboru nesmí být null");
        File file = new File(pathname);
        validateFile(file);
    }

    public static void validateFile(File file) {
        try {
            file.getCanonicalPath();
        } catch (SecurityException esec) {
            esec.printStackTrace();
        } catch (IOException eio) {
            eio.printStackTrace();
        }
    }


    public static String normalizeDirname(final String dirname) {
        String normDirname = Normalizer.normalize(dirname, Normalizer.Form.NFD);
        normDirname = normDirname.replaceAll("\\p{M}", "");   // for unicode
        normDirname = normDirname.replaceAll(" ", "_");   // for unicode
//        filepath = filepath.replaceAll("[^\\p{ASCII}]", "");    // otherwise
        return normDirname;
    }

    public static String NormalizeDirnamesAndJoin(final String dirname1, final String dirname2) {
        return normalizeDirname(dirname1) + "__" + normalizeDirname(dirname2);
    }

//    public static Path getKontDocPath(final String docRoot, final String kontFolder) {
//        Path path = Paths.get(docRoot, kontFolder);
////        Path path2 = path.resolve(path);
////        Path path3 = path.relativize(path);
//        return path;
//    }



    static List<ProjFolder> rootZakProjFolders;
    static List<ProjFolder> rootKontProjFolders = new ArrayList<>();

    static {

        ProjFolder folderInzenyring = new ProjFolder(null, "INZENYRING");
        ProjFolder folderOrg = new ProjFolder(null, "ORG");

        List<ProjFolder> orgSubFolders = new ArrayList<>();
        orgSubFolders.add(new ProjFolder(folderOrg, "1_SEZNAMY"));
        orgSubFolders.add(new ProjFolder(folderOrg, "2_TEXTY_PROJEKTU"));
        orgSubFolders.add(new ProjFolder(folderOrg, "3_PRIPOMINKY"));
        orgSubFolders.add(new ProjFolder(folderOrg, "4_ZAZNAMY"));

        folderOrg.setChildFolders(orgSubFolders);

        List<ProjFolder> rootProjFolders = new ArrayList<ProjFolder>();
        rootProjFolders.add(folderInzenyring);
        rootProjFolders.add(folderOrg);
    }


    static class ProjFolder {

        ProjFolder parentFolder;
        String folderName;
        List<ProjFolder> childFolders;

        public ProjFolder(ProjFolder parentFolder, String folderName) {
            this.parentFolder = parentFolder;
            this.folderName = folderName;
        }

        public ProjFolder getParentFolder() {
            return parentFolder;
        }

        public void setParentFolder(ProjFolder parentFolder) {
            this.parentFolder = parentFolder;
        }

        public String getFolderName() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }

        public List<ProjFolder> getChildFolders() {
            return childFolders;
        }

        public void setChildFolders(List<ProjFolder> childFolders) {
            this.childFolders = childFolders;
        }
    }

// ----------------------------------------------------------


    public static boolean kontProjRootExists(String projRoot, String kontFolder) {
        return kontProjRootExists(getKontProjRootPath(projRoot, kontFolder));
    }

    public static boolean kontProjRootExists(Path kontDocRootPath) {
        return (kontDocRootPath.toFile().exists());
    }

    public static Path getKontProjRootPath(String projRoot, String kontFolder) {
        return Paths.get(
                null == projRoot ? "###-NOT-SET-KONT-PROJ-ROOT-###" : projRoot
                , null == kontFolder ? "###-NOT-SET-KONT-PROJ-FOLDER-###" : kontFolder
        );
    }

    public static boolean createKontProjDirs(String projRoot, String kontFolder) {

        Path kontDirProjRootPath = getKontDocRootPath(projRoot, kontFolder);
        if (!kontDirProjRootPath.toFile().exists()) {
            try {
                Files.createDirectories(kontDirProjRootPath);
                return true;
            } catch (IOException ioExceptionObj) {
                System.out.println("Problem Occured While Creating The Directory Structure = " + ioExceptionObj.getMessage());
            }
        }
        return false;
    }

// ----------------------------------------------------------


    public static boolean kontDocRootExists(String docRoot, String kontFolder) {
        return kontDocRootExists(getKontDocRootPath(docRoot, kontFolder));
    }

    public static boolean kontDocRootExists(Path kontDocRootPath) {
        return (kontDocRootPath.toFile().exists());
    }

    public static Path getKontDocRootPath(String docRoot, String kontFolder) {
        return Paths.get(
                null == docRoot ? "###-NOT-SET-KONT-DOC-ROOT-###" : docRoot
                , null == kontFolder ? "###-NOT-SET-KONT-DOC-FOLDER-###" : kontFolder
        );
    }

    public static boolean createKontDocDirs(String docRoot, String kontFolder) {

        Path kontDocRootPath = getKontDocRootPath(docRoot, kontFolder);
        if (!kontDocRootPath.toFile().exists()) {
            try {
                Files.createDirectories(kontDocRootPath);
                Path zeroSodPath = Paths.get(kontDocRootPath.toString(), "0__SOD");
                Files.createDirectories(zeroSodPath);
                return true;
            } catch (IOException ioExceptionObj) {
                System.out.println("Problem Occured While Creating The Directory Structure = " + ioExceptionObj.getMessage());
            }
        }
        return false;
    }

// --------------------------------------------------


    public static boolean zakProjRootExists(String projRoot, String kontFolder, String zakFolder) {
        return zakProjRootExists(getZakProjRootPath(projRoot, kontFolder, zakFolder));
    }

    public static boolean zakProjRootExists(Path zakProjRootPath) {
        return (zakProjRootPath.toFile().exists());
    }

    public static Path getZakProjRootPath(String projRoot, String kontFolder, String zakFolder) {
        return Paths.get(
                null == projRoot ? "###-NOT-SET-KONT-PROJ-ROOT-###" : projRoot
                , null == kontFolder ? "###-NOT-SET-KONT-PROJ-FOLDER-###" : kontFolder
                , null == zakFolder ? "###-NOT-SET-ZAK-PROJ-FOLDER-###" : zakFolder
        );
    }

    public static boolean createZakProjDirs(String projRoot, String kontFolder, String zakFolder) {

        Path zakProjRootPath = getZakProjRootPath(projRoot, kontFolder, zakFolder);
        if (!zakProjRootPath.toFile().exists()) {
            try {
                Files.createDirectories(zakProjRootPath);
                Path inzenyringPath = Paths.get(zakProjRootPath.toString(), "INZENYRING");
                Files.createDirectories(inzenyringPath);
                Path orgPath = Paths.get(zakProjRootPath.toString(), "ORG");
                Files.createDirectories(orgPath);
                return true;
            } catch (IOException ioExceptionObj) {
                System.out.println("Problem Occured While Creating The Directory Structure = " + ioExceptionObj.getMessage());
            }
        }
        return false;
    }

// --------------------------------------------------


    public static boolean zakDocRootExists(String docRoot, String kontFolder, String zakFolder) {
        return zakDocRootExists(getZakDocRootPath(docRoot, kontFolder, zakFolder));
    }

    public static boolean zakDocRootExists(Path zakDocRootPath) {
        return (zakDocRootPath.toFile().exists());
    }

    public static Path getZakDocRootPath(String docRoot, String kontFolder, String zakFolder) {
        return Paths.get(
                null == docRoot ? "###-NOT-SET-KONT-DOC-ROOT-###" : docRoot
                , null == kontFolder ? "###-NOT-SET-KONT-DOC-FOLDER-###" : kontFolder
                , null == zakFolder ? "###-NOT-SET-ZAK-DOC-FOLDER-###" : zakFolder
        );
    }

    public static boolean createZakDocDirs(String docRoot, String kontFolder, String zakFolder) {

        Path zakDocRootPath = getZakDocRootPath(docRoot, kontFolder, zakFolder);
        if (!zakDocRootPath.toFile().exists()) {
            try {
                Files.createDirectories(zakDocRootPath);
                Path dopisyPath = Paths.get(zakDocRootPath.toString(), "Odesilaci_dopisy");
                Files.createDirectories(dopisyPath);
                Path fakturyPath = Paths.get(zakDocRootPath.toString(), "Faktury");
                Files.createDirectories(fakturyPath);
                return true;
            } catch (IOException ioExceptionObj) {
                System.out.println("Problem Occured While Creating The Directory Structure = " + ioExceptionObj.getMessage());
            }
        }
        return false;
    }



}
