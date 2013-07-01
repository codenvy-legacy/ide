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

import com.codenvy.ide.ext.git.shared.BranchCheckoutRequest;
import com.codenvy.ide.rest.Marshallable;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Marshaller to create branch checkout request in JSON format.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 8, 2011 3:45:46 PM anya $
 */
public class BranchCheckoutRequestMarshaller implements Marshallable, Constants {
    /** Branch checkout request. */
    private BranchCheckoutRequest branchCheckoutRequest;

    /**
     * @param branchCheckoutRequest
     *         branch checkout request
     */
    public BranchCheckoutRequestMarshaller(BranchCheckoutRequest branchCheckoutRequest) {
        this.branchCheckoutRequest = branchCheckoutRequest;
    }

    /** {@inheritDoc} */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NAME, new JSONString(branchCheckoutRequest.getName()));

        if (branchCheckoutRequest.getStartPoint() != null) {
            jsonObject.put(START_POINT, new JSONString(branchCheckoutRequest.getStartPoint()));
        }

        jsonObject.put(CREATE_NEW, JSONBoolean.getInstance(branchCheckoutRequest.createNew()));

        return jsonObject.toString();
    }
}