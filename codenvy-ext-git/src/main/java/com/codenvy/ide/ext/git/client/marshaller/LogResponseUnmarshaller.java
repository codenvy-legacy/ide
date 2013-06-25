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
import com.codenvy.ide.ext.git.shared.GitUser;
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
    private LogResponse logResponse;

    /** If <code>true</code> - the response is in text format, else - the list of revisions in JSON format is returned. */
    private boolean isText;

    /**
     * @param logResponse
     *         log response
     * @param isText
     *         if <code>true</code> - the response is in text format
     */
    public LogResponseUnmarshaller(LogResponse logResponse, boolean isText) {
        this.logResponse = logResponse;
        this.isText = isText;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        if (isText) {
            logResponse.setTextLog(response.getText());
            return;
        }

        JsonArray<Revision> revisions = JsonCollections.createArray();
        JSONObject logObject = JSONParser.parseStrict(response.getText()).isObject();
        if (logObject == null)
            return;

        JSONArray array = (logObject.get(COMMITS) != null) ? logObject.get(COMMITS).isArray() : null;
        if (array == null || array.size() <= 0)
            return;

        for (int i = 0; i < array.size(); i++) {
            JSONObject revisionObject = array.get(i).isObject();
            String id =
                    (revisionObject.get(ID) != null && revisionObject.get(ID).isString() != null) ? revisionObject.get(ID)
                                                                                                                  .isString()
                                                                                                                  .stringValue()
                                                                                                  : "";
            String message =
                    (revisionObject.get(MESSAGE) != null && revisionObject.get(MESSAGE).isString() != null)
                    ? revisionObject
                            .get(MESSAGE).isString().stringValue() : "";
            long commitTime =
                    (long)((revisionObject.get(COMMIT_TIME) != null && revisionObject.get(COMMIT_TIME).isNumber() != null)
                           ? revisionObject.get(COMMIT_TIME).isNumber().doubleValue() : 0);

            Revision revision = new Revision(id, message, commitTime, null);
            if (revisionObject.get(COMMITTER) != null && revisionObject.get(COMMITTER).isObject() != null) {
                JSONObject committerObject = revisionObject.get(COMMITTER).isObject();
                String name =
                        (committerObject.containsKey(NAME) && committerObject.get(NAME).isString() != null)
                        ? committerObject
                                .get(NAME).isString().stringValue() : "";
                String email =
                        (committerObject.containsKey(EMAIL) && committerObject.get(EMAIL).isString() != null)
                        ? committerObject
                                .get(EMAIL).isString().stringValue() : "";

                GitUser gitUser = new GitUser(name, email);
                revision.setCommitter(gitUser);
            }
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