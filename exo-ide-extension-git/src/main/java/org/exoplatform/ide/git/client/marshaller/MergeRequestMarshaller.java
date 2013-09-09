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
import org.exoplatform.ide.git.shared.MergeRequest;

/**
 * The marshaller for merge operation request.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 11:56:09 AM anya $
 */
public class MergeRequestMarshaller implements Marshallable, Constants {
    /** Request for merge. */
    private MergeRequest mergeRequest;

    /**
     * @param mergeRequest request for merge
     */
    public MergeRequestMarshaller(MergeRequest mergeRequest) {
        this.mergeRequest = mergeRequest;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {
        JSONObject json = new JSONObject();
        json.put(COMMIT, new JSONString(mergeRequest.getCommit()));
        return json.toString();
    }
}
