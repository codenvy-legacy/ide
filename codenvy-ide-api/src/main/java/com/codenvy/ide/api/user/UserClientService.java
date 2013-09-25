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
package com.codenvy.ide.api.user;

import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client service for manage information of user.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface UserClientService {
    /**
     * Returns current user with additional information. Which additional information includes into User described into {@link
     * com.codenvy.ide.api.user.User} interface.
     *
     * @param callback
     * @throws RequestException
     */
    void getUser(AsyncRequestCallback<User> callback) throws RequestException;

    /**
     * Updates user's attributes from information what contains into updateUserAttributes. If some attributes aren't exist then these
     * attributes will be added. This method can add/update many attribute per one operation.
     *
     * @param updateUserAttributes
     * @param callback
     * @throws RequestException
     */
    void updateUserAttributes(UpdateUserAttributes updateUserAttributes, AsyncRequestCallback<Void> callback) throws RequestException;
}