/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin.ui.model;

import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author wjohnson000
 *
 */
public class CollectionTreeModel implements TreeModel {

    private FolderNode root = new FolderNode(FolderType.ROOT, "S3-ROOT", "");

    public CollectionTreeModel(List<FolderNode> model) {
        model.stream().forEach(root::addChild);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        FolderNode node = (FolderNode)parent;
        if (index < 0  ||  index >= node.getChildCount()) {
            return null;
        } else {
            return node.getChildren().get(index);
        }
    }

    @Override
    public int getChildCount(Object parent) {
        FolderNode node = (FolderNode)parent;
        return (node.getType() == FolderType.FILE) ? 0 : node.getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        FolderNode fnode = (FolderNode)node;
        return fnode.getType() == FolderType.FILE;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        FolderNode node = (FolderNode)parent;
        return node.getChildren().indexOf(child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }

    
}
