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
package com.codenvy.ide.factory.client.copy;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CopyProjectEvent extends GwtEvent<CopyProjectHandler> {
    public static Type<CopyProjectHandler> TYPE = new Type<CopyProjectHandler>();

    public Type<CopyProjectHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(CopyProjectHandler handler) {
        handler.onCopyProject(this);
    }
}
