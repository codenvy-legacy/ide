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

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.git.shared.ResetRequest;

/**
 * Marshaller for reset files request in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 13, 2011 5:51:18 PM anya $
 */
public class ResetRequestMarshaller implements Marshallable, Constants {
    /** Reset request. */
    private ResetRequest resetRequest;

    /**
     * @param resetRequest reset request
     */
    public ResetRequestMarshaller(ResetRequest resetRequest) {
        this.resetRequest = resetRequest;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();

        if (resetRequest.getCommit() != null) {
            jsonObject.put(COMMIT, new JSONString(resetRequest.getCommit()));
        }
        if (resetRequest.getType() != null) {
            jsonObject.put(TYPE, new JSONString(resetRequest.getType().name()));
        }
        return jsonObject.toString();
    }
}
