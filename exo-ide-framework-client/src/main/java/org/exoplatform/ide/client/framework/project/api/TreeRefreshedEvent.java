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
package org.exoplatform.ide.client.framework.project.api;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class TreeRefreshedEvent extends GwtEvent<TreeRefreshedHandler> {

    public static final GwtEvent.Type<TreeRefreshedHandler> TYPE = new GwtEvent.Type<TreeRefreshedHandler>();

    private FolderModel                                     folder;

    private Item                                            itemToSelect;

    public TreeRefreshedEvent(FolderModel folder) {
        this.folder = folder;
    }

    public TreeRefreshedEvent(FolderModel folder, Item itemToSelect) {
        this.folder = folder;
        this.itemToSelect = itemToSelect;
    }

    public FolderModel getFolder() {
        return folder;
    }

    public Item getItemToSelect() {
        return itemToSelect;
    }

    @Override
    public GwtEvent.Type<TreeRefreshedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TreeRefreshedHandler handler) {
        handler.onTreeRefreshed(this);
    }

}
