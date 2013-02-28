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
package org.exoplatform.ide.extension.maven.client.build;

import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.extension.maven.client.BuilderExtension;
import org.exoplatform.ide.job.Job;
import org.exoplatform.ide.job.Job.JobStatus;
import org.exoplatform.ide.job.JobChangeEvent;
import org.exoplatform.ide.rest.RequestStatusHandler;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildRequestStatusHandler.java Feb 28, 2012 13:03:10 PM azatsarynnyy $
 */
public class BuildRequestStatusHandler implements RequestStatusHandler
{
   private String projectName;

   private EventBus eventBus;

   /**
    * @param projectName project's name
    */
   public BuildRequestStatusHandler(String projectName, EventBus eventBus)
   {
      super();
      this.projectName = projectName;
      this.eventBus = eventBus;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestInProgress(java.lang.String)
    */
   @Override
   public void requestInProgress(String id)
   {
      Job job = new Job(id, JobStatus.STARTED);
      job.setStartMessage(BuilderExtension.LOCALIZATION_CONSTANT.buildStarted(projectName));
      eventBus.fireEvent(new JobChangeEvent(job));
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestFinished(java.lang.String)
    */
   @Override
   public void requestFinished(String id)
   {
      Job job = new Job(id, JobStatus.FINISHED);
      job.setFinishMessage(BuilderExtension.LOCALIZATION_CONSTANT.buildFinished(projectName));
      eventBus.fireEvent(new JobChangeEvent(job));
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestError(java.lang.String, java.lang.Throwable)
    */
   @Override
   public void requestError(String id, Throwable exception)
   {
      Job job = new Job(id, JobStatus.ERROR);
      job.setError(exception);
      eventBus.fireEvent(new JobChangeEvent(job));
   }
}
