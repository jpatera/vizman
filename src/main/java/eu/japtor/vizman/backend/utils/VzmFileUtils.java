package eu.japtor.vizman.backend.utils;

import com.vaadin.flow.data.provider.hierarchy.TreeData;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Fakt;
import eu.japtor.vizman.backend.entity.VzmFolderType;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.Zak;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class VzmFileUtils implements HasLogger {

    private final static Logger LOG = LoggerFactory.getLogger(VzmFileUtils.class);
    private static final String ZAK_DOPISY_FOLDER = "Odesilaci_dopisy";
    private static final String ZAK_FAKTURY_FOLDER = "Faktury";
    private static final String KONT_SOD_FOLDER = "0__SOD";

    private static final Pattern accentCharsPattern = Pattern.compile("\\p{M}");   // for unicode
    private static final Pattern illegalFileCharsPattern = Pattern.compile("[^\\w.-]");

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

    private static void validateFile(File file) {
        try {
            file.getCanonicalPath();
        } catch (SecurityException | IOException esec) {
            esec.printStackTrace();
        }
    }


    public static String normalizeDirFileName(final String dirname) {

        String normDirname = Normalizer.normalize(dirname, Normalizer.Form.NFD);
        normDirname = accentCharsPattern.matcher(normDirname).replaceAll("");
        normDirname = illegalFileCharsPattern.matcher(normDirname).replaceAll("_");   // for unicode
        return normDirname;
    }

    public static String NormalizeDirnamesAndJoin(final String dirname1, final String dirname2) {
        String dn1 = normalizeDirFileName(dirname1);
        String dn2 = normalizeDirFileName(dirname2);
        String devider = StringUtils.isBlank(dn2) && StringUtils.isBlank(dn2) ? "" : "__";
        return dn1 + devider + dn2;
    }

//    public static Path getKontDocPath(final String docRoot, final String kontFolder) {
//        Path path = Paths.get(docRoot, kontFolder);
////        Path path2 = path.resolve(path);
////        Path path3 = path.relativize(path);
//        return path;
//    }

//    private String getDocRootServer() {
//        return cfgPropsCache.getStringValue("app.document.root.server");
//    }

//    private String getProjRootServer() {
//        return cfgPropsCache.getStringValue("app.project.root.server");
//    }

    static List<ProjFolder> rootZakProjFolders;
    static List<ProjFolder> rootKontProjFolders = new ArrayList<>();

//    static {
//
//        ProjFolder folderInzenyring = new ProjFolder(null, "INZENYRING");
//        ProjFolder folderOrg = new ProjFolder(null, "ORG");
//
//        List<ProjFolder> orgSubFolders = new ArrayList<>();
//        orgSubFolders.add(new ProjFolder(folderOrg, "1_SEZNAMY"));
//        orgSubFolders.add(new ProjFolder(folderOrg, "2_TEXTY_PROJEKTU"));
//        orgSubFolders.add(new ProjFolder(folderOrg, "3_PRIPOMINKY"));
//        orgSubFolders.add(new ProjFolder(folderOrg, "4_ZAZNAMY"));
//
//        folderOrg.setChildFolders(orgSubFolders);
//
//        List<ProjFolder> rootProjFolders = new ArrayList<ProjFolder>();
//        rootProjFolders.add(folderInzenyring);
//        rootProjFolders.add(folderOrg);
//    }


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

    private static boolean kontProjRootExists(Path kontDocRootPath) {
        return (kontDocRootPath.toFile().exists());
    }

    private static Path getKontProjRootPath(String projRoot, String kontFolder) {
        return Paths.get(
                null == projRoot ? "###-NOT-SET-PROJ-ROOT-###" : projRoot
                , null == kontFolder ? "###-NOT-SET-KONT-PROJ-FOLDER-###" : kontFolder
        );
    }

    public static boolean createKontProjDirs(String projRoot, String kontFolderNew) {
        if (StringUtils.isBlank(kontFolderNew)) {
            return false;
        }
        try {
            Path kontDirProjRootPath = getKontProjRootPath(projRoot, kontFolderNew);
            if (!kontDirProjRootPath.toFile().exists()) {
                Files.createDirectories(kontDirProjRootPath);
                return true;
            }
//        } catch (IOException ioExceptionObj) {
        } catch (Exception e) {
            LOG.error("Problem while creating the KONT-PROJ dir structure: " + e.getMessage());
            LOG.debug("", e);
        }
        return false;
    }

// ----------------------------------------------------------


    public static boolean kontDocRootExists(String docRoot, String kontFolder) {
        return (getKontDocRootPath(docRoot, kontFolder)).toFile().exists();
    }


    public static Path getKontDocRootPath(String docRoot, String kontFolder) {
        return Paths.get(
                null == docRoot ? "###-NOT-SET-DOC-ROOT-###" : docRoot
                , null == kontFolder ? "###-NOT-SET-KONT-DOC-FOLDER-###" : kontFolder
        );
    }

    private static Path getFolderPath(File parentDir, String childFolder) {
        return Paths.get(
                null == parentDir ? "" : parentDir.toString()
                , null == childFolder ? "" : childFolder
        );
    }

    public static boolean createKontDocDirs(String docRoot, String kontFolderNew) {
        if (StringUtils.isBlank(kontFolderNew)) {
            return false;
        }
        try {
            Path kontDocRootPath = getKontDocRootPath(docRoot, kontFolderNew);
            if (!kontDocRootPath.toFile().exists()) {
                Files.createDirectories(kontDocRootPath);
            }
            Path zeroSodPath = Paths.get(kontDocRootPath.toString(), KONT_SOD_FOLDER);
            if (!zeroSodPath.toFile().exists()) {
                Files.createDirectories(zeroSodPath);
                return true;
            }
            LOG.info("KONT-DOC dir structure created for " + kontFolderNew);
            return true;
//        } catch (IOException ioExceptionObj) {
        } catch (Exception e) {
            LOG.error("Problem while creating the KONT-DOC dir structure: " + e.getMessage());
        }
        return false;
    }

// ============================================================

    public static class VzmFile extends File {
        private boolean vzmControledDir;
        private VzmFolderType vzmFolderType;
        private int vzmFolderLevel;

        public VzmFile (
                final String pathName
                , final boolean vzmControlledDir
                , final VzmFolderType vzmFolderType
                , final int vzmFolderLevel
        ) {
            super(pathName);
            this.vzmControledDir = vzmControlledDir;
            this.vzmFolderType = vzmFolderType;
            this.vzmFolderLevel = vzmFolderLevel;
        }

        public VzmFolderType getVzmFolderType() {
            return vzmFolderType;
        }

        public int getVzmFolderLevel() {
            return vzmFolderLevel;
        }

//        public VzmFile (final URI uri, final boolean vzmControledDir) {
//            super(uri);
//            this.vzmControledDir = vzmControledDir;
//        }

        public VzmFile (final Path path, final boolean vzmControlledDir, final VzmFolderType vzmFolderType, final int vzmFolderLevel) {
            this(path.toString(), vzmControlledDir, vzmFolderType, vzmFolderLevel);
//            this(path.Uri(), vzmControledDir);
        }

        public VzmFile (final File file, final boolean vzmControlledDir, final VzmFolderType vzmFolderType, final int vzmFolderLevel) {
            this(file.toString(), vzmControlledDir, vzmFolderType, vzmFolderLevel);
//            this(path.Uri(), vzmControledDir);
        }

        boolean isVzmControledDir() {
            return vzmControledDir;
        }

        public Stream<VzmFile> listVzmFiles(VzmFile parent, FilenameFilter fileFilter)  {
            if (null == parent) {
                return Stream.empty();
            }
            File[] filteredFiles = parent.listFiles(fileFilter);
            if (filteredFiles.length == 0) {
                return Stream.empty();
            }
            return Stream.of(filteredFiles)
                .map(file -> new VzmFile(file, true, VzmFolderType.OTHER, 0))
//                .collect(Collectors.toList())
            ;
        }

        public Stream<VzmFile> listVzmFiles(VzmFile parent)  {
            if (null == parent) {
                return Stream.empty();
            }
            File[] files = parent.listFiles();
            if (files.length == 0) {
                return Stream.empty();
            }
            return Stream.of(parent.listFiles())
                .map(file -> new VzmFile(file, true, VzmFolderType.OTHER, 0))
//                .collect(Collectors.toList())
            ;
        }

        public Stream<VzmFile> listVzmFolders(VzmFile parent)  {
            if (null == parent) {
                return Stream.empty();
            }

            VzmFolderType parentVzmFolderType = (null == parent ? VzmFolderType.KONT : parent.getVzmFolderType());
            int parentVzmFolderLevel = (null == parent ? 0 : parent.getVzmFolderLevel());

            List<VzmFile> vzmFolders = new ArrayList<>();
//        kontDocRootSubDirs.add(new VzmFile(getFolderPath(kontDocDir, KONT_SOD_FOLDER).toUri(), true));
            if (parentVzmFolderType == VzmFolderType.KONT) {
                if (parentVzmFolderLevel == 0) {
                    vzmFolders.add(new VzmFile(
                                getFolderPath(parent.getParentFile(), KONT_SOD_FOLDER).toString()
                                , true
                                , parentVzmFolderType
                                , 1
                            )
                    );
                } else if (parentVzmFolderLevel == 1) {

                }
//                vzmFolders.add(new VzmFile(getFolderPath(kontDocDir, KONT_SOD_FOLDER).toString(), true));
            } else if (parentVzmFolderType == VzmFolderType.ZAK) {
                if (parentVzmFolderLevel == 0) {
                    vzmFolders.add(new VzmFile(
                            getFolderPath(parent.getParentFile(), ZAK_DOPISY_FOLDER).toString()
                            , true
                            , VzmFolderType.ZAK
                            , 1
                            )
                    );
                    vzmFolders.add(new VzmFile(
                                    getFolderPath(parent.getParentFile(), ZAK_FAKTURY_FOLDER).toString()
                                    , true
                                    , VzmFolderType.ZAK
                                    , 1
                            )
                    );
//                } else if (parentVzmFolderLevel == 1) {
                }
            }
            if (vzmFolders.size() == 0) {
                return Stream.empty();
            } else {
                return vzmFolders.stream();
            }
        }
    }

    public static TreeData<VzmFile> getExpectedKontFolderTree(final String docRoot, final Kont kont) {
        Assert.notNull(kont, "CHYBA při generování adresářů: nedefinovaný kontrakt.");

        TreeData<VzmFile> expectedTree = new TreeData<>();
        String expectedKontFolder = getExpectedKontFolder(kont);
        VzmFile docRootDir = new VzmFile(docRoot, true, VzmFolderType.KONT, 0);
//        VzmFile kontDocDir = new VzmFile(getFolderPath(docRootDir, expectedKontFolder).toUri(), true);
        VzmFile kontDocDir = new VzmFile(getFolderPath(docRootDir, expectedKontFolder).toString(), true, VzmFolderType.KONT, 0);

        addExpectedKontDocSubDirs(expectedTree, kontDocDir, true);
        addExpectedKontZakSubDirs(expectedTree, kontDocDir, kont);

        return expectedTree;
    }

    public static TreeData<VzmFile> getExpectedZakDocFolderFilesTree(final String kontDocRoot, final Zak zak) {
        Assert.notNull(zak, "CHYBA při generování adresářů: nedefinovaná zakázka.");

        TreeData<VzmFile> expectedTree = new TreeData<>();
//        String kontDocRootDir = NormalizeDirnamesAndJoin(docRoot, getExpectedKontFolder(zak.getKont()));

        File kontDocRootDir = new File(kontDocRoot);
        String expectedZakFolder = getExpectedZakFolder(zak);
//        VzmFile zakDocDir = new VzmFile(getFolderPath(kontDocRootDir, expectedZakFolder).toUri(), true);
        VzmFile zakDocDir = new VzmFile(getFolderPath(kontDocRootDir, expectedZakFolder).toString(), true, VzmFolderType.ZAK, 0);

        addExpectedZakDocSubDirs(expectedTree, zakDocDir, true);
        return expectedTree;
    }

    public static String getExpectedKontFolder(final Kont kont) {
        if (null == kont) {
            return null;
        } else {
            return NormalizeDirnamesAndJoin(kont.getCkont(), kont.getText());
        }
    }

    public static String getExpectedZakFolder(final Zak zak) {
        if (null == zak) {
            return null;
        } else {
            return NormalizeDirnamesAndJoin(zak.getCzak().toString(), zak.getText());
        }
    }

    private static void addExpectedKontDocSubDirs(TreeData<VzmFile> kontDocTreeData, final VzmFile kontDocDir, boolean asRootItems) {
        List<VzmFile> kontDocRootSubDirs = new ArrayList<>();
//        kontDocRootSubDirs.add(new VzmFile(getFolderPath(kontDocDir, KONT_SOD_FOLDER).toUri(), true));
        kontDocRootSubDirs.add(new VzmFile(getFolderPath(kontDocDir, KONT_SOD_FOLDER).toString(), true, VzmFolderType.OTHER.KONT, 1));
        kontDocTreeData.addItems(asRootItems ? null : kontDocDir, kontDocRootSubDirs);
    }

    public static void addNotExpectedKontSubDirs(TreeData<VzmFile> kontDocTreeData, final VzmFile kontDocDir) {
        if (null == kontDocDir || !kontDocDir.exists()) {
             return;
        }
        try {
            Files.newDirectoryStream(kontDocDir.toPath()).forEach(realKontSubPath -> {
                if (kontDocTreeData.getChildren(null).stream().noneMatch(
                        treeKontSub -> treeKontSub.getName().equals(realKontSubPath.getFileName().toString()))) {
                    kontDocTreeData.addItem(null, new VzmFile(getFolderPath(kontDocDir, realKontSubPath.getFileName().toString()), false, VzmFolderType.OTHER, 99));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addExpectedKontZakSubDirs(TreeData<VzmFile> kontDocTreeData, final VzmFile kontDocDir, final Kont kont) {
        List<VzmFile> zakDocDirs = new ArrayList<>();
        kont.getZaks().forEach(zak -> {
            String expZakFolder = NormalizeDirnamesAndJoin(zak.getCzak().toString(), zak.getText());
//            VzmFile zakDocDir = new VzmFile(getFolderPath(kontDocDir, expZakFolder).toUri(), true);
            VzmFile zakDocDir = new VzmFile(getFolderPath(kontDocDir, expZakFolder).toString(), true, VzmFolderType.ZAK, 0);
            kontDocTreeData.addItem(null, zakDocDir);
            addExpectedZakDocSubDirs(kontDocTreeData, zakDocDir, false);
//            zakDocDirs.add(new VzmFile(getFolderPath(kontDocDir, expZakFolder).toUri(), true));
            zakDocDirs.add(new VzmFile(getFolderPath(kontDocDir, expZakFolder).toString(), true, VzmFolderType.ZAK, 0));
        });
    }

    private static void addExpectedZakDocSubDirs(TreeData<VzmFile> treeData, final VzmFile zakDocDir, boolean asRootItems) {
        List<VzmFile> zakDocSubDirs = new ArrayList<>();
//        zakDocSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_DOPISY_FOLDER).toUri(), true));
//        zakDocSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_FAKTURY_FOLDER).toUri(), true));
        zakDocSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_DOPISY_FOLDER).toString(), true, VzmFolderType.ZAK, 1));
        zakDocSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_FAKTURY_FOLDER).toString(), true, VzmFolderType.ZAK, 1));
        treeData.addItems(asRootItems ? null : zakDocDir, zakDocSubDirs);
    }

