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
 * Copy projects from temporary workspace into permanent event.
 */
public class CopyProjectEvent extends GwtEvent<CopyProjectHandler> {
    private boolean createAction = false;

    public CopyProjectEvent(boolean createAction) {
        this.createAction = createAction;
    }

    public CopyProjectEvent() {
        this.createAction = false;
    }

    public static Type<CopyProjectHandler> TYPE = new Type<CopyProjectHandler>();

    /** {@inheritDoc} */
    @Override
    public Type<CopyProjectHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(CopyProjectHandler handler) {
        handler.onCopyProject(this);
    }

    /**
     * If event was generated from create new account button.
     *
     * @return true if create account performed.
     */
    public boolean isCreateAction() {
        return createAction;
    }
}
