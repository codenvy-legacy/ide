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

    /**
     * @param revision
     *         revision information
     */
    public RevisionUnmarshaller(DtoClientImpls.RevisionImpl revision) {
        this.revision = revision;
    }

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