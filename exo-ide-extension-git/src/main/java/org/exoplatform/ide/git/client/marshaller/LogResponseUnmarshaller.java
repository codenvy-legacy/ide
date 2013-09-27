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
package org.exoplatform.ide.git.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.Revision;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 14, 2011 4:10:34 PM anya $
 */
public class LogResponseUnmarshaller implements Unmarshallable<LogResponse>, Constants {
    /** Log response. */
    private LogResponse logResponse;

    /** If <code>true</code> - the response is in text format, else - the list of revisions in JSON format is returned. */
    private boolean     isText;

    /**
     * @param logResponse log response
     * @param isText if <code>true</code> - the response is in text format
     */
    public LogResponseUnmarshaller(LogResponse logResponse, boolean isText) {
        this.logResponse = logResponse;
        this.isText = isText;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        if (isText) {
            logResponse.setTextLog(response.getText());
            return;
        }

        List<Revision> revisions = new ArrayList<Revision>();
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

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public LogResponse getPayload() {
        return logResponse;
    }
}
