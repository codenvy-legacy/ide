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
package org.exoplatform.ide.extension.jenkins.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.extension.jenkins.shared.Job;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;

/**
 * Client service for Jenkins Extension
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class JenkinsService
{

   private String restContext;

   private Loader loader;

   private static JenkinsService instance;

   private static final String JENKINS = "/ide/jenkins";

   /**
    * @param restContext
    * @param loader
    */
   public JenkinsService(String restContext, Loader loader)
   {
      super();
      this.restContext = restContext;
      this.loader = loader;
      instance = this;
   }

   /**
    * @return instance of {@link JenkinsService}
    */
   public static JenkinsService get()
   {
      if (instance == null)
         throw new IllegalStateException("Jenkins Service uninitialized");
      return instance;
   }

   /**
    * Create new Jenkins job
    * 
    * @param name of job
    * @param git Got repository URL
    * @param user User name
    * @param mail User e-mail
    * @param workDir Git working directory
    * @param callback
    * @throws RequestException 
    */
   public void createJenkinsJob(String name, String user, String mail, String vfsId, String projectId,
      AsyncRequestCallback<Job> callback) throws RequestException
   {
      String url =
         restContext + JENKINS + "/job/create?name=" + name + "&user=" + user + "&email=" + mail + "&vfsid=" + vfsId
            + "&projectid=" + projectId;
      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
   }

   /**
    * Start building job
    * 
    * @param jobName Name of Job
    * @param callback
    * @throws RequestException 
    */
   public void buildJob(String vfsId, String projectId, String jobName, AsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restContext + JENKINS + "/job/build?name=" + jobName + "&vfsid=" + vfsId + "&projectid=" + projectId;
      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
   }

   /**
    * Get Job status
    * 
    * @param jobName Name of Job
    * @param callback
    * @throws RequestException 
    */
   public void jobStatus(String vfsId, String projectId, String jobName, AsyncRequestCallback<JobStatus> callback) throws RequestException
   {
      String url =
         restContext + JENKINS + "/job/status?name=" + jobName + "&vfsid=" + vfsId + "&projectid=" + projectId;
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

   public void getJenkinsOutput(String vfsId, String projectId, String jobName, AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      String url =
         restContext + JENKINS + "/job/console-output?name=" + jobName + "&vfsid=" + vfsId + "&projectid=" + projectId;
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }
}
