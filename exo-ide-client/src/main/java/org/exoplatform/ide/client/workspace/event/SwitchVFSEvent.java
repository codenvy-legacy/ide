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
package org.exoplatform.ide.client.workspace.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class SwitchVFSEvent extends GwtEvent<SwitchVFSHandler> {

    public static final GwtEvent.Type<SwitchVFSHandler> TYPE = new GwtEvent.Type<SwitchVFSHandler>();

    /** Virtual file system id. */
    private String vfsID;

    /**
     * @param vfsID
     *         virtual file system id
     */
    public SwitchVFSEvent(String vfsID) {
        this.vfsID = vfsID;
    }

    @Override
    protected void dispatch(SwitchVFSHandler handler) {
        handler.onSwitchVFS(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<SwitchVFSHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return the ID of virtual file system */
    public String getVfsID() {
        return vfsID;
    }

}
