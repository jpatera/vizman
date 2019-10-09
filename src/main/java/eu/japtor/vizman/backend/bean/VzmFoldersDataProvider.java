package eu.japtor.vizman.backend.bean;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import eu.japtor.vizman.backend.utils.VzmFileUtils.VzmFile;
import eu.japtor.vizman.backend.utils.VzmFileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VzmFoldersDataProvider extends
        AbstractBackEndHierarchicalDataProvider<VzmFileUtils.VzmFile, FilenameFilter> {

    private static final Comparator<VzmFileUtils.VzmFile> nameComparator = (fileA, fileB) ->
            String.CASE_INSENSITIVE_ORDER.compare(fileA.getName(), fileB.getName());

    private static final Comparator<VzmFileUtils.VzmFile> sizeComparator = Comparator.comparingLong(File::length);

    private static final Comparator<VzmFileUtils.VzmFile> lastModifiedComparator = Comparator.comparingLong(File::lastModified);

    private final VzmFileUtils.VzmFile root;

    public VzmFoldersDataProvider(VzmFileUtils.VzmFile root) {
        this.root = root;
    }

    @Override
    public int getChildCount(
            HierarchicalQuery<VzmFileUtils.VzmFile, FilenameFilter> query) {
        return (int) fetchChildren(query).count();
    }

    @Override
    protected Stream<VzmFileUtils.VzmFile> fetchChildrenFromBackEnd(
            HierarchicalQuery<VzmFileUtils.VzmFile, FilenameFilter> query) {

        final VzmFileUtils.VzmFile parent = query.getParentOptional().orElse(root);
        Stream<VzmFileUtils.VzmFile> filteredFiles = query.getFilter()
                .map(filter -> parent.listVzmFiles(parent, filter))
                .orElse(parent.listVzmFiles(parent))
                .skip(query.getOffset()).limit(query.getLimit());
        return sortFileStream(filteredFiles, query.getSortOrders());
    }

    @Override
    public boolean hasChildren(VzmFileUtils.VzmFile item) {
        return (item.list() != null) && (item.list().length > 0);
    }

    private Stream<VzmFileUtils.VzmFile> sortFileStream(Stream<VzmFileUtils.VzmFile> fileStream,
                                        List<QuerySortOrder> sortOrders) {

        if (sortOrders.isEmpty()) {
            return fileStream;
        }

        List<Comparator<VzmFileUtils.VzmFile>> comparators = sortOrders.stream()
                .map(sortOrder -> {
                    Comparator<VzmFileUtils.VzmFile> comparator = null;
                    if (sortOrder.getSorted().equals("file-name")) {
                        comparator = nameComparator;
                    } else if (sortOrder.getSorted().equals("file-size")) {
                        comparator = sizeComparator;
                    } else if (sortOrder.getSorted().equals("file-last-modified")) {
                        comparator = lastModifiedComparator;
                    }
                    if (comparator != null && sortOrder
                            .getDirection() == SortDirection.DESCENDING) {
                        comparator = comparator.reversed();
                    }
                    return comparator;
                }).filter(Objects::nonNull).collect(Collectors.toList());

        if (comparators.isEmpty()) {
            return fileStream;
        }

        Comparator<VzmFileUtils.VzmFile> first = comparators.remove(0);
        Comparator<VzmFileUtils.VzmFile> combinedComparators = comparators.stream()
                .reduce(first, Comparator::thenComparing);
        return fileStream.sorted(combinedComparators);
    }






//    private static void addExpectedKontDocSubDirs(TreeData<VzmFile> kontDocTreeData, final VzmFile kontDocDir, boolean asRootItems) {
//        List<VzmFile> kontDocRootSubDirs = new ArrayList<>();
////        kontDocRootSubDirs.add(new VzmFile(getFolderPath(kontDocDir, KONT_SOD_FOLDER).toUri(), true));
//        kontDocRootSubDirs.add(new VzmFile(getFolderPath(kontDocDir, KONT_SOD_FOLDER).toString(), true, 1));
//        kontDocTreeData.addItems(asRootItems ? null : kontDocDir, kontDocRootSubDirs);
//    }
//
//    public static void addNotExpectedKontSubDirs(TreeData<VzmFile> kontDocTreeData, final VzmFile kontDocDir) {
//        if (null == kontDocDir || !kontDocDir.exists()) {
//            return;
//        }
//        try {
//            Files.newDirectoryStream(kontDocDir.toPath()).forEach(realKontSubPath -> {
//                if (kontDocTreeData.getChildren(null).stream().noneMatch(
//                        treeKontSub -> treeKontSub.getName().equals(realKontSubPath.getFileName().toString()))) {
//                    kontDocTreeData.addItem(null, new VzmFile(getFolderPath(kontDocDir, realKontSubPath.getFileName().toString()), false));
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void addExpectedKontZakSubDirs(TreeData<VzmFile> kontDocTreeData, final VzmFile kontDocDir, final Kont kont) {
//        List<VzmFile> zakDocDirs = new ArrayList<>();
//        kont.getZaks().forEach(zak -> {
//            String expZakFolder = NormalizeDirnamesAndJoin(zak.getCkz().toString(), zak.getText());
////            VzmFile zakDocDir = new VzmFile(getFolderPath(kontDocDir, expZakFolder).toUri(), true);
//            VzmFileUtils.VzmFile zakDocDir = new VzmFile(getFolderPath(kontDocDir, expZakFolder).toString(), true);
//            kontDocTreeData.addItem(null, zakDocDir);
//            addExpectedZakDocSubDirs(kontDocTreeData, zakDocDir, false);
////            zakDocDirs.add(new VzmFile(getFolderPath(kontDocDir, expZakFolder).toUri(), true));
//            zakDocDirs.add(new VzmFile(getFolderPath(kontDocDir, expZakFolder).toString(), true));
//        });
//    }
//
//    private static void addExpectedZakDocSubDirs(TreeData<VzmFile> treeData, final VzmFile zakDocDir, boolean asRootItems) {
//        List<VzmFile> zakDocSubDirs = new ArrayList<>();
////        zakDocSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_DOPISY_FOLDER).toUri(), true));
////        zakDocSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_FAKTURY_FOLDER).toUri(), true));
//        zakDocSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_DOPISY_FOLDER).toString(), true));
//        zakDocSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_FAKTURY_FOLDER).toString(), true));
//        treeData.addItems(asRootItems ? null : zakDocDir, zakDocSubDirs);
//    }
//
////    public static void addExpectedZakDocRootSubDirs(TreeData<VzmFile> zakDocTreeData, final VzmFile zakDocDir) {
////        List<VzmFile> zakDocRootSubDirs = new ArrayList<>();
//////        zakDocRootSubDirs.add(new VzmFile(getFolderPath(zakDocDir, KONT_SOD_FOLDER).toUri(), true));
////        zakDocRootSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_DOPISY_FOLDER).toUri(), true));
////        zakDocRootSubDirs.add(new VzmFile(getFolderPath(zakDocDir, ZAK_FAKTURY_FOLDER).toUri(), true));
////        zakDocTreeData.addItems(null, zakDocRootSubDirs);
////    }

}
