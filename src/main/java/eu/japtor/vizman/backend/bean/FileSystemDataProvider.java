package eu.japtor.vizman.backend.bean;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemDataProvider extends
        AbstractBackEndHierarchicalDataProvider<File, FilenameFilter> {

        private static final Comparator<File> nameComparator = (fileA, fileB) -> {
            return String.CASE_INSENSITIVE_ORDER.compare(fileA.getName(), fileB.getName());
        };

        private static final Comparator<File> sizeComparator = (fileA, fileB) -> {
            return Long.compare(fileA.length(), fileB.length());
        };

        private static final Comparator<File> lastModifiedComparator = (fileA, fileB) -> {
            return Long.compare(fileA.lastModified(), fileB.lastModified());
        };

        private final File root;

        public FileSystemDataProvider(File root) {
            this.root = root;
        }

        @Override
        public int getChildCount(
                HierarchicalQuery<File, FilenameFilter> query) {
            return (int) fetchChildren(query).count();
        }

        @Override
        protected Stream<File> fetchChildrenFromBackEnd(
                HierarchicalQuery<File, FilenameFilter> query) {
            final File parent = query.getParentOptional().orElse(root);
            Stream<File> filteredFiles = query.getFilter()
                    .map(filter -> Stream.of(parent.listFiles(filter)))
                    .orElse(Stream.of(parent.listFiles()))
                    .skip(query.getOffset()).limit(query.getLimit());
            return sortFileStream(filteredFiles, query.getSortOrders());
        }

        @Override
        public boolean hasChildren(File item) {
            return item.list() != null && item.list().length > 0;
        }

        private Stream<File> sortFileStream(Stream<File> fileStream,
                                            List<QuerySortOrder> sortOrders) {

            if (sortOrders.isEmpty()) {
                return fileStream;
            }

            List<Comparator<File>> comparators = sortOrders.stream()
                    .map(sortOrder -> {
                        Comparator<File> comparator = null;
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

            Comparator<File> first = comparators.remove(0);
            Comparator<File> combinedComparators = comparators.stream()
                    .reduce(first, Comparator::thenComparing);
            return fileStream.sorted(combinedComparators);
        }

}
