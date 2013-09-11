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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class FolderOpenedEvent extends GwtEvent<FolderOpenedHandler> {

    public static final GwtEvent.Type<FolderOpenedHandler> TYPE = new GwtEvent.Type<FolderOpenedHandler>();

    private FolderModel folder;

    private List<Item> children;

    public FolderOpenedEvent(FolderModel folder) {
        this.folder = folder;
    }

    public FolderOpenedEvent(FolderModel folder, List<Item> children) {
        this.folder = folder;
        this.children = children;
    }

    public FolderModel getFolder() {
        return folder;
    }

    public List<Item> getChildren() {
        if (children == null) {
            children = new ArrayList<Item>();
        }

        return children;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<FolderOpenedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FolderOpenedHandler handler) {
        handler.onFolderOpened(this);
    }

}
