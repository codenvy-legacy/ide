/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.invite;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.util.Utils;

import java.util.List;

/**
 * Implementation of {@link GoogleContactsService} service.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: GoogleContactsServiceImpl.java Aug 20, 2012 4:53:52 PM azatsarynnyy $
 */

public class GoogleContactsService {

    private static GoogleContactsService instance;

    public static GoogleContactsService getInstance() {
        if (instance == null)
            instance = new GoogleContactsService();
        return instance;
    }

    /** @see org.exoplatform.ide.client.framework.invite.GoogleContactsService#getContacts(org.exoplatform.gwtframework.commons.rest
     * .AsyncRequestCallback) */
    public void getContacts(AsyncRequestCallback<List<GoogleContact>> callback) throws RequestException {
        String url = Utils.getRestContext() + Utils.getWorkspaceName() + "/googlecontacts/all";
        AsyncRequest.build(RequestBuilder.GET, url).loader(IDELoader.get()).send(callback);
    }

    public void isAuthenticate(AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = Utils.getRestContext() + Utils.getWorkspaceName() + "/googlecontacts/is-authenticate";
        AsyncRequest.build(RequestBuilder.GET, url).loader(IDELoader.get()).send(callback);
    }
}
