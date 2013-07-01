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

import com.codenvy.ide.ext.git.shared.DiffRequest;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.Marshallable;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Marshaller for creating diff request in JSON format.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 4, 2011 10:57:37 AM anya $
 */
public class DiffRequestMarshaller implements Marshallable, Constants {
    /** Diff request. */
    private DiffRequest diffRequest;

    /**
     * @param diffRequest
     *         diff request
     */
    public DiffRequestMarshaller(DiffRequest diffRequest) {
        this.diffRequest = diffRequest;
    }

    /** {@inheritDoc} */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();

        JsonArray<String> fileFilter = diffRequest.getFileFilter();
        if (fileFilter != null && !fileFilter.isEmpty()) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < fileFilter.size(); i++) {
                String filter = fileFilter.get(i);
                array.set(i, new JSONString(filter));
            }
            jsonObject.put(FILE_FILTER, array);
        }
        jsonObject.put(NO_RENAMES, JSONBoolean.getInstance(diffRequest.noRenames()));
        jsonObject.put(CACHED, JSONBoolean.getInstance(diffRequest.cached()));

        if (diffRequest.getType() != null) {
            jsonObject.put(TYPE, new JSONString(diffRequest.getType().name()));
        }

        if (diffRequest.getCommitA() != null) {
            jsonObject.put(COMMIT_A, new JSONString(diffRequest.getCommitA()));
        }

        if (diffRequest.getCommitB() != null) {
            jsonObject.put(COMMIT_B, new JSONString(diffRequest.getCommitB()));
        }
        return jsonObject.toString();
    }
}