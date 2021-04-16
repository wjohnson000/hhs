/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin.ui.component;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import std.wlj.hhs.admin.ui.model.FolderNode;
import std.wlj.hhs.admin.ui.model.FolderType;

/**
 * @author wjohnson000
 *
 */
public class S3TreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 7483923266748501696L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree,
            Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {

        String text = value.toString();
        if (value instanceof FolderNode) {
            FolderNode folder = (FolderNode)value;
            if (folder.getType() == FolderType.ROOT  ||
                   (folder.getType() == FolderType.COLLECTION  &&  folder.getFileCount() > 2)) {
                StringBuilder buff = new StringBuilder();
                buff.append("<html><b>").append(text).append("</b></html>");
                text = buff.toString();
            }
        }

        return super.getTreeCellRendererComponent(tree, text, sel, expanded, leaf, row, hasFocus);
    }
}
