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
package org.exoplatform.ide.extension.samples.client.inviting;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.extension.samples.client.inviting.manage.UserInvitations;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class InviteClientService {

    private static InviteClientService instance;

    public static InviteClientService getInstance() {
        return instance;
    }

    private String restServiceContext;

    public InviteClientService(String restServiceContext, String wsName) {
        this.restServiceContext = restServiceContext + wsName;
        instance = this;
    }

    public void inviteUser(String email, String message, AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + "/invite/" + email;
        AsyncRequest.build(RequestBuilder.POST, url).loader(new EmptyLoader())
                    .header(HTTPHeader.CONTENTTYPE, "text/html; charset=utf-8").data(message).send(callback);
    }

    public void getInvitesList(AsyncRequestCallback<List<UserInvitations>> callback) throws RequestException {
        String url = restServiceContext + "/invite";
        AsyncRequest.build(RequestBuilder.GET, url).loader(new EmptyLoader())
                    .header(HTTPHeader.ACCEPT, MediaType.APPLICATION_JSON).send(callback);
    }
}
