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
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for {@link Branch} in JSON format.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 11, 2011 12:29:46 PM anya $
 */
public class BranchUnmarshaller implements Unmarshallable<Branch>, Constants {
    /** Branch. */
    private DtoClientImpls.BranchImpl branch;

    /**
     * @param branch
     *         branch
     */
    public BranchUnmarshaller(DtoClientImpls.BranchImpl branch) {
        this.branch = branch;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONObject object = JSONParser.parseStrict(response.getText()).isObject();
        if (object == null)
            return;
        if (object.containsKey(ACTIVE)) {
            boolean active = object.get(ACTIVE).isBoolean() != null && object.get(ACTIVE).isBoolean().booleanValue();
            branch.setActive(active);
        }
        if (object.containsKey(NAME)) {
            String name = (object.get(NAME).isString() != null) ? object.get(NAME).isString().stringValue() : "";
            branch.setName(name);
        }
        if (object.containsKey(DISPLAY_NAME)) {
            String displayName =
                    (object.get(DISPLAY_NAME).isString() != null) ? object.get(DISPLAY_NAME).isString().stringValue() : "";
            branch.setDisplayName(displayName);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Branch getPayload() {
        return branch;
    }
}