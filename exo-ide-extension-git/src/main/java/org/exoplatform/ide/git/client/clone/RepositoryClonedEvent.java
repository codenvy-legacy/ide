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
package org.exoplatform.ide.git.client.clone;

import com.google.gwt.event.shared.GwtEvent;

/** Event when repository cloned successfully. */
public class RepositoryClonedEvent extends GwtEvent<RepositoryClonedHandler> {

    public static final GwtEvent.Type<RepositoryClonedHandler> TYPE = new GwtEvent.Type<RepositoryClonedHandler>();

    private String vcsUrl;

    public RepositoryClonedEvent(String vcsUrl) {
        this.vcsUrl = vcsUrl;
    }

    public String getVcsUrl() {
        return vcsUrl;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RepositoryClonedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RepositoryClonedHandler handler) {
        handler.onRepositoryCloned(this);
    }

}
