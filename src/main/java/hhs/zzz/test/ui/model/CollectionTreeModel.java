/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.zzz.test.ui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author wjohnson000
 *
 */
public class CollectionTreeModel implements TreeModel {

    private FolderNode root = new FolderNode(FolderType.ROOT, "S3-ROOT", "");

    private List<TreeModelListener> listeners = new ArrayList<>();

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
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public void removeFolder(FolderNode model) {
        FolderNode parent = findParent(model, root);
        if (parent != null) {
            parent.getChildren().remove(model);
            fireTreeStructureChanged();
        }
    }

    public FolderNode findParent(FolderNode model, FolderNode startHere) {
        if (startHere.getChildren().contains(model)) {
            return startHere;
        } else {
            for (FolderNode child : startHere.getChildren()) {
                FolderNode parent = findParent(model, child);
                if (parent != null) {
                    return parent;
                }
            }
        }

        return null;
    }

    protected void fireTreeStructureChanged() {
        TreeModelEvent ev = new TreeModelEvent(this, new Object[] { root });
        for (TreeModelListener tml : listeners) {
            tml.treeStructureChanged(ev);
        }
    }
}
