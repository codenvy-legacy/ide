/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git.client.clone;

import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.job.RequestStatusHandlerBase;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 28, 2011 evgen $
 * 
 */
public class CloneRequestStatusHandler extends RequestStatusHandlerBase
{

   private String remoteUri;

   /**
    * @param remoteUri
    */
   public CloneRequestStatusHandler(String projectName, String remoteUri)
   {
      super(projectName);
      this.remoteUri = remoteUri;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler#requestInProgress(java.lang.String)
    */
   @Override
   public void requestInProgress(String id)
   {
      Job job = new Job(id, JobStatus.STARTED);
      job.setStartMessage(GitExtension.MESSAGES.cloneStarted(projectName, remoteUri));
      IDE.fireEvent(new JobChangeEvent(job));
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler#requestFinished(java.lang.String)
    */
   @Override
   public void requestFinished(String id)
   {
      Job job = new Job(id, JobStatus.FINISHED);
      job.setFinishMessage(GitExtension.MESSAGES.cloneFinished(projectName, remoteUri));
      IDE.fireEvent(new JobChangeEvent(job));
   }

}