//    public static void addExpectedZakDocRootSubDirs(TreeData<VzmFile> zakDocTreeData, final VzmFile zakDocDir) {
//        List<VzmFile> zakDocRootSubDirs = new ArrayList<>();
////        zakDocRootSubDirs.add(new VzmFile(getFolderPath(zakDocDir, KONT_SOD_FOLDER).toUri(), true));
//        zakDocRootSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_DOPISY_FOLDER).toUri(), true));
//        zakDocRootSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_FAKTURY_FOLDER).toUri(), true));
//        zakDocTreeData.addItems(null, zakDocRootSubDirs);
//    }


    //    public static void addFilesToExpectedVzmTreeData(TreeData<VzmFile> expectedTreeData, VzmFile parent) {
    public static void addFilesToExpectedVzmTreeData(
            TreeData<VzmFile> expectedTreeData, File[] filesToBeScanned, VzmFile scannedDirsVzmParent) {
//    public static void addFilesToExpectedVzmTreeData(
//            TreeData<VzmFile> expectedTreeData, List<VzmFile> expectedVzmDirs, VzmFile parent) {

//        if (null != parent && !expectedTreeData.contains(parent)) {
//            return;
//        }
        if (null == filesToBeScanned || filesToBeScanned.length == 0) {
            return;
        }

//        List<VzmFile> expectedVzmDirs = expectedTreeData.getChildren(parent);
        for (File fileScanned : filesToBeScanned) {
//            VzmFile vzmFileScanned = new VzmFile(fileScanned.toURI(), false);
            VzmFile vzmFileScanned = new VzmFile(
                    fileScanned.toString()
                    , false
                    , null == scannedDirsVzmParent ? VzmFolderType.ROOT : scannedDirsVzmParent.getVzmFolderType()
                    , 99);

//            if (!expectedTreeData.contains(expectedDir)) {
//                continue;
//            }

            if (!expectedTreeData.contains(vzmFileScanned)) {
                expectedTreeData.addItem(scannedDirsVzmParent, vzmFileScanned);
            }
            if (vzmFileScanned.isFile()) {
                continue;
            } else if (vzmFileScanned.isDirectory()) {
                if (vzmFileScanned.exists()) {
                    File[] dirContent = vzmFileScanned.listFiles();
                    addFilesToExpectedVzmTreeData(
                            expectedTreeData, dirContent, vzmFileScanned
                    );
                }
//                    for (File file : dirContent) {
//                        if (file.isFile()) {
//                            expectedTreeData.addItem(
//                                    scannedDirsVzmParent, new VzmFile(file.toPath(), false));
//                        } else {
//    //                        VzmFile vzmFile = new VzmFile(file.toPath(), true);
//    //                        if (expectedTreeData.contains(expectedVzmDir)) {
//                            VzmFile nextDir = new VzmFile(file.toPath(), false);
//                            if (!expectedTreeData.contains(nextDir)) {
//                                expectedTreeData.addItem(
//                                        expectedVzmDir, new VzmFile(nextDir.toPath(), false)
//                                );
//                                addFilesToExpectedVzmTreeData(
//                                        expectedTreeData, nextDir.listFiles(), expectedVzmDir
//    //                                    expectedTreeData, expectedTreeData.getChildren(expectedVzmDir), expectedVzmDir
//                                );
//    //                        }
//                        }
//                    }
//                }
            }
        }
    }


