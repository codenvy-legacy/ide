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
package org.exoplatform.ide.extension.jenkins.shared;

import org.exoplatform.ide.extension.jenkins.shared.JobStatusBean.Status;

/**
 * Interface represents the Jenkins build job status.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: JobStatus.java Mar 15, 2012 4:04:15 PM azatsarynnyy $
 */
public interface JobStatus {

    /**
     * Get the name of the build job.
     *
     * @return name of the build job.
     */
    public String getName();

    /**
     * Set the job name.
     *
     * @param job
     *         name.
     */
    public void setName(String name);

    /**
     * Get the current status of the build job.
     *
     * @return status of the build job.
     */
    public Status getStatus();

    /**
     * Set the current status of the build job.
     *
     * @param status
     *         status of the build job.
     */
    public void setStatus(Status status);

    /**
     * Get result of the last build job.
     *
     * @return result of the last build job.
     */
    public String getLastBuildResult();

    /**
     * Set result of the last build job.
     *
     * @param lastBuildResult
     *         result of the last build job.
     */
    public void setLastBuildResult(String lastBuildResult);

    /**
     * Get the URL to download artifact.
     *
     * @return URL to download artifact.
     */
    public String getArtifactUrl();

    /**
     * Set the URL to download artifact.
     *
     * @param artifactUrl
     *         URL to download artifact.
     */
    public void setArtifactUrl(String artifactUrl);

}