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
package org.exoplatform.ide.shell.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;

/**
 * Service for getting running jobs (asynchronous tasks).
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 13, 2012 9:58:05 AM anya $
 * 
 */
public class JobService
{
   /**
    * Service.
    */
   private static JobService jobService;

   /**
    * REST context.
    */
   private final String REST_CONTEXT = "rest/private";

   /**
    * Path to job's list service.
    */
   private final String JOBS_PATH = "async";

   protected JobService()
   {
   }

   /**
    * @return {@link JobService} job service
    */
   public static JobService getService()
   {
      if (jobService == null)
      {
         jobService = new JobService();
      }
      return jobService;
   }

   /**
    * Get the list of running jobs (asynchronous tasks) in text format.
    * 
    * @param callback callback
    * @throws RequestException
    */
   public void getJobs(AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      String url = REST_CONTEXT + "/" + JOBS_PATH;

      AsyncRequest.build(RequestBuilder.GET, url).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN).send(callback);
   }

   public void killJob(String jobId, AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      StringBuilder url = new StringBuilder(REST_CONTEXT).append("/").append(JOBS_PATH).append("/").append(jobId);

      AsyncRequest.build(RequestBuilder.DELETE, url.toString()).send(callback);
   }
}
