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

import com.codenvy.ide.MimeType;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.add.AddRequestHandler;
import com.codenvy.ide.ext.git.client.clone.CloneRequestStatusHandler;
import com.codenvy.ide.ext.git.client.commit.CommitRequestHandler;
import com.codenvy.ide.ext.git.client.fetch.FetchRequestHandler;
import com.codenvy.ide.ext.git.client.init.InitRequestStatusHandler;
import com.codenvy.ide.ext.git.client.pull.PullRequestHandler;
import com.codenvy.ide.ext.git.client.push.PushRequestHandler;
import com.codenvy.ide.ext.git.shared.AddRequest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.BranchCheckoutRequest;
import com.codenvy.ide.ext.git.shared.BranchCreateRequest;
import com.codenvy.ide.ext.git.shared.BranchDeleteRequest;
import com.codenvy.ide.ext.git.shared.BranchListRequest;
import com.codenvy.ide.ext.git.shared.CloneRequest;
import com.codenvy.ide.ext.git.shared.CommitRequest;
import com.codenvy.ide.ext.git.shared.Commiters;
import com.codenvy.ide.ext.git.shared.DiffRequest;
import com.codenvy.ide.ext.git.shared.FetchRequest;
import com.codenvy.ide.ext.git.shared.GitUrlVendorInfo;
import com.codenvy.ide.ext.git.shared.InitRequest;
import com.codenvy.ide.ext.git.shared.LogRequest;
import com.codenvy.ide.ext.git.shared.MergeRequest;
import com.codenvy.ide.ext.git.shared.PullRequest;
import com.codenvy.ide.ext.git.shared.PushRequest;
import com.codenvy.ide.ext.git.shared.RemoteAddRequest;
import com.codenvy.ide.ext.git.shared.RemoteListRequest;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.RmRequest;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
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

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.codenvy.ide.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.MimeType.TEXT_PLAIN;
import static com.codenvy.ide.rest.HTTPHeader.ACCEPT;
import static com.codenvy.ide.rest.HTTPHeader.CONTENTTYPE;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * Implementation of the {@link GitClientService}.
 *
 * @author Ann Zhuleva
 */
@Singleton
public class GitClientServiceImpl implements GitClientService {
    public static final String ADD               = "/add";
    public static final String BRANCH_LIST       = "/branch-list";
    public static final String BRANCH_CHECKOUT   = "/branch-checkout";
    public static final String BRANCH_CREATE     = "/branch-create";
    public static final String BRANCH_DELETE     = "/branch-delete";
    public static final String BRANCH_RENAME     = "/branch-rename";
    public static final String CLONE             = "/clone";
    public static final String COMMIT            = "/commit";
    public static final String DIFF              = "/diff";
    public static final String FETCH             = "/fetch";
    public static final String INIT              = "/init";
    public static final String LOG               = "/log";
    public static final String MERGE             = "/merge";
    public static final String STATUS            = "/status";
    public static final String RO_URL            = "/read-only-url";
    public static final String PUSH              = "/push";
    public static final String PULL              = "/pull";
    public static final String REMOTE_LIST       = "/remote-list";
    public static final String REMOTE_ADD        = "/remote-add";
    public static final String REMOTE_DELETE     = "/remote-delete";
    public static final String REMOVE            = "/rm";
    public static final String RESET             = "/reset";
    public static final String COMMITERS         = "/commiters";
    public static final String DELETE_REPOSITORY = "/delete-repository";

    /** REST service context. */
    private final String                  baseHttpUrl;
    private final String                  gitServicePath;
    /** Loader to be displayed. */
    private final Loader                  loader;
    private final MessageBus              wsMessageBus;
    private final EventBus                eventBus;
    private final GitLocalizationConstant constant;
    private final DtoFactory              dtoFactory;

