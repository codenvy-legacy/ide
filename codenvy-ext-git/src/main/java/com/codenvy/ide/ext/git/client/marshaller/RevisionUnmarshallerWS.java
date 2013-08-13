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

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RevisionUnmarshallerWS.java Nov 22, 2012 12:38:47 PM azatsarynnyy $
 */
public class RevisionUnmarshallerWS implements Unmarshallable<Revision> {
    /** Represents revision info. */
    private DtoClientImpls.RevisionImpl revision;

    /**
     * @param revision
     *         revision information
     */
    public RevisionUnmarshallerWS(DtoClientImpls.RevisionImpl revision) {
        this.revision = revision;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        String body = response.getBody();
        if (body == null || body.isEmpty()) {
            return;
        }

        JSONValue json = JSONParser.parseStrict(body);
        if (json == null)
            return;
        JSONObject revisionObject = json.isObject();
        if (revisionObject == null)
            return;

        String value = revisionObject.toString();
        DtoClientImpls.RevisionImpl revision = DtoClientImpls.RevisionImpl.deserialize(value);
        this.revision.setId(revision.getId());
        this.revision.setCommitTime(revision.getCommitTime());
        this.revision.setMessage(revision.getMessage());
        this.revision.setBranch(revision.getBranch());
        this.revision.setCommitter(revision.getCommitter());
        this.revision.setFake(revision.fake());
    }

    /** {@inheritDoc} */
    @Override
    public Revision getPayload() {
        return revision;
    }
}