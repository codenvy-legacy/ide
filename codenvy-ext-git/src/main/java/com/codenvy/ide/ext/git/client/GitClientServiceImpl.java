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
package com.codenvy.ide.ext.git.client;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.ext.git.client.add.AddRequestHandler;
import com.codenvy.ide.ext.git.client.clone.CloneRequestStatusHandler;
import com.codenvy.ide.ext.git.client.commit.CommitRequestHandler;
import com.codenvy.ide.ext.git.client.fetch.FetchRequestHandler;
import com.codenvy.ide.ext.git.client.init.InitRequestStatusHandler;
import com.codenvy.ide.ext.git.client.marshaller.*;
import com.codenvy.ide.ext.git.client.pull.PullRequestHandler;
import com.codenvy.ide.ext.git.client.push.PushRequestHandler;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.*;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Implementation of the {@link GitClientService}.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 23, 2011 11:52:24 AM anya $
 */
@Singleton
public class GitClientServiceImpl implements GitClientService {
    public static final String ADD               = "/ide/git/add";
    public static final String BRANCH_LIST       = "/ide/git/branch-list";
    public static final String BRANCH_CHECKOUT   = "/ide/git/branch-checkout";
    public static final String BRANCH_CREATE     = "/ide/git/branch-create";
    public static final String BRANCH_DELETE     = "/ide/git/branch-delete";
    public static final String BRANCH_RENAME     = "/ide/git/branch-rename";
    public static final String CLONE             = "/ide/git/clone";
    public static final String COMMIT            = "/ide/git/commit";
    public static final String DIFF              = "/ide/git/diff";
    public static final String FETCH             = "/ide/git/fetch";
    public static final String INIT              = "/ide/git/init";
    public static final String LOG               = "/ide/git/log";
    public static final String MERGE             = "/ide/git/merge";
    public static final String STATUS            = "/ide/git/status";
    public static final String RO_URL            = "/ide/git/read-only-url";
    public static final String PUSH              = "/ide/git/push";
    public static final String PULL              = "/ide/git/pull";
    public static final String REMOTE_LIST       = "/ide/git/remote-list";
    public static final String REMOTE_ADD        = "/ide/git/remote-add";
    public static final String REMOTE_DELETE     = "/ide/git/remote-delete";
    public static final String REMOVE            = "/ide/git/rm";
    public static final String RESET             = "/ide/git/reset";
    public static final String COMMITERS         = "/ide/git/commiters";
    public static final String DELETE_REPOSITORY = "/ide/git/delete-repository";
    /** REST service context. */
    private String                  restServiceContext;
    /** Loader to be displayed. */
    private Loader                  loader;
    private MessageBus              wsMessageBus;
    private EventBus                eventBus;
    private GitLocalizationConstant constant;

