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
package org.exoplatform.ide.client.framework.application.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * Called when entry point was changed.
 * <p/>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class VfsChangedEvent extends GwtEvent<VfsChangedHandler> {

    public static final GwtEvent.Type<VfsChangedHandler> TYPE = new GwtEvent.Type<VfsChangedHandler>();

    private VirtualFileSystemInfo vfsInfo;

    public VfsChangedEvent(VirtualFileSystemInfo vfsInfo) {
        this.vfsInfo = vfsInfo;
    }

    public VirtualFileSystemInfo getVfsInfo() {
        return vfsInfo;
    }

    @Override
    protected void dispatch(VfsChangedHandler handler) {
        handler.onVfsChanged(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<VfsChangedHandler> getAssociatedType() {
        return TYPE;
    }
}
