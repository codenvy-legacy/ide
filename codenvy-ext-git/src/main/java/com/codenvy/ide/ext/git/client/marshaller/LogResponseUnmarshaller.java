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

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 14, 2011 4:10:34 PM anya $
 */
public class LogResponseUnmarshaller implements Unmarshallable<LogResponse>, Constants {
    /** Log response. */
    private DtoClientImpls.LogResponseImpl logResponse;

    /** If <code>true</code> - the response is in text format, else - the list of revisions in JSON format is returned. */
    private boolean isText;

    /**
     * @param logResponse
     *         log response
     * @param isText
     *         if <code>true</code> - the response is in text format
     */
    public LogResponseUnmarshaller(DtoClientImpls.LogResponseImpl logResponse, boolean isText) {
        this.logResponse = logResponse;
        this.isText = isText;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        if (isText) {
            logResponse.setTextLog(text);
            return;
        }

        JsonArray<Revision> revisions = JsonCollections.createArray();
        JSONObject logObject = JSONParser.parseStrict(text).isObject();
        if (logObject == null)
            return;

        JSONArray array = (logObject.get(COMMITS) != null) ? logObject.get(COMMITS).isArray() : null;
        if (array == null || array.size() <= 0)
            return;

        for (int i = 0; i < array.size(); i++) {
            JSONObject revisionObject = array.get(i).isObject();
            String value = revisionObject.toString();
            DtoClientImpls.RevisionImpl revision = DtoClientImpls.RevisionImpl.deserialize(value);
            revisions.add(revision);
        }
        logResponse.setCommits(revisions);
    }

    /** {@inheritDoc} */
    @Override
    public LogResponse getPayload() {
        return logResponse;
    }
}