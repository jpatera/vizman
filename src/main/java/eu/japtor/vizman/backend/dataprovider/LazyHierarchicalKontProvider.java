package eu.japtor.vizman.backend.dataprovider;

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.KzTreeAware;
import eu.japtor.vizman.backend.service.KontService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class LazyHierarchicalKontProvider extends
        AbstractBackEndHierarchicalDataProvider<KzTreeAware, Void> {

    private final KontService kontService;

    public LazyHierarchicalKontProvider(final KontService kontService) {
        this.kontService = kontService;
    }


    @Override
    protected Stream<KzTreeAware> fetchChildrenFromBackEnd(HierarchicalQuery<KzTreeAware, Void> hierarchicalQuery) {

        final KzTreeAware parent = hierarchicalQuery.getParentOptional().orElse(null);
        if (null == parent) {
            return ((Collection<KzTreeAware>) kontService.fetchAll()).stream();
        } else {
            return parent.getNodes().stream()
                    .skip(hierarchicalQuery.getOffset()).limit(hierarchicalQuery.getLimit());
        }
//        return kontService.fetchAll();
//        return null;
    }

    @Override
    public int getChildCount(HierarchicalQuery<KzTreeAware, Void> hierarchicalQuery) {
        return (int) fetchChildren(hierarchicalQuery).count();
//        return 0;
    }

    @Override
    public boolean hasChildren(KzTreeAware kzTreeAware) {
        return kzTreeAware.getNodes() != null && kzTreeAware.getNodes().size() > 0;
//        return false;
    }


//        AbstractBackEndHierarchicalDataProvider<KzTreeAware, ObjednatelFilter> {




//
////        private final File root;
////        private final List<KzTreeAware> rootList;
//        private final KontService kontService;
//
//        public LazyHierarchicalKontProvider(final KontService kontService) {
//            this.kontService = kontService;
//        }
//
//        @Override
//        public int getChildCount(HierarchicalQuery<? extends KzTreeAware, Void> query) {
//            return (int) fetchChildren(query).count();
//        }
//
//        @Override
//        public Stream<? extends KzTreeAware> fetchChildrenFromBackEnd(
//                HierarchicalQuery<KzTreeAware, Void> query) {
//
//            final KzTreeAware parent = query.getParentOptional().orElse(null);
////            return query.getFilter()
////                    .map(filter -> Stream.of(parent.getNodes(filter)))
////                    .orElse(Stream.of(parent.getNodes()))
////                    .skip(query.getOffset()).limit(query.getLimit());
////            return Stream.of(parent.getNodes())
//
//            if (null == query.getParent()) {
//                List<? super Kont> kzList = kontService.fetchAll();
//                return kzList.stream();
//                        parent.getNodes().stream()
//                        .skip(query.getOffset()).limit(query.getLimit());
//            }
//            return parent.getNodes().stream()
//                    .skip(query.getOffset()).limit(query.getLimit());
//        }
//
//        @Override
//        public boolean hasChildren(KzTreeAware item) {
//            return item.getNodes() != null && item.getNodes().size() > 0;
//        }
    }


//        private final int nodesPerLevel;
//        private final int depth;
//
//        public LazyHierarchicalKontProvider(int nodesPerLevel, int depth) {
//            this.nodesPerLevel = nodesPerLevel;
//            this.depth = depth;
//        }
//
//        @Override
//        public int getChildCount(
//                HierarchicalQuery<Kont, Void> query) {
//
//            Optional<Integer> count = query.getParentOptional()
//                    .flatMap(parent -> Optional.of(Integer.valueOf(
//                            (internalHasChildren(parent) ? nodesPerLevel : 0))));
//
//            return count.orElse(nodesPerLevel);
//        }
//
//        @Override
//        public boolean hasChildren(Kont item) {
//            return internalHasChildren(item);
//        }
//
//        private boolean internalHasChildren(Kont node) {
//            return node instanceof Kont;
////            .getDepth() < depth;
//        }
//
//        @Override
//        protected Stream<Kont> fetchChildrenFromBackEnd(HierarchicalQuery<Kont, Void> query) {
//
////            final int depth = query.getParentOptional().isPresent()
////                    ? query.getParent().getDepth() + 1
////                    : 0;
////            final Optional<String> parentKey = query.getParentOptional()
////                    .flatMap(parent -> Optional.of(parent.getId()));
//
//            List<Kont> list = new ArrayList<>();
////            int limit = Math.min(query.getLimit(), nodesPerLevel);
////            for (int i = 0; i < limit; i++) {
////                list.add(new Kont(parentKey.orElse(null), depth,
////                        i + query.getOffset()));
////            }
//            return list.stream();
//        }
////    }
//}
