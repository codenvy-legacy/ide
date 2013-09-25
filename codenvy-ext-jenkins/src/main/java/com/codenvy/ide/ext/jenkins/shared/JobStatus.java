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
package com.codenvy.ide.ext.jenkins.shared;

import com.codenvy.ide.dto.DTO;

/**
 * Interface represents the Jenkins build job status.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: JobStatus.java Mar 15, 2012 4:04:15 PM azatsarynnyy $
 */
@DTO
public interface JobStatus {
    public enum Status {
        QUEUE("In queue..."), //
        BUILD("Building..."), //
        END("End."); //

        private final String value;

        private Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Get the name of the build job.
     *
     * @return name of the build job.
     */
    String getName();

    /**
     * Get the current status of the build job.
     *
     * @return status of the build job.
     */
    Status getStatus();

    /**
     * Get result of the last build job.
     *
     * @return result of the last build job.
     */
    String getLastBuildResult();

    /**
     * Get the URL to download artifact.
     *
     * @return URL to download artifact.
     */
    String getArtifactUrl();
}