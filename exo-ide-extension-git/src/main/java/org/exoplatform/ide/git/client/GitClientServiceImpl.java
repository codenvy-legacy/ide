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
package org.exoplatform.ide.git.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.git.client.marshaller.AddRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.CloneRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.CommitRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.InitRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RevisionUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.StatusRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.StatusResponse;
import org.exoplatform.ide.git.client.marshaller.StatusResponseUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponseUnmarshaller;
import org.exoplatform.ide.git.shared.AddRequest;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.CommitRequest;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.git.shared.StatusRequest;

/**
 * Implementation of the {@link GitClientService}.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 23, 2011 11:52:24 AM anya $
 *
 */
public class GitClientServiceImpl extends GitClientService
{
   public static final String ADD = "/ide/git/add";

   public static final String CLONE = "/ide/git/clone";
   
   public static final String COMMIT = "/ide/git/commit";

   public static final String INIT = "/ide/git/init";

   public static final String STATUS = "/ide/git/status";

   public static final String GET_WORKDIR = "/ide/git-repo/workdir";

   private HandlerManager eventBus;

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   /**
    * @param eventBus eventBus
    * @param restContext rest context
    * @param loader loader to show on server request
    */
   public GitClientServiceImpl(HandlerManager eventBus, String restContext, Loader loader)
   {
      this.loader = loader;
      this.eventBus = eventBus;
      this.restServiceContext = restContext;
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#init(java.lang.String, boolean)
    */
   @Override
   public void init(String href, boolean bare, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + INIT;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);

      InitRequest initRequest = new InitRequest(workDir, bare);
      InitRequestMarshaller marshaller = new InitRequestMarshaller(initRequest);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#cloneRepository(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void cloneRepository(String href, String remoteUri, String remoteName, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + CLONE;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      CloneRequest cloneRequest = new CloneRequest(remoteUri, workDir);
      cloneRequest.setRemoteName(remoteName);
      CloneRequestMarshaller marshaller = new CloneRequestMarshaller(cloneRequest);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#status(java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void status(String href, boolean shortFormat, String[] fileFilter,
      AsyncRequestCallback<StatusResponse> callback)
   {
      String url = restServiceContext + STATUS;

      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      StatusRequest statusRequest = new StatusRequest(fileFilter, shortFormat);
      StatusRequestMarshaller marshaller = new StatusRequestMarshaller(statusRequest);

      StatusResponse statusResponse = new StatusResponse();
      StatusResponseUnmarshaller unmarshaller = new StatusResponseUnmarshaller(statusResponse);
      callback.setResult(statusResponse);
      callback.setPayload(unmarshaller);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#getWorkDir(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getWorkDir(String href, AsyncRequestCallback<WorkDirResponse> callback)
   {
      String url = restServiceContext + GET_WORKDIR;
      callback.setEventBus(eventBus);

      WorkDirResponse workDirResponse = new WorkDirResponse();
      WorkDirResponseUnmarshaller unmarshaller = new WorkDirResponseUnmarshaller(workDirResponse);
      callback.setResult(workDirResponse);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.LOCATION, href).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#add(java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void add(String href, boolean update, String[] filePattern, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + ADD;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      
      AddRequest addRequest = new AddRequest(filePattern, update);
      AddRequestMarshaller marshaller = new AddRequestMarshaller(addRequest);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#commit(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void commit(String href, String message, AsyncRequestCallback<Revision> callback)
   {
      String url = restServiceContext + COMMIT;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      
      CommitRequest commitRequest = new CommitRequest(message);
      CommitRequestMarshaller marshaller = new CommitRequestMarshaller(commitRequest);
      
      Revision revision = new Revision(null, message, 0, null);
      RevisionUnmarshaller unmarshaller = new RevisionUnmarshaller(revision);
      
      callback.setPayload(unmarshaller);
      callback.setResult(revision);
      
      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

}
