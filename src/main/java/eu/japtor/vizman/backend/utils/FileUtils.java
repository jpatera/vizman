package eu.japtor.vizman.backend.utils;

import org.springframework.util.Assert;
import sun.misc.JavaIOAccess;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;

public class FileUtils {

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


    public static String normalizeFilepath(String filepath) {
        filepath = Normalizer.normalize(filepath, Normalizer.Form.NFD);
        filepath = filepath.replaceAll("\\p{M}", "");   // for unicode
//        filepath = filepath.replaceAll("[^\\p{ASCII}]", "");    // otherwise
        return filepath;
    }
}
