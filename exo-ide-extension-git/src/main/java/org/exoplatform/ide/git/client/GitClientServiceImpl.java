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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessage;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessageBuilder;
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
import org.exoplatform.ide.git.client.marshaller.CloneRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.CommitRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.DiffRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.FetchRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.InitRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.LogRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.client.marshaller.MergeRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.PullRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.PushRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoteAddRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoteListRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoveRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.ResetRequestMarshaller;
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
import org.exoplatform.ide.git.shared.Commiters;
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
import org.exoplatform.ide.git.shared.RepoInfo;
import org.exoplatform.ide.git.shared.ResetRequest;
import org.exoplatform.ide.git.shared.ResetRequest.ResetType;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.git.shared.RmRequest;
import org.exoplatform.ide.git.shared.Status;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Implementation of the {@link GitClientService}.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 23, 2011 11:52:24 AM anya $
 */
public class GitClientServiceImpl extends GitClientService {
    
    public static final String ADD               =  "/git/add";

    public static final String BRANCH_LIST       =  "/git/branch-list";

    public static final String BRANCH_CHECKOUT   =  "/git/branch-checkout";

    public static final String BRANCH_CREATE     =  "/git/branch-create";

    public static final String BRANCH_DELETE     =  "/git/branch-delete";

    public static final String BRANCH_RENAME     =  "/git/branch-rename";

    public static final String CLONE             =  "/git/clone";

    public static final String COMMIT            =  "/git/commit";

    public static final String DIFF              =  "/git/diff";

    public static final String FETCH             =  "/git/fetch";

    public static final String INIT              =  "/git/init";

    public static final String LOG               =  "/git/log";

    public static final String MERGE             =  "/git/merge";

    public static final String STATUS            =  "/git/status";

    public static final String RO_URL            =  "/git/read-only-url";

    public static final String PUSH              =  "/git/push";

    public static final String PULL              =  "/git/pull";

    public static final String REMOTE_LIST       =  "/git/remote-list";

    public static final String REMOTE_ADD        =  "/git/remote-add";

    public static final String REMOTE_DELETE     =  "/git/remote-delete";

    public static final String REMOVE            =  "/git/rm";

    public static final String RESET             =  "/git/reset";

    public static final String COMMITERS         =  "/git/commiters";

    public static final String DELETE_REPOSITORY =  "/git/delete-repository";

    /** REST service context. */
    private final String             restServiceContext;

    /** Loader to be displayed. */
    private Loader             loader;

    private Loader             emptyLoader       = new EmptyLoader();

    private MessageBus         wsMessageBus;

    private final String wsName;

    /**
     * @param eventBus eventBus
     * @param restContext rest context
     * @param loader loader to show on server request
     * @param restContext 
     */
    public GitClientServiceImpl(String restConetxt, String wsName, Loader loader, MessageBus wsMessageBus) {
        this.wsName = wsName;
        restServiceContext = restConetxt + wsName;
        this.loader = loader;
        this.wsMessageBus = wsMessageBus;
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#init(java.lang.String, java.lang.String, java.lang.String, boolean,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    public void init(String vfsId, String projectid, String projectName, boolean bare,
                     AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + INIT;

        InitRequest initRequest = new InitRequest(projectid, bare);
        InitRequestMarshaller marshaller = new InitRequestMarshaller(initRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).delay(2000)
                    .requestStatusHandler(new InitRequestStatusHandler(projectName)).send(callback);
    }

