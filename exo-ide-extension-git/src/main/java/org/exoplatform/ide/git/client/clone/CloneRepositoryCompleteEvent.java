/*
 * Copyright (C) 2012 eXo Platform SAS.
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
