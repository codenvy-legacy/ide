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
