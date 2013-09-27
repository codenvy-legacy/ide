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
