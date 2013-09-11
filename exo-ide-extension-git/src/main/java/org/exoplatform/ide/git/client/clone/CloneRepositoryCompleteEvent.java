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

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class CloneRepositoryCompleteEvent extends GwtEvent<CloneRepositoryCompleteHandler> {

    public static final GwtEvent.Type<CloneRepositoryCompleteHandler> TYPE =
                                                                             new GwtEvent.Type<CloneRepositoryCompleteHandler>();

    private String                                                    user;

    private String                                                    repositoryName;

    public CloneRepositoryCompleteEvent(String user, String repositoryName) {
        this.user = user;
        this.repositoryName = repositoryName;
    }

    public String getUser() {
        return user;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CloneRepositoryCompleteHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CloneRepositoryCompleteHandler handler) {
        handler.onCloneRepositoryComplete(this);
    }

}
