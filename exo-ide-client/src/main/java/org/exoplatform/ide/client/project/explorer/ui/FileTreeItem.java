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
package org.exoplatform.ide.client.project.explorer.ui;

import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class FileTreeItem extends ProjectExplorerTreeItem {

    public FileTreeItem(FileModel file) {
        super(file);
    }

    @Override
    public List<Item> getItems() {
        return null;
    }

    @Override
    public void refresh(boolean expand) {
        render();
    }
    
    @Override
    public boolean select(Item item) {
        if (item.getId().equals(((Item)getUserObject()).getId()))
        {
            getTree().setSelectedItem(this);
            getTree().ensureSelectedItemVisible();
            return true;
        }

        return false;
    }

}
