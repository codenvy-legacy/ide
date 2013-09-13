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
package org.exoplatform.ide.git.server.jgit;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryState;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.URIish;
import org.exoplatform.ide.git.server.DiffPage;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.server.LogPage;
import org.exoplatform.ide.git.server.StatusImpl;
import org.exoplatform.ide.git.shared.AddRequest;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;
import org.exoplatform.ide.git.shared.BranchCreateRequest;
import org.exoplatform.ide.git.shared.BranchDeleteRequest;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.CommitRequest;
import org.exoplatform.ide.git.shared.DiffRequest;
import org.exoplatform.ide.git.shared.FetchRequest;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.LogRequest;
import org.exoplatform.ide.git.shared.MergeRequest;
import org.exoplatform.ide.git.shared.MergeResult;
import org.exoplatform.ide.git.shared.MoveRequest;
import org.exoplatform.ide.git.shared.PullRequest;
import org.exoplatform.ide.git.shared.PushRequest;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.git.shared.RemoteAddRequest;
import org.exoplatform.ide.git.shared.RemoteListRequest;
import org.exoplatform.ide.git.shared.RemoteUpdateRequest;
import org.exoplatform.ide.git.shared.ResetRequest;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.git.shared.RmRequest;
import org.exoplatform.ide.git.shared.Status;
import org.exoplatform.ide.git.shared.Tag;
import org.exoplatform.ide.git.shared.TagCreateRequest;
import org.exoplatform.ide.git.shared.TagDeleteRequest;
import org.exoplatform.ide.git.shared.TagListRequest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: JGitConnection.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class JGitConnection implements GitConnection {
    // -------------------------
    private final Repository repository;

    private final GitUser    user;

    /**
     * @param repository the JGit repository
     * @param user the user
     */
    JGitConnection(Repository repository, GitUser user) {
        this.repository = repository;
        this.user = user;
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#add(org.exoplatform.ide.git.shared.AddRequest) */
    @Override
    public void add(AddRequest request) throws GitException {
        AddCommand addCommand = new Git(repository).add().setUpdate(request.isUpdate());

        String[] filepattern = request.getFilepattern();
        if (filepattern == null) {
            filepattern = AddRequest.DEFAULT_PATTERN;
        }
        for (int i = 0; i < filepattern.length; i++) {
            addCommand.addFilepattern(filepattern[i]);
        }

        try {
            addCommand.call();
        } catch (NoFilepatternException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (GitAPIException e) {
            throw new GitException(e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#branchCheckout(org.exoplatform.ide.git.shared.BranchCheckoutRequest) */
    @Override
    public void branchCheckout(BranchCheckoutRequest request) throws GitException {
        CheckoutCommand checkoutCommand = new Git(repository).checkout();
        String startPoint = request.getStartPoint();
        String cleanName = cleanRemoteName(request.getName());
        if (startPoint != null) {
            checkoutCommand.setStartPoint(startPoint);
        }
        checkoutCommand.setCreateBranch(request.isCreateNew());
        checkoutCommand.setName(cleanName);
        checkoutCommand.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM);
        try {
            checkoutCommand.call();
        } catch (GitAPIException e) {
            if (e.getMessage().endsWith("already exists")) {
                throw new IllegalArgumentException("fatal: A branch named '" + cleanName + "' already exists.");
            }
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#branchCreate(org.exoplatform.ide.git.shared.BranchCreateRequest) */
    @Override
    public Branch branchCreate(BranchCreateRequest request) throws GitException {
        CreateBranchCommand createBranchCommand = new Git(repository).branchCreate().setName(request.getName());
        String start = request.getStartPoint();
        if (start != null) {
            createBranchCommand.setStartPoint(start);
        }
        try {
            Ref brRef = createBranchCommand.call();
            String refName = brRef.getName();

            return new Branch(refName, false, Repository.shortenRefName(refName), false);
        } catch (GitAPIException e) {
            throw new GitException(e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#branchDelete(org.exoplatform.ide.git.shared.BranchDeleteRequest) */
    @Override
    public void branchDelete(BranchDeleteRequest request) throws GitException {
        try {
            new Git(repository).branchDelete().setBranchNames(request.getName()).setForce(request.isForce()).call();
        } catch (GitAPIException e) {
            throw new GitException(e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#branchRename(String oldName, String newName) */
    @Override
    public void branchRename(String oldName, String newName) throws GitException {
        try {
            new Git(repository).branchRename().setOldName(oldName).setNewName(newName).call();
        } catch (GitAPIException e) {
            throw new GitException(e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#branchList(org.exoplatform.ide.git.shared.BranchListRequest) */
    @Override
    public List<Branch> branchList(BranchListRequest request) throws GitException {
        String listMode = request.getListMode();
        if (listMode != null
            && !(listMode.equals(BranchListRequest.LIST_ALL) || listMode.equals(BranchListRequest.LIST_REMOTE))) {
            throw new IllegalArgumentException("Unsupported list mode '" + listMode + "'. Must be either 'a' or 'r'. ");
        }

        ListBranchCommand listBranchCommand = new Git(repository).branchList();
        if (listMode != null) {
            if (listMode.equals(BranchListRequest.LIST_ALL)) {
                listBranchCommand.setListMode(ListMode.ALL);
            } else if (listMode.equals(BranchListRequest.LIST_REMOTE)) {
                listBranchCommand.setListMode(ListMode.REMOTE);
            }
        }
        List<Ref> refs;
        try {
            refs = listBranchCommand.call();
        } catch (GitAPIException err) {
            throw new GitException(err);
        }
        String current = null;
        try {
            Ref headRef = repository.getRef(Constants.HEAD);
            if (!(headRef == null || Constants.HEAD.equals(headRef.getLeaf().getName()))) {
                current = headRef.getLeaf().getName();
            }
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        }

        List<Branch> branches = new ArrayList<Branch>();
        if (current == null) {
            branches.add(new Branch("(no branch)", true, "(no branch)", false));
        }

        for (Ref brRef : refs) {
            String refName = brRef.getName();
            Branch branch = new Branch(refName, refName.equals(current), Repository.shortenRefName(refName),
                                       brRef.getName().startsWith("refs/remotes"));
            branches.add(branch);
        }
        return branches;
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#clone(org.exoplatform.ide.git.shared.CloneRequest) */
    public GitConnection clone(CloneRequest request) throws GitException {
        try {
            if (request.getRemoteName() == null) {
                request.setRemoteName("origin");
            }
            if (request.getWorkingDir() == null) {
                request.setWorkingDir(repository.getWorkTree().getCanonicalPath());
            }
            CloneCommand cloneCom = Git.cloneRepository();
            cloneCom.setRemote(request.getRemoteName());
            cloneCom.setURI(request.getRemoteUri());
            cloneCom.setDirectory(new File(request.getWorkingDir()));
            if (request.getBranchesToFetch() != null) {
                cloneCom.setBranchesToClone(Arrays.asList(request.getBranchesToFetch()));
            }
            else {
                cloneCom.setCloneAllBranches(true);
            }
            Repository repo = cloneCom.call().getRepository();
            StoredConfig config = repo.getConfig();
            GitUser gitUser = getUser();
            if (gitUser != null) {
                config.setString("user", null, "name", gitUser.getName());
                config.setString("user", null, "email", gitUser.getEmail());
            }

            config.save();
            return new JGitConnection(repo, this.user);

        } catch (InvalidRemoteException e) {
            throw new GitException(e);
        } catch (TransportException e) {
            throw new GitException(e.getMessage());
        } catch (GitAPIException e) {
            throw new GitException(e);
        } catch (IOException e) {
            throw new GitException(e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#commit(org.exoplatform.ide.git.shared.CommitRequest) */
    @Override
    public Revision commit(CommitRequest request) throws GitException {
        try {
            if (!repository.getRepositoryState().canCommit()) {
                Revision rev = new Revision();
                rev.setMessage("Commit is not possible because repository state is '"
                               + repository.getRepositoryState().getDescription() + "'");
                return rev;
            }

            if (request.isAmend() && !repository.getRepositoryState().canAmend()) {
                Revision rev = new Revision();
                rev.setMessage("Amend is not possible because repository state is '"
                               + repository.getRepositoryState().getDescription() + "'");
                return rev;
            }

            StatusImpl stat = (StatusImpl)status(false);
            if (stat.getAdded().isEmpty() && stat.getChanged().isEmpty() && stat.getRemoved().isEmpty()) {
                if (request.isAll()) {
                    if (stat.getMissing().isEmpty() && stat.getModified().isEmpty()) {
                        Revision rev = new Revision();
                        rev.setMessage(stat.createString(false));
                        return rev;
                    }
                } else {
                    if (stat.getMissing().isEmpty() && stat.getModified().isEmpty()) {
                        Revision rev = new Revision();
                        rev.setMessage(stat.createString(false));
                        return rev;
                    } else {
                        Revision rev = new Revision();
                        rev.setMessage(stat.createString(false));
                        return rev;
                    }
                }
            }

            CommitCommand commitCommand = new Git(repository).commit();

            String configName = repository.getConfig().getString("user", null, "name");
            String configEmail = repository.getConfig().getString("user", null, "email");

            String gitName = getUser().getName();
            String gitEmail = getUser().getEmail();

            String comitterName = configName != null ? configName : gitName;
            String comitterEmail = configEmail != null ? configEmail : gitEmail;

            commitCommand.setCommitter(comitterName, comitterEmail);
            commitCommand.setAuthor(comitterName, comitterEmail);
            commitCommand.setMessage(request.getMessage());
            commitCommand.setAll(request.isAll());
            commitCommand.setAmend(request.isAmend());

            RevCommit result = commitCommand.call();

            return new Revision(getCurrentBranch(), result.getId().getName(), result.getFullMessage(),
                                (long)result.getCommitTime() * 1000, new GitUser(comitterName, comitterEmail));
        } catch (GitAPIException e) {
            throw new GitException(e);
        } catch (UnsupportedEncodingException e) {
            throw new GitException(e);
        } catch (IOException e) {
            throw new GitException(e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#diff(org.exoplatform.ide.git.shared.DiffRequest) */
    @Override
    public DiffPage diff(DiffRequest request) throws GitException {
        return new JGitDiffPage(request, repository);
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#fetch(org.exoplatform.ide.git.shared.FetchRequest) */
    @Override
    public void fetch(FetchRequest request) throws GitException {
        try {
            List<RefSpec> fetchRefSpecs = null;
            String[] refSpec = request.getRefSpec();
            if (refSpec != null && refSpec.length > 0) {
                fetchRefSpecs = new ArrayList<RefSpec>(refSpec.length);
                for (int i = 0; i < refSpec.length; i++) {
                    RefSpec fetchRefSpec = (refSpec[i].indexOf(':') < 0) //
                        ? new RefSpec(Constants.R_HEADS + refSpec[i] + ":") //
                        : new RefSpec(refSpec[i]);
                    fetchRefSpecs.add(fetchRefSpec);
                }
            }

            FetchCommand fetchCommand = new Git(repository).fetch();

            String remote = request.getRemote();
            if (remote != null) {
                fetchCommand.setRemote(remote);
            }
            if (fetchRefSpecs != null) {
                fetchCommand.setRefSpecs(fetchRefSpecs);
            }
            int timeout = request.getTimeout();
            if (timeout > 0) {
                fetchCommand.setTimeout(timeout);
            }
            fetchCommand.setRemoveDeletedRefs(request.isRemoveDeletedRefs());

            fetchCommand.call();
        } catch (GitAPIException e) {
            throw new GitException(e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#init(org.exoplatform.ide.git.shared.InitRequest) */
    @Override
    public GitConnection init(InitRequest request) throws GitException {
        File workDir = repository.getWorkTree();
        if (!workDir.exists()) {
            throw new GitException("Working folder " + workDir + " not exists . ");
        }

        boolean bare = request.isBare();

        try {
            repository.create(bare);

            if (!bare) {
                try {
                    Git git = new Git(repository);
                    git.add().addFilepattern(".").call();
                    git.commit().setMessage("init").call();
                } catch (GitAPIException e) {
                    throw new GitException(e);
                }
            }
            GitUser gitUser = getUser();
            if (gitUser != null) {
                StoredConfig config = repository.getConfig();
                config.setString("user", null, "name", gitUser.getName());
                config.setString("user", null, "email", gitUser.getEmail());
                config.save();
            }
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        }
        return this;
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#log(org.exoplatform.ide.git.shared.LogRequest) */
    @Override
    public LogPage log(LogRequest request) throws GitException {
        LogCommand logCommand = new Git(repository).log();
        try {
            Iterator<RevCommit> revIterator = logCommand.call().iterator();
            List<Revision> commits = new ArrayList<Revision>();
            while (revIterator.hasNext()) {
                RevCommit commit = revIterator.next();
                PersonIdent committerIdentity = commit.getCommitterIdent();
                commits.add(new Revision(commit.getId().getName(), commit.getFullMessage(),
                                         (long)commit.getCommitTime() * 1000, new GitUser(committerIdentity.getName(),
                                                                                          committerIdentity
                                                                                                           .getEmailAddress())));
            }
            return new LogPage(commits);
        } catch (GitAPIException e) {
            throw new GitException(e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#log(org.exoplatform.ide.git.shared.LogRequest) */
    @Override
    public List<GitUser> getCommiters() throws GitException {
        List<GitUser> gitUsers = new ArrayList<GitUser>();
        try {
            LogCommand logCommand = new Git(repository).log();
            Iterator<RevCommit> revIterator = logCommand.call().iterator();
            while (revIterator.hasNext()) {
                RevCommit commit = revIterator.next();
                PersonIdent committerIdentity = commit.getCommitterIdent();
                GitUser gitUser = new GitUser(committerIdentity.getName(), committerIdentity.getEmailAddress());
                if (!gitUsers.contains(gitUser)) {
                    gitUsers.add(gitUser);
                }
            }
        } catch (GitAPIException e) {
            throw new GitException(e);
        }

        return gitUsers;
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#merge(org.exoplatform.ide.git.shared.MergeRequest) */
    @Override
    public MergeResult merge(MergeRequest request) throws GitException {
        try {
            Ref ref = repository.getRef(request.getCommit());
            if (ref == null) {
                throw new IllegalArgumentException("Invalid reference to commit for merge " + request.getCommit());
            }
            org.eclipse.jgit.api.MergeResult jgitMergeResult = new Git(repository).merge().include(ref).call();
            return new JGitMergeResult(jgitMergeResult);
        } catch (CheckoutConflictException e) {
            org.eclipse.jgit.api.MergeResult jgitMergeResult = new org.eclipse.jgit.api.MergeResult(e.getConflictingPaths());
            return new JGitMergeResult(jgitMergeResult);
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        } catch (GitAPIException e) {
            throw new GitException(e.getMessage(), e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#mv(org.exoplatform.ide.git.shared.MoveRequest) */
    @Override
    public void mv(MoveRequest request) throws GitException {
        throw new RuntimeException("Not implemented yet. ");
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#pull(org.exoplatform.ide.git.shared.PullRequest) */
    @Override
    public void pull(PullRequest request) throws GitException {
        try {
            if (repository.getRepositoryState().equals(RepositoryState.MERGING)) {
                throw new GitException("Pull request cannot be performed because repository state is 'MERGING'");
            }
            String fullBranch = repository.getFullBranch();
            if (!fullBranch.startsWith(Constants.R_HEADS)) {
                throw new DetachedHeadException("HEAD is detached. Cannot pull. ");
            }

            String branch = fullBranch.substring(Constants.R_HEADS.length());

            StoredConfig config = repository.getConfig();
            String remote = request.getRemote();
            if (remote == null) {
                remote = config.getString(ConfigConstants.CONFIG_BRANCH_SECTION, branch, ConfigConstants.CONFIG_KEY_REMOTE);
                if (remote == null) {
                    remote = Constants.DEFAULT_REMOTE_NAME;
                }
            }

            String remoteBranch = null;
            RefSpec fetchRefSpecs = null;
            String refSpec = request.getRefSpec();
            if (refSpec != null) {
                fetchRefSpecs = (refSpec.indexOf(':') < 0) //
                    ? new RefSpec(Constants.R_HEADS + refSpec + ":" + fullBranch) //
                    : new RefSpec(refSpec);
                remoteBranch = fetchRefSpecs.getSource();
            } else {
                remoteBranch =
                               config.getString(ConfigConstants.CONFIG_BRANCH_SECTION, branch, ConfigConstants.CONFIG_KEY_MERGE);
            }

            if (remoteBranch == null) {
                String key = ConfigConstants.CONFIG_BRANCH_SECTION + "." + branch + "." + ConfigConstants.CONFIG_KEY_MERGE;
                throw new GitException("Remote branch is not specified in request and " + key
                                       + " in configuration is not set. ");
            }

            FetchCommand fetchCommand = new Git(repository).fetch();
            fetchCommand.setRemote(remote);
            if (fetchRefSpecs != null) {
                fetchCommand.setRefSpecs(fetchRefSpecs);
            }
            int timeout = request.getTimeout();
            if (timeout > 0) {
                fetchCommand.setTimeout(timeout);
            }

            FetchResult fetchResult = fetchCommand.call();

            Ref remoteBranchRef = fetchResult.getAdvertisedRef(remoteBranch);
            if (remoteBranchRef == null) {
                remoteBranchRef = fetchResult.getAdvertisedRef(Constants.R_HEADS + remoteBranch);
            }
            if (remoteBranchRef == null) {
                throw new GitException("Cannot get ref for remote branch " + remoteBranch + ". ");
            }
            org.eclipse.jgit.api.MergeResult res = new Git(repository).merge().include(remoteBranchRef).call();
            if (res.getMergeStatus().equals(org.eclipse.jgit.api.MergeResult.MergeStatus.ALREADY_UP_TO_DATE)) {
                throw new GitException(res.getMergeStatus().toString());
            }

            if (res.getConflicts() != null) {
                StringBuilder message = new StringBuilder("Merge conflict appeared in files:</br>");
                Map<String, int[][]> allConflicts = res.getConflicts();
                for (String path : allConflicts.keySet()) {
                    message.append(path + "</br>");
                }
                message.append("Automatic merge failed; fix conflicts and then commit the result.");
                throw new GitException(message.toString());
            }
        } catch (CheckoutConflictException e) {
            StringBuilder message =
                                    new StringBuilder(
                                                      "error: Your local changes to the following files would be overwritten by merge:</br>");
            for (String path : e.getConflictingPaths()) {
                message.append(path + "</br>");
            }
            message.append("Please, commit your changes before you can merge. Aborting.");
            throw new GitException(message.toString());
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        } catch (GitAPIException e) {
            throw new GitException(e.getMessage(), e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#push(org.exoplatform.ide.git.shared.PushRequest) */
    @Override
    public void push(PushRequest request) throws GitException {
        StringBuilder message = new StringBuilder();
        try {
            PushCommand pushCommand = new Git(repository).push();
            String remote = request.getRemote();
            if (request.getRemote() != null) {
                pushCommand.setRemote(remote);
            }

            String[] refSpec = request.getRefSpec();
            if (refSpec != null && refSpec.length > 0) {
                List<RefSpec> refSpecInst = new ArrayList<RefSpec>(refSpec.length);
                for (int i = 0; i < refSpec.length; i++) {
                    refSpecInst.add(new RefSpec(refSpec[i]));
                }
                pushCommand.setRefSpecs(refSpecInst);
            }

            pushCommand.setForce(request.isForce());

            int timeout = request.getTimeout();
            if (timeout > 0) {
                pushCommand.setTimeout(timeout);
            }

            Iterable<PushResult> list = pushCommand.call();
            for (PushResult pushResult : list) {
                Collection<RemoteRefUpdate> refUpdates = pushResult.getRemoteUpdates();
                for (RemoteRefUpdate remoteRefUpdate : refUpdates) {
                    if (!remoteRefUpdate.getStatus().equals(org.eclipse.jgit.transport.RemoteRefUpdate.Status.OK)) {

                        if (remoteRefUpdate.getStatus().equals(org.eclipse.jgit.transport.RemoteRefUpdate.Status.UP_TO_DATE)) {
                            message.append("Already up-to-date.");
                        }
                        else {
                            message.append("! [rejected] " + getCurrentBranch() + " -> " + request.getRefSpec()[0].split(":")[1]
                                           + " (non-fast-forward)\n");
                            message.append("error: failed to push some refs to " + request.getRemote() + "\n");
                            message.append("To prevent you from losing history, non-fast-forward updates were rejected\n");
                            message.append("Merge the remote changes (e.g. git pull) before pushing again.\n");
                        }
                        throw new GitException(message.toString());
                    }
                }
            }
        } catch (GitAPIException e) {
            throw new GitException(e.getMessage(), e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#remoteAdd(org.exoplatform.ide.git.shared.RemoteAddRequest) */
    @Override
    public void remoteAdd(RemoteAddRequest request) throws GitException {
        String remoteName = request.getName();
        if (remoteName == null || remoteName.length() == 0) {
            throw new IllegalArgumentException("Remote name required. ");
        }

        StoredConfig config = repository.getConfig();
        Set<String> remoteNames = config.getSubsections("remote");
        if (remoteNames.contains(remoteName)) {
            throw new IllegalArgumentException("Remote " + remoteName + " already exists. ");
        }

        String url = request.getUrl();
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("Remote url required. ");
        }

        RemoteConfig remoteConfig;
        try {
            remoteConfig = new RemoteConfig(config, remoteName);
        } catch (URISyntaxException e) {
            // Not happen since it is newly created remote.
            throw new GitException(e.getMessage(), e);
        }

        try {
            remoteConfig.addURI(new URIish(url));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Remote url " + url + " is invalid. ");
        }

        String[] branches = request.getBranches();
        if (branches != null) {
            for (int i = 0; i < branches.length; i++) {
                remoteConfig.addFetchRefSpec( //
                new RefSpec(Constants.R_HEADS + branches[i] + ":" + Constants.R_REMOTES + remoteName + "/" +
                            branches[i])
                                        .setForceUpdate(true));
            }
        } else {
            remoteConfig.addFetchRefSpec(new RefSpec(Constants.R_HEADS + "*" + ":" + Constants.R_REMOTES + remoteName
                                                     + "/*").setForceUpdate(true));
        }

        remoteConfig.update(config);

        try {
            config.save();
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#remoteDelete(java.lang.String) */
    @Override
    public void remoteDelete(String name) throws GitException {
        StoredConfig config = repository.getConfig();
        Set<String> remoteNames = config.getSubsections(ConfigConstants.CONFIG_KEY_REMOTE);
        if (!remoteNames.contains(name)) {
            throw new IllegalArgumentException("Remote " + name + " not found. ");
        }

        config.unsetSection(ConfigConstants.CONFIG_REMOTE_SECTION, name);
        Set<String> branches = config.getSubsections(ConfigConstants.CONFIG_BRANCH_SECTION);
        for (String branch : branches) {
            String r = config.getString(ConfigConstants.CONFIG_BRANCH_SECTION, branch, ConfigConstants.CONFIG_KEY_REMOTE);
            if (name.equals(r)) {
                config.unset(ConfigConstants.CONFIG_BRANCH_SECTION, branch, ConfigConstants.CONFIG_KEY_REMOTE);
                config.unset(ConfigConstants.CONFIG_BRANCH_SECTION, branch, ConfigConstants.CONFIG_KEY_MERGE);
                List<Branch> remoteBranches = branchList(new BranchListRequest("r"));
                for (Branch remoteBranch : remoteBranches) {
                    if (remoteBranch.getDisplayName().startsWith(name)) {
                        branchDelete(new BranchDeleteRequest(remoteBranch.getName(), true));
                    }
                    ;
                }
            }
        }

        try {
            config.save();
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#remoteList(org.exoplatform.ide.git.shared.RemoteListRequest) */
    @Override
    public List<Remote> remoteList(RemoteListRequest request) throws GitException {
        StoredConfig config = repository.getConfig();
        Set<String> remoteNames = new HashSet<String>(config.getSubsections(ConfigConstants.CONFIG_KEY_REMOTE));
        String remote = request.getRemote();

        if (remote != null && remoteNames.contains(remote)) {
            remoteNames.clear();
            remoteNames.add(remote);
        }

        boolean verbose = request.isVerbose();
        List<Remote> result = new ArrayList<Remote>(remoteNames.size());
        for (String rn : remoteNames) {
            if (verbose) {
                try {
                    List<URIish> uris = new RemoteConfig(config, rn).getURIs();
                    result.add(new Remote(rn, uris.isEmpty() ? null : uris.get(0).toString()));
                } catch (URISyntaxException e) {
                    throw new GitException(e.getMessage(), e);
                }
            } else {
                result.add(new Remote(rn, null));
            }
        }
        return result;
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#remoteUpdate(org.exoplatform.ide.git.shared.RemoteUpdateRequest) */
    @Override
    public void remoteUpdate(RemoteUpdateRequest request) throws GitException {
        String remoteName = request.getName();
        if (remoteName == null || remoteName.length() == 0) {
            throw new IllegalArgumentException("Remote name required. ");
        }

        StoredConfig config = repository.getConfig();
        Set<String> remoteNames = config.getSubsections(ConfigConstants.CONFIG_KEY_REMOTE);
        if (!remoteNames.contains(remoteName)) {
            throw new IllegalArgumentException("Remote " + remoteName + " not found. ");
        }

        RemoteConfig remoteConfig;
        try {
            remoteConfig = new RemoteConfig(config, remoteName);
        } catch (URISyntaxException e) {
            throw new GitException(e.getMessage(), e);
        }

        String[] tmp;

        tmp = request.getBranches();
        if (tmp != null && tmp.length > 0) {
            if (!request.isAddBranches()) {
                remoteConfig.setFetchRefSpecs(new ArrayList<RefSpec>());
                remoteConfig.setPushRefSpecs(new ArrayList<RefSpec>());
            } else {
                // Replace wildcard refspec if any.
                remoteConfig.removeFetchRefSpec(new RefSpec(Constants.R_HEADS + "*" + ":" + Constants.R_REMOTES
                                                            + remoteName + "/*").setForceUpdate(true));
                remoteConfig.removeFetchRefSpec(new RefSpec(Constants.R_HEADS + "*" + ":" + Constants.R_REMOTES
                                                            + remoteName + "/*"));
            }

            // Add new refspec.
            for (int i = 0; i < tmp.length; i++) {
                remoteConfig.addFetchRefSpec( //
                new RefSpec(
                            Constants.R_HEADS + tmp[i] + ":" + Constants.R_REMOTES + remoteName + "/" + tmp[i])
                                                                                                               .setForceUpdate(true));
            }
        }

        // Remove URLs first.
        tmp = request.getRemoveUrl();
        if (tmp != null) {
            for (int i = 0; i < tmp.length; i++) {
                try {
                    remoteConfig.removeURI(new URIish(tmp[i]));
                } catch (URISyntaxException e) {
                    // Ignore this error. Cannot remove invalid URL.
                }
            }
        }

        // Add new URLs.
        tmp = request.getAddUrl();
        if (tmp != null) {
            for (int i = 0; i < tmp.length; i++) {
                try {
                    remoteConfig.addURI(new URIish(tmp[i]));
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Remote url " + tmp[i] + " is invalid. ");
                }
            }
        }

        // Remove URLs for pushing.
        tmp = request.getRemovePushUrl();
        if (tmp != null) {
            for (int i = 0; i < tmp.length; i++) {
                try {
                    remoteConfig.removePushURI(new URIish(tmp[i]));
                } catch (URISyntaxException e) {
                    // Ignore this error. Cannot remove invalid URL.
                }
            }
        }

        // Add URLs for pushing.
        tmp = request.getAddPushUrl();
        if (tmp != null) {
            for (int i = 0; i < tmp.length; i++) {
                try {
                    remoteConfig.addPushURI(new URIish(tmp[i]));
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Remote push url " + tmp[i] + " is invalid. ");
                }
            }
        }

        remoteConfig.update(config);

        try {
            config.save();
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#reset(org.exoplatform.ide.git.shared.ResetRequest) */
    @Override
    public void reset(ResetRequest request) throws GitException {
        try {
            if (!repository.getRepositoryState().canResetHead()) {
                throw new GitException("Reset is not possible because repository state is" +
                                       repository.getRepositoryState().getDescription() + ".");
            }

            ResetCommand req = new Git(repository).reset();
            req.setRef(request.getCommit());

            if (request.getType().equals(ResetRequest.ResetType.HARD)) {
                req.setMode(ResetCommand.ResetType.HARD);
            }
            else if (request.getType().equals(ResetRequest.ResetType.KEEP)) {
                req.setMode(ResetCommand.ResetType.KEEP);
            }
            else if (request.getType().equals(ResetRequest.ResetType.MERGE)) {
                req.setMode(ResetCommand.ResetType.MERGE);
            }
            else if (request.getType().equals(ResetRequest.ResetType.MIXED)) {
                req.setMode(ResetCommand.ResetType.MIXED);
            }
            else if (request.getType().equals(ResetRequest.ResetType.SOFT)) {
                req.setMode(ResetCommand.ResetType.SOFT);
            }

            req.call();
        } catch (CheckoutConflictException e) {
            throw new GitException(e);
        } catch (GitAPIException e) {
            throw new GitException(e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#rm(org.exoplatform.ide.git.shared.RmRequest) */
    @Override
    public void rm(RmRequest request) throws GitException {
        String[] files = request.getFiles();
        RmCommand rmCommand = new Git(repository).rm();

        rmCommand.setCached(request.getCached());

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                rmCommand.addFilepattern(files[i]);
            }
        }
        try {
            rmCommand.call();
        } catch (NoFilepatternException e) {
            throw new IllegalArgumentException("File pattern may not be null or empty. ");
        } catch (GitAPIException e) {
            throw new GitException(e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#status(org.exoplatform.ide.git.shared.StatusRequest) */
    @Override
    public Status status(boolean shortFormat) throws GitException {
        try {
            String currentBranch = getCurrentBranch();
            org.eclipse.jgit.api.Status status = new Git(repository).status().call();
            return new StatusImpl(currentBranch, shortFormat, status);
        } catch (GitAPIException e) {
            throw new GitException(e.getMessage(), e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#tagCreate(org.exoplatform.ide.git.shared.TagCreateRequest) */
    @Override
    public Tag tagCreate(TagCreateRequest request) throws GitException {
        String commit = request.getCommit();
        if (commit == null) {
            commit = Constants.HEAD;
        }

        try {
            RevWalk revWalk = new RevWalk(repository);
            RevObject revObject;
            try {
                revObject = revWalk.parseAny(repository.resolve(commit));
            } finally {
                revWalk.release();
            }

            TagCommand tagCommand =
                                    new Git(repository).tag().setName(request.getName()).setObjectId(revObject)
                                                       .setMessage(request.getMessage()).setForceUpdate(request.isForce());

            GitUser tagger = getUser();
            if (tagger != null) {
                tagCommand.setTagger(new PersonIdent(tagger.getName(), tagger.getEmail()));
            }

            Ref revTagRef = tagCommand.call();
            RevTag revTag = revWalk.parseTag(revTagRef.getLeaf().getObjectId());
            return new Tag(revTag.getTagName());
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        } catch (GitAPIException e) {
            throw new GitException(e.getMessage(), e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#tagDelete(org.exoplatform.ide.git.shared.TagDeleteRequest) */
    @Override
    public void tagDelete(TagDeleteRequest request) throws GitException {
        try {
            String tagName = request.getName();
            Ref tagRef = repository.getRef(tagName);
            if (tagRef == null) {
                throw new IllegalArgumentException("Tag " + tagName + " not found. ");
            }

            RefUpdate updateRef = repository.updateRef(tagRef.getName());
            updateRef.setRefLogMessage("tag deleted", false);
            updateRef.setForceUpdate(true);
            Result deleteResult;
            deleteResult = updateRef.delete();
            if (deleteResult != Result.FORCED && deleteResult != Result.FAST_FORWARD) {
                throw new GitException("Can't delete tag " + tagName + ". Result " + deleteResult);
            }
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        }
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#tagList(org.exoplatform.ide.git.shared.TagListRequest) */
    @Override
    public List<Tag> tagList(TagListRequest request) throws GitException {
        String patternStr = request.getPattern();
        Pattern pattern = null;
        if (patternStr != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < patternStr.length(); i++) {
                char c = patternStr.charAt(i);
                if (c == '*' || c == '?') {
                    sb.append('.');
                } else if (c == '.' || c == '(' || c == ')' || c == '[' || c == ']' || c == '^' || c == '$' || c == '|') {
                    sb.append('\\');
                }
                sb.append(c);
            }
            pattern = Pattern.compile(sb.toString());
        }

        Set<String> tagNames = repository.getTags().keySet();
        List<Tag> tags = new ArrayList<Tag>(tagNames.size());

        for (String tagName : tagNames) {
            if (pattern == null) {
                tags.add(new Tag(tagName));
            } else if (pattern.matcher(tagName).matches()) {
                tags.add(new Tag(tagName));
            }
        }
        return tags;
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#getUser() */
    public GitUser getUser() {
        return user;
    }

    /** @see org.exoplatform.ide.git.server.GitConnection#close() */
    @Override
    public void close() {
        repository.close();
    }

    public Repository getRepository() {
        return repository;
    }

    public String getCurrentBranch() throws GitException {
        try {
            Ref headRef;
            headRef = repository.getRef(Constants.HEAD);
            return Repository.shortenRefName(headRef.getLeaf().getName());
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        }
    }

    /**
     * Method for cleaning name of remote branch to be checked out. I.e. it takes something like "origin/testBranch" and returns
     * "testBranch". This is needed for view-compatibility with console Git client.
     * 
     * @param branchName is a name of branch to be cleaned
     * @return branchName without remote repository name
     * @throws GitException
     */
    private String cleanRemoteName(String branchName) throws GitException {
        String returnName = branchName;
        List<Remote> remotes = this.remoteList(new RemoteListRequest());
        for (Remote remote : remotes) {
            if (branchName.startsWith(remote.getName())) {
                returnName = branchName.replaceFirst(remote.getName() + "/", "");
            }
        }
        return returnName;
    }
}
