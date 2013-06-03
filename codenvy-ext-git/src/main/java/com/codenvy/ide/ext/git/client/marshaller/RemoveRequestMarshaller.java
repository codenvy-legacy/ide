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

import com.codenvy.ide.ext.git.shared.RmRequest;
import com.codenvy.ide.rest.Marshallable;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Marshaller for remove files request in JSON format.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 12, 2011 2:55:02 PM anya $
 */
public class RemoveRequestMarshaller implements Marshallable, Constants {
    /** Remove request. */
    private RmRequest rmRequest;

    /**
     * @param rmRequest
     *         remove request
     */
    public RemoveRequestMarshaller(RmRequest rmRequest) {
        this.rmRequest = rmRequest;
    }

    /** {@inheritDoc} */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CACHED, JSONBoolean.getInstance(rmRequest.getCached()));
        if (rmRequest.getFiles() != null && rmRequest.getFiles().length > 0) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < rmRequest.getFiles().length; i++) {
                array.set(i, new JSONString(rmRequest.getFiles()[i]));
            }
            jsonObject.put(FILES, array);
        }
        return jsonObject.toString();
    }
}