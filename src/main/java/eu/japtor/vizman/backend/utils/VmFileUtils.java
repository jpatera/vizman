package eu.japtor.vizman.backend.utils;

import eu.japtor.vizman.backend.service.CfgPropsCache;
import org.apache.commons.io.FileUtils;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class VmFileUtils {

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

    public static Path getKontDocPath(final String docRoot, final String kontFolder) {
        Path path = Paths.get(docRoot, kontFolder);
//        Path path2 = path.resolve(path);
//        Path path3 = path.relativize(path);
        return path;
    }



    static List<ProjFolder> rootZakProjFolders;
    static {

        ProjFolder folderInzenyring = new ProjFolder(null, "INZENYRING");
        ProjFolder folderOrg = new ProjFolder(null, "INZENYRING");

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
}
