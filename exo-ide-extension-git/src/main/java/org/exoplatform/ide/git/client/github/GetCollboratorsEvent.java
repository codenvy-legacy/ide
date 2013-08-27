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
package org.exoplatform.ide.git.client.github;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GetCollboratorsEvent.java Aug 6, 2012
 */
public class GetCollboratorsEvent extends GwtEvent<GetCollaboratorsHandler> {
    public static final GwtEvent.Type<GetCollaboratorsHandler> TYPE = new GwtEvent.Type<GetCollaboratorsHandler>();

    private String                                             user;

    private String                                             repository;

    public GetCollboratorsEvent() {
        user = "exoplatform";
        repository = "exogtn";
    }

    public GetCollboratorsEvent(String user, String repository) {
        this.user = user;
        this.repository = repository;
    }

    @Override
    protected void dispatch(GetCollaboratorsHandler handler) {
        handler.onGetCollaborators(this);
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<GetCollaboratorsHandler> getAssociatedType() {
        return TYPE;
    }

    public String getUser() {
        return user;
    }

    public String getRepository() {
        return repository;
    }

}
