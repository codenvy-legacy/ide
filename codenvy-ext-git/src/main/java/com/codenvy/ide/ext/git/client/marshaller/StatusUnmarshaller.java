/*
 * Copyright (C) 2013 eXo Platform SAS.
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
import com.codenvy.ide.ext.git.shared.Status;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * The unmarshaller for git status.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class StatusUnmarshaller implements Unmarshallable<Status>, Constants {
    private DtoClientImpls.StatusImpl status;

    /**
     * Create unmarshaller.
     *
     * @param status
     */
    public StatusUnmarshaller(DtoClientImpls.StatusImpl status) {
        this.status = status;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        JSONObject json = JSONParser.parseStrict(text).isObject();
        String value = json.toString();
        DtoClientImpls.StatusImpl status = DtoClientImpls.StatusImpl.deserialize(value);
        this.status.setAdded(status.getAdded());
        this.status.setChanged(status.getChanged());
        this.status.setBranchName(status.getBranchName());
        this.status.setClean(status.clean());
        this.status.setConflicting(status.getConflicting());
        this.status.setMissing(status.getMissing());
        this.status.setModified(status.getModified());
        this.status.setRemoved(status.getRemoved());
        this.status.setShortFormat(status.getShortFormat());
        this.status.setUntracked(status.getUntracked());
        this.status.setUntrackedFolders(status.getUntrackedFolders());
    }

    /** {@inheritDoc} */
    @Override
    public Status getPayload() {
        return status;
    }
}