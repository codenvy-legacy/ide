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
import org.exoplatform.ide.git.shared.AddRequest;

/**
 * Marshaller for add changes to index request.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 29, 2011 5:27:47 PM anya $
 */
public class AddRequestMarshaller implements Marshallable, Constants {
    /** Add changes to index request. */
    private AddRequest addRequest;

    /**
     * @param addRequest add changes to index request
     */
    public AddRequestMarshaller(AddRequest addRequest) {
        this.addRequest = addRequest;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(UPDATE, JSONBoolean.getInstance(addRequest.isUpdate()));
        if (addRequest.getFilepattern() != null && addRequest.getFilepattern().length > 0) {
            JSONArray filePatternArray = new JSONArray();
            for (int i = 0; i < addRequest.getFilepattern().length; i++) {
                filePatternArray.set(i, new JSONString(addRequest.getFilepattern()[i]));
            }
            jsonObject.put(FILE_PATTERN, filePatternArray);
        }
        return jsonObject.toString();
    }

}
