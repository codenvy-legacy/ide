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

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;

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
     * @param branchCheckoutRequest branch checkout request
     */
    public BranchCheckoutRequestMarshaller(BranchCheckoutRequest branchCheckoutRequest) {
        this.branchCheckoutRequest = branchCheckoutRequest;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NAME, new JSONString(branchCheckoutRequest.getName()));

        if (branchCheckoutRequest.getStartPoint() != null) {
            jsonObject.put(START_POINT, new JSONString(branchCheckoutRequest.getStartPoint()));
        }

        jsonObject.put(CREATE_NEW, JSONBoolean.getInstance(branchCheckoutRequest.isCreateNew()));

        return jsonObject.toString();
    }

}
