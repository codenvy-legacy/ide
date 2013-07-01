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

import com.codenvy.ide.ext.git.shared.AddRequest;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.Marshallable;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Marshaller for add changes to index request.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 29, 2011 5:27:47 PM anya $
 */
public class AddRequestMarshaller implements Marshallable, Constants {
    /** Add changes to index request. */
    private AddRequest addRequest;

    /**
     * @param addRequest
     *         add changes to index request
     */
    public AddRequestMarshaller(AddRequest addRequest) {
        this.addRequest = addRequest;
    }

    /** {@inheritDoc} */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(UPDATE, JSONBoolean.getInstance(addRequest.update()));
        JsonArray<String> filepattern = addRequest.getFilepattern();
        if (filepattern != null && !filepattern.isEmpty()) {
            JSONArray filePatternArray = new JSONArray();
            for (int i = 0; i < filepattern.size(); i++) {
                String pattern = filepattern.get(i);
                filePatternArray.set(i, new JSONString(pattern));
            }
            jsonObject.put(FILE_PATTERN, filePatternArray);
        }
        return jsonObject.toString();
    }
}