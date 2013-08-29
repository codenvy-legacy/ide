/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.git.client;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.ext.git.client.add.AddRequestHandler;
import com.codenvy.ide.ext.git.client.clone.CloneRequestStatusHandler;
import com.codenvy.ide.ext.git.client.commit.CommitRequestHandler;
import com.codenvy.ide.ext.git.client.fetch.FetchRequestHandler;
import com.codenvy.ide.ext.git.client.init.InitRequestStatusHandler;
import com.codenvy.ide.ext.git.client.pull.PullRequestHandler;
import com.codenvy.ide.ext.git.client.push.PushRequestHandler;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.*;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
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

import static com.codenvy.ide.rest.HTTPHeader.ACCEPT;
import static com.codenvy.ide.rest.HTTPHeader.CONTENTTYPE;
import static com.codenvy.ide.rest.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.rest.MimeType.TEXT_PLAIN;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * Implementation of the {@link GitClientService}.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 23, 2011 11:52:24 AM anya $
 */
@Singleton
public class GitClientServiceImpl implements GitClientService {
    private static final String BASE_URL          = "/git";
    public static final  String ADD               = BASE_URL + "/add";
    public static final  String BRANCH_LIST       = BASE_URL + "/branch-list";
    public static final  String BRANCH_CHECKOUT   = BASE_URL + "/branch-checkout";
    public static final  String BRANCH_CREATE     = BASE_URL + "/branch-create";
    public static final  String BRANCH_DELETE     = BASE_URL + "/branch-delete";
    public static final  String BRANCH_RENAME     = BASE_URL + "/branch-rename";
    public static final  String CLONE             = BASE_URL + "/clone";
    public static final  String COMMIT            = BASE_URL + "/commit";
    public static final  String DIFF              = BASE_URL + "/diff";
    public static final  String FETCH             = BASE_URL + "/fetch";
    public static final  String INIT              = BASE_URL + "/init";
    public static final  String LOG               = BASE_URL + "/log";
    public static final  String MERGE             = BASE_URL + "/merge";
    public static final  String STATUS            = BASE_URL + "/status";
    public static final  String RO_URL            = BASE_URL + "/read-only-url";
    public static final  String PUSH              = BASE_URL + "/push";
    public static final  String PULL              = BASE_URL + "/pull";
    public static final  String REMOTE_LIST       = BASE_URL + "/remote-list";
    public static final  String REMOTE_ADD        = BASE_URL + "/remote-add";
    public static final  String REMOTE_DELETE     = BASE_URL + "/remote-delete";
    public static final  String REMOVE            = BASE_URL + "/rm";
    public static final  String RESET             = BASE_URL + "/reset";
    public static final  String COMMITERS         = BASE_URL + "/commiters";
    public static final  String DELETE_REPOSITORY = BASE_URL + "/delete-repository";
    private String                  wsName;
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
        this.wsName = '/' + Utils.getWorkspaceName();
        this.restServiceContext = restContext + wsName;
        this.wsMessageBus = wsMessageBus;
        this.eventBus = eventBus;
        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public void init(@NotNull String vfsId, @NotNull String projectid, @NotNull String projectName, boolean bare,
                     @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        DtoClientImpls.InitRequestImpl initRequest = DtoClientImpls.InitRequestImpl.make();
        initRequest.setBare(bare);
        initRequest.setWorkingDir(projectid);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + INIT + "?" + params;

        AsyncRequest.build(POST, url, true).data(initRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON).delay(2000)
                    .requestStatusHandler(new InitRequestStatusHandler(projectName, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void initWS(@NotNull String vfsId, @NotNull String projectid, @NotNull String projectName, boolean bare,
                       @NotNull RequestCallback<String> callback) throws WebSocketException {
        DtoClientImpls.InitRequestImpl initRequest = DtoClientImpls.InitRequestImpl.make();
        initRequest.setBare(bare);
        initRequest.setWorkingDir(projectid);

        callback.setStatusHandler(new InitRequestStatusHandler(projectName, eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = wsName + INIT + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(initRequest.serialize()).header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cloneRepository(@NotNull String vfsId, @NotNull Project project, @NotNull String remoteUri, @NotNull String remoteName,
                                @NotNull AsyncRequestCallback<RepoInfo> callback) throws RequestException {
        DtoClientImpls.CloneRequestImpl cloneRequest = DtoClientImpls.CloneRequestImpl.make();
        cloneRequest.setRemoteName(remoteName);
        cloneRequest.setRemoteUri(remoteUri);
        cloneRequest.setWorkingDir(project.getId());

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = restServiceContext + CLONE + params;

        AsyncRequest.build(POST, url, true)
                    .requestStatusHandler(new CloneRequestStatusHandler(project.getName(), remoteUri, eventBus, constant))
                    .data(cloneRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .header(ACCEPT, APPLICATION_JSON).send(callback);
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

        String url = wsName + CLONE + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(cloneRequest.serialize())
               .header(CONTENTTYPE, APPLICATION_JSON)
               .header(ACCEPT, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void statusText(@NotNull String vfsId, @NotNull String projectid, boolean shortFormat,
                           @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + STATUS;
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid + "&short=" + shortFormat;

        AsyncRequest.build(POST, url + params).loader(loader)
                    .header(CONTENTTYPE, APPLICATION_JSON).header(ACCEPT, TEXT_PLAIN)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void add(@NotNull String vfsId, @NotNull Project project, boolean update, @Nullable JsonArray<String> filePattern,
                    @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        DtoClientImpls.AddRequestImpl addRequest = DtoClientImpls.AddRequestImpl.make();
        addRequest.setUpdate(update);
        if (filePattern == null) {
            addRequest.setFilepattern(AddRequest.DEFAULT_PATTERN);
        } else {
            addRequest.setFilepattern(filePattern);
        }

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = restServiceContext + ADD + params;

        AsyncRequest.build(POST, url, true).data(addRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON)
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

        callback.setStatusHandler(new AddRequestHandler(project.getName(), eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = wsName + ADD + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(addRequest.serialize())
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull String vfsId, @NotNull Project project, @NotNull String message, boolean all, boolean amend,
                       @NotNull AsyncRequestCallback<Revision> callback) throws RequestException {
        DtoClientImpls.CommitRequestImpl commitRequest = DtoClientImpls.CommitRequestImpl.make();
        commitRequest.setMessage(message);
        commitRequest.setAmend(amend);
        commitRequest.setAll(all);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = restServiceContext + COMMIT + params;

        AsyncRequest.build(POST, url, true).data(commitRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON)
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

        callback.setStatusHandler(new CommitRequestHandler(project.getName(), message, eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = wsName + COMMIT + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(commitRequest.serialize())
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message requestMessage = builder.build();

        wsMessageBus.send(requestMessage, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void push(@NotNull String vfsId, @NotNull Project project, @NotNull JsonArray<String> refSpec, @NotNull String remote,
                     boolean force, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        DtoClientImpls.PushRequestImpl pushRequest = DtoClientImpls.PushRequestImpl.make();
        pushRequest.setRemote(remote);
        pushRequest.setRefSpec(refSpec);
        pushRequest.setForce(force);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = restServiceContext + PUSH + params;

        PushRequestHandler requestHandler = new PushRequestHandler(project.getName(), refSpec, eventBus, constant);
        AsyncRequest.build(POST, url, true).data(pushRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON).requestStatusHandler(requestHandler).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void pushWS(@NotNull String vfsId, @NotNull Project project, @NotNull JsonArray<String> refSpec, @NotNull String remote,
                       boolean force, @NotNull RequestCallback<String> callback) throws WebSocketException {
        DtoClientImpls.PushRequestImpl pushRequest = DtoClientImpls.PushRequestImpl.make();
        pushRequest.setRemote(remote);
        pushRequest.setRefSpec(refSpec);
        pushRequest.setForce(force);

        callback.setStatusHandler(new PushRequestHandler(project.getName(), refSpec, eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = wsName + PUSH + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(pushRequest.serialize())
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remoteList(@NotNull String vfsId, @NotNull String projectid, @Nullable String remoteName, boolean verbose,
                           @NotNull AsyncRequestCallback<JsonArray<Remote>> callback) throws RequestException {
        DtoClientImpls.RemoteListRequestImpl remoteListRequest = DtoClientImpls.RemoteListRequestImpl.make();
        if (remoteName != null) {
            remoteListRequest.setRemote(remoteName);
        }
        remoteListRequest.setVerbose(verbose);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + REMOTE_LIST + params;

        AsyncRequest.build(POST, url).loader(loader).data(remoteListRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchList(@NotNull String vfsId, @NotNull String projectid, @Nullable String remoteMode,
                           @NotNull AsyncRequestCallback<JsonArray<Branch>> callback) throws RequestException {
        DtoClientImpls.BranchListRequestImpl branchListRequest = DtoClientImpls.BranchListRequestImpl.make();
        branchListRequest.setListMode(remoteMode);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + BRANCH_LIST + params;

        AsyncRequest.build(POST, url).data(branchListRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void status(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<Status> callback)
            throws RequestException {
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid + "&short=false";
        String url = restServiceContext + STATUS + params;

        AsyncRequest.build(POST, url).loader(loader)
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .header(ACCEPT, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchDelete(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, boolean force,
                             @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        DtoClientImpls.BranchDeleteRequestImpl branchDeleteRequest = DtoClientImpls.BranchDeleteRequestImpl.make();
        branchDeleteRequest.setName(name);
        branchDeleteRequest.setForce(force);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + BRANCH_DELETE + params;

        AsyncRequest.build(POST, url).loader(loader).data(branchDeleteRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchRename(@NotNull String vfsId, @NotNull String projectid, @NotNull String oldName, @NotNull String newName,
                             @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid + "&oldName=" + oldName + "&newName=" + newName;
        String url = restServiceContext + BRANCH_RENAME + params;

        AsyncRequest.build(POST, url).loader(loader)
                    .header(CONTENTTYPE, MimeType.APPLICATION_FORM_URLENCODED).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchCreate(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                             @NotNull AsyncRequestCallback<Branch> callback) throws RequestException {

        DtoClientImpls.BranchCreateRequestImpl branchCreateRequest = DtoClientImpls.BranchCreateRequestImpl.make();
        branchCreateRequest.setName(name);
        branchCreateRequest.setStartPoint(startPoint);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + BRANCH_CREATE + params;

        AsyncRequest.build(POST, url).loader(loader).data(branchCreateRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .header(ACCEPT, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchCheckout(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                               boolean createNew, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        DtoClientImpls.BranchCheckoutRequestImpl branchCheckoutRequest = DtoClientImpls.BranchCheckoutRequestImpl.make();
        branchCheckoutRequest.setName(name);
        branchCheckoutRequest.setStartPoint(startPoint);
        branchCheckoutRequest.setCreateNew(createNew);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + BRANCH_CHECKOUT + params;

        AsyncRequest.build(POST, url).loader(loader).data(branchCheckoutRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remove(@NotNull String vfsId, @NotNull String projectid, JsonArray<String> files, boolean cached,
                       @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        DtoClientImpls.RmRequestImpl rmRequest = DtoClientImpls.RmRequestImpl.make();
        rmRequest.setFiles(files);
        rmRequest.setCached(cached);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + REMOVE + params;

        AsyncRequest.build(POST, url).loader(loader).data(rmRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void reset(@NotNull String vfsId, @NotNull String projectid, @NotNull String commit, @Nullable ResetRequest.ResetType resetType,
                      @NotNull AsyncRequestCallback<String> callback) throws RequestException {

        DtoClientImpls.ResetRequestImpl resetRequest = DtoClientImpls.ResetRequestImpl.make();
        resetRequest.setCommit(commit);
        if (resetType != null) {
            resetRequest.setType(resetType);
        }

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + RESET + params;

        AsyncRequest.build(POST, url).loader(loader).data(resetRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void log(@NotNull String vfsId, @NotNull String projectid, boolean isTextFormat,
                    @NotNull AsyncRequestCallback<LogResponse> callback) throws RequestException {
        DtoClientImpls.LogRequestImpl logRequest = DtoClientImpls.LogRequestImpl.make();
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + LOG + params;

        if (isTextFormat) {
            AsyncRequest.build(POST, url).data(logRequest.serialize())
                        .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
        } else {
            AsyncRequest.build(POST, url).loader(loader).data(logRequest.serialize())
                        .header(CONTENTTYPE, APPLICATION_JSON)
                        .header(ACCEPT, APPLICATION_JSON).send(callback);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void remoteAdd(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String repositoryURL,
                          @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        DtoClientImpls.RemoteAddRequestImpl remoteAddRequest = DtoClientImpls.RemoteAddRequestImpl.make();
        remoteAddRequest.setName(name);
        remoteAddRequest.setUrl(repositoryURL);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + REMOTE_ADD + params;

        AsyncRequest.build(POST, url).loader(loader).data(remoteAddRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remoteDelete(@NotNull String vfsId, @NotNull String projectid, @NotNull String name,
                             @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + REMOTE_DELETE + '/' + name + params;

        AsyncRequest.build(POST, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void fetch(@NotNull String vfsId, @NotNull Project project, @NotNull String remote, JsonArray<String> refspec,
                      boolean removeDeletedRefs, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        DtoClientImpls.FetchRequestImpl fetchRequest = DtoClientImpls.FetchRequestImpl.make();
        fetchRequest.setRemote(remote);
        fetchRequest.setRefSpec(refspec);
        fetchRequest.setRemoveDeletedRefs(removeDeletedRefs);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = restServiceContext + FETCH + params;

        AsyncRequest.build(POST, url).data(fetchRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON)
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

        callback.setStatusHandler(new FetchRequestHandler(project.getName(), refspec, eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = wsName + FETCH + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(fetchRequest.serialize())
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void pull(@NotNull String vfsId, @NotNull Project project, @NotNull String refSpec, @NotNull String remote,
                     @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        DtoClientImpls.PullRequestImpl pullRequest = DtoClientImpls.PullRequestImpl.make();
        pullRequest.setRemote(remote);
        pullRequest.setRefSpec(refSpec);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = restServiceContext + PULL + params;

        AsyncRequest.build(POST, url, true).data(pullRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .requestStatusHandler(new PullRequestHandler(project.getName(), refSpec, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void pullWS(@NotNull String vfsId, @NotNull Project project, @NotNull String refSpec, @NotNull String remote,
                       @NotNull RequestCallback<String> callback) throws WebSocketException {
        DtoClientImpls.PullRequestImpl pullRequest = DtoClientImpls.PullRequestImpl.make();
        pullRequest.setRemote(remote);
        pullRequest.setRefSpec(refSpec);

        callback.setStatusHandler(new PullRequestHandler(project.getName(), refSpec, eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = wsName + PULL + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(pullRequest.serialize())
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void diff(@NotNull String vfsId, @NotNull String projectid, @NotNull JsonArray<String> fileFilter,
                     @NotNull DiffRequest.DiffType type,
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
    public void diff(@NotNull String vfsId, @NotNull String projectid, @NotNull JsonArray<String> fileFilter,
                     @NotNull DiffRequest.DiffType type, boolean noRenames, int renameLimit, @NotNull String commitA, boolean cached,
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
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + DIFF + params;

        AsyncRequest.build(POST, url).loader(loader).data(diffRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void merge(@NotNull String vfsId, @NotNull String projectid, @NotNull String commit,
                      @NotNull AsyncRequestCallback<MergeResult> callback) throws RequestException {
        DtoClientImpls.MergeRequestImpl mergeRequest = DtoClientImpls.MergeRequestImpl.make();
        mergeRequest.setCommit(commit);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + MERGE + params;

        AsyncRequest.build(POST, url).loader(loader).data(mergeRequest.serialize())
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .header(ACCEPT, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getGitReadOnlyUrl(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        String url = restServiceContext + RO_URL;
        url += "?vfsid=" + vfsId + "&projectid=" + projectid;
        AsyncRequest.build(RequestBuilder.GET, url).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getCommiters(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<Commiters> callback)
            throws RequestException {
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + COMMITERS + params;
        AsyncRequest.build(RequestBuilder.GET, url).header(ACCEPT, APPLICATION_JSON)
                    .send(callback);
    }


    /** {@inheritDoc} */
    @Override
    public void deleteRepository(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + DELETE_REPOSITORY + params;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader)
                    .header(CONTENTTYPE, APPLICATION_JSON).header(ACCEPT, TEXT_PLAIN)
                    .send(callback);
    }
}