// --------------------------------------------------


    public static boolean zakProjRootExists(String projRoot, String kontFolder, String zakFolder) {
        return zakProjRootExists(getZakProjRootPath(projRoot, kontFolder, zakFolder));
    }

    private static boolean zakProjRootExists(Path zakProjRootPath) {
        return (zakProjRootPath.toFile().exists());
    }

    private static Path getZakProjRootPath(String projRoot, String kontFolder, String zakFolder) {
        return Paths.get(
                null == projRoot ? "###-NOT-SET-PROJ-ROOT-###" : projRoot
                , null == kontFolder ? "###-NOT-SET-KONT-PROJ-FOLDER-###" : kontFolder
                , null == zakFolder ? "###-NOT-SET-ZAK-PROJ-FOLDER-###" : zakFolder
        );
    }

    public static boolean createZakProjDirs(String projRoot, String kontFolder, String zakFolderNew) {

        if (StringUtils.isBlank(kontFolder) || StringUtils.isBlank(zakFolderNew)) {
            return false;
        }

        try {
            Path zakProjRootPath = getZakProjRootPath(projRoot, kontFolder, zakFolderNew);
            if (!zakProjRootPath.toFile().exists()) {
                Files.createDirectories(zakProjRootPath);
            }
            Path inzenyringPath = Paths.get(zakProjRootPath.toString(), "INZENYRING");
            if (!inzenyringPath.toFile().exists()) {
                Files.createDirectories(inzenyringPath);
            }

            Path orgPath = Paths.get(zakProjRootPath.toString(), "ORG");
            if (!orgPath.toFile().exists()) {
                Files.createDirectories(orgPath);
            }
            Path orgSeznamyPath = Paths.get(orgPath.toString(), "1_SEZNAMY");
            if (!orgSeznamyPath.toFile().exists()) {
                Files.createDirectories(orgSeznamyPath);
            }
            Path orgTextyPath = Paths.get(orgPath.toString(), "2_TEXTY_PROJEKTU");
            if (!orgTextyPath.toFile().exists()) {
                Files.createDirectories(orgTextyPath);
            }
            Path orgPripominkyPath = Paths.get(orgPath.toString(), "3_PRIPOMINKY");
            if (!orgPripominkyPath.toFile().exists()) {
                Files.createDirectories(orgPripominkyPath);
            }
            Path orgZaznamyPath = Paths.get(orgPath.toString(), "4_ZAZNAMY");
            if (!orgZaznamyPath.toFile().exists()) {
                Files.createDirectories(orgZaznamyPath);
            }
            Path orgFotoPath = Paths.get(orgPath.toString(), "5_FOTO");
            if (!orgFotoPath.toFile().exists()) {
                Files.createDirectories(orgFotoPath);
            }
            Path orgAdPath = Paths.get(orgPath.toString(), "6_AD");
            if (!orgAdPath.toFile().exists()) {
                Files.createDirectories(orgAdPath);
            }
            Path orgVicepracePath = Paths.get(orgPath.toString(), "7_VICEPRACE");
            if (!orgVicepracePath.toFile().exists()) {
                Files.createDirectories(orgVicepracePath);
            }
            Path orgVtanoviskaPath = Paths.get(orgPath.toString(), "8_STANOVISKA");
            if (!orgVtanoviskaPath.toFile().exists()) {
                Files.createDirectories(orgVtanoviskaPath);
            }

            Path podkladyPath = Paths.get(zakProjRootPath.toString(), "PODKLADY");
            if (!podkladyPath.toFile().exists()) {
                Files.createDirectories(podkladyPath);
            }

            Path podkladyOutPath = Paths.get(zakProjRootPath.toString(), "PODKLADY_OUT");
            if (!podkladyOutPath.toFile().exists()) {
                Files.createDirectories(podkladyOutPath);
            }

            Path projektPath = Paths.get(zakProjRootPath.toString(), "PROJEKT");
            if (!projektPath.toFile().exists()) {
                Files.createDirectories(projektPath);
            }
            Path projStatikaPath = Paths.get(projektPath.toString(), "STATIKA");
            if (!projStatikaPath.toFile().exists()) {
                Files.createDirectories(projStatikaPath);
            }
            Path projVykresyPath = Paths.get(projektPath.toString(), "VYKRESY");
            if (!projVykresyPath.toFile().exists()) {
                Files.createDirectories(projVykresyPath);
            }
            Path projVymeryPath = Paths.get(projektPath.toString(), "VYMERY");
            if (!projVymeryPath.toFile().exists()) {
                Files.createDirectories(projVymeryPath);
            }

            Path projektDigital = Paths.get(zakProjRootPath.toString(), "PROJEKT_DIGITAL");
            if (!projektDigital.toFile().exists()) {
                Files.createDirectories(projektDigital);
            }

            LOG.info("ZAK-DOC dir structure created for " + kontFolder);
            return true;
//            }
//        } catch (IOException ioExceptionObj) {
//            System.out.println("Problem occured while creating the directory structure = " + ioExceptionObj.getMessage());
//        }
        } catch (Exception e) {
            LOG.error("Problem while creating the ZAK-DOC dir structure: " + e.getMessage());
            LOG.debug("", e);
        }
        return false;
    }

    public static boolean renameKontDocRoot(String docRoot, String kontFolderNew, String kontFolderOrig) {
        Path origKontDocRootPath = getKontDocRootPath(docRoot, kontFolderOrig);
        Path newKontDocRootPath = getKontDocRootPath(docRoot, kontFolderNew);
        File origKontDocRootDir = origKontDocRootPath.toFile();
        if (origKontDocRootDir.exists() && origKontDocRootDir.isDirectory()) {
            try {
                Files.move(origKontDocRootPath, newKontDocRootPath);
                return true;
            } catch (IOException ioExceptionObj) {
                System.out.println("Problem occurred while renaming kontrakt document root dir = " + ioExceptionObj.getMessage());
            }
        }
        return false;
    }

    public static boolean renameKontProjRoot(String projRoot, String kontFolderNew, String kontFolderOrig) {
        Path origKontProjRootPath = getKontProjRootPath(projRoot, kontFolderOrig);
        Path newKontProjRootPath = getKontDocRootPath(projRoot, kontFolderNew);
        File origKontProjRootDir = origKontProjRootPath.toFile();
        if (origKontProjRootDir.exists() && origKontProjRootDir.isDirectory()) {
            try {
                Files.move(origKontProjRootPath, newKontProjRootPath);
                return true;
            } catch (IOException ioExceptionObj) {
                System.out.println("Problem occurred while renaming kontrakt project root dir = " + ioExceptionObj.getMessage());
            }
        }
        return false;
    }

