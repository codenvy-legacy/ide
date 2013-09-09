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
import org.exoplatform.ide.git.shared.PullRequest;

/**
 * Marshaller for pull request in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 20, 2011 3:20:49 PM anya $
 */
public class PullRequestMarshaller implements Marshallable, Constants {
    /** Pull request. */
    private PullRequest pullRequest;

    /**
     * @param pullRequest pull request
     */
    public PullRequestMarshaller(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        if (pullRequest.getRefSpec() != null) {
            jsonObject.put(REF_SPEC, new JSONString(pullRequest.getRefSpec()));
        }

        if (pullRequest.getRemote() != null) {
            jsonObject.put(REMOTE, new JSONString(pullRequest.getRemote()));
        }
        return jsonObject.toString();
    }

}
