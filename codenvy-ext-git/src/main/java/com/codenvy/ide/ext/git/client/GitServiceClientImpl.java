/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.client;

import com.codenvy.ide.MimeType;
import com.codenvy.ide.collections.Array;
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
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.MergeRequest;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.ext.git.shared.PullRequest;
import com.codenvy.ide.ext.git.shared.PushRequest;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.ext.git.shared.RemoteAddRequest;
import com.codenvy.ide.ext.git.shared.RemoteListRequest;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.ext.git.shared.RmRequest;
import com.codenvy.ide.ext.git.shared.Status;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
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
 * Implementation of the {@link GitServiceClient}.
 *
 * @author Ann Zhuleva
 */
@Singleton
public class GitServiceClientImpl implements GitServiceClient {
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
    private final AsyncRequestFactory     asyncRequestFactory;

    @Inject
    protected GitServiceClientImpl(@Named("restContext") String restContext,
                                   @Named("workspaceId") String workspaceId,
                                   Loader loader,
                                   MessageBus wsMessageBus,
                                   EventBus eventBus,
                                   GitLocalizationConstant constant,
                                   DtoFactory dtoFactory,
                                   AsyncRequestFactory asyncRequestFactory) {
        this.loader = loader;
        this.gitServicePath = "/git/" + workspaceId;
        this.baseHttpUrl = restContext + gitServicePath;
        this.wsMessageBus = wsMessageBus;
        this.eventBus = eventBus;
        this.constant = constant;
        this.dtoFactory = dtoFactory;
        this.asyncRequestFactory = asyncRequestFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void init(@NotNull String projectid, @NotNull String projectName, boolean bare,
                     @NotNull RequestCallback<Void> callback) throws WebSocketException {
        InitRequest initRequest = dtoFactory.createDto(InitRequest.class);
        initRequest.setBare(bare);
        initRequest.setWorkingDir(projectid);
        initRequest.setInitCommit(true);

        callback.setStatusHandler(new InitRequestStatusHandler(projectName, eventBus, constant));
        String url = gitServicePath + INIT + "?projectid=" + projectid;

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(initRequest)).header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cloneRepository(@NotNull Project project, @NotNull String remoteUri, @NotNull String remoteName,
                                @NotNull RequestCallback<RepoInfo> callback) throws WebSocketException {
        CloneRequest cloneRequest = dtoFactory.createDto(CloneRequest.class).withRemoteName(remoteName).withRemoteUri(remoteUri)
                                              .withWorkingDir(project.getPath());

        String params = "?projectid=" + project.getPath()/*project.getId()*/;
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
    public void statusText(@NotNull String projectid, boolean shortFormat,
                           @NotNull AsyncRequestCallback<String> callback) {
        String url = baseHttpUrl + STATUS;
        String params = "?projectid=" + projectid + "&short=" + shortFormat;

        asyncRequestFactory.createPostRequest(url + params, null)
                           .loader(loader)
                           .header(CONTENTTYPE, APPLICATION_JSON)
                           .header(ACCEPT, TEXT_PLAIN)
                           .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void add(@NotNull Project project, boolean update, @Nullable List<String> filePattern,
                    @NotNull RequestCallback<Void> callback) throws WebSocketException {
        AddRequest addRequest = dtoFactory.createDto(AddRequest.class).withUpdate(update);
        if (filePattern == null) {
            addRequest.setFilepattern(AddRequest.DEFAULT_PATTERN);
        } else {
            addRequest.setFilepattern(filePattern);
        }
        callback.setStatusHandler(new AddRequestHandler(project.getName(), eventBus, constant));
        String url = gitServicePath + ADD + "?projectid=" + project.getId();

        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(addRequest))
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull Project project, @NotNull String message, boolean all, boolean amend,
                       @NotNull AsyncRequestCallback<Revision> callback)  {
        CommitRequest commitRequest =
                dtoFactory.createDto(CommitRequest.class).withMessage(message).withAmend(amend).withAll(all);
//        callback.setStatusHandler(new CommitRequestHandler(project.getName(), message, eventBus, constant));
        String url = baseHttpUrl + COMMIT + "?projectid=" + project.getId();

        asyncRequestFactory.createPostRequest(url, commitRequest).loader(loader).send(callback);

//        MessageBuilder builder = new MessageBuilder(POST, url);
//        builder.data(dtoFactory.toJson(commitRequest))
//               .header(CONTENTTYPE, APPLICATION_JSON);
//        Message requestMessage = builder.build();
//        wsMessageBus.send(requestMessage, null);
    }

    /** {@inheritDoc} */
    @Override
    public void push(@NotNull Project project, @NotNull List<String> refSpec, @NotNull String remote,
                     boolean force, @NotNull RequestCallback<String> callback) throws WebSocketException {
        PushRequest pushRequest =
                dtoFactory.createDto(PushRequest.class).withRemote(remote).withRefSpec(refSpec).withForce(force);

        callback.setStatusHandler(new PushRequestHandler(project.getName(), refSpec, eventBus, constant));
        String url = gitServicePath + PUSH + "?projectid=" + project.getId();
        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(pushRequest))
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remoteList(@NotNull String projectid, @Nullable String remoteName, boolean verbose,
                           @NotNull AsyncRequestCallback<Array<Remote>> callback) {
        RemoteListRequest remoteListRequest = dtoFactory.createDto(RemoteListRequest.class).withVerbose(verbose);
        if (remoteName != null) {
            remoteListRequest.setRemote(remoteName);
        }
        String url = baseHttpUrl + REMOTE_LIST + "?projectid=" + projectid;
        asyncRequestFactory.createPostRequest(url, remoteListRequest).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchList(@NotNull String projectid, @Nullable String remoteMode,
                           @NotNull AsyncRequestCallback<Array<Branch>> callback) {
        BranchListRequest branchListRequest = dtoFactory.createDto(BranchListRequest.class).withListMode(remoteMode);
        String url = baseHttpUrl + BRANCH_LIST + "?projectid=" + projectid;
        asyncRequestFactory.createPostRequest(url, branchListRequest).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void status(@NotNull String projectid, @NotNull AsyncRequestCallback<Status> callback) {
        String params = "?projectid=" + projectid + "&short=false";
        String url = baseHttpUrl + STATUS + params;
        asyncRequestFactory.createPostRequest(url, null).loader(loader)
                           .header(CONTENTTYPE, APPLICATION_JSON)
                           .header(ACCEPT, APPLICATION_JSON)
                           .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchDelete(@NotNull String projectid, @NotNull String name, boolean force,
                             @NotNull AsyncRequestCallback<String> callback) {
        BranchDeleteRequest branchDeleteRequest =
                dtoFactory.createDto(BranchDeleteRequest.class).withName(name).withForce(force);
        String url = baseHttpUrl + BRANCH_DELETE + "?projectid=" + projectid;
        asyncRequestFactory.createPostRequest(url, branchDeleteRequest).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchRename(@NotNull String projectid, @NotNull String oldName, @NotNull String newName,
                             @NotNull AsyncRequestCallback<String> callback) {
        String params = "?projectid=" + projectid + "&oldName=" + oldName + "&newName=" + newName;
        String url = baseHttpUrl + BRANCH_RENAME + params;
        asyncRequestFactory.createPostRequest(url, null).loader(loader)
                           .header(CONTENTTYPE, MimeType.APPLICATION_FORM_URLENCODED)
                           .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchCreate(@NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                             @NotNull AsyncRequestCallback<Branch> callback) {

        BranchCreateRequest branchCreateRequest =
                dtoFactory.createDto(BranchCreateRequest.class).withName(name).withStartPoint(startPoint);
        String url = baseHttpUrl + BRANCH_CREATE + "?projectid=" + projectid;
        asyncRequestFactory.createPostRequest(url, branchCreateRequest).loader(loader).header(ACCEPT, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void branchCheckout(@NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                               boolean createNew, @NotNull AsyncRequestCallback<String> callback) {
        BranchCheckoutRequest branchCheckoutRequest =
                dtoFactory.createDto(BranchCheckoutRequest.class).withName(name).withStartPoint(startPoint)
                          .withCreateNew(createNew);
        String url = baseHttpUrl + BRANCH_CHECKOUT + "?projectid=" + projectid;
        asyncRequestFactory.createPostRequest(url, branchCheckoutRequest).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remove(@NotNull String projectid, List<String> files, boolean cached,
                       @NotNull AsyncRequestCallback<String> callback) {
        RmRequest rmRequest = dtoFactory.createDto(RmRequest.class).withFiles(files).withCached(cached);
        String url = baseHttpUrl + REMOVE + "?projectid=" + projectid;
        asyncRequestFactory.createPostRequest(url, rmRequest).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void reset(@NotNull String projectid, @NotNull String commit, @Nullable ResetRequest.ResetType resetType,
                      @NotNull AsyncRequestCallback<Void> callback) {

        ResetRequest resetRequest = dtoFactory.createDto(ResetRequest.class).withCommit(commit);
        if (resetType != null) {
            resetRequest.setType(resetType);
        }
        String url = baseHttpUrl + RESET + "?projectid=" + projectid;
        asyncRequestFactory.createPostRequest(url, resetRequest).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void log(@NotNull String projectid, boolean isTextFormat,
                    @NotNull AsyncRequestCallback<LogResponse> callback) {
        LogRequest logRequest = dtoFactory.createDto(LogRequest.class);
        String url = baseHttpUrl + LOG + "?projectid=" + projectid;
        if (isTextFormat) {
            asyncRequestFactory.createPostRequest(url, logRequest).send(callback);
        } else {
            asyncRequestFactory.createPostRequest(url, logRequest).loader(loader).header(ACCEPT, APPLICATION_JSON).send(callback);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void remoteAdd(@NotNull String projectid, @NotNull String name, @NotNull String repositoryURL,
                          @NotNull AsyncRequestCallback<String> callback) {
        RemoteAddRequest remoteAddRequest = dtoFactory.createDto(RemoteAddRequest.class).withName(name).withUrl(repositoryURL);
        String url = baseHttpUrl + REMOTE_ADD + "?projectid=" + projectid;
        asyncRequestFactory.createPostRequest(url, remoteAddRequest).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void remoteDelete(@NotNull String projectid, @NotNull String name,
                             @NotNull AsyncRequestCallback<String> callback) {
        String url = baseHttpUrl + REMOTE_DELETE + '/' + name + "?projectid=" + projectid;
        asyncRequestFactory.createPostRequest(url, null).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void fetch(@NotNull Project project, @NotNull String remote, List<String> refspec,
                      boolean removeDeletedRefs, @NotNull RequestCallback<String> callback) throws WebSocketException {
        FetchRequest fetchRequest = dtoFactory.createDto(FetchRequest.class).withRefSpec(refspec).withRemote(remote)
                                              .withRemoveDeletedRefs(removeDeletedRefs);

        callback.setStatusHandler(new FetchRequestHandler(project.getName(), refspec, eventBus, constant));
        String url = gitServicePath + FETCH + "?projectid=" + project.getId();
        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(fetchRequest))
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();
        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void pull(@NotNull Project project, @NotNull String refSpec, @NotNull String remote,
                     @NotNull RequestCallback<String> callback) throws WebSocketException {
        PullRequest pullRequest = dtoFactory.createDto(PullRequest.class).withRemote(remote).withRefSpec(refSpec);
        callback.setStatusHandler(new PullRequestHandler(project.getName(), refSpec, eventBus, constant));
        String url = gitServicePath + PULL + "?projectid=" + project.getId();
        MessageBuilder builder = new MessageBuilder(POST, url);
        builder.data(dtoFactory.toJson(pullRequest))
               .header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();
        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void diff(@NotNull String projectid, @NotNull List<String> fileFilter,
                     @NotNull DiffRequest.DiffType type, boolean noRenames, int renameLimit, @NotNull String commitA,
                     @NotNull String commitB, @NotNull AsyncRequestCallback<String> callback) {
        DiffRequest diffRequest = dtoFactory.createDto(DiffRequest.class)
                                            .withFileFilter(fileFilter)
                                            .withType(type)
                                            .withNoRenames(noRenames)
                                            .withCommitA(commitA)
                                            .withCommitB(commitB)
                                            .withRenameLimit(renameLimit);

        diff(diffRequest, projectid, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void diff(@NotNull String projectid, @NotNull List<String> fileFilter,
                     @NotNull DiffRequest.DiffType type, boolean noRenames, int renameLimit, @NotNull String commitA, boolean cached,
                     @NotNull AsyncRequestCallback<String> callback) {
        DiffRequest diffRequest = dtoFactory.createDto(DiffRequest.class)
                                            .withFileFilter(fileFilter).withType(type)
                                            .withNoRenames(noRenames)
                                            .withCommitA(commitA)
                                            .withRenameLimit(renameLimit)
                                            .withCached(cached);

        diff(diffRequest, projectid, callback);
    }

    /**
     * Make diff request.
     *
     * @param diffRequest
     *         request for diff
     * @param projectid
     *         project id
     * @param callback
     *         callback
     */
    private void diff(DiffRequest diffRequest, String projectid, AsyncRequestCallback<String> callback) {
        String url = baseHttpUrl + DIFF + "?projectid=" + projectid;
        asyncRequestFactory.createPostRequest(url, diffRequest).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void merge(@NotNull String projectid, @NotNull String commit,
                      @NotNull AsyncRequestCallback<MergeResult> callback) {
        MergeRequest mergeRequest = dtoFactory.createDto(MergeRequest.class).withCommit(commit);
        String url = baseHttpUrl + MERGE + "?projectid=" + projectid;
        asyncRequestFactory.createPostRequest(url, mergeRequest).loader(loader)
                           .header(ACCEPT, APPLICATION_JSON)
                           .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getGitReadOnlyUrl(@NotNull String projectid, @NotNull AsyncRequestCallback<String> callback) {
        String url = baseHttpUrl + RO_URL + "?projectid=" + projectid;
        asyncRequestFactory.createGetRequest(url).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getCommitters(@NotNull String projectid, @NotNull AsyncRequestCallback<Commiters> callback) {
        String url = baseHttpUrl + COMMITERS + "?projectid=" + projectid;
        asyncRequestFactory.createGetRequest(url).header(ACCEPT, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteRepository(@NotNull String projectid, @NotNull AsyncRequestCallback<Void> callback) {
        String url = baseHttpUrl + DELETE_REPOSITORY + "?projectid=" + projectid;
        asyncRequestFactory.createGetRequest(url).loader(loader)
                           .header(CONTENTTYPE, APPLICATION_JSON).header(ACCEPT, TEXT_PLAIN)
                           .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getUrlVendorInfo(@NotNull String vcsUrl, @NotNull AsyncRequestCallback<GitUrlVendorInfo> callback) {
        asyncRequestFactory.createGetRequest(baseHttpUrl + "/git-service/info?vcsurl=" + vcsUrl).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(
                callback);
    }
}