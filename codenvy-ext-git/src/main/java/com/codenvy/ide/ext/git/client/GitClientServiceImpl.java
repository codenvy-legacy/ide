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

import java.util.List;

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
    private DtoFactory              dtoFactory;
    
    /**
     * @param restContext
     *         rest context
     * @param loader
     *         loader to show on server request
     */
    @Inject
    protected GitClientServiceImpl(@Named("restContext") String restContext, Loader loader, MessageBus wsMessageBus, EventBus eventBus,
                                   GitLocalizationConstant constant, DtoFactory dtoFactory) {
        this.loader = loader;
        this.wsName = '/' + Utils.getWorkspaceName();
        this.restServiceContext = restContext + wsName;
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
        String url = restServiceContext + INIT + "?" + params;

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
        String url = wsName + INIT + params;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(initRequest)).header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cloneRepository(@NotNull String vfsId, @NotNull Project project, @NotNull String remoteUri, @NotNull String remoteName,
                                @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        CloneRequest cloneRequest = dtoFactory.createDto(CloneRequest.class).withRemoteName(remoteName).withRemoteUri(remoteUri).withWorkingDir(project.getId());

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = restServiceContext + CLONE + params;

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
        CloneRequest cloneRequest = dtoFactory.createDto(CloneRequest.class).withRemoteName(remoteName).withRemoteUri(remoteUri).withWorkingDir(project.getId());

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        callback.setStatusHandler(new CloneRequestStatusHandler(project.getName(), remoteUri, eventBus, constant));

        String url = wsName + CLONE + params;

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
        String url = restServiceContext + STATUS;
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
        String url = restServiceContext + ADD + params;

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
        String url = wsName + ADD + params;

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
        String url = restServiceContext + COMMIT + params;

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
        String url = wsName + COMMIT + params;

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
        String url = restServiceContext + PUSH + params;

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
        String url = wsName + PUSH + params;

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
        String url = restServiceContext + REMOTE_LIST + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(remoteListRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchList(@NotNull String vfsId, @NotNull String projectid, @Nullable String remoteMode,
                           @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        BranchListRequest branchListRequest = dtoFactory.createDto(BranchListRequest.class).withListMode(remoteMode);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + BRANCH_LIST + params;

        AsyncRequest.build(POST, url).data(dtoFactory.toJson(branchListRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void status(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<String> callback)
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
        BranchDeleteRequest branchDeleteRequest = dtoFactory.createDto(BranchDeleteRequest.class).withName(name).withForce(force);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + BRANCH_DELETE + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(branchDeleteRequest))
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

        BranchCreateRequest branchCreateRequest = dtoFactory.createDto(BranchCreateRequest.class).withName(name).withStartPoint(startPoint);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + BRANCH_CREATE + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(branchCreateRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .header(ACCEPT, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchCheckout(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                               boolean createNew, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        BranchCheckoutRequest branchCheckoutRequest = dtoFactory.createDto(BranchCheckoutRequest.class).withName(name).withStartPoint(startPoint).withCreateNew(createNew);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + BRANCH_CHECKOUT + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(branchCheckoutRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remove(@NotNull String vfsId, @NotNull String projectid, List<String> files, boolean cached,
                       @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        RmRequest rmRequest = dtoFactory.createDto(RmRequest.class).withFiles(files).withCached(cached);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + REMOVE + params;

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
        String url = restServiceContext + RESET + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(resetRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void log(@NotNull String vfsId, @NotNull String projectid, boolean isTextFormat,
                    @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        LogRequest logRequest = dtoFactory.createDto(LogRequest.class);
        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + LOG + params;

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
        String url = restServiceContext + REMOTE_ADD + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(remoteAddRequest))
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
    public void fetch(@NotNull String vfsId, @NotNull Project project, @NotNull String remote, List<String> refspec,
                      boolean removeDeletedRefs, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        FetchRequest fetchRequest = dtoFactory.createDto(FetchRequest.class).withRefSpec(refspec).withRemote(remote).withRemoveDeletedRefs(removeDeletedRefs);

        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = restServiceContext + FETCH + params;

        AsyncRequest.build(POST, url).data(dtoFactory.toJson(fetchRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON)
                    .requestStatusHandler(new FetchRequestHandler(project.getName(), refspec, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void fetchWS(@NotNull String vfsId, @NotNull Project project, @NotNull String remote, List<String> refspec,
                        boolean removeDeletedRefs, @NotNull RequestCallback<String> callback) throws WebSocketException {
        FetchRequest fetchRequest = dtoFactory.createDto(FetchRequest.class).withRefSpec(refspec).withRemote(remote).withRemoveDeletedRefs(removeDeletedRefs);

        callback.setStatusHandler(new FetchRequestHandler(project.getName(), refspec, eventBus, constant));
        String params = "?vfsid=" + vfsId + "&projectid=" + project.getId();
        String url = wsName + FETCH + params;

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
        String url = restServiceContext + PULL + params;

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
        String url = wsName + PULL + params;

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
        String url = restServiceContext + DIFF + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(diffRequest))
                    .header(CONTENTTYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void merge(@NotNull String vfsId, @NotNull String projectid, @NotNull String commit,
                      @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        MergeRequest mergeRequest = dtoFactory.createDto(MergeRequest.class).withCommit(commit);

        String params = "?vfsid=" + vfsId + "&projectid=" + projectid;
        String url = restServiceContext + MERGE + params;

        AsyncRequest.build(POST, url).loader(loader).data(dtoFactory.toJson(mergeRequest))
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