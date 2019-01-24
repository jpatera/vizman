package eu.japtor.vizman.fsdataprovider;

import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.function.SerializablePredicate;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class FilesystemDataProvider  extends TreeDataProvider<File> {
    private boolean recursive;
    FilesystemData treeData;

    public FilesystemDataProvider(FilesystemData treeData) {
        super(treeData);
        this.recursive = treeData.isRecursive();
        this.treeData = treeData;
    }

    public int getChildCount(HierarchicalQuery<File, SerializablePredicate<File>> query) {
        File parent = (File)query.getParentOptional().orElse(this.treeData.getRootItems().get(0));
        return parent.isFile() ? 0 : (int)this.fetchChildren(query).count();
    }

    public boolean hasChildren(File item) {
        if (this.isInMemory()) {
            return super.hasChildren(item);
        } else {
            return item.isDirectory() && !this.treeData.getChildrenFromFilesystem(item).isEmpty();
        }
    }

    public Stream<File> fetchChildren(HierarchicalQuery<File, SerializablePredicate<File>> query) {
        if (!this.isInMemory()) {
            File parent = (File)query.getParentOptional().orElse(this.treeData.getRootItems().get(0));
            if (this.treeData.getChildren(parent).isEmpty()) {
                List<File> files = this.treeData.getChildrenFromFilesystem(parent);
                this.treeData.addItems(parent, files);
                return files.stream();
            } else {
                return super.fetchChildren(query);
            }
        } else {
            return super.fetchChildren(query);
        }
    }

    public boolean isInMemory() {
        return this.recursive;
    }

}
