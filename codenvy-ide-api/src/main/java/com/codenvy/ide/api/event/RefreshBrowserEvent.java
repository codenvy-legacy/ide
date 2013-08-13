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
package com.codenvy.ide.api.event;

import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.event.shared.GwtEvent;

import java.util.ArrayList;
import java.util.List;

/** @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a> */
public class RefreshBrowserEvent extends GwtEvent<RefreshBrowserHandler> {

    public static final GwtEvent.Type<RefreshBrowserHandler> TYPE = new Type<RefreshBrowserHandler>();

    private List<Folder> folders;

    private Resource itemToSelect;

    public RefreshBrowserEvent() {
    }

    public RefreshBrowserEvent(Folder folder) {
        folders = new ArrayList<Folder>();
        folders.add(folder);
    }

    public RefreshBrowserEvent(Folder folder, Resource itemToSelect) {
        folders = new ArrayList<Folder>();
        folders.add(folder);
        this.itemToSelect = itemToSelect;
    }

    public RefreshBrowserEvent(List<Folder> folders, Resource itemToSelect) {
        this.folders = folders;
        this.itemToSelect = itemToSelect;
    }

    @Override
    protected void dispatch(RefreshBrowserHandler handler) {
        handler.onRefreshBrowser(this);
    }

    public List<Folder> getFolders() {
        ArrayList<Folder> folderList = new ArrayList<Folder>();
        if (folders != null) {
            folderList.addAll(folders);
        }

        return folderList;
    }

    public Resource getItemToSelect() {
        return itemToSelect;
    }

    @Override
    public GwtEvent.Type<RefreshBrowserHandler> getAssociatedType() {
        return TYPE;
    }

}
