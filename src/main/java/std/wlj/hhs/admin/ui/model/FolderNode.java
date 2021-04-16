/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin.ui.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wjohnson000
 *
 */
public class FolderNode implements Comparable<FolderNode> {

    private String           id;
    private String           path;
    private FolderType       type;
    private List<FolderNode> children = new ArrayList<>(); 

    public FolderNode(FolderType type, String id, String path) {
        this.id = id;
        this.path = path;
        this.type = type;
    }
    
    public FolderType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void addChild(FolderNode child) {
        this.children.add(child);
    }

    public List<FolderNode> getChildren() {
        return children;
    }

    public int getChildCount() {
        return children.size();
    }

    public long getFileCount() {
        return children.stream()
                       .mapToLong(fn -> (fn.getType() == FolderType.FILE ? 1 : fn.getFileCount()))
                       .sum();
    }

    @Override
    public int compareTo(FolderNode that) {
        return this.getId().compareToIgnoreCase(that.getId());
    }

    @Override
    public String toString() {
        return getId();
    }
}
