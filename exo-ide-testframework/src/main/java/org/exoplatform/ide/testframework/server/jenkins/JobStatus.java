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
package org.exoplatform.ide.testframework.server.jenkins;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: JobStatus.java Aug 23, 2011 12:19:17 PM vereshchaka $
 */
public class JobStatus {
    public enum Status {
        QUEUE("In queue..."), //
        BUILD("Building..."), //
        END("End."); //

        private final String value;

        private Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /** Job name. */
    private String name;

    /** Current job status. */
    private Status status;

    /** Result of last build. Should be always <code>null</code> if {@link #status} other then {@link Status#END}. */
    private String lastBuildResult;

    /**
     * URL to download artifact. Should be always <code>null</code> if {@link #status} other then {@link Status#END} and
     * {@link #lastBuildResult} other then 'SUCCESS'.
     */
    private String artifactUrl;

    public JobStatus(String name, Status status, String lastBuildResult, String artifactUrl) {
        this.name = name;
        this.status = status;
        this.lastBuildResult = lastBuildResult;
        this.artifactUrl = artifactUrl;
    }

    public JobStatus() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getLastBuildResult() {
        return lastBuildResult;
    }

    public void setLastBuildResult(String lastBuildResult) {
        this.lastBuildResult = lastBuildResult;
    }

    public String getArtifactUrl() {
        return artifactUrl;
    }

    public void setArtifactUrl(String artifactUrl) {
        this.artifactUrl = artifactUrl;
    }

    @Override
    public String toString() {
        return "JobStatus [name=" + name + ", status=" + status + ", lastBuildResult=" + lastBuildResult
               + ", artifactUrl=" + artifactUrl + "]";
    }
}
