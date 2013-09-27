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
package org.exoplatform.ide.client.framework.navigation.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FolderModel;

/**
 * Event occurs when folder content is refreshed in browser tree. It is needed to known when the content in browser
 * tree
 * is
 * updated in extensions. Implement {@link FolderRefreshedHandler} handler to process the event.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 6, 2011 4:10:23 PM anya $
 * @deprecated
 */
public class FolderRefreshedEvent extends GwtEvent<FolderRefreshedHandler> {

    /** Type used to register this event. */
    public static final GwtEvent.Type<FolderRefreshedHandler> TYPE = new GwtEvent.Type<FolderRefreshedHandler>();

    /** Refreshed folder. */
    private FolderModel folder;

    /**
     * @param folder
     *         refreshed folder
     */
    public FolderRefreshedEvent(FolderModel folder) {
        this.folder = folder;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<FolderRefreshedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(FolderRefreshedHandler handler) {
        handler.onFolderRefreshed(this);
    }

    /** @return the folder */
    public FolderModel getFolder() {
        return folder;
    }
}
