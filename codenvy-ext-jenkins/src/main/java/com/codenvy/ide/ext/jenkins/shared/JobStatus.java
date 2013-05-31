/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.jenkins.shared;

/**
 * Interface represents the Jenkins build job status.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: JobStatus.java Mar 15, 2012 4:04:15 PM azatsarynnyy $
 */
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