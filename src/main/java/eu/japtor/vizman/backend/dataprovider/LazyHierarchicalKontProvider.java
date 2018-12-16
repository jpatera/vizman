package eu.japtor.vizman.backend.dataprovider;

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import eu.japtor.vizman.backend.entity.Kont;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class LazyHierarchicalKontProvider extends
        AbstractBackEndHierarchicalDataProvider<Kont, Void> {

        private final int nodesPerLevel;
        private final int depth;

        public LazyHierarchicalKontProvider(int nodesPerLevel, int depth) {
            this.nodesPerLevel = nodesPerLevel;
            this.depth = depth;
        }

        @Override
        public int getChildCount(
                HierarchicalQuery<Kont, Void> query) {

            Optional<Integer> count = query.getParentOptional()
                    .flatMap(parent -> Optional.of(Integer.valueOf(
                            (internalHasChildren(parent) ? nodesPerLevel : 0))));

            return count.orElse(nodesPerLevel);
        }

        @Override
        public boolean hasChildren(Kont item) {
            return internalHasChildren(item);
        }

        private boolean internalHasChildren(Kont node) {
            return node instanceof Kont;
//            .getDepth() < depth;
        }

        @Override
        protected Stream<Kont> fetchChildrenFromBackEnd(HierarchicalQuery<Kont, Void> query) {

//            final int depth = query.getParentOptional().isPresent()
//                    ? query.getParent().getDepth() + 1
//                    : 0;
//            final Optional<String> parentKey = query.getParentOptional()
//                    .flatMap(parent -> Optional.of(parent.getId()));

            List<Kont> list = new ArrayList<>();
//            int limit = Math.min(query.getLimit(), nodesPerLevel);
//            for (int i = 0; i < limit; i++) {
//                list.add(new Kont(parentKey.orElse(null), depth,
//                        i + query.getOffset()));
//            }
            return list.stream();
        }
//    }
}
