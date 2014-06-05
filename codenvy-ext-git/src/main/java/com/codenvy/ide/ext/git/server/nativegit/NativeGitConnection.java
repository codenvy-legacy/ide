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
package com.codenvy.ide.ext.git.server.nativegit;


import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.git.server.DiffPage;
import com.codenvy.ide.ext.git.server.GitConnection;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.LogPage;
import com.codenvy.ide.ext.git.server.NotAuthorizedException;
import com.codenvy.ide.ext.git.server.nativegit.commands.AddCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.BranchCheckoutCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.BranchCreateCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.BranchDeleteCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.BranchListCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.CloneCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.CommitCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.EmptyGitCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.FetchCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.InitCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.LogCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.PullCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.PushCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.RemoteListCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.RemoteUpdateCommand;
import com.codenvy.ide.ext.git.shared.AddRequest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.BranchCheckoutRequest;
import com.codenvy.ide.ext.git.shared.BranchCreateRequest;
import com.codenvy.ide.ext.git.shared.BranchDeleteRequest;
import com.codenvy.ide.ext.git.shared.BranchListRequest;
import com.codenvy.ide.ext.git.shared.CloneRequest;
import com.codenvy.ide.ext.git.shared.CommitRequest;
import com.codenvy.ide.ext.git.shared.DiffRequest;
import com.codenvy.ide.ext.git.shared.FetchRequest;
import com.codenvy.ide.ext.git.shared.GitUser;
import com.codenvy.ide.ext.git.shared.InitRequest;
import com.codenvy.ide.ext.git.shared.LogRequest;
import com.codenvy.ide.ext.git.shared.MergeRequest;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.ext.git.shared.MoveRequest;
import com.codenvy.ide.ext.git.shared.PullRequest;
import com.codenvy.ide.ext.git.shared.PushRequest;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.ext.git.shared.RemoteAddRequest;
import com.codenvy.ide.ext.git.shared.RemoteListRequest;
import com.codenvy.ide.ext.git.shared.RemoteUpdateRequest;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.ext.git.shared.RmRequest;
import com.codenvy.ide.ext.git.shared.Status;
import com.codenvy.ide.ext.git.shared.Tag;
import com.codenvy.ide.ext.git.shared.TagCreateRequest;
import com.codenvy.ide.ext.git.shared.TagDeleteRequest;
import com.codenvy.ide.ext.git.shared.TagListRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Native implementation of GitConnection
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class NativeGitConnection implements GitConnection {

    private final static Logger LOG = LoggerFactory.getLogger(NativeGitConnection.class);
    private final NativeGit                nativeGit;
    private final CredentialsLoader        credentialsLoader;
    private final Set<CredentialsProvider> credentialsProviders;
    private       GitUser                  user;
    private       SshKeysManager           keysManager;

    private static final Pattern authErrorPattern =
            Pattern.compile(
                    ".*fatal: Authentication failed for '.*'.*|.*fatal: Could not read from remote repository\\.\\n\\nPlease make sure " +
                    "you have the correct access rights\\nand the repository exists\\.\\n.*",
                    Pattern.MULTILINE);

    /**
     * @param repository
     *         directory where commands will be invoked
     * @param user
     *         git user
     * @param keysManager
     *         manager for ssh keys. If it is null default ssh will be used;
     * @param credentialsLoader
     *          loader for credentials
     * @param credentialsProviders
     *          set of credentials providers
     * @throws GitException
     *         when some error occurs
     */
    public NativeGitConnection(File repository, GitUser user, SshKeysManager keysManager, CredentialsLoader credentialsLoader,
                               Set<CredentialsProvider> credentialsProviders) throws GitException {
        this.user = user;
        this.keysManager = keysManager;
        this.credentialsLoader = credentialsLoader;
        this.credentialsProviders = credentialsProviders;
        nativeGit = new NativeGit(repository);
    }

    @Override
    public void add(AddRequest request) throws GitException {
        AddCommand command = nativeGit.createAddCommand();
        command.setFilePattern(request.getFilepattern() == null ?
                               AddRequest.DEFAULT_PATTERN :
                               request.getFilepattern());
        command.setUpdate(request.isUpdate());
        command.execute();
    }

    @Override
    public void branchCheckout(BranchCheckoutRequest request) throws GitException {
        BranchCheckoutCommand command = nativeGit.createBranchCheckoutCommand();
        /*
         * IF branch name is origin/HEAD then *(no branch).
         * Create new means that remote branch was selected,
         * so git checkout -t remote/branchName will create
         * branchName tracked to remote/branchName
         */
        if (request.isCreateNew()) {
            try {
                if (!(getBranchRef(request.getName()).startsWith("refs/remotes/")
                      && request.getName().endsWith("/HEAD"))) {
                    command.setRemote(true);
                }
            } catch (GitException ignored) {
                command.setCreateNew(true);
                command.setStartPoint(request.getStartPoint());
            }
        }
        command.setBranchName(request.getName()).execute();
    }

    @Override
    public Branch branchCreate(BranchCreateRequest request) throws GitException {
        BranchCreateCommand branchCreateCommand = nativeGit.createBranchCreateCommand();
        branchCreateCommand.setBranchName(request.getName())
                           .setStartPoint(request.getStartPoint())
                           .execute();
        return DtoFactory.getInstance().createDto(Branch.class).withName(getBranchRef(request.getName())).withActive(false).withDisplayName(request.getName()).withRemote(false);
    }

    @Override
    public void branchDelete(BranchDeleteRequest request) throws GitException {
        //convert ref name to displayed name
        String name = getBranchRef(request.getName());
        if (name.startsWith("refs/")) {
            name = name.substring(name.indexOf('/', 6) + 1);
        }
        BranchDeleteCommand branchDeleteCommand = nativeGit.createBranchDeleteCommand();
        branchDeleteCommand.setBranchName(name)
                           .setDeleteFullyMerged(request.isForce())
                           .execute();
    }

    @Override
    public void branchRename(String oldName, String newName) throws GitException {
        nativeGit.createBranchRenameCommand().setNames(oldName, newName).execute();
    }

    @Override
    public List<Branch> branchList(BranchListRequest request) throws GitException {
        String listMode = request.getListMode();
        if (listMode != null
            && !(listMode.equals(BranchListRequest.LIST_ALL) || listMode.equals(BranchListRequest.LIST_REMOTE))) {
            throw new IllegalArgumentException("Unsupported list mode '" + listMode + "'. Must be either 'a' or 'r'. ");
        }
        List<Branch> branches;
        BranchListCommand branchListCommand = nativeGit.createBranchListCommand();
        if (request.getListMode() == null) {
            branches = branchListCommand.execute();
        } else if (request.getListMode().equals(BranchListRequest.LIST_ALL)) {
            branches = branchListCommand.execute();
            branches.addAll(branchListCommand.setShowRemotes(true).execute());
        } else {
            branches = branchListCommand.setShowRemotes(true).execute();
        }
        return branches;
    }

    @Override
    public GitConnection clone(CloneRequest request) throws URISyntaxException, GitException {
        if (request.getWorkingDir() != null) {
            nativeGit.setRepository(new File(request.getWorkingDir()));
        }
        CloneCommand clone;
        String key;
        if ((key = keysManager.storeKeyIfNeed(request.getRemoteUri())) != null) {
            clone = nativeGit.createCloneCommand(key);
        } else {
            clone = nativeGit.createCloneCommand();
        }
        clone.setUri(request.getRemoteUri());
        clone.setRemoteName(request.getRemoteName());
        if (clone.getTimeout() > 0) {
            clone.setTimeout(request.getTimeout());
        }
        executeWithCredentials(clone, request.getRemoteUri());
        File repository = clone.getRepository();
        //set up back default url
        new RemoteUpdateCommand(repository).setRemoteName(request.getRemoteName() == null
                                                          ? "origin"
                                                          : request.getRemoteName())
                                           .setNewUrl(request.getRemoteUri())
                                           .execute();
        nativeGit.createConfig().setUser(user).saveUser();
        return new NativeGitConnection(repository, user, keysManager, credentialsLoader, credentialsProviders);
    }

    @Override
    public Revision commit(CommitRequest request) throws GitException {
        Config config = new Config(nativeGit.getRepository());
        final String remoteOriginURL = config.loadValue("remote.origin.url");

        GitUser committer;

        CredentialItem.AuthenticatedUserName authenticatedUserName = new CredentialItem.AuthenticatedUserName();
        CredentialItem.AuthenticatedUserEmail authenticatedUserEmail = new CredentialItem.AuthenticatedUserEmail();
        boolean isCredentialsPresent = false;
        for (CredentialsProvider cp : credentialsProviders) {
            if (isCredentialsPresent = cp.getUser(remoteOriginURL, authenticatedUserName, authenticatedUserEmail)) {
                break;
            }
        }

        if (isCredentialsPresent) {
            committer = DtoFactory.getInstance().createDto(GitUser.class)
                                  .withName(authenticatedUserName.getValue())
                                  .withEmail(authenticatedUserEmail.getValue());
        } else {
            committer = user;
        }

        nativeGit.createConfig().setUser(committer).saveUser();
        CommitCommand command = nativeGit.createCommitCommand();
        command.setAuthor(committer);
        command.setCommitter(committer);
        command.setAll(request.isAll());
        command.setAmend(request.isAmend());
        command.setMessage(request.getMessage());
        Revision revision = DtoFactory.getInstance().createDto(Revision.class);
        try {
            command.execute();
            LogCommand log = nativeGit.createLogCommand();
            Revision rev = log.execute().get(0);
            rev.setBranch(getCurrentBranch());
            return rev;
        } catch (Exception e) {
            revision.setMessage(e.getMessage());
        }
        return revision;
    }

    @Override
    public DiffPage diff(DiffRequest request) throws GitException {
        return new NativeGitDiffPage(request, nativeGit);
    }

    @Override
    public void fetch(FetchRequest request) throws GitException {
        FetchCommand fetchCommand;
        String url;
        //get url
        try {
            url = nativeGit.createRemoteListCommand()
                           .setRemoteName(request.getRemote())
                           .execute()
                           .get(0)
                           .getUrl();
        } catch (GitException ignored) {
            url = request.getRemote();
        }
        String key;
        //try to store key
        if ((key = keysManager.storeKeyIfNeed(url)) != null) {
            fetchCommand = nativeGit.createFetchCommand(key);
        } else {
            fetchCommand = nativeGit.createFetchCommand();
        }
        fetchCommand.setRemote(url)
                    .setPrune(request.isRemoveDeletedRefs())
                    .setRefSpec(request.getRefSpec())
                    .setTimeout(request.getTimeout());
        executeWithCredentials(fetchCommand, url);
    }

    @Override
    public GitConnection init(InitRequest request) throws GitException {
        if (request.getWorkingDir() != null) {
            nativeGit.setRepository(new File(request.getWorkingDir()));
        }
        if (request.isBare()) {
            File dotGit = new File(request.getWorkingDir(), ".git");
            if (!dotGit.exists()) {
                dotGit.mkdir();
            }
            nativeGit.setRepository(dotGit);
        }
        if (!nativeGit.getRepository().exists()) {
            throw new GitException("Working folder " + nativeGit.getRepository() + " not exists . ");
        }
        InitCommand initCommand = nativeGit.createInitCommand();
        initCommand.setBare(request.isBare());
        initCommand.execute();
        nativeGit.createConfig().setUser(user).saveUser();
        //make initial commit.
        if (!request.isBare() && request.isInitCommit()) {
            try {
                nativeGit.createAddCommand()
                         .setFilePattern(new ArrayList<String>(Arrays.asList(".")))
                         .execute();
                nativeGit.createCommitCommand()
                         .setMessage("init")
                         .execute();
            } catch (GitException ignored) {
                //if nothing to commit
            }
        }
        return new NativeGitConnection(nativeGit.getRepository(), user, keysManager, credentialsLoader, credentialsProviders);
    }

    @Override
    public LogPage log(LogRequest request) throws GitException {
        return new LogPage(nativeGit.createLogCommand().execute());
    }

    @Override
    public MergeResult merge(MergeRequest request) throws GitException {
        if (getBranchRef(request.getCommit()) == null) {
            throw new GitException("Invalid reference to commit for merge " + request.getCommit());
        }
        return nativeGit.createMergeCommand().setCommit(request.getCommit()).execute();
    }

    @Override
    public void mv(MoveRequest request) throws GitException {
        nativeGit.createMoveCommand()
                 .setSource(request.getSource())
                 .setTarget(request.getTarget())
                 .execute();
    }

    @Override
    public void pull(PullRequest request) throws GitException {
        PullCommand pullCommand;
        String url;
        //get url
        try {
            url = nativeGit.createRemoteListCommand()
                           .setRemoteName(request.getRemote())
                           .execute()
                           .get(0)
                           .getUrl();
        } catch (GitException ignored) {
            url = request.getRemote();
        }
        String key;
        //try to store key
        if ((key = keysManager.storeKeyIfNeed(url)) != null) {
            pullCommand = nativeGit.createPullCommand(key);
        } else {
            pullCommand = nativeGit.createPullCommand();
        }
        pullCommand.setRemote(url);
        pullCommand.setRefSpec(request.getRefSpec())
                   .setTimeout(request.getTimeout());
        executeWithCredentials(pullCommand, url);
        if (pullCommand.getOutputMessage().toLowerCase().contains("already up-to-date")) {
            throw new AlreadyUpToDateException();
        }
    }

    @Override
    public void push(PushRequest request) throws GitException {
        PushCommand pushCommand;
        String url;
        //get url
        try {
            url = nativeGit.createRemoteListCommand()
                           .setRemoteName(request.getRemote())
                           .execute()
                           .get(0)
                           .getUrl();
        } catch (GitException ignored) {
            url = request.getRemote();
        }
        String key;
        //try to store ssh key if it is ssh or git url
        if ((key = keysManager.storeKeyIfNeed(url)) != null) {
            pushCommand = nativeGit.createPushCommand(key);
        } else {
            pushCommand = nativeGit.createPushCommand();
        }
        pushCommand.setRemote(request.getRemote()).setForce(request.isForce())
                   .setRefSpec(request.getRefSpec())
                   .setTimeout(request.getTimeout());
        executeWithCredentials(pushCommand, url);
        if (pushCommand.getOutputMessage().toLowerCase().contains("everything up-to-date")) {
            throw new AlreadyUpToDateException("Everything up-to-date");
        }
    }

    @Override
    public void remoteAdd(RemoteAddRequest request) throws GitException {
        nativeGit.createRemoteAddCommand()
                 .setName(request.getName())
                 .setUrl(request.getUrl())
                 .setBranches(request.getBranches())
                 .execute();
    }

    @Override
    public void remoteDelete(String name) throws GitException {
        nativeGit.createRemoteDeleteCommand().setName(name).execute();
    }

    @Override
    public List<Remote> remoteList(RemoteListRequest request) throws GitException {
        RemoteListCommand remoteListCommand = nativeGit.createRemoteListCommand();
        return remoteListCommand.setRemoteName(request.getRemote()).execute();
    }

    @Override
    public void remoteUpdate(RemoteUpdateRequest request) throws GitException {
        nativeGit.createRemoteUpdateCommand()
                 .setRemoteName(request.getName())
                 .setAddUrl(request.getAddUrl())
                 .setBranchesToAdd(request.getBranches())
                 .setAddBranches(request.isAddBranches())
                 .setAddPushUrl(request.getAddPushUrl())
                 .setRemovePushUrl(request.getRemovePushUrl())
                 .setRemoveUrl(request.getRemoveUrl())
                 .execute();
    }

    @Override
    public void reset(ResetRequest request) throws GitException {
        nativeGit.createResetCommand()
                 .setMode(request.getType().getValue())
                 .setCommit(request.getCommit())
                 .execute();
    }

    @Override
    public void rm(RmRequest request) throws GitException {
        nativeGit.createRemoveCommand().setListOfFiles(request.getFiles()).execute();
    }

    @Override
    public Status status(boolean shortFormat) throws GitException {
        return new NativeGitStatusImpl(getCurrentBranch(), nativeGit, shortFormat);
    }

    @Override
    public Tag tagCreate(TagCreateRequest request) throws GitException {
        return nativeGit.createTagCreateCommand().setName(request.getName())
                        .setCommit(request.getCommit())
                        .setMessage(request.getMessage())
                        .setForce(request.isForce())
                        .execute();
    }

    @Override
    public void tagDelete(TagDeleteRequest request) throws GitException {
        nativeGit.createTagDeleteCommand().setName(request.getName()).execute();
    }

    @Override
    public List<Tag> tagList(TagListRequest request) throws GitException {
        return nativeGit.createTagListCommand().setPattern(request.getPattern()).execute();
    }

    @Override
    public GitUser getUser() {
        return user;
    }

    @Override
    public List<GitUser> getCommiters() throws GitException {
        List<GitUser> users = new LinkedList<>();
        List<Revision> revList = nativeGit.createLogCommand().execute();
        for (Revision rev : revList) {
            users.add(rev.getCommitter());
        }
        return users;
    }

    @Override
    public void close() {
        //do not need to do anything
    }

    /** @return NativeGit for this connection */
    public NativeGit getNativeGit() {
        return nativeGit;
    }

    /**
     * Gets current branch name.
     *
     * @return name of current branch or <code>null</code> if current branch not exists
     * @throws GitException
     *         if any error occurs
     */
    public String getCurrentBranch() throws GitException {
        BranchListCommand command = nativeGit.createBranchListCommand();
        command.execute();
        String branchName = null;
        for (String outLine : command.getOutput()) {
            if (outLine.indexOf('*') != -1) {
                branchName = outLine.substring(2);
            }
        }
        return branchName;
    }

    /**
     * Executes git command with credentials.
     *
     * @param command
     *         command that will be executed
     * @param url
     *         given URL
     * @throws GitException
     *         when it is not possible to store credentials or
     *         authentication failed or command execution failed
     */
    public void executeWithCredentials(GitCommand command, String url) throws GitException {
        //create empty credentials
        CredentialItem.Username username = new CredentialItem.Username();
        username.setValue("");
        CredentialItem.Password password = new CredentialItem.Password();
        password.setValue("");
        //set up empty credentials
        command.setAskPassScriptPath(credentialsLoader.createGitAskPassScript(username, password).toString());
        try {
            //after failed clone, git will remove directory
            if (!nativeGit.getRepository().exists()) {
                nativeGit.getRepository().mkdirs();
            }
            command.execute();
        } catch (GitException e) {
            if (isOperationNeedAuth(e.getMessage())) {
                //try to search available credentials and execute command with it
                command.setAskPassScriptPath(credentialsLoader.findCredentialsAndCreateGitAskPassScript(url).toString());
                try {
                    //after failed clone, git will remove directory
                    if (!nativeGit.getRepository().exists()) {
                        nativeGit.getRepository().mkdirs();
                    }
                    command.execute();
                } catch (GitException inner) {
                    //if not authorized again make runtime exception
                    if (isOperationNeedAuth(inner.getMessage())) {
                        throw new NotAuthorizedException();
                    } else {
                        throw inner;
                    }
                }
            } else {
                throw e;
            }
        }
    }

    /** Check if error message from git output corresponding authenticate issue. */
    private boolean isOperationNeedAuth(String errorMessage) {
        return authErrorPattern.matcher(errorMessage).find();
    }

    /**
     * Gets branch ref by branch name.
     *
     * @param branchName
     *         existing git branch name
     * @return ref to the branch
     * @throws GitException
     *         when it is not possible to get branchName ref
     */
    private String getBranchRef(String branchName) throws GitException {
        EmptyGitCommand command = new EmptyGitCommand(nativeGit.getRepository());
        command.setNextParameter("show-ref").setNextParameter(branchName).execute();
        if (command.getOutputMessage().length() > 0) {
            return command.getOutputMessage().split(" ")[1];
        } else {
            return null;
        }
    }
}
