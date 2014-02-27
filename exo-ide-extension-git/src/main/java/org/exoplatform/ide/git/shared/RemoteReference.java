/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
 * Reference of remote repository in format: commitId referenceName.
 *
 * @author Alexander Garagatyi
 */
public class RemoteReference {
    private String commitId;

    private String referenceName;

    public RemoteReference(String commitId, String referenceName) {
        this.commitId = commitId;
        this.referenceName = referenceName;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteReference)) return false;

        RemoteReference that = (RemoteReference)o;

        if (commitId != null ? !commitId.equals(that.commitId) : that.commitId != null) return false;
        if (referenceName != null ? !referenceName.equals(that.referenceName) : that.referenceName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = commitId != null ? commitId.hashCode() : 0;
        result = 31 * result + (referenceName != null ? referenceName.hashCode() : 0);
        return result;
    }
}
