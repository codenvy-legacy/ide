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
package org.exoplatform.ide.extension.jenkins.shared;

import org.exoplatform.ide.extension.jenkins.shared.JobStatusBean.Status;

/**
 * Interface represents the Jenkins build job status.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: JobStatus.java Mar 15, 2012 4:04:15 PM azatsarynnyy $
 *
 */
public interface JobStatus
{

   /**
    * Get the name of the build job.
    * 
    * @return name of the build job.
    */
   public String getName();

   /**
    * Set the job name.
    * 
    * @param job name.
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
    * @param status status of the build job.
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
    * @param lastBuildResult result of the last build job.
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
    * @param artifactUrl URL to download artifact.
    */
   public void setArtifactUrl(String artifactUrl);

}