    /**
     * @param baseHttpUrl
     *         rest context
     * @param loader
     *         loader to show on server request
     */
    @Inject
    protected GitClientServiceImpl(@Named("restContext") String baseHttpUrl,
                                   Loader loader,
                                   MessageBus wsMessageBus,
                                   EventBus eventBus,
                                   GitLocalizationConstant constant,
                                   DtoFactory dtoFactory) {
        this.loader = loader;
        this.gitServicePath = "/git/" + Utils.getWorkspaceId();
        this.baseHttpUrl = baseHttpUrl + gitServicePath;
        this.wsMessageBus = wsMessageBus;
        this.eventBus = eventBus;
        this.constant = constant;
        this.dtoFactory = dtoFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void init(@NotNull String vfsId, @NotNull String projectid, @NotNull String projectName, boolean bare,
                     @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        InitRequest initRequest = dtoFactory.createDto(InitRequest.class);
        initRequest.setBare(bare);
        initRequest.setWorkingDir(projectid);

        String params = "vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + INIT + "?" + params;

        AsyncRequest.build(POST, url, true).data(dtoFactory.toJson(initRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).delay(2000)
                    .requestStatusHandler(new InitRequestStatusHandler(projectName, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void initWS(@NotNull String vfsId, @NotNull String projectid, @NotNull String projectName, boolean bare,
                       @NotNull RequestCallback<String> callback) throws WebSocketException {
        InitRequest initRequest = dtoFactory.createDto(InitRequest.class);
        initRequest.setBare(bare);
        initRequest.setWorkingDir(projectid);

        callback.setStatusHandler(new InitRequestStatusHandler(projectName, eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = gitServicePath + INIT + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(initRequest)).header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cloneRepository(@NotNull String vfsId, @NotNull Project project, @NotNull String remoteUri, @NotNull String remoteName,
                                @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        CloneRequest cloneRequest = dtoFactory.createDto(CloneRequest.class).withRemoteName(remoteName).withRemoteUri(remoteUri)
                                              .withWorkingDir(project.getId());

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = baseHttpUrl + CLONE + params;

        AsyncRequest.build(POST, url, true)
                    .requestStatusHandler(new CloneRequestStatusHandler(project.getName(), remoteUri, eventBus, constant))
                    .data(dtoFactory.toJson(cloneRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .header(ACCEPT, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cloneRepositoryWS(@NotNull String vfsId, @NotNull Project project, @NotNull String remoteUri, @NotNull String remoteName,
                                  @NotNull RequestCallback<String> callback) throws WebSocketException {
        CloneRequest cloneRequest =
                dtoFactory.createDto(CloneRequest.class).withRemoteName(remoteName).withRemoteUri(remoteUri)
                          .withWorkingDir(project.getId());

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new CloneRequestStatusHandler(project.getName(), remoteUri, eventBus, constant));

        String url = gitServicePath + CLONE + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(cloneRequest))
               .header(CONTENTTYPE, APPLICATION_JSON)
               .header(ACCEPT, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void statusText(@NotNull String vfsId, @NotNull String projectid, boolean shortFormat,
                           @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = baseHttpUrl + STATUS;
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid + "&short=" + shortFormat;

        AsyncRequest.build(POST, url + params).loader(loader)
                    .header(CONTENTTYPE, APPLICATION_JSON).header(ACCEPT, TEXT_PLAIN)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void add(@NotNull String vfsId, @NotNull Project project, boolean update, @Nullable List<String> filePattern,
                    @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AddRequest addRequest = dtoFactory.createDto(AddRequest.class).withUpdate(update);
        if (filePattern == null) {
            addRequest.setFilepattern(AddRequest.DEFAULT_PATTERN);
        } else {
            addRequest.setFilepattern(filePattern);
        }

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = baseHttpUrl + ADD + params;

        AsyncRequest.build(POST, url, true).data(dtoFactory.toJson(addRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .requestStatusHandler(new AddRequestHandler(project.getName(), eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void addWS(@NotNull String vfsId, @NotNull Project project, boolean update, @Nullable List<String> filePattern,
                      @NotNull RequestCallback<String> callback) throws WebSocketException {
        AddRequest addRequest = dtoFactory.createDto(AddRequest.class).withUpdate(update);
        if (filePattern == null) {
            addRequest.setFilepattern(AddRequest.DEFAULT_PATTERN);
        } else {
            addRequest.setFilepattern(filePattern);
        }

        callback.setStatusHandler(new AddRequestHandler(project.getName(), eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = gitServicePath + ADD + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(addRequest))
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull String vfsId, @NotNull Project project, @NotNull String message, boolean all, boolean amend,
                       @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        CommitRequest commitRequest = dtoFactory.createDto(CommitRequest.class).withMessage(message).withAmend(amend).withAll(all);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = baseHttpUrl + COMMIT + params;

        AsyncRequest.build(POST, url, true).data(dtoFactory.toJson(commitRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .requestStatusHandler(new CommitRequestHandler(project.getName(), message, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void commitWS(@NotNull String vfsId, @NotNull Project project, @NotNull String message, boolean all, boolean amend,
                         @NotNull RequestCallback<String> callback) throws WebSocketException {
        CommitRequest commitRequest = dtoFactory.createDto(CommitRequest.class).withMessage(message).withAmend(amend).withAll(all);
        callback.setStatusHandler(new CommitRequestHandler(project.getName(), message, eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = gitServicePath + COMMIT + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(commitRequest))
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message requestMessage = builder.build();

        wsMessageBus.send(requestMessage, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void push(@NotNull String vfsId, @NotNull Project project, @NotNull List<String> refSpec, @NotNull String remote,
                     boolean force, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        PushRequest pushRequest = dtoFactory.createDto(PushRequest.class).withRemote(remote).withRefSpec(refSpec).withForce(force);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = baseHttpUrl + PUSH + params;

        PushRequestHandler requestHandler = new PushRequestHandler(project.getName(), refSpec, eventBus, constant);
        AsyncRequest.build(POST, url, true).data(dtoFactory.toJson(pushRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).requestStatusHandler(requestHandler).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void pushWS(@NotNull String vfsId, @NotNull Project project, @NotNull List<String> refSpec, @NotNull String remote,
                       boolean force, @NotNull RequestCallback<String> callback) throws WebSocketException {
        PushRequest pushRequest = dtoFactory.createDto(PushRequest.class).withRemote(remote).withRefSpec(refSpec).withForce(force);

        callback.setStatusHandler(new PushRequestHandler(project.getName(), refSpec, eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = gitServicePath + PUSH + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(pushRequest))
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remoteList(@NotNull String vfsId, @NotNull String projectid, @Nullable String remoteName, boolean verbose,
                           @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        RemoteListRequest remoteListRequest = dtoFactory.createDto(RemoteListRequest.class).withVerbose(verbose);
        if (remoteName != null) {
            remoteListRequest.setRemote(remoteName);
        }

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + REMOTE_LIST + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(remoteListRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchList(@NotNull String vfsId, @NotNull String projectid, @Nullable String remoteMode,
                           @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        BranchListRequest branchListRequest = dtoFactory.createDto(BranchListRequest.class).withListMode(remoteMode);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + BRANCH_LIST + params;

        AsyncRequest.build(POST, url).data(dtoFactory.toJson(branchListRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void status(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid + "&short=false";
        String url = baseHttpUrl + STATUS + params;

        AsyncRequest.build(POST, url).loader(loader)
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .header(ACCEPT, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchDelete(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, boolean force,
                             @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        BranchDeleteRequest branchDeleteRequest = dtoFactory.createDto(BranchDeleteRequest.class).withName(name).withForce(force);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + BRANCH_DELETE + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(branchDeleteRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchRename(@NotNull String vfsId, @NotNull String projectid, @NotNull String oldName, @NotNull String newName,
                             @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid + "&oldName=" + oldName + "&newName=" + newName;
        String url = baseHttpUrl + BRANCH_RENAME + params;

        AsyncRequest.build(POST, url).loader(loader)
                    .header(CONTENTTYPE, MimeType.APPLICATION_FORM_URLENCODED).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchCreate(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                             @NotNull AsyncRequestCallback<Branch> callback) throws RequestException {

        BranchCreateRequest branchCreateRequest = dtoFactory.createDto(BranchCreateRequest.class).withName(name).withStartPoint(startPoint);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + BRANCH_CREATE + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(branchCreateRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .header(ACCEPT, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchCheckout(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                               boolean createNew, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        BranchCheckoutRequest branchCheckoutRequest =
                dtoFactory.createDto(BranchCheckoutRequest.class).withName(name).withStartPoint(startPoint).withCreateNew(createNew);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + BRANCH_CHECKOUT + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(branchCheckoutRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remove(@NotNull String vfsId, @NotNull String projectid, List<String> files, boolean cached,
                       @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        RmRequest rmRequest = dtoFactory.createDto(RmRequest.class).withFiles(files).withCached(cached);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + REMOVE + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(rmRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void reset(@NotNull String vfsId, @NotNull String projectid, @NotNull String commit, @Nullable ResetRequest.ResetType resetType,
                      @NotNull AsyncRequestCallback<String> callback) throws RequestException {

        ResetRequest resetRequest = dtoFactory.createDto(ResetRequest.class).withCommit(commit);
        if (resetType != null) {
            resetRequest.setType(resetType);
        }

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + RESET + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(resetRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void log(@NotNull String vfsId, @NotNull String projectid, boolean isTextFormat,
                    @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        LogRequest logRequest = dtoFactory.createDto(LogRequest.class);
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + LOG + params;

        if (isTextFormat) {
            AsyncRequest.build(POST, url).data(dtoFactory.toJson(logRequest))
                        .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
        } else {
            AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(logRequest))
                        .header(CONTENTTYPE, APPLICATION_JSON)
                        .header(ACCEPT, APPLICATION_JSON).send(callback);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void remoteAdd(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String repositoryURL,
                          @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        RemoteAddRequest remoteAddRequest = dtoFactory.createDto(RemoteAddRequest.class).withName(name).withUrl(repositoryURL);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + REMOTE_ADD + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(remoteAddRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remoteDelete(@NotNull String vfsId, @NotNull String projectid, @NotNull String name,
                             @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + REMOTE_DELETE + '/' + name + params;

        AsyncRequest.build(POST, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void fetch(@NotNull String vfsId, @NotNull Project project, @NotNull String remote, List<String> refspec,
                      boolean removeDeletedRefs, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        FetchRequest fetchRequest =
                dtoFactory.createDto(FetchRequest.class).withRefSpec(refspec).withRemote(remote).withRemoveDeletedRefs(removeDeletedRefs);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = baseHttpUrl + FETCH + params;

        AsyncRequest.build(POST, url).data(dtoFactory.toJson(fetchRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .requestStatusHandler(new FetchRequestHandler(project.getName(), refspec, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void fetchWS(@NotNull String vfsId, @NotNull Project project, @NotNull String remote, List<String> refspec,
                        boolean removeDeletedRefs, @NotNull RequestCallback<String> callback) throws WebSocketException {
        FetchRequest fetchRequest =
                dtoFactory.createDto(FetchRequest.class).withRefSpec(refspec).withRemote(remote).withRemoveDeletedRefs(removeDeletedRefs);

        callback.setStatusHandler(new FetchRequestHandler(project.getName(), refspec, eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = gitServicePath + FETCH + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(fetchRequest))
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void pull(@NotNull String vfsId, @NotNull Project project, @NotNull String refSpec, @NotNull String remote,
                     @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        PullRequest pullRequest = dtoFactory.createDto(PullRequest.class).withRemote(remote).withRefSpec(refSpec);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = baseHttpUrl + PULL + params;

        AsyncRequest.build(POST, url, true).data(dtoFactory.toJson(pullRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .requestStatusHandler(new PullRequestHandler(project.getName(), refSpec, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void pullWS(@NotNull String vfsId, @NotNull Project project, @NotNull String refSpec, @NotNull String remote,
                       @NotNull RequestCallback<String> callback) throws WebSocketException {
        PullRequest pullRequest = dtoFactory.createDto(PullRequest.class).withRemote(remote).withRefSpec(refSpec);

        callback.setStatusHandler(new PullRequestHandler(project.getName(), refSpec, eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = gitServicePath + PULL + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(pullRequest))
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void diff(@NotNull String vfsId, @NotNull String projectid, @NotNull List<String> fileFilter,
                     @NotNull DiffRequest.DiffType type, boolean noRenames, int renameLimit, @NotNull String commitA,
                     @NotNull String commitB, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        DiffRequest diffRequest =
                dtoFactory.createDto(DiffRequest.class).withFileFilter(fileFilter).withType(type)
                          .withNoRenames(noRenames).withCommitA(commitA).withCommitB(commitB).withRenameLimit(renameLimit);

        diff(diffRequest, vfsId, projectid, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void diff(@NotNull String vfsId, @NotNull String projectid, @NotNull List<String> fileFilter,
                     @NotNull DiffRequest.DiffType type, boolean noRenames, int renameLimit, @NotNull String commitA, boolean cached,
                     @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        DiffRequest diffRequest =
                dtoFactory.createDto(DiffRequest.class).withFileFilter(fileFilter).withType(type)
                          .withNoRenames(noRenames).withCommitA(commitA).withRenameLimit(renameLimit).withCached(cached);

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
    private void diff(DiffRequest diffRequest, String vfsId, String projectid, AsyncRequestCallback<String> callback)
            throws RequestException {
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + DIFF + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(diffRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void merge(@NotNull String vfsId, @NotNull String projectid, @NotNull String commit,
                      @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        MergeRequest mergeRequest = dtoFactory.createDto(MergeRequest.class).withCommit(commit);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + MERGE + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(mergeRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .header(ACCEPT, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getGitReadOnlyUrl(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        String url = baseHttpUrl + RO_URL;
        url += "?vfsid=" + vfsId + "&projectid=" + projectid;
        AsyncRequest.build(RequestBuilder.GET, url).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getCommiters(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<Commiters> callback)
            throws RequestException {
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + COMMITERS + params;
        AsyncRequest.build(RequestBuilder.GET, url).header(ACCEPT, APPLICATION_JSON)
                    .send(callback);
    }


    /** {@inheritDoc} */
    @Override
    public void deleteRepository(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = baseHttpUrl + DELETE_REPOSITORY + params;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader)
                    .header(CONTENTTYPE, APPLICATION_JSON).header(ACCEPT, TEXT_PLAIN)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getUrlVendorInfo(@NotNull String vcsUrl, @NotNull AsyncRequestCallback<GitUrlVendorInfo> callback) throws RequestException {
        String url = baseHttpUrl + "/git-service/info";

        String params = "vcsurl=" + vcsUrl;
        AsyncRequest.build(RequestBuilder.GET, url + "?" + params).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }
}