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
package org.exoplatform.ide.git.server.nativegit;


import org.exoplatform.ide.git.server.*;
import org.exoplatform.ide.git.server.nativegit.commands.*;
import org.exoplatform.ide.git.shared.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * Native implementation of GitConnection
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class NativeGitConnection implements GitConnection {

    private final static Logger LOG = LoggerFactory.getLogger(NativeGitConnection.class);
    private final NativeGit         nativeGit;
    private final CredentialsLoader credentialsLoader;
    private       GitUser           user;
    private       SshKeysManager    keysManager;

    /**
     * @param repository
     *         directory where commands will be invoked
     * @param user
     *         git user
     * @param keysManager
     *         manager for ssh keys. If it is null default ssh will be used;
     * @param credentialsLoader
     *         loader for credentials
     * @throws GitException
     *         when some error occurs
     */
    public NativeGitConnection(File repository, GitUser user, SshKeysManager keysManager,
                               CredentialsLoader credentialsLoader) throws GitException {
        this.user = user;
        this.keysManager = keysManager;
        this.credentialsLoader = credentialsLoader;
        nativeGit = new NativeGit(repository);
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#add(org.exoplatform.ide.git.shared.AddRequest) */
    @Override
    public void add(AddRequest request) throws GitException {
        AddCommand command = nativeGit.createAddCommand();
        command.setFilePattern(request.getFilepattern() == null ?
                               AddRequest.DEFAULT_PATTERN :
                               request.getFilepattern());
        command.setUpdate(request.isUpdate());
        command.execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#branchCheckout(org.exoplatform.ide.git.shared.BranchCheckoutRequest) */
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

    /** @see org.exoplatform.ide.git.server.GitConnection#branchCreate(org.exoplatform.ide.git.shared.BranchCreateRequest) */
    @Override
    public Branch branchCreate(BranchCreateRequest request) throws GitException {
        BranchCreateCommand branchCreateCommand = nativeGit.createBranchCreateCommand();
        branchCreateCommand.setBranchName(request.getName())
                           .setStartPoint(request.getStartPoint())
                           .execute();
        return new Branch(getBranchRef(request.getName()), false, request.getName(), false);
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#branchDelete(org.exoplatform.ide.git.shared.BranchDeleteRequest) */
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

    /** @see org.exoplatform.ide.git.server.GitConnection#branchRename(String oldName, String newName) */
    @Override
    public void branchRename(String oldName, String newName) throws GitException {
        nativeGit.createBranchRenameCommand().setNames(oldName, newName).execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#branchList(org.exoplatform.ide.git.shared.BranchListRequest) */
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

    /** @see org.exoplatform.ide.git.server.GitConnection#clone(org.exoplatform.ide.git.shared.CloneRequest) */
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
        return new NativeGitConnection(repository, user, keysManager, credentialsLoader);
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#commit(org.exoplatform.ide.git.shared.CommitRequest) */
    @Override
    public Revision commit(CommitRequest request) throws GitException {
        nativeGit.createConfig().setUser(user).saveUser();
        CommitCommand command = nativeGit.createCommitCommand();
        command.setAuthor(user);
        command.setCommitter(user);
        command.setAll(request.isAll());
        command.setAmend(request.isAmend());
        command.setMessage(request.getMessage());
        Revision revision = new Revision();
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

    /** @see org.exoplatform.ide.git.server.GitConnection#diff(org.exoplatform.ide.git.shared.DiffRequest) */
    @Override
    public DiffPage diff(DiffRequest request) throws GitException {
        return new NativeGitDiffPage(request, nativeGit);
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#fetch(org.exoplatform.ide.git.shared.FetchRequest) */
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

    /** @see org.exoplatform.ide.git.server.GitConnection#init(org.exoplatform.ide.git.shared.InitRequest) */
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
        if (!request.isBare()) {
            try {
                nativeGit.createAddCommand()
                         .setFilePattern(new String[]{"."})
                         .execute();
                nativeGit.createCommitCommand()
                         .setMessage("init")
                         .execute();
            } catch (GitException ignored) {
                //if nothing to commit
            }
        }
        return new NativeGitConnection(nativeGit.getRepository(), user, keysManager, credentialsLoader);
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#log(org.exoplatform.ide.git.shared.LogRequest) */
    @Override
    public LogPage log(LogRequest request) throws GitException {
        return new LogPage(nativeGit.createLogCommand().execute());
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#merge(org.exoplatform.ide.git.shared.MergeRequest) */
    @Override
    public MergeResult merge(MergeRequest request) throws GitException {
        if (getBranchRef(request.getCommit()) == null) {
            throw new GitException("Invalid reference to commit for merge " + request.getCommit());
        }
        return nativeGit.createMergeCommand().setCommit(request.getCommit()).execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#mv(org.exoplatform.ide.git.shared.MoveRequest) */
    @Override
    public void mv(MoveRequest request) throws GitException {
        nativeGit.createMoveCommand()
                 .setSource(request.getSource())
                 .setTarget(request.getTarget())
                 .execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#pull(org.exoplatform.ide.git.shared.PullRequest) */
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

    /** @see org.exoplatform.ide.git.server.GitConnection#push(org.exoplatform.ide.git.shared.PushRequest) */
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
        pushCommand.setRemote(url).setForce(request.isForce())
                   .setRefSpec(request.getRefSpec())
                   .setTimeout(request.getTimeout());
        executeWithCredentials(pushCommand, url);
        if (pushCommand.getOutputMessage().toLowerCase().contains("everything up-to-date")) {
            throw new AlreadyUpToDateException("Everything up-to-date");
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#remoteAdd(org.exoplatform.ide.git.shared.RemoteAddRequest) */
    @Override
    public void remoteAdd(RemoteAddRequest request) throws GitException {
        nativeGit.createRemoteAddCommand()
                 .setName(request.getName())
                 .setUrl(request.getUrl())
                 .setBranches(request.getBranches())
                 .execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#remoteDelete(java.lang.String) */
    @Override
    public void remoteDelete(String name) throws GitException {
        nativeGit.createRemoteDeleteCommand().setName(name).execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#remoteList(org.exoplatform.ide.git.shared.RemoteListRequest) */
    @Override
    public List<Remote> remoteList(RemoteListRequest request) throws GitException {
        RemoteListCommand remoteListCommand = nativeGit.createRemoteListCommand();
        return remoteListCommand.setRemoteName(request.getRemote()).execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#remoteUpdate(org.exoplatform.ide.git.shared.RemoteUpdateRequest) */
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

    /** @see org.exoplatform.ide.git.server.GitConnection#reset(org.exoplatform.ide.git.shared.ResetRequest) */
    @Override
    public void reset(ResetRequest request) throws GitException {
        nativeGit.createResetCommand()
                 .setMode(request.getType().toString())
                 .setCommit(request.getCommit())
                 .execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#rm(org.exoplatform.ide.git.shared.RmRequest) */
    @Override
    public void rm(RmRequest request) throws GitException {
        nativeGit.createRemoveCommand().setListOfFiles(request.getFiles()).execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#status(boolean) */
    @Override
    public Status status(boolean shortFormat) throws GitException {
        return new NativeGitStatusImpl(getCurrentBranch(), nativeGit, shortFormat);
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#tagCreate(org.exoplatform.ide.git.shared.TagCreateRequest) */
    @Override
    public Tag tagCreate(TagCreateRequest request) throws GitException {
        return nativeGit.createTagCreateCommand().setName(request.getName())
                        .setCommit(request.getCommit())
                        .setMessage(request.getMessage())
                        .setForce(request.isForce())
                        .execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#tagDelete(org.exoplatform.ide.git.shared.TagDeleteRequest) */
    @Override
    public void tagDelete(TagDeleteRequest request) throws GitException {
        nativeGit.createTagDeleteCommand().setName(request.getName()).execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#tagList(org.exoplatform.ide.git.shared.TagListRequest) */
    @Override
    public List<Tag> tagList(TagListRequest request) throws GitException {
        return nativeGit.createTagListCommand().setPattern(request.getPattern()).execute();
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#getUser() */
    @Override
    public GitUser getUser() {
        return user;
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#getCommiters() */
    @Override
    public List<GitUser> getCommiters() throws GitException {
        List<GitUser> users = new LinkedList<>();
        List<Revision> revList = nativeGit.createLogCommand().execute();
        for (Revision rev : revList) {
            users.add(rev.getCommitter());
        }
        return users;
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#close() */
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
            if (!nativeGit.getRepository().exists()) {
                nativeGit.getRepository().mkdirs();
            }
            //if not authorized
            if (e.getMessage().toLowerCase().startsWith("fatal: authentication failed")) {
                //try to search available credentials and execute command with it
                command.setAskPassScriptPath(credentialsLoader.findCredentialsAndCreateGitAskPassScript(url).toString());
                try {
                    command.execute();
                } catch (GitException inner) {
                    if (!nativeGit.getRepository().exists()) {
                        nativeGit.getRepository().mkdirs();
                    }
                    //if not authorized again make runtime exception
                    if (inner.getMessage().toLowerCase().startsWith("fatal: authentication failed")) {
                        throw new NotAuthorizedException("not authorized");
                    } else {
                        throw inner;
                    }
                }
            } else if (e.getMessage().toLowerCase().contains("please make sure you have the correct access rights")) {
                //in case that user tries to clone repository via ssh and he doesn't have ssh key
                throw new GitException("SSH key not found or you have not rights to access this repository.");
            } else {
                throw e;
            }
        }
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
