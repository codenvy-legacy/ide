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
package org.exoplatform.ide.client.operation.rename;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RenameItemEvent extends GwtEvent<RenameItemHander> {

    public static final GwtEvent.Type<RenameItemHander> TYPE = new GwtEvent.Type<RenameItemHander>();

    @Override
    protected void dispatch(RenameItemHander handler) {
        handler.onRenameItem(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RenameItemHander> getAssociatedType() {
        return TYPE;
    }

}
