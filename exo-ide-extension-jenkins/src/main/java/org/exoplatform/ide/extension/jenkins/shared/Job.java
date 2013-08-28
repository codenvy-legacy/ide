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

/**
 * Build job info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Job.java Mar 15, 2012 3:14:27 PM azatsarynnyy $
 */
public interface Job {
    /**
     * Get name of the job.
     *
     * @return name of the job.
     */
    public String getName();

    /**
     * Set the job name.
     *
     * @param name
     *         name of the job.
     */
    public void setName(String name);

    /**
     * Get the url of the build job.
     *
     * @return url of the build job.
     */
    public String getBuildUrl();

    /**
     * Set the url for build job.
     *
     * @param buildUrl
     *         of the build job.
     */
    public void setBuildUrl(String buildUrl);

    /**
     * Get the url for check status of the build job.
     *
     * @return url for check status of the build job.
     */
    public String getStatusUrl();

    /**
     * Set the url for check status of the build job.
     *
     * @param statusUrl
     *         url for check status of the build job.
     */
    public void setStatusUrl(String statusUrl);

}