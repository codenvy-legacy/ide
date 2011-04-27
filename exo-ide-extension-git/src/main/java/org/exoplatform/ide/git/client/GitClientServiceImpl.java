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
import org.exoplatform.ide.git.client.marshaller.BranchCheckoutRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchCreateRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchDeleteRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchListRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchListUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.CloneRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.CommitRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.FetchRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.InitRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.LogRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.client.marshaller.LogResponseUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.PullRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.PushRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoteAddRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoteListRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoteListUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoveRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.ResetRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RevisionUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.StatusRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.StatusResponse;
import org.exoplatform.ide.git.client.marshaller.StatusResponseUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponseUnmarshaller;
import org.exoplatform.ide.git.shared.AddRequest;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;
import org.exoplatform.ide.git.shared.BranchCreateRequest;
import org.exoplatform.ide.git.shared.BranchDeleteRequest;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.CommitRequest;
import org.exoplatform.ide.git.shared.FetchRequest;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.LogRequest;
import org.exoplatform.ide.git.shared.PullRequest;
import org.exoplatform.ide.git.shared.PushRequest;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.git.shared.RemoteAddRequest;
import org.exoplatform.ide.git.shared.RemoteListRequest;
import org.exoplatform.ide.git.shared.ResetRequest;
import org.exoplatform.ide.git.shared.ResetRequest.ResetType;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.git.shared.RmRequest;
import org.exoplatform.ide.git.shared.StatusRequest;