    /**
     * @throws WebSocketException
     * @see org.exoplatform.ide.git.client.GitClientService#initWS(java.lang.String, java.lang.String, java.lang.String, boolean,
     *      org.exoplatform.ide.client.framework.websocket.rest.RequestCallback)
     */
    @Override
    public void initWS(String vfsId, String projectid, String projectName, boolean bare, RequestCallback<String> callback)
                                                                                                                          throws WebSocketException {
        InitRequest initRequest = new InitRequest(projectid, bare);
        InitRequestMarshaller marshaller = new InitRequestMarshaller(initRequest);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        callback.setStatusHandler(new InitRequestStatusHandler(projectName));

        RequestMessage message =
                                 RequestMessageBuilder.build(RequestBuilder.POST, wsName + INIT + params).data(marshaller.marshal())
                                                      .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#cloneRepository(java.lang.String,
     *      org.exoplatform.ide.vfs.client.model.FolderModel, java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void cloneRepository(String vfsId, FolderModel folder, String remoteUri, String remoteName,
                                AsyncRequestCallback<RepoInfo> callback) throws RequestException {
        String url = restServiceContext + CLONE;

        CloneRequest cloneRequest = new CloneRequest(remoteUri, folder.getId());
        cloneRequest.setRemoteName(remoteName);
        CloneRequestMarshaller marshaller = new CloneRequestMarshaller(cloneRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + folder.getId();

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true)
                    .requestStatusHandler(new CloneRequestStatusHandler(folder.getName(), remoteUri)).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws WebSocketException
     * @see org.exoplatform.ide.git.client.GitClientService#cloneRepositoryWS(java.lang.String,
     *      org.exoplatform.ide.vfs.client.model.FolderModel, java.lang.String, java.lang.String,
     *      org.exoplatform.ide.client.framework.websocket.rest.RequestCallback)
     */
    @Override
    public void cloneRepositoryWS(String vfsId, FolderModel folder, String remoteUri, String remoteName,
                                  RequestCallback<RepoInfo> callback) throws WebSocketException {
        CloneRequest cloneRequest = new CloneRequest(remoteUri, folder.getId());
        cloneRequest.setRemoteName(remoteName);
        CloneRequestMarshaller marshaller = new CloneRequestMarshaller(cloneRequest);

        String params = "?vfsid=" + vfsId + "&projectid=" + folder.getId();
        callback.setStatusHandler(new CloneRequestStatusHandler(folder.getName(), remoteUri));

        RequestMessage message =
                                 RequestMessageBuilder.build(RequestBuilder.POST, wsName + CLONE + params).data(marshaller.marshal())
                                                      .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                                                      .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#status(java.lang.String, boolean,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void statusText(String vfsId, String projectid, boolean shortFormat, AsyncRequestCallback<String> callback)
                                                                                                                      throws RequestException {
        String url = restServiceContext + STATUS;

        String params = "vfsid=" + vfsId + "&projectid=" + projectid + "&short=" + shortFormat;
        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN)
                    .send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#add(java.lang.String, boolean,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void add(String vfsId, ProjectModel project, boolean update, String[] filePattern,
                    AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + ADD;

        AddRequest addRequest = new AddRequest(filePattern, update);
        AddRequestMarshaller marshaller = new AddRequestMarshaller(addRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .requestStatusHandler(new AddRequestHandler(project.getName())).send(callback);
    }

    /**
     * @throws WebSocketException
     * @see org.exoplatform.ide.git.client.GitClientService#addWS(java.lang.String, boolean,
     *      org.exoplatform.ide.client.framework.websocket.rest.RequestCallback)
     */
    @Override
    public void addWS(String vfsId, ProjectModel project, boolean update, String[] filePattern,
                      RequestCallback<String> callback) throws WebSocketException {
        AddRequest addRequest = new AddRequest(filePattern, update);
        AddRequestMarshaller marshaller = new AddRequestMarshaller(addRequest);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new AddRequestHandler(project.getName()));

        RequestMessage message =
                                 RequestMessageBuilder.build(RequestBuilder.POST, wsName + ADD + params).data(marshaller.marshal())
                                                      .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#commit(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void commit(String vfsId, ProjectModel project, String message, boolean all, boolean amend,
                       AsyncRequestCallback<Revision> callback) throws RequestException {
        String url = restServiceContext + COMMIT;

        CommitRequest commitRequest = new CommitRequest(message, all, amend);
        CommitRequestMarshaller marshaller = new CommitRequestMarshaller(commitRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .requestStatusHandler(new CommitRequestHandler(project.getName(), message)).send(callback);
    }

    /**
     * @throws WebSocketException
     * @see org.exoplatform.ide.git.client.GitClientService#commitWS(java.lang.String, java.lang.String,
     *      org.exoplatform.ide.client.framework.websocket.rest.RequestCallback)
     */
    @Override
    public void commitWS(String vfsId, ProjectModel project, String message, boolean all, boolean amend,
                         RequestCallback<Revision> callback) throws WebSocketException {
        CommitRequest commitRequest = new CommitRequest(message, all, amend);
        CommitRequestMarshaller marshaller = new CommitRequestMarshaller(commitRequest);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new CommitRequestHandler(project.getName(), message));

        RequestMessage requestMessage =
                                        RequestMessageBuilder.build(RequestBuilder.POST, wsName + COMMIT + params).data(marshaller.marshal())
                                                             .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).getRequestMessage();
        wsMessageBus.send(requestMessage, callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#push(java.lang.String, org.exoplatform.ide.vfs.client.model.ProjectModel,
     *      java.lang.String[], java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void push(String vfsId, ProjectModel project, String[] refSpec, String remote, boolean force,
                     AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + PUSH;
        PushRequest pushRequest = new PushRequest();
        pushRequest.setRemote(remote);
        pushRequest.setRefSpec(refSpec);
        pushRequest.setForce(force);

        PushRequestMarshaller marshaller = new PushRequestMarshaller(pushRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

        PushRequestHandler requestHandler = new PushRequestHandler(project.getName(), refSpec);
        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).requestStatusHandler(requestHandler).send(callback);
    }

    /**
     * @throws WebSocketException
     * @see org.exoplatform.ide.git.client.GitClientService#push(java.lang.String, org.exoplatform.ide.vfs.client.model.ProjectModel,
     *      java.lang.String[], java.lang.String, boolean, org.exoplatform.ide.client.framework.websocket.rest.RequestCallback)
     */
    @Override
    public void pushWS(String vfsId, ProjectModel project, String[] refSpec, String remote, boolean force,
                       RequestCallback<String> callback) throws WebSocketException {
        PushRequest pushRequest = new PushRequest();
        pushRequest.setRemote(remote);
        pushRequest.setRefSpec(refSpec);
        pushRequest.setForce(force);

        PushRequestMarshaller marshaller = new PushRequestMarshaller(pushRequest);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new PushRequestHandler(project.getName(), refSpec));

        RequestMessage message =
                                 RequestMessageBuilder.build(RequestBuilder.POST, wsName + PUSH + params).data(marshaller.marshal())
                                                      .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#remoteList(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void remoteList(String vfsId, String projectid, String remoteName, boolean verbose,
                           AsyncRequestCallback<List<Remote>> callback) throws RequestException {
        String url = restServiceContext + REMOTE_LIST;

        RemoteListRequest remoteListRequest = new RemoteListRequest(remoteName, verbose);
        RemoteListRequestMarshaller marshaller = new RemoteListRequestMarshaller(remoteListRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#branchList(java.lang.String, boolean,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void branchList(String vfsId, String projectid, String remoteMode, AsyncRequestCallback<List<Branch>> callback)
                                                                                                                          throws RequestException {
        String url = restServiceContext + BRANCH_LIST;

        BranchListRequest branchListRequest = new BranchListRequest();
        branchListRequest.setListMode(remoteMode);

        BranchListRequestMarshaller marshaller = new BranchListRequestMarshaller(branchListRequest);
        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#status(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void status(String vfsId, String projectid, AsyncRequestCallback<Status> callback) throws RequestException {
        String url = restServiceContext + STATUS;
        String params = "vfsid=" + vfsId + "&projectid=" + projectid + "&short=false";

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(emptyLoader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#branchDelete(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void branchDelete(String vfsId, String projectid, String name, boolean force,
                             AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + BRANCH_DELETE;

        BranchDeleteRequest branchDeleteRequest = new BranchDeleteRequest(name, force);
        BranchDeleteRequestMarshaller marshaller = new BranchDeleteRequestMarshaller(branchDeleteRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#branchRename(java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void branchRename(String vfsId, String projectid, String oldName, String newName,
                             AsyncRequestCallback<String> callback) throws RequestException {

        String url = restServiceContext + BRANCH_RENAME;

        String params = "vfsid=" + vfsId + "&projectid=" + projectid + "&oldName=" + oldName + "&newName=" + newName;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_FORM_URLENCODED).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#branchCreate(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void branchCreate(String vfsId, String projectid, String name, String startPoint,
                             AsyncRequestCallback<Branch> callback) throws RequestException {
        String url = restServiceContext + BRANCH_CREATE;

        BranchCreateRequest branchCreateRequest = new BranchCreateRequest(name, startPoint);
        BranchCreateRequestMarshaller marshaller = new BranchCreateRequestMarshaller(branchCreateRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#branchCheckout(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void branchCheckout(String vfsId, String projectid, String name, String startPoint, boolean createNew,
                               AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + BRANCH_CHECKOUT;

        BranchCheckoutRequest branchCheckoutRequest = new BranchCheckoutRequest(name, startPoint, createNew);
        BranchCheckoutRequestMarshaller marshaller = new BranchCheckoutRequestMarshaller(branchCheckoutRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#remove(java.lang.String, java.lang.String[],
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void remove(String vfsId, String projectid, String[] files, Boolean cached, AsyncRequestCallback<String> callback)
                                                                                                                             throws RequestException {
        String url = restServiceContext + REMOVE;

        RmRequest rmRequest = new RmRequest(files);
        rmRequest.setCached(cached);
        RemoveRequestMarshaller marshaller = new RemoveRequestMarshaller(rmRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#reset(java.lang.String, java.lang.String[], java.lang.String,
     *      org.exoplatform.ide.git.shared.ResetRequest.ResetType, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void reset(String vfsId, String projectid, String commit, ResetType resetType,
                      AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + RESET;

        ResetRequest resetRequest = new ResetRequest();
        resetRequest.setCommit(commit);
        resetRequest.setType(resetType);

        ResetRequestMarshaller marshaller = new ResetRequestMarshaller(resetRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#log(java.lang.String, boolean,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void log(String vfsId, String projectid, boolean isTextFormat, AsyncRequestCallback<LogResponse> callback)
                                                                                                                     throws RequestException {
        String url = restServiceContext + LOG;

        LogRequest logRequest = new LogRequest();
        LogRequestMarshaller marshaller = new LogRequestMarshaller(logRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        if (isTextFormat) {
            AsyncRequest.build(RequestBuilder.POST, url + "?" + params).data(marshaller.marshal())
                        .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
        } else {
            AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                        .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                        .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
        }

    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#remoteAdd(java.lang.String, java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void remoteAdd(String vfsId, String projectid, String name, String repositoryURL,
                          AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + REMOTE_ADD;

        RemoteAddRequest remoteAddRequest = new RemoteAddRequest(name, repositoryURL);

        RemoteAddRequestMarshaller marshaller = new RemoteAddRequestMarshaller(remoteAddRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#remoteDelete(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void remoteDelete(String vfsId, String projectid, String name, AsyncRequestCallback<String> callback)
                                                                                                                throws RequestException {
        String url = restServiceContext + REMOTE_DELETE + "/" + name;

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#fetch(java.lang.String, java.lang.String, java.lang.String[], boolean,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void fetch(String vfsId, ProjectModel project, String remote, String[] refspec, boolean removeDeletedRefs,
                      AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + FETCH;

        FetchRequest fetchRequest = new FetchRequest(refspec, remote, removeDeletedRefs, 0);
        FetchRequestMarshaller marshaller = new FetchRequestMarshaller(fetchRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .requestStatusHandler(new FetchRequestHandler(project.getName(), refspec)).send(callback);
    }

    /**
     * @throws WebSocketException
     * @see org.exoplatform.ide.git.client.GitClientService#fetch(java.lang.String, java.lang.String, java.lang.String[], boolean,
     *      org.exoplatform.ide.client.framework.websocket.rest.RequestCallback)
     */
    @Override
    public void fetchWS(String vfsId, ProjectModel project, String remote, String[] refspec, boolean removeDeletedRefs,
                        RequestCallback<String> callback) throws WebSocketException {
        FetchRequest fetchRequest = new FetchRequest(refspec, remote, removeDeletedRefs, 0);
        FetchRequestMarshaller marshaller = new FetchRequestMarshaller(fetchRequest);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new FetchRequestHandler(project.getName(), refspec));

        RequestMessage message =
                                 RequestMessageBuilder.build(RequestBuilder.POST, wsName + FETCH + params).data(marshaller.marshal())
                                                      .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    /**
     * @see org.exoplatform.ide.git.client.GitClientService#pull(java.lang.String, java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void pull(String vfsId, ProjectModel project, String refSpec, String remote,
                     AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + PULL;

        PullRequest pullRequest = new PullRequest(remote, refSpec, 0);
        PullRequestMarshaller marshaller = new PullRequestMarshaller(pullRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .requestStatusHandler(new PullRequestHandler(project.getName(), refSpec)).send(callback);
    }

    /**
     * @throws WebSocketException
     * @see org.exoplatform.ide.git.client.GitClientService#pull(java.lang.String, java.lang.String, java.lang.String,
     *      org.exoplatform.ide.client.framework.websocket.rest.RequestCallback)
     */
    @Override
    public void pullWS(String vfsId, ProjectModel project, String refSpec, String remote,
                       RequestCallback<String> callback) throws WebSocketException {
        PullRequest pullRequest = new PullRequest(remote, refSpec, 0);
        PullRequestMarshaller marshaller = new PullRequestMarshaller(pullRequest);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new PullRequestHandler(project.getName(), refSpec));

        RequestMessage message =
                                 RequestMessageBuilder.build(RequestBuilder.POST, wsName + PULL + params).data(marshaller.marshal())
                                                      .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#diff(java.lang.String, java.lang.String[],
     *      org.exoplatform.ide.git.shared.DiffRequest.DiffType, boolean, int, java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void diff(String vfsId, String projectid, String[] fileFilter, DiffType type, boolean noRenames,
                     int renameLimit, String commitA, String commitB, AsyncRequestCallback<StringBuilder> callback)
                                                                                                                   throws RequestException {
        DiffRequest diffRequest = new DiffRequest(fileFilter, type, noRenames, renameLimit, commitA, commitB);
        diff(diffRequest, vfsId, projectid, callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#diff(java.lang.String[], org.exoplatform.ide.git.shared.DiffRequest.DiffType,
     *      boolean, int, java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void diff(String vfsId, String projectid, String[] fileFilter, DiffType type, boolean noRenames,
                     int renameLimit, String commitA, boolean cached, AsyncRequestCallback<StringBuilder> callback)
                                                                                                                   throws RequestException {
        DiffRequest diffRequest = new DiffRequest(fileFilter, type, noRenames, renameLimit, commitA, cached);
        diff(diffRequest, vfsId, projectid, callback);
    }

    /**
     * Make diff request.
     * 
     * @param diffRequest request for diff
     * @param href working directory's href
     * @param callback callback
     * @throws RequestException
     */
    protected void diff(DiffRequest diffRequest, String vfsId, String projectid,
                        AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = restServiceContext + DIFF;

        DiffRequestMarshaller marshaller = new DiffRequestMarshaller(diffRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#merge(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void merge(String vfsId, String projectid, String commit, AsyncRequestCallback<MergeResult> callback)
                                                                                                                throws RequestException {
        String url = restServiceContext + MERGE;

        MergeRequest mergeRequest = new MergeRequest(commit);
        MergeRequestMarshaller marshaller = new MergeRequestMarshaller(mergeRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#getGitReadOnlyUrl(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getGitReadOnlyUrl(String vfsId, String projectid, AsyncRequestCallback<StringBuilder> callback)
                                                                                                               throws RequestException {
        String url = restServiceContext + RO_URL;
        url += "?vfsid=" + vfsId + "&projectid=" + projectid;
        AsyncRequest.build(RequestBuilder.GET, url).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#getCommiters(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getCommiters(String vfsId, String projectid, AsyncRequestCallback<Commiters> callback)
                                                                                                      throws RequestException {
        String url = restServiceContext + COMMITERS;
        String params = "vfsid=" + vfsId + "&projectid=" + projectid;
        AsyncRequest.build(RequestBuilder.GET, url + "?" + params).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }


    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.GitClientService#status(java.lang.String, boolean,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void deleteRepository(String vfsId, String projectid, AsyncRequestCallback<Void> callback) throws RequestException {
        String url = restServiceContext + DELETE_REPOSITORY;

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;
        AsyncRequest.build(RequestBuilder.GET, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN)
                    .send(callback);
    }

}
