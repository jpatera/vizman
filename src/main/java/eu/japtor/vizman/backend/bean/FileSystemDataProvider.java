package eu.japtor.vizman.backend.bean;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import eu.japtor.vizman.backend.utils.VzmFileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemDataProvider extends
        AbstractBackEndHierarchicalDataProvider<VzmFileUtils.VzmFile, FilenameFilter> {

        private static final Comparator<VzmFileUtils.VzmFile> nameComparator = (fileA, fileB) ->
                String.CASE_INSENSITIVE_ORDER.compare(fileA.getName(), fileB.getName());

        private static final Comparator<VzmFileUtils.VzmFile> sizeComparator = Comparator.comparingLong(File::length);

        private static final Comparator<VzmFileUtils.VzmFile> lastModifiedComparator = Comparator.comparingLong(File::lastModified);

        private final VzmFileUtils.VzmFile root;

        public FileSystemDataProvider(VzmFileUtils.VzmFile root) {
            this.root = root;
        }

        @Override
        public int getChildCount(HierarchicalQuery<VzmFileUtils.VzmFile, FilenameFilter> query) {
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

}
