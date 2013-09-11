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
import org.exoplatform.ide.git.shared.FetchRequest;

/**
 * Marshaller for creation fetch request in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 20, 2011 3:05:47 PM anya $
 */
public class FetchRequestMarshaller implements Marshallable, Constants {
    /** Fetch request. */
    private FetchRequest fetchRequest;

    /**
     * @param fetchRequest fetch request
     */
    public FetchRequestMarshaller(FetchRequest fetchRequest) {
        this.fetchRequest = fetchRequest;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        if (fetchRequest.getRefSpec() != null || fetchRequest.getRefSpec().length > 0) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < fetchRequest.getRefSpec().length; i++) {
                array.set(i, new JSONString(fetchRequest.getRefSpec()[i]));
            }
            jsonObject.put(REF_SPEC, array);
        }

        if (fetchRequest.getRemote() != null) {
            jsonObject.put(REMOTE, new JSONString(fetchRequest.getRemote()));
        }

        jsonObject.put(REMOVE_DELETED_REFS, JSONBoolean.getInstance(fetchRequest.isRemoveDeletedRefs()));

        return jsonObject.toString();
    }

}
