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
package org.exoplatform.ide.client.framework.job;

import org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * Standard handling of errors for {@link RequestStatusHandler} interface
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Oct 31, 2011 evgen $
 *
 */
public abstract class RequestStatusHandlerBase implements RequestStatusHandler
{

   protected String projectName;

   /**
    * @param projectName
    */
   public RequestStatusHandlerBase(String projectName)
   {
      super();
      this.projectName = projectName;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler#requestError(java.lang.String, java.lang.Throwable)
    */
   @Override
   public void requestError(String id, Throwable exception)
   {
      Job job = new Job(id, JobStatus.ERROR);
      job.setError(exception);
      IDE.EVENT_BUS.fireEvent(new JobChangeEvent(job));
   }

}
