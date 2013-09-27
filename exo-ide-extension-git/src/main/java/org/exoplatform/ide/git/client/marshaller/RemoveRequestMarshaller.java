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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.git.shared.RmRequest;

/**
 * Marshaller for remove files request in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 12, 2011 2:55:02 PM anya $
 */
public class RemoveRequestMarshaller implements Marshallable, Constants {
    /** Remove request. */
    private RmRequest rmRequest;

    /**
     * @param rmRequest remove request
     */
    public RemoveRequestMarshaller(RmRequest rmRequest) {
        this.rmRequest = rmRequest;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CACHED, JSONBoolean.getInstance(rmRequest.getCached()));
        if (rmRequest.getFiles() != null && rmRequest.getFiles().length > 0) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < rmRequest.getFiles().length; i++) {
                array.set(i, new JSONString(rmRequest.getFiles()[i]));
            }
            jsonObject.put(FILES, array);
        }
        return jsonObject.toString();
    }
}
