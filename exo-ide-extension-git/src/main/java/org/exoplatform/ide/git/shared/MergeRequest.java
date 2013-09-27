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
package org.exoplatform.ide.git.shared;

/**
 * Request to merge {@link #commit} with HEAD.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MergeRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class MergeRequest extends GitRequest {
    /** Commit to merge with HEAD. Typically it is the name of other branch. */
    private String commit;

    /**
     * @param commit commit to merge
     */
    public MergeRequest(String commit) {
        this.commit = commit;
    }

    /**
     * "Empty" merge request. Corresponding setters used to setup required parameters.
     */
    public MergeRequest() {
    }

    /** @return commit to merge */
    public String getCommit() {
        return commit;
    }

    /**
     * @param commit commit to merge
     */
    public void setCommit(String commit) {
        this.commit = commit;
    }
}
