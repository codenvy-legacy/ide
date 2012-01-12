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
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.git.client.add.AddRequestHandler;
import org.exoplatform.ide.git.client.clone.CloneRequestStatusHandler;
import org.exoplatform.ide.git.client.commit.CommitRequestHandler;
import org.exoplatform.ide.git.client.fetch.FetchRequestHandler;
import org.exoplatform.ide.git.client.init.InitRequestStatusHandler;
import org.exoplatform.ide.git.client.marshaller.AddRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchCheckoutRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchCreateRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchDeleteRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchListRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchListUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.CloneRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.CommitRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.DiffRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.DiffResponse;
import org.exoplatform.ide.git.client.marshaller.DiffResponseUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.FetchRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.InitRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.LogRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.client.marshaller.LogResponseUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.Merge;
import org.exoplatform.ide.git.client.marshaller.MergeRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.MergeUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.PullRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.PushRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoteAddRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoteListRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoteListUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoveRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.ResetRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.StatusRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.StatusResponse;
import org.exoplatform.ide.git.client.marshaller.StatusResponseUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponseUnmarshaller;
import org.exoplatform.ide.git.client.pull.PullRequestHandler;
import org.exoplatform.ide.git.client.push.PushRequestHandler;
import org.exoplatform.ide.git.shared.AddRequest;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;
import org.exoplatform.ide.git.shared.BranchCreateRequest;
import org.exoplatform.ide.git.shared.BranchDeleteRequest;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.CommitRequest;
import org.exoplatform.ide.git.shared.DiffRequest;
import org.exoplatform.ide.git.shared.DiffRequest.DiffType;
import org.exoplatform.ide.git.shared.FetchRequest;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.LogRequest;
import org.exoplatform.ide.git.shared.MergeRequest;
import org.exoplatform.ide.git.shared.MergeResult;
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
import org.exoplatform.ide.vfs.client.model.ProjectModel;

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

   public static final String BRANCH_LIST = "/ide/git/branch-list";

   public static final String BRANCH_CHECKOUT = "/ide/git/branch-checkout";

   public static final String BRANCH_CREATE = "/ide/git/branch-create";

   public static final String BRANCH_DELETE = "/ide/git/branch-delete";

   public static final String CLONE = "/ide/git/clone";

   public static final String COMMIT = "/ide/git/commit";

   public static final String DIFF = "/ide/git/diff";

   public static final String FETCH = "/ide/git/fetch";

   public static final String INIT = "/ide/git/init";

   public static final String LOG = "/ide/git/log";

   public static final String MERGE = "/ide/git/merge";

   public static final String STATUS = "/ide/git/status";

   public static final String WORKDIR = "/ide/git-repo/workdir";

   public static final String RO_URL = "/ide/git/read-only-url";

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
    * @throws RequestException 
    * @see org.exoplatform.ide.git.client.GitClientService#init(java.lang.String, boolean)
    */
   public void init(String vfsId, String projectid, boolean bare,
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + INIT;

      InitRequest initRequest = new InitRequest(projectid, bare);
      InitRequestMarshaller marshaller = new InitRequestMarshaller(initRequest);
      String params = "vfsid=" + vfsId + "&projectid=" + projectid;
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true)
         .data(marshaller.marshal()).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).delay(2000)
         .requestStatusHandler(new InitRequestStatusHandler(projectid)).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.git.client.GitClientService#cloneRepository(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void cloneRepository(String vfsId, ProjectModel project, String remoteUri, String remoteName,
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + CLONE;
      //      callback.setEventBus(eventBus);
      CloneRequest cloneRequest = new CloneRequest(remoteUri, project.getId());
      cloneRequest.setRemoteName(remoteName);
      CloneRequestMarshaller marshaller = new CloneRequestMarshaller(cloneRequest);

      String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true)
         .requestStatusHandler(new CloneRequestStatusHandler(project.getName(), remoteUri)).data(marshaller.marshal())
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#status(java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void statusText(String vfsId, String projectid, boolean shortFormat, String[] fileFilter,
      AsyncRequestCallback<StatusResponse> callback)
   {
      String url = restServiceContext + STATUS;

      callback.setEventBus(eventBus);
      StatusRequest statusRequest = new StatusRequest(fileFilter, shortFormat);
      StatusRequestMarshaller marshaller = new StatusRequestMarshaller(statusRequest);

      StatusResponse statusResponse = new StatusResponse();
      StatusResponseUnmarshaller unmarshaller = new StatusResponseUnmarshaller(statusResponse, true);
      callback.setResult(statusResponse);
      callback.setPayload(unmarshaller);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#getWorkDir(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   @Deprecated
   public void getWorkDir(String vfsId, String projectid, AsyncRequestCallback<WorkDirResponse> callback)
   {
      String url = restServiceContext + WORKDIR;
      url += "?vfsid=" + vfsId + "&projectid=" + projectid;
      callback.setEventBus(eventBus);

      WorkDirResponse workDirResponse = new WorkDirResponse();
      WorkDirResponseUnmarshaller unmarshaller = new WorkDirResponseUnmarshaller(workDirResponse);
      callback.setResult(workDirResponse);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.git.client.GitClientService#add(java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void add(String vfsId, ProjectModel project, boolean update, String[] filePattern,
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + ADD;

      AddRequest addRequest = new AddRequest(filePattern, update);
      AddRequestMarshaller marshaller = new AddRequestMarshaller(addRequest);

      String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true)
         .data(marshaller.marshal()).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .requestStatusHandler(new AddRequestHandler(project.getName())).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.git.client.GitClientService#commit(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void commit(String vfsId, ProjectModel project, String message, boolean all,
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<Revision> callback) throws RequestException
   {
      String url = restServiceContext + COMMIT;

      CommitRequest commitRequest = new CommitRequest(message, all);
      CommitRequestMarshaller marshaller = new CommitRequestMarshaller(commitRequest);

      String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true)
         .data(marshaller.marshal()).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .requestStatusHandler(new CommitRequestHandler(project.getName())).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#push(java.lang.String, java.lang.String[], java.lang.String, boolean)
    */
   @Override
   public void push(String vfsId, ProjectModel project, String[] refSpec, String remote, boolean force,
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + PUSH;
      PushRequest pushRequest = new PushRequest();
      pushRequest.setRemote(remote);
      pushRequest.setRefSpec(refSpec);
      pushRequest.setForce(force);

      PushRequestMarshaller marshaller = new PushRequestMarshaller(pushRequest);

      String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

      PushRequestHandler requestHandler = new PushRequestHandler(project.getName(), refSpec);
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true)
         .data(marshaller.marshal()).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .requestStatusHandler(requestHandler).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#remoteList(java.lang.String, java.lang.String, boolean)
    */
   @Override
   public void remoteList(String vfsId, String projectid, String remoteName, boolean verbose,
      AsyncRequestCallback<List<Remote>> callback)
   {
      String url = restServiceContext + REMOTE_LIST;
      callback.setEventBus(eventBus);

      List<Remote> remotes = new ArrayList<Remote>();
      RemoteListRequest remoteListRequest = new RemoteListRequest(remoteName, verbose);
      RemoteListUnmarshaller unmarshaller = new RemoteListUnmarshaller(remotes);
      RemoteListRequestMarshaller marshaller = new RemoteListRequestMarshaller(remoteListRequest);

      callback.setPayload(unmarshaller);
      callback.setResult(remotes);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#branchList(java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void branchList(String vfsId, String projectid, boolean remote, AsyncRequestCallback<List<Branch>> callback)
   {
      String url = restServiceContext + BRANCH_LIST;
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

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#status(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void status(String vfsId, String projectid, AsyncRequestCallback<StatusResponse> callback)
   {
      String url = restServiceContext + STATUS;

      callback.setEventBus(eventBus);
      StatusRequest statusRequest = new StatusRequest(null, true);
      StatusRequestMarshaller marshaller = new StatusRequestMarshaller(statusRequest);

      StatusResponse statusResponse = new StatusResponse();
      StatusResponseUnmarshaller unmarshaller = new StatusResponseUnmarshaller(statusResponse, false);
      callback.setResult(statusResponse);
      callback.setPayload(unmarshaller);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#branchDelete(java.lang.String, java.lang.String, boolean)
    */
   @Override
   public void branchDelete(String vfsId, String projectid, String name, boolean force,
      AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + BRANCH_DELETE;

      callback.setEventBus(eventBus);
      BranchDeleteRequest branchDeleteRequest = new BranchDeleteRequest(name, force);
      BranchDeleteRequestMarshaller marshaller = new BranchDeleteRequestMarshaller(branchDeleteRequest);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#branchCreate(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void branchCreate(String vfsId, String projectid, String name, String startPoint,
      AsyncRequestCallback<Branch> callback)
   {
      String url = restServiceContext + BRANCH_CREATE;

      callback.setEventBus(eventBus);
      BranchCreateRequest branchCreateRequest = new BranchCreateRequest(name, startPoint);
      BranchCreateRequestMarshaller marshaller = new BranchCreateRequestMarshaller(branchCreateRequest);

      Branch branch = new Branch();
      BranchUnmarshaller unmarshaller = new BranchUnmarshaller(branch);
      callback.setResult(branch);
      callback.setPayload(unmarshaller);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#branchCheckout(java.lang.String, java.lang.String, java.lang.String, boolean)
    */
   @Override
   public void branchCheckout(String vfsId, String projectid, String name, String startPoint, boolean createNew,
      AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + BRANCH_CHECKOUT;

      callback.setEventBus(eventBus);
      BranchCheckoutRequest branchCheckoutRequest = new BranchCheckoutRequest(name, startPoint, createNew);
      BranchCheckoutRequestMarshaller marshaller = new BranchCheckoutRequestMarshaller(branchCheckoutRequest);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#remove(java.lang.String, java.lang.String[], org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void remove(String vfsId, String projectid, String[] files, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + REMOVE;

      callback.setEventBus(eventBus);
      RmRequest rmRequest = new RmRequest(files);
      RemoveRequestMarshaller marshaller = new RemoveRequestMarshaller(rmRequest);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#reset(java.lang.String, java.lang.String[], java.lang.String, org.exoplatform.ide.git.shared.ResetRequest.ResetType, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void reset(String vfsId, String projectid, String[] paths, String commit, ResetType resetType,
      AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + RESET;

      callback.setEventBus(eventBus);
      ResetRequest resetRequest = new ResetRequest();
      resetRequest.setPaths(paths);
      resetRequest.setCommit(commit);
      resetRequest.setType(resetType);

      ResetRequestMarshaller marshaller = new ResetRequestMarshaller(resetRequest);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#log(java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void log(String vfsId, String projectid, boolean isTextFormat, AsyncRequestCallback<LogResponse> callback)
   {
      String url = restServiceContext + LOG;

      callback.setEventBus(eventBus);
      LogRequest logRequest = new LogRequest();
      LogRequestMarshaller marshaller = new LogRequestMarshaller(logRequest);

      LogResponse logResponse = new LogResponse();
      LogResponseUnmarshaller unmarshaller = new LogResponseUnmarshaller(logResponse, isTextFormat);
      callback.setResult(logResponse);
      callback.setPayload(unmarshaller);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

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
   public void remoteAdd(String vfsId, String projectid, String name, String repositoryURL,
      AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + REMOTE_ADD;

      callback.setEventBus(eventBus);
      RemoteAddRequest remoteAddRequest = new RemoteAddRequest(name, repositoryURL);

      RemoteAddRequestMarshaller marshaller = new RemoteAddRequestMarshaller(remoteAddRequest);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#remoteDelete(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void remoteDelete(String vfsId, String projectid, String name, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + REMOTE_DELETE + "/" + name;

      callback.setEventBus(eventBus);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#fetch(java.lang.String, java.lang.String, java.lang.String[], boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void fetch(String vfsId, ProjectModel project, String remote, String[] refspec, boolean removeDeletedRefs,
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + FETCH;

      FetchRequest fetchRequest = new FetchRequest(refspec, remote, removeDeletedRefs, 0);
      FetchRequestMarshaller marshaller = new FetchRequestMarshaller(fetchRequest);

      String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true)
         .data(marshaller.marshal()).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .requestStatusHandler(new FetchRequestHandler(project.getName(), refspec)).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#pull(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void pull(String vfsId, ProjectModel project, String refSpec, String remote,
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + PULL;

      PullRequest pullRequest = new PullRequest(remote, refSpec, 0);
      PullRequestMarshaller marshaller = new PullRequestMarshaller(pullRequest);

      String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true)
         .data(marshaller.marshal()).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .requestStatusHandler(new PullRequestHandler(project.getName(), refSpec)).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#diff(java.lang.String, java.lang.String[], org.exoplatform.ide.git.shared.DiffRequest.DiffType, boolean, int, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void diff(String vfsId, String projectid, String[] fileFilter, DiffType type, boolean noRenames,
      int renameLimit, String commitA, String commitB, AsyncRequestCallback<DiffResponse> callback)
   {
      DiffRequest diffRequest = new DiffRequest(fileFilter, type, noRenames, renameLimit, commitA, commitB);
      diff(diffRequest, vfsId, projectid, callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#diff(java.lang.String[], org.exoplatform.ide.git.shared.DiffRequest.DiffType, boolean, int, java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void diff(String vfsId, String projectid, String[] fileFilter, DiffType type, boolean noRenames,
      int renameLimit, String commitA, boolean cached, AsyncRequestCallback<DiffResponse> callback)
   {
      DiffRequest diffRequest = new DiffRequest(fileFilter, type, noRenames, renameLimit, commitA, cached);
      diff(diffRequest, vfsId, projectid, callback);
   }

   /**
    * Make diff request.
    * 
    * @param diffRequest request for diff
    * @param href working directory's href
    * @param callback callback
    */
   protected void diff(DiffRequest diffRequest, String vfsId, String projectid,
      AsyncRequestCallback<DiffResponse> callback)
   {
      String url = restServiceContext + DIFF;
      callback.setEventBus(eventBus);

      DiffRequestMarshaller marshaller = new DiffRequestMarshaller(diffRequest);

      DiffResponse diffResponse = new DiffResponse();
      DiffResponseUnmarshaller unmarshaller = new DiffResponseUnmarshaller(diffResponse);
      callback.setResult(diffResponse);
      callback.setPayload(unmarshaller);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#deleteWorkDir(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void deleteWorkDir(String vfsId, String projectid, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + WORKDIR;
      url += "?vfsid=" + vfsId + "&projectid=" + projectid;
      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.GitClientService#merge(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void merge(String vfsId, String projectid, String commit, AsyncRequestCallback<MergeResult> callback)
   {
      String url = restServiceContext + MERGE;

      callback.setEventBus(eventBus);
      MergeRequest mergeRequest = new MergeRequest(commit);
      MergeRequestMarshaller marshaller = new MergeRequestMarshaller(mergeRequest);

      Merge merge = new Merge();
      MergeUnmarshaller unmarshaller = new MergeUnmarshaller(merge);
      callback.setResult(merge);
      callback.setPayload(unmarshaller);

      String params = "vfsid=" + vfsId + "&projectid=" + projectid;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   @Override
   public void getGitReadOnlyUrl(String vfsId, String projectid,
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<StringBuilder> callback)
      throws RequestException
   {
      String url = restServiceContext + RO_URL;
      url += "?vfsid=" + vfsId + "&projectid=" + projectid;
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

}
