/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

    private String user;

    private String repository;

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
