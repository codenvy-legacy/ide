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