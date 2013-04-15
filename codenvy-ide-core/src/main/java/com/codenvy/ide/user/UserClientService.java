/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.user;

import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.shared.RemoveUserAttribute;
import com.codenvy.ide.shared.UpdateUserAttribute;
import com.codenvy.ide.shared.UpdateUserAttributes;
import com.codenvy.ide.shared.User;
import com.google.gwt.http.client.RequestException;

/**
 * Client service for manage information of user.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface UserClientService {

    /**
     * Returns current user with additional information. Which additional information includes into User described into {@link
     * com.codenvy.ide.shared.User} interface.
     *
     * @param callback
     * @throws RequestException
     */
    void getUser(AsyncRequestCallback<User> callback) throws RequestException;

    /**
     * Updates user's attribute from information what contains into updateUserAttribute. If the attribute isn't exist then this attribute
     * will add.
     *
     * @param updateUserAttribute
     * @param callback
     * @throws RequestException
     */
    void updateUserAttribute(UpdateUserAttribute updateUserAttribute, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Removes user's attribute. Which attribute needs to remove describe into removeUserAttribute. If the attribute isn't exist then no
     * attribute will be removed.
     *
     * @param removeUserAttribute
     * @param callback
     * @throws RequestException
     */
    void removeUserAttribute(RemoveUserAttribute removeUserAttribute, AsyncRequestCallback<Void> callback) throws RequestException;

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