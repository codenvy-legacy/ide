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
import org.exoplatform.ide.git.shared.DiffRequest;

/**
 * Marshaller for creating diff request in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 4, 2011 10:57:37 AM anya $
 */
public class DiffRequestMarshaller implements Marshallable, Constants {
    /** Diff request. */
    private DiffRequest diffRequest;

    /**
     * @param diffRequest diff request
     */
    public DiffRequestMarshaller(DiffRequest diffRequest) {
        this.diffRequest = diffRequest;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();

        if (diffRequest.getFileFilter() != null && diffRequest.getFileFilter().length > 0) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < diffRequest.getFileFilter().length; i++) {
                array.set(i, new JSONString(diffRequest.getFileFilter()[i]));
            }
            jsonObject.put(FILE_FILTER, array);
        }
        jsonObject.put(NO_RENAMES, JSONBoolean.getInstance(diffRequest.isNoRenames()));
        jsonObject.put(CACHED, JSONBoolean.getInstance(diffRequest.isCached()));

        if (diffRequest.getType() != null) {
            jsonObject.put(TYPE, new JSONString(diffRequest.getType().name()));
        }

        if (diffRequest.getCommitA() != null) {
            jsonObject.put(COMMIT_A, new JSONString(diffRequest.getCommitA()));
        }

        if (diffRequest.getCommitB() != null) {
            jsonObject.put(COMMIT_B, new JSONString(diffRequest.getCommitB()));
        }
        return jsonObject.toString();
    }
}
