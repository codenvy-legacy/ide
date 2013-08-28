/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.extension.java.server.parser.scanner;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Nov 28, 2011 4:05:23 PM evgen $
 */
public class FolderScanner {
    private Folder folder;

    private VirtualFileSystem vfs;

    private Set<Filter> filters = new HashSet<Filter>();

    public FolderScanner(Folder folder, VirtualFileSystem vfs) {
        super();
        this.folder = folder;
        this.vfs = vfs;
    }

    public List<Item> scan() throws VirtualFileSystemException {
        final List<Item> items = new ArrayList<Item>();
        ItemVisitor visitor = new ItemVisitor() {

            @Override
            public void visit(Item item) {
                items.add(item);
            }
        };
        ItemList<Item> children;

        children = vfs.getChildren(folder.getId(), -1, 0, null, false, PropertyFilter.NONE_FILTER);
        for (Item item : children.getItems()) {
            scan(item, visitor);
        }

        return items;
    }

    private void scan(Item i, ItemVisitor v) throws VirtualFileSystemException {
        if (i.getItemType() == ItemType.FOLDER) {
            applyFilters(i, v);

            ItemList<Item> children = vfs.getChildren(i.getId(), -1, 0, null, false, PropertyFilter.NONE_FILTER);
            for (Item item : children.getItems()) {
                scan(item, v);
            }

        } else {
            applyFilters(i, v);
        }
    }

    /**
     * @param i
     * @param v
     */
    private void applyFilters(Item i, ItemVisitor v) {
        for (Filter f : filters) {
            if (!f.filter(i))
                return;
        }
        v.visit(i);
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }
}
