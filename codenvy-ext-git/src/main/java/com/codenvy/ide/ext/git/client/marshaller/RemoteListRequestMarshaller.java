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
package com.codenvy.ide.ext.git.client.marshaller;

import com.codenvy.ide.ext.git.shared.RemoteListRequest;
import com.codenvy.ide.rest.Marshallable;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Marshaller for creating remote list request in JSON format.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 4, 2011 1:52:47 PM anya $
 */
public class RemoteListRequestMarshaller implements Marshallable, Constants {
    /** Remote list request. */
    private RemoteListRequest remoteListRequest;

    /** @param remoteListRequest */
    public RemoteListRequestMarshaller(RemoteListRequest remoteListRequest) {
        this.remoteListRequest = remoteListRequest;
    }

    /** {@inheritDoc} */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        if (remoteListRequest.getRemote() != null) {
            jsonObject.put(REMOTE, new JSONString(remoteListRequest.getRemote()));
        } else {
            jsonObject.put(REMOTE, JSONNull.getInstance());
        }
        jsonObject.put(VERBOSE, JSONBoolean.getInstance(remoteListRequest.verbose()));
        return jsonObject.toString();
    }
}