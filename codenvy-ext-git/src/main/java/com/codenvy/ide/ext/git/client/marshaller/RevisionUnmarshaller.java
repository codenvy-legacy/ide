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
package com.codenvy.ide.ext.git.client.marshaller;

import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 31, 2011 11:15:57 AM anya $
 */
public class RevisionUnmarshaller implements Unmarshallable<Revision> {
    /** Represents revision info. */
    private DtoClientImpls.RevisionImpl revision;

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) {
        String text = response.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        JSONValue json = JSONParser.parseStrict(text);
        if (json == null)
            return;
        JSONObject revisionObject = json.isObject();
        if (revisionObject == null)
            return;

        String value = revisionObject.toString();
        revision = DtoClientImpls.RevisionImpl.deserialize(value);
    }

    /** {@inheritDoc} */
    @Override
    public Revision getPayload() {
        return revision;
    }
}