import java.util.ArrayList;
import java.util.List;

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

   public static final String BRANCH_LIST = "/ide/git/branch-list";

   public static final String BRANCH_CHECKOUT = "/ide/git/branch-checkout";

   public static final String BRANCH_CREATE = "/ide/git/branch-create";

   public static final String BRANCH_DELETE = "/ide/git/branch-delete";

   public static final String FETCH = "/ide/git/fetch";

   public static final String INIT = "/ide/git/init";

   public static final String LOG = "/ide/git/log";

   public static final String STATUS = "/ide/git/status";

   public static final String GET_WORKDIR = "/ide/git-repo/workdir";

   public static final String PUSH = "/ide/git/push";
   
   public static final String PULL = "/ide/git/pull";

   public static final String REMOTE_LIST = "/ide/git/remote-list";

   public static final String REMOTE_ADD = "/ide/git/remote-add";

   public static final String REMOTE_DELETE = "/ide/git/remote-delete";

   public static final String REMOVE = "/ide/git/rm";

   public static final String RESET = "/ide/git/reset";

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
   public void statusText(String href, boolean shortFormat, String[] fileFilter,
      AsyncRequestCallback<StatusResponse> callback)
   {
      String url = restServiceContext + STATUS;

      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      StatusRequest statusRequest = new StatusRequest(fileFilter, shortFormat);
      StatusRequestMarshaller marshaller = new StatusRequestMarshaller(statusRequest);

      StatusResponse statusResponse = new StatusResponse();
      StatusResponseUnmarshaller unmarshaller = new StatusResponseUnmarshaller(statusResponse, true);
      callback.setResult(statusResponse);
      callback.setPayload(unmarshaller);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN)
         .send(callback);
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
   public void commit(String href, String message, boolean all, AsyncRequestCallback<Revision> callback)
   {
      String url = restServiceContext + COMMIT;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);

      CommitRequest commitRequest = new CommitRequest(message, all);
      CommitRequestMarshaller marshaller = new CommitRequestMarshaller(commitRequest);

      Revision revision = new Revision(null, message, 0, null);
      RevisionUnmarshaller unmarshaller = new RevisionUnmarshaller(revision);

      callback.setPayload(unmarshaller);
      callback.setResult(revision);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#push(java.lang.String, java.lang.String[], java.lang.String, boolean)
    */
   @Override
   public void push(String href, String[] refSpec, String remote, boolean force, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + PUSH;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      PushRequest pushRequest = new PushRequest();
      pushRequest.setRemote(remote);
      pushRequest.setRefSpec(refSpec);
      pushRequest.setForce(force);

      PushRequestMarshaller marshaller = new PushRequestMarshaller(pushRequest);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#remoteList(java.lang.String, java.lang.String, boolean)
    */
   @Override
   public void remoteList(String href, String remoteName, boolean verbose, AsyncRequestCallback<List<Remote>> callback)
   {
      String url = restServiceContext + REMOTE_LIST;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);

      List<Remote> remotes = new ArrayList<Remote>();
      RemoteListRequest remoteListRequest = new RemoteListRequest(remoteName, verbose);
      RemoteListUnmarshaller unmarshaller = new RemoteListUnmarshaller(remotes);
      RemoteListRequestMarshaller marshaller = new RemoteListRequestMarshaller(remoteListRequest);

      callback.setPayload(unmarshaller);
      callback.setResult(remotes);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#branchList(java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void branchList(String href, boolean remote, AsyncRequestCallback<List<Branch>> callback)
   {
      String url = restServiceContext + BRANCH_LIST;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);

      BranchListRequest branchListRequest = new BranchListRequest();
      if (remote)
      {
         branchListRequest.setListMode(BranchListRequest.LIST_REMOTE);
      }

      BranchListRequestMarshaller marshaller = new BranchListRequestMarshaller(branchListRequest);
      List<Branch> branches = new ArrayList<Branch>();

      BranchListUnmarshaller unmarshaller = new BranchListUnmarshaller(branches);
      callback.setPayload(unmarshaller);
      callback.setResult(branches);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#status(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void status(String href, AsyncRequestCallback<StatusResponse> callback)
   {
      String url = restServiceContext + STATUS;

      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      StatusRequest statusRequest = new StatusRequest(null, true);
      StatusRequestMarshaller marshaller = new StatusRequestMarshaller(statusRequest);

      StatusResponse statusResponse = new StatusResponse();
      StatusResponseUnmarshaller unmarshaller = new StatusResponseUnmarshaller(statusResponse, false);
      callback.setResult(statusResponse);
      callback.setPayload(unmarshaller);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#branchDelete(java.lang.String, java.lang.String, boolean)
    */
   @Override
   public void branchDelete(String href, String name, boolean force, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + BRANCH_DELETE;

      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      BranchDeleteRequest branchDeleteRequest = new BranchDeleteRequest(name, force);
      BranchDeleteRequestMarshaller marshaller = new BranchDeleteRequestMarshaller(branchDeleteRequest);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#branchCreate(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void branchCreate(String href, String name, String startPoint, AsyncRequestCallback<Branch> callback)
   {
      String url = restServiceContext + BRANCH_CREATE;

      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      BranchCreateRequest branchCreateRequest = new BranchCreateRequest(name, startPoint);
      BranchCreateRequestMarshaller marshaller = new BranchCreateRequestMarshaller(branchCreateRequest);

      Branch branch = new Branch();
      BranchUnmarshaller unmarshaller = new BranchUnmarshaller(branch);
      callback.setResult(branch);
      callback.setPayload(unmarshaller);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#branchCheckout(java.lang.String, java.lang.String, java.lang.String, boolean)
    */
   @Override
   public void branchCheckout(String href, String name, String startPoint, boolean createNew,
      AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + BRANCH_CHECKOUT;

      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      BranchCheckoutRequest branchCheckoutRequest = new BranchCheckoutRequest(name, startPoint, createNew);
      BranchCheckoutRequestMarshaller marshaller = new BranchCheckoutRequestMarshaller(branchCheckoutRequest);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#remove(java.lang.String, java.lang.String[], org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void remove(String href, String[] files, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + REMOVE;

      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      RmRequest rmRequest = new RmRequest(files);
      RemoveRequestMarshaller marshaller = new RemoveRequestMarshaller(rmRequest);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#reset(java.lang.String, java.lang.String[], java.lang.String, org.exoplatform.ide.git.shared.ResetRequest.ResetType, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void reset(String href, String[] paths, String commit, ResetType resetType,
      AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + RESET;

      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      ResetRequest resetRequest = new ResetRequest();
      resetRequest.setPaths(paths);
      resetRequest.setCommit(commit);
      resetRequest.setType(resetType);

      ResetRequestMarshaller marshaller = new ResetRequestMarshaller(resetRequest);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#log(java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void log(String href, boolean isTextFormat, AsyncRequestCallback<LogResponse> callback)
   {
      String url = restServiceContext + LOG;

      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      LogRequest logRequest = new LogRequest();
      LogRequestMarshaller marshaller = new LogRequestMarshaller(logRequest);

      LogResponse logResponse = new LogResponse();
      LogResponseUnmarshaller unmarshaller = new LogResponseUnmarshaller(logResponse, isTextFormat);
      callback.setResult(logResponse);
      callback.setPayload(unmarshaller);

      String params = "workdir=" + workDir;

      if (isTextFormat)
      {
         AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
            .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
      }
      else
      {
         AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
            .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
            .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
      }

   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#remoteAdd(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void remoteAdd(String href, String name, String repositoryURL, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + REMOTE_ADD;

      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      RemoteAddRequest remoteAddRequest = new RemoteAddRequest(name, repositoryURL);

      RemoteAddRequestMarshaller marshaller = new RemoteAddRequestMarshaller(remoteAddRequest);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#remoteDelete(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void remoteDelete(String href, String name, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + REMOTE_DELETE + "/" + name;

      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#fetch(java.lang.String, java.lang.String, java.lang.String[], boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void fetch(String href, String remote, String[] refspec, boolean removeDeletedRefs,
      AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + FETCH;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);

      FetchRequest fetchRequest = new FetchRequest(refspec, remote, removeDeletedRefs, 0);
      FetchRequestMarshaller marshaller = new FetchRequestMarshaller(fetchRequest);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#pull(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void pull(String href, String refSpec, String remote, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + PULL;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);

      PullRequest pullRequest = new PullRequest(remote, refSpec, 0);
      PullRequestMarshaller marshaller = new PullRequestMarshaller(pullRequest);

      String params = "workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }
}