    /**
     * @param restContext
     *         rest context
     * @param loader
     *         loader to show on server request
     */
    @Inject
    protected GitClientServiceImpl(@Named("restContext") String restContext, Loader loader, MessageBus wsMessageBus, EventBus eventBus,
                                   GitLocalizationConstant constant) {
        this.loader = loader;
        this.restServiceContext = restContext;
        this.wsMessageBus = wsMessageBus;
        this.eventBus = eventBus;
        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public void init(@NotNull String vfsId, @NotNull String projectid, @NotNull String projectName, boolean bare,
                     @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + INIT;

        DtoClientImpls.InitRequestImpl initRequest = DtoClientImpls.InitRequestImpl.make();
        initRequest.setBare(bare);
        initRequest.setWorkingDir(projectid);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(initRequest.serialize())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).delay(2000)
                    .requestStatusHandler(new InitRequestStatusHandler(projectName, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void initWS(@NotNull String vfsId, @NotNull String projectid, @NotNull String projectName, boolean bare,
                       @NotNull RequestCallback<String> callback) throws WebSocketException {
        DtoClientImpls.InitRequestImpl initRequest = DtoClientImpls.InitRequestImpl.make();
        initRequest.setBare(bare);
        initRequest.setWorkingDir(projectid);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        callback.setStatusHandler(new InitRequestStatusHandler(projectName, eventBus, constant));

        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, INIT + params);
        builder.data(initRequest.serialize()).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cloneRepository(@NotNull String vfsId, @NotNull Project project, @NotNull String remoteUri, @NotNull String remoteName,
                                @NotNull AsyncRequestCallback<RepoInfo> callback) throws RequestException {
        String url = restServiceContext + CLONE;

        DtoClientImpls.CloneRequestImpl cloneRequest = DtoClientImpls.CloneRequestImpl.make();
        cloneRequest.setRemoteName(remoteName);
        cloneRequest.setRemoteUri(remoteUri);
        cloneRequest.setWorkingDir(project.getId());

        String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true)
                    .requestStatusHandler(new CloneRequestStatusHandler(project.getName(), remoteUri, eventBus, constant))
                    .data(cloneRequest.serialize())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cloneRepositoryWS(@NotNull String vfsId, @NotNull Project project, @NotNull String remoteUri, @NotNull String remoteName,
                                  @NotNull RequestCallback<RepoInfo> callback) throws WebSocketException {
        DtoClientImpls.CloneRequestImpl cloneRequest = DtoClientImpls.CloneRequestImpl.make();
        cloneRequest.setRemoteName(remoteName);
        cloneRequest.setRemoteUri(remoteUri);
        cloneRequest.setWorkingDir(project.getId());

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new CloneRequestStatusHandler(project.getName(), remoteUri, eventBus, constant));

        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, CLONE + params);
        builder.data(cloneRequest.serialize())
               .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
               .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void statusText(@NotNull String vfsId, @NotNull String projectid, boolean shortFormat,
                           @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + STATUS;

        String params = "vfsid=" + vfsId + "&projectid=" + projectid + "&short=" + shortFormat;
        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void add(@NotNull String vfsId, @NotNull Project project, boolean update, @Nullable JsonArray<String> filePattern,
                    @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + ADD;

        DtoClientImpls.AddRequestImpl addRequest = DtoClientImpls.AddRequestImpl.make();
        addRequest.setUpdate(update);
        if (filePattern == null) {
            addRequest.setFilepattern(AddRequest.DEFAULT_PATTERN);
        } else {
            addRequest.setFilepattern(filePattern);
        }

        String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(addRequest.serialize())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .requestStatusHandler(new AddRequestHandler(project.getName(), eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void addWS(@NotNull String vfsId, @NotNull Project project, boolean update, @Nullable JsonArray<String> filePattern,
                      @NotNull RequestCallback<String> callback) throws WebSocketException {
        DtoClientImpls.AddRequestImpl addRequest = DtoClientImpls.AddRequestImpl.make();
        addRequest.setUpdate(update);
        if (filePattern == null) {
            addRequest.setFilepattern(AddRequest.DEFAULT_PATTERN);
        } else {
            addRequest.setFilepattern(filePattern);
        }

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new AddRequestHandler(project.getName(), eventBus, constant));

        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, ADD + params);
        builder.data(addRequest.serialize())
               .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull String vfsId, @NotNull Project project, @NotNull String message, boolean all, boolean amend,
                       @NotNull AsyncRequestCallback<Revision> callback) throws RequestException {
        String url = restServiceContext + COMMIT;

        DtoClientImpls.CommitRequestImpl commitRequest = DtoClientImpls.CommitRequestImpl.make();
        commitRequest.setMessage(message);
        commitRequest.setAmend(amend);
        commitRequest.setAll(all);

        String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(commitRequest.serialize())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .requestStatusHandler(new CommitRequestHandler(project.getName(), message, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void commitWS(@NotNull String vfsId, @NotNull Project project, @NotNull String message, boolean all, boolean amend,
                         @NotNull RequestCallback<Revision> callback) throws WebSocketException {
        DtoClientImpls.CommitRequestImpl commitRequest = DtoClientImpls.CommitRequestImpl.make();
        commitRequest.setMessage(message);
        commitRequest.setAmend(amend);
        commitRequest.setAll(all);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new CommitRequestHandler(project.getName(), message, eventBus, constant));

        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, COMMIT + params);
        builder.data(commitRequest.serialize())
               .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON);
        Message requestMessage = builder.build();

        wsMessageBus.send(requestMessage, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void push(@NotNull String vfsId, @NotNull Project project, JsonArray<String> refSpec, @NotNull String remote, boolean force,
                     @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + PUSH;

        DtoClientImpls.PushRequestImpl pushRequest = DtoClientImpls.PushRequestImpl.make();
        pushRequest.setRemote(remote);
        pushRequest.setRefSpec(refSpec);
        pushRequest.setForce(force);
        // TODO marshaller
        PushRequestMarshaller marshaller = new PushRequestMarshaller(pushRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

        PushRequestHandler requestHandler = new PushRequestHandler(project.getName(), refSpec, eventBus, constant);
        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).requestStatusHandler(requestHandler).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void pushWS(@NotNull String vfsId, @NotNull Project project, JsonArray<String> refSpec, @NotNull String remote, boolean force,
                       @NotNull RequestCallback<String> callback) throws WebSocketException {
        DtoClientImpls.PushRequestImpl pushRequest = DtoClientImpls.PushRequestImpl.make();
        pushRequest.setRemote(remote);
        pushRequest.setRefSpec(refSpec);
        pushRequest.setForce(force);
        // TODO marshaller
        PushRequestMarshaller marshaller = new PushRequestMarshaller(pushRequest);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new PushRequestHandler(project.getName(), refSpec, eventBus, constant));

        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, PUSH + params);
        builder.data(marshaller.marshal())
               .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remoteList(@NotNull String vfsId, @NotNull String projectid, @NotNull String remoteName, boolean verbose,
                           @NotNull AsyncRequestCallback<JsonArray<Remote>> callback) throws RequestException {
        String url = restServiceContext + REMOTE_LIST;

        DtoClientImpls.RemoteListRequestImpl remoteListRequest = DtoClientImpls.RemoteListRequestImpl.make();
        remoteListRequest.setRemote(remoteName);
        remoteListRequest.setVerbose(verbose);
        // TODO marshaller
        RemoteListRequestMarshaller marshaller = new RemoteListRequestMarshaller(remoteListRequest);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchList(@NotNull String vfsId, @NotNull String projectid, @NotNull String remoteMode,
                           @NotNull AsyncRequestCallback<JsonArray<Branch>> callback) throws RequestException {
        String url = restServiceContext + BRANCH_LIST;

        DtoClientImpls.BranchListRequestImpl branchListRequest = DtoClientImpls.BranchListRequestImpl.make();
        branchListRequest.setListMode(remoteMode);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).data(branchListRequest.serialize())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void status(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<Status> callback)
            throws RequestException {
        String url = restServiceContext + STATUS;
        String params = "vfsid=" + vfsId + "&projectid=" + projectid + "&short=false";

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchDelete(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, boolean force,
                             @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + BRANCH_DELETE;

        DtoClientImpls.BranchDeleteRequestImpl branchDeleteRequest = DtoClientImpls.BranchDeleteRequestImpl.make();
        branchDeleteRequest.setName(name);
        branchDeleteRequest.setForce(force);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(branchDeleteRequest.serialize())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchRename(@NotNull String vfsId, @NotNull String projectid, @NotNull String oldName, @NotNull String newName,
                             @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + BRANCH_RENAME;

        String params = "vfsid=" + vfsId + "&projectid=" + projectid + "&oldName=" + oldName + "&newName=" + newName;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_FORM_URLENCODED).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchCreate(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                             @NotNull AsyncRequestCallback<Branch> callback) throws RequestException {
        String url = restServiceContext + BRANCH_CREATE;

        DtoClientImpls.BranchCreateRequestImpl branchCreateRequest = DtoClientImpls.BranchCreateRequestImpl.make();
        branchCreateRequest.setName(name);
        branchCreateRequest.setStartPoint(startPoint);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(branchCreateRequest.serialize())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchCheckout(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                               boolean createNew, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + BRANCH_CHECKOUT;

        DtoClientImpls.BranchCheckoutRequestImpl branchCheckoutRequest = DtoClientImpls.BranchCheckoutRequestImpl.make();
        branchCheckoutRequest.setName(name);
        branchCheckoutRequest.setStartPoint(startPoint);
        branchCheckoutRequest.setCreateNew(createNew);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(branchCheckoutRequest.serialize())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remove(@NotNull String vfsId, @NotNull String projectid, JsonArray<String> files, boolean cached,
                       @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + REMOVE;

        DtoClientImpls.RmRequestImpl rmRequest = DtoClientImpls.RmRequestImpl.make();
        rmRequest.setFiles(files);
        rmRequest.setCached(cached);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(rmRequest.serialize())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void reset(@NotNull String vfsId, @NotNull String projectid, @NotNull String commit, @NotNull ResetRequest.ResetType resetType,
                      @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + RESET;

        DtoClientImpls.ResetRequestImpl resetRequest = DtoClientImpls.ResetRequestImpl.make();
        resetRequest.setCommit(commit);
        resetRequest.setType(resetType);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(resetRequest.serialize())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void log(@NotNull String vfsId, @NotNull String projectid, boolean isTextFormat,
                    @NotNull AsyncRequestCallback<LogResponse> callback) throws RequestException {
        String url = restServiceContext + LOG;

        DtoClientImpls.LogRequestImpl logRequest = DtoClientImpls.LogRequestImpl.make();
        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        if (isTextFormat) {
            AsyncRequest.build(RequestBuilder.POST, url + "?" + params).data(logRequest.serialize())
                        .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
        } else {
            AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(logRequest.serialize())
                        .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                        .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void remoteAdd(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String repositoryURL,
                          @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + REMOTE_ADD;

        DtoClientImpls.RemoteAddRequestImpl remoteAddRequest = DtoClientImpls.RemoteAddRequestImpl.make();
        remoteAddRequest.setName(name);
        remoteAddRequest.setUrl(repositoryURL);
        // TODO marshaller
        RemoteAddRequestMarshaller marshaller = new RemoteAddRequestMarshaller(remoteAddRequest);
        // System.out.println(marshaller.marshal());
        // System.out.println(remoteAddRequest.serialize());

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remoteDelete(@NotNull String vfsId, @NotNull String projectid, @NotNull String name,
                             @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + REMOTE_DELETE + "/" + name;

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void fetch(@NotNull String vfsId, @NotNull Project project, @NotNull String remote, JsonArray<String> refspec,
                      boolean removeDeletedRefs, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + FETCH;

        DtoClientImpls.FetchRequestImpl fetchRequest = DtoClientImpls.FetchRequestImpl.make();
        fetchRequest.setRemote(remote);
        fetchRequest.setRefSpec(refspec);
        fetchRequest.setRemoveDeletedRefs(removeDeletedRefs);
        fetchRequest.setTimeout(0);
        // TODO marshaller
        FetchRequestMarshaller marshaller = new FetchRequestMarshaller(fetchRequest);
        // System.out.println(marshaller.marshal());
        // System.out.println(fetchRequest.serialize());

        String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .requestStatusHandler(new FetchRequestHandler(project.getName(), refspec, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void fetchWS(@NotNull String vfsId, @NotNull Project project, @NotNull String remote, JsonArray<String> refspec,
                        boolean removeDeletedRefs, @NotNull RequestCallback<String> callback) throws WebSocketException {
        DtoClientImpls.FetchRequestImpl fetchRequest = DtoClientImpls.FetchRequestImpl.make();
        fetchRequest.setRemote(remote);
        fetchRequest.setRefSpec(refspec);
        fetchRequest.setRemoveDeletedRefs(removeDeletedRefs);
        fetchRequest.setTimeout(0);
        // TODO marshaller
        FetchRequestMarshaller marshaller = new FetchRequestMarshaller(fetchRequest);
        // System.out.println(marshaller.marshal());
        // System.out.println(fetchRequest.serialize());

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new FetchRequestHandler(project.getName(), refspec, eventBus, constant));

        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, FETCH + params);
        builder.data(marshaller.marshal())
               .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void pull(@NotNull String vfsId, @NotNull Project project, @NotNull String refSpec, @NotNull String remote,
                     @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + PULL;

        DtoClientImpls.PullRequestImpl pullRequest = DtoClientImpls.PullRequestImpl.make();
        pullRequest.setRemote(remote);
        pullRequest.setRefSpec(refSpec);
        pullRequest.setTimeout(0);
        // TODO marshaller
        PullRequestMarshaller marshaller = new PullRequestMarshaller(pullRequest);
        // System.out.println(marshaller.marshal());
        // System.out.println(pullRequest.serialize());

        String params = "vfsid=" + vfsId + "&projectid=" + project.getId();

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .requestStatusHandler(new PullRequestHandler(project.getName(), refSpec, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void pullWS(@NotNull String vfsId, @NotNull Project project, @NotNull String refSpec, @NotNull String remote,
                       @NotNull RequestCallback<String> callback) throws WebSocketException {
        DtoClientImpls.PullRequestImpl pullRequest = DtoClientImpls.PullRequestImpl.make();
        pullRequest.setRemote(remote);
        pullRequest.setRefSpec(refSpec);
        pullRequest.setTimeout(0);
        // TODO marshaller
        PullRequestMarshaller marshaller = new PullRequestMarshaller(pullRequest);
        // System.out.println(marshaller.marshal());
        // System.out.println(pullRequest.serialize());

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new PullRequestHandler(project.getName(), refSpec, eventBus, constant));

        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, PULL + params);
        builder.data(marshaller.marshal())
               .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void diff(@NotNull String vfsId, @NotNull String projectid, JsonArray<String> fileFilter, @NotNull DiffRequest.DiffType type,
                     boolean noRenames, int renameLimit, @NotNull String commitA, @NotNull String commitB,
                     @NotNull AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        DtoClientImpls.DiffRequestImpl diffRequest = DtoClientImpls.DiffRequestImpl.make();
        diffRequest.setFileFilter(fileFilter);
        diffRequest.setType(type);
        diffRequest.setNoRenames(noRenames);
        diffRequest.setRenameLimit(renameLimit);
        diffRequest.setCommitA(commitA);
        diffRequest.setCommitB(commitB);

        diff(diffRequest, vfsId, projectid, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void diff(@NotNull String vfsId, @NotNull String projectid, JsonArray<String> fileFilter, @NotNull DiffRequest.DiffType type,
                     boolean noRenames, int renameLimit, @NotNull String commitA, boolean cached,
                     @NotNull AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        DtoClientImpls.DiffRequestImpl diffRequest = DtoClientImpls.DiffRequestImpl.make();
        diffRequest.setFileFilter(fileFilter);
        diffRequest.setType(type);
        diffRequest.setNoRenames(noRenames);
        diffRequest.setRenameLimit(renameLimit);
        diffRequest.setCommitA(commitA);
        diffRequest.setCached(cached);

        diff(diffRequest, vfsId, projectid, callback);
    }

    /**
     * Make diff request.
     *
     * @param diffRequest
     *         request for diff
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project id
     * @param callback
     *         callback
     * @throws RequestException
     */
    private void diff(DtoClientImpls.DiffRequestImpl diffRequest, String vfsId, String projectid,
                      AsyncRequestCallback<StringBuilder> callback)
            throws RequestException {
        String url = restServiceContext + DIFF;

        DiffRequestMarshaller marshaller = new DiffRequestMarshaller(diffRequest);
        // TODO need to check
        // System.out.println(marshaller.marshal());
        // System.out.println(diffRequest.serialize());

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(marshaller.marshal())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void merge(@NotNull String vfsId, @NotNull String projectid, @NotNull String commit,
                      @NotNull AsyncRequestCallback<MergeResult> callback) throws RequestException {
        String url = restServiceContext + MERGE;

        DtoClientImpls.MergeRequestImpl mergeRequest = DtoClientImpls.MergeRequestImpl.make();
        mergeRequest.setCommit(commit);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).data(mergeRequest.serialize())
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getGitReadOnlyUrl(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<StringBuilder> callback)
            throws RequestException {
        String url = restServiceContext + RO_URL;
        url += "?vfsid=" + vfsId + "&projectid=" + projectid;
        AsyncRequest.build(RequestBuilder.GET, url).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getCommiters(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<Commiters> callback)
            throws RequestException {
        String url = restServiceContext + COMMITERS;
        String params = "vfsid=" + vfsId + "&projectid=" + projectid;
        AsyncRequest.build(RequestBuilder.GET, url + "?" + params).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }


    /** {@inheritDoc} */
    @Override
    public void deleteRepository(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        String url = restServiceContext + DELETE_REPOSITORY;

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;
        AsyncRequest.build(RequestBuilder.GET, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN)
                    .send(callback);
    }
}