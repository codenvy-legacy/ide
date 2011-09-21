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
package org.exoplatform.ide.git.client.create;

import org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Sep 16, 2011 evgen $
 *
 */
public class InitRequestStatusHandler implements RequestStatusHandler
{

   private String workDir;

   /**
    * @param workDir
    */
   public InitRequestStatusHandler(String workDir)
   {
      super();
      //remove first '/' in workDir
      this.workDir = workDir.substring(1);
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler#requestInProgress(java.lang.String)
    */
   @Override
   public void requestInProgress(String id)
   {
      Job job = new Job(id, JobStatus.STARTED);
      job.setStartMessage(GitExtension.MESSAGES.initStarted(workDir));
      IDE.EVENT_BUS.fireEvent(new JobChangeEvent(job));
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler#requestFinished(java.lang.String)
    */
   @Override
   public void requestFinished(String id)
   {
      Job job = new Job(id, JobStatus.FINISHED);
      job.setFinishMessage(GitExtension.MESSAGES.initFinished(workDir));
      IDE.EVENT_BUS.fireEvent(new JobChangeEvent(job));
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler#requestError(java.lang.Throwable)
    */
   @Override
   public void requestError(String id, Throwable exception)
   {
      Job job = new Job(id, JobStatus.ERROR);
      job.setError(exception);
      IDE.EVENT_BUS.fireEvent(new JobChangeEvent(job));
   }

}