// --------------------------------------------------


    public static boolean zakDocRootExists(String docRoot, String kontFolder, String zakFolder) {
        return (getZakDocRootPath(docRoot, kontFolder, zakFolder)).toFile().exists();
    }

//    public static boolean zakDocRootExists(Path zakDocRootPath) {
//        return (zakDocRootPath.toFile().exists());
//    }

    public static Path getZakDocRootPath(String docRoot, String kontFolder, String zakFolder) {
        return Paths.get(
                null == docRoot ? "###-NOT-SET-KONT-DOC-ROOT-###" : docRoot
                , null == kontFolder ? "###-NOT-SET-KONT-DOC-FOLDER-###" : kontFolder
                , null == zakFolder ? "###-NOT-SET-ZAK-DOC-FOLDER-###" : zakFolder
        );
    }

    public static boolean createZakDocDirs(String docRoot, String kontFolder, String zakFolderNew) {

        if (StringUtils.isBlank(kontFolder) || StringUtils.isBlank(zakFolderNew)) {
            return false;
        }

        try {
            Path zakDocRootPath = getZakDocRootPath(docRoot, kontFolder, zakFolderNew);
            if (!zakDocRootPath.toFile().exists()) {
                Files.createDirectories(zakDocRootPath);
            }
            Path dopisyPath = Paths.get(zakDocRootPath.toString(), ZAK_DOPISY_FOLDER);
            if (!dopisyPath.toFile().exists()) {
                Files.createDirectories(dopisyPath);
            }
            Path fakturyPath = Paths.get(zakDocRootPath.toString(), ZAK_FAKTURY_FOLDER);
            if (!fakturyPath.toFile().exists()) {
                Files.createDirectories(fakturyPath);
                return true;
            }
//        } catch (IOException ioExceptionObj) {
//            System.out.println("Problem occured while creating zakazka directory structure = " + ioExceptionObj.getMessage());
        } catch (Exception e) {
            LOG.error("Problem while creating the ZAK-DOC dir structure: " + e.getMessage());
            LOG.debug("", e);
        }
        return false;
    }

    public static boolean renameZakDocRoot(String docRoot, String kontFolder, String zakFolderNew, String zakFolderOrig) {
        Path origZakDocRootPath = getZakDocRootPath(docRoot, kontFolder, zakFolderOrig);
        Path newZakDocRootPath = getZakDocRootPath(docRoot, kontFolder, zakFolderNew);
        File origZakDocRootDir = origZakDocRootPath.toFile();
        if (origZakDocRootDir.exists() && origZakDocRootDir.isDirectory()) {
            try {
                Files.move(origZakDocRootPath, newZakDocRootPath);
                return true;
            } catch (IOException ioExceptionObj) {
                System.out.println("Problem occured while renaming zakazka document root dir = " + ioExceptionObj.getMessage());
            }
        }
        return false;
    }

    public static boolean renameZakProjRoot(String projRoot, String kontFolder, String zakFolderNew, String zakFolderOrig) {
        Path origZakProjRootPath = getZakProjRootPath(projRoot, kontFolder, zakFolderOrig);
        Path newZakProjRootPath = getZakProjRootPath(projRoot, kontFolder, zakFolderNew);
        File origZakProjRootDir = origZakProjRootPath.toFile();
        if (origZakProjRootDir.exists() && origZakProjRootDir.isDirectory()) {
            try {
                Files.move(origZakProjRootPath, newZakProjRootPath);
                return true;
            } catch (IOException ioExceptionObj) {
                System.out.println("Problem occured while renaming zakazka project root dir = " + ioExceptionObj.getMessage());
            }
        }
        return false;
    }

}
