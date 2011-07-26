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

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.jenkins.client.marshal.StringContentUnmarshaller;
import org.exoplatform.ide.extension.jenkins.client.marshal.JenkinsJobStatusUnmarshaller;
import org.exoplatform.ide.extension.jenkins.client.marshal.JenkinsJobUnmarshaller;
import org.exoplatform.ide.extension.jenkins.shared.Job;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;

/**
 * Client service for Jenkins Extension
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
    * @param name of job
    * @param git Got repository URL
    * @param user User name
    * @param mail User e-mail
    * @param workDir Git working directory
    * @param callback
    */
   public void createJenkinsJob(String name, String git, String user, String mail, String workDir,
      AsyncRequestCallback<Job> callback)
   {
      String url =
         restContext + JENKINS + "/job/create?name=" + name + "&user=" + user + "&email=" + mail + "&workdir="
            + workDir + "&git=" + git;
      Job job = new Job();
      JenkinsJobUnmarshaller unmarshaller = new JenkinsJobUnmarshaller(job);
      callback.setEventBus(IDE.EVENT_BUS);
      callback.setPayload(unmarshaller);
      callback.setResult(job);
      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   /**
    * Start building job
    * @param jobName Name of Job
    * @param callback
    */
   public void buildJob(String jobName, AsyncRequestCallback<String> callback)
   {
      String url = restContext + JENKINS + "/job/build?name=" + jobName;
      callback.setEventBus(IDE.EVENT_BUS);
      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   /**
    * Get Job status
    * @param jobName Name of Job
    * @param callback
    */
   public void jobStatus(String jobName, AsyncRequestCallback<JobStatus> callback)
   {
      String url = restContext + JENKINS + "/job/status?name=" + jobName;
      JobStatus jobStatus = new JobStatus();
      callback.setEventBus(IDE.EVENT_BUS);
      callback.setResult(jobStatus);
      callback.setPayload(new JenkinsJobStatusUnmarshaller(jobStatus));
      AsyncRequest.build(RequestBuilder.GET, url, null).send(callback);
   }

   /**
    * @param dirUrl
    * @param fileName
    * @param callback
    */
   public void getFileContent(String dirUrl, String fileName, AsyncRequestCallback<String> callback)
   {
      String url = restContext + "/ide/discovery/find/content?location=" + dirUrl + "&name=" + fileName;
      callback.setEventBus(IDE.EVENT_BUS);
      callback.setPayload(new StringContentUnmarshaller(callback));
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   public void getJenkinsOutput(String jobName, AsyncRequestCallback<String> callback)
   {
      String url = restContext + JENKINS + "/job/console-output?name=" + jobName;
      callback.setEventBus(IDE.EVENT_BUS);
      callback.setPayload(new StringContentUnmarshaller(callback));
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }
}
