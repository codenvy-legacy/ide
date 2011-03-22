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
package org.exoplatform.ide.git.server.jgit;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidMergeHeadsException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.NotMergedException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuildIterator;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.errors.UnmergedPathException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.server.InfoPage;
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
import org.exoplatform.ide.git.shared.ResetRequest;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.git.shared.RmRequest;
import org.exoplatform.ide.git.shared.StatusRequest;
import org.exoplatform.ide.git.shared.Tag;
import org.exoplatform.ide.git.shared.TagCreateRequest;
import org.exoplatform.ide.git.shared.TagDeleteRequest;
import org.exoplatform.ide.git.shared.TagListRequest;
import org.exoplatform.ide.git.shared.ResetRequest.ResetType;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: JGitConnection.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class JGitConnection implements GitConnection
{
   // -------------------------
   private String branchName = "master";
   private String branchRef = "refs/heads/master";
   // -------------------------
   private final Repository repository;
   // TODO
   private CredentialsProvider credentialsProvider;

   /**
    * @param repository
    */
   JGitConnection(Repository repository)
   {
      this.repository = repository;
   }

   //   /**
   //    * JGitClient which is not assigned to any repository. This constructor
   //    * should be used when need clone remote repository or init new repository
   //    * only. In this case local repository is not exist yet. Repository will be
   //    * initialized after successful execution on {@link #clone(CloneRequest)} or
   //    * {@link #init(InitRequest)} method.
   //    */
   //   JGitClient()
   //   {
   //   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#add(org.exoplatform.ide.git.shared.AddRequest)
    */
   @Override
   public void add(AddRequest request) throws GitException
   {
      AddCommand addCommand = new Git(repository).add().setUpdate(request.isUpdate());

      String[] filepattern = request.getFilepattern();
      if (filepattern == null)
         filepattern = AddRequest.DEFAULT_PATTERN;
      for (int i = 0; i < filepattern.length; i++)
         addCommand.addFilepattern(filepattern[i]);

      try
      {
         addCommand.call();
      }
      catch (NoFilepatternException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#branchCheckout(org.exoplatform.ide.git.shared.BranchCheckoutRequest)
    */
   @Override
   public void branchCheckout(BranchCheckoutRequest request) throws GitException
   {
      CheckoutCommand checkoutCommand = new Git(repository).checkout().setName(request.getName());
      String startPoint = request.getStartPoint();
      if (startPoint != null)
         checkoutCommand.setStartPoint(startPoint);
      checkoutCommand.setCreateBranch(request.isCreateNew());

      try
      {
         checkoutCommand.call();
      }
      catch (JGitInternalException e)
      {
         Throwable cause = e.getCause();
         if (cause != null)
            throw new GitException(cause.getMessage(), cause);
         throw new GitException(e.getMessage(), e);
      }
      catch (RefAlreadyExistsException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
      catch (RefNotFoundException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
      catch (InvalidRefNameException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#branchCreate(org.exoplatform.ide.git.shared.BranchCreateRequest)
    */
   @Override
   public Branch branchCreate(BranchCreateRequest request) throws GitException
   {
      CreateBranchCommand createBranchCommand = new Git(repository).branchCreate().setName(request.getName());
      String start = request.getStartPoint();
      if (start != null)
         createBranchCommand.setStartPoint(start);
      try
      {
         Ref brRef = createBranchCommand.call();
         String refName = brRef.getName();
         Branch branch = new Branch(refName, false, Repository.shortenRefName(refName));

         return branch;
      }
      catch (JGitInternalException e)
      {
         Throwable cause = e.getCause();
         if (cause != null)
            throw new GitException(cause.getMessage(), cause);
         throw new GitException(e.getMessage(), e);
      }
      catch (RefAlreadyExistsException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
      catch (RefNotFoundException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
      catch (InvalidRefNameException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#branchDelete(org.exoplatform.ide.git.shared.BranchDeleteRequest)
    */
   @Override
   public void branchDelete(BranchDeleteRequest request) throws GitException
   {
      try
      {
         new Git(repository).branchDelete().setBranchNames(request.getName()).setForce(request.isForce()).call();
      }
      catch (JGitInternalException e)
      {
         Throwable cause = e.getCause();
         if (cause != null)
            throw new GitException(cause.getMessage(), cause);
         throw new GitException(e.getMessage(), e);
      }
      catch (NotMergedException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
      catch (CannotDeleteCurrentBranchException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#branchList(org.exoplatform.ide.git.shared.BranchListRequest)
    */
   @Override
   public List<Branch> branchList(BranchListRequest request) throws GitException
   {
      String listMode = request.getListMode();
      if (listMode != null
         && !(listMode.equals(BranchListRequest.LIST_ALL) || listMode.equals(BranchListRequest.LIST_REMOTE)))
         throw new IllegalArgumentException("Unsupported list mode '" + listMode + "'. Must be either 'a' or 'r'. ");

      ListBranchCommand listBranchCommand = new Git(repository).branchList();
      if (listMode != null)
      {
         if (listMode.equals(BranchListRequest.LIST_ALL))
            listBranchCommand.setListMode(ListMode.ALL);
         else if (listMode.equals(BranchListRequest.LIST_REMOTE))
            listBranchCommand.setListMode(ListMode.REMOTE);
      }
      List<Ref> refs = listBranchCommand.call();
      String current = null;
      try
      {
         Ref headRef = repository.getRef(Constants.HEAD);
         if (headRef != null)
            current = headRef.getLeaf().getName();
      }
      catch (IOException e)
      {
         throw new GitException(e.getMessage(), e);
      }

      List<Branch> branches = new ArrayList<Branch>(refs.size());

      for (Ref brRef : refs)
      {
         String refName = brRef.getName();
         Branch branch = new Branch(refName, refName.equals(current), Repository.shortenRefName(refName));
         branches.add(branch);
      }

      return branches;
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#clone(org.exoplatform.ide.git.shared.CloneRequest)
    */
   public GitConnection clone(CloneRequest request) throws URISyntaxException, GitException
   {
      try
      {
         File workDir = repository.getWorkTree();
         if (!(workDir.exists() || workDir.mkdirs()))
            throw new GitException("Can't create working folder " + workDir + ". ");
         repository.create();

         StoredConfig config = repository.getConfig();
         String remoteName = request.getRemoteName();
         if (remoteName == null)
            remoteName = Constants.DEFAULT_REMOTE_NAME;

         RemoteConfig remoteConfig = new RemoteConfig(config, remoteName);
         remoteConfig.addURI(new URIish(request.getRemoteUri()));

         RefSpec fetchRefSpec =
            new RefSpec(Constants.R_HEADS + "*" + ":" + Constants.R_REMOTES + remoteName + "/*").setForceUpdate(true);

         String[] branchesToFetch = request.getBranchesToFetch();
         if (branchesToFetch != null)
         {
            for (int i = 0; i < branchesToFetch.length; i++)
            {
               if (fetchRefSpec.matchSource(branchesToFetch[i]))
                  remoteConfig.addFetchRefSpec(new RefSpec(branchesToFetch[i]));
            }
         }
         else
         {
            remoteConfig.addFetchRefSpec(fetchRefSpec);
         }

         config.setBoolean(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_BARE, false);
         config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, branchName, ConfigConstants.CONFIG_KEY_REMOTE,
            remoteName);
         config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, branchName, ConfigConstants.CONFIG_KEY_MERGE,
            branchRef);

         remoteConfig.update(config);

         GitUser gitUser = request.getUser();
         if (gitUser != null)
         {
            config.setString("user", null, "name", gitUser.getName());
            config.setString("user", null, "email", gitUser.getEmail());
            config.save();
         }

         config.save();

         // Fetch data from remote repository.
         Transport transport = Transport.open(repository, remoteConfig);

         if (credentialsProvider != null)
            transport.setCredentialsProvider(credentialsProvider);

         int timeout = request.getTimeout();
         if (timeout > 0)
            transport.setTimeout(timeout);

         FetchResult fetchResult;
         try
         {
            fetchResult = transport.fetch(new TextProgressMonitor(), null);
         }
         finally
         {
            transport.close();
         }

         // Merge command is not work here. Looks like JGit bug. It fails with NPE that should not happen.
         // But 'merge' command from C git (original) works as well on repository create and fetched with JGit.
         Ref headRef = fetchResult.getAdvertisedRef(branchRef);
         if (headRef == null || headRef.getObjectId() == null)
            return this;

         RevWalk revWalk = new RevWalk(repository);
         RevCommit commit;
         try
         {
            commit = revWalk.parseCommit(headRef.getObjectId());
         }
         finally
         {
            revWalk.release();
         }

         boolean detached = !headRef.getName().startsWith(Constants.R_HEADS);
         RefUpdate updateRef = repository.updateRef(Constants.HEAD, detached);
         updateRef.setNewObjectId(commit.getId());
         updateRef.forceUpdate();

         DirCache dirCache = null;
         try
         {
            dirCache = repository.lockDirCache();
            DirCacheCheckout dirCacheCheckout = new DirCacheCheckout(repository, dirCache, commit.getTree());
            dirCacheCheckout.setFailOnConflict(true);
            dirCacheCheckout.checkout();
         }
         finally
         {
            if (dirCache != null)
               dirCache.unlock();
         }

         return this;
      }
      catch (TransportException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (MissingObjectException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (IncorrectObjectTypeException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (NotSupportedException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new GitException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#commit(org.exoplatform.ide.git.shared.CommitRequest)
    */
   @Override
   public Revision commit(CommitRequest request) throws GitException
   {
      try
      {
         CommitCommand commitCommand = new Git(repository).commit().setMessage(request.getMessage());

         GitUser committer = request.getUser();
         if (committer != null)
            commitCommand.setCommitter(committer.getName(), committer.getEmail());

         RevCommit commit = commitCommand.call();

         PersonIdent committerIdent = commit.getCommitterIdent();
         Revision revision =
            new Revision(commit.getId().getName(), commit.getFullMessage(), (long)commit.getCommitTime() * 1000,
               new GitUser(committerIdent.getName(), committerIdent.getEmailAddress()));

         return revision;
      }
      catch (NoHeadException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (NoMessageException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
      catch (UnmergedPathException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (ConcurrentRefUpdateException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (JGitInternalException e)
      {
         Throwable cause = e.getCause();
         if (cause != null)
            throw new GitException(cause.getMessage(), cause);
         throw new GitException(e.getMessage(), e);
      }
      catch (WrongRepositoryStateException e)
      {
         throw new GitException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#diff(org.exoplatform.ide.git.shared.DiffRequest)
    */
   @Override
   public InfoPage diff(DiffRequest request) throws GitException
   {
      try
      {
         ObjectId head = repository.resolve(Constants.HEAD);

         RevWalk revWalk = new RevWalk(repository);
         RevTree headTree;
         try
         {
            headTree = revWalk.parseTree(head);
         }
         finally
         {
            revWalk.release();
         }

         TreeWalk treeWalk = new TreeWalk(repository);
         treeWalk.reset();
         treeWalk.setRecursive(true);
         List<DiffEntry> diff;
         try
         {
            treeWalk.addTree(headTree);
            treeWalk.addTree(new FileTreeIterator(repository));

            String[] rawFileFilter = request.getFileFilter();
            TreeFilter pathFilter =
               (rawFileFilter != null && rawFileFilter.length > 0) ? pathFilter =
                  PathFilterGroup.createFromStrings(Arrays.asList(rawFileFilter)) : TreeFilter.ALL;

            treeWalk.setFilter(AndTreeFilter.create(TreeFilter.ANY_DIFF, pathFilter));

            diff = DiffEntry.scan(treeWalk);
         }
         finally
         {
            treeWalk.release();
         }

         if (!request.isNoRenames())
         {
            RenameDetector rd = new RenameDetector(repository);
            int renameLimit = request.getRenameLimit();
            if (renameLimit > 0)
               rd.setRenameLimit(renameLimit);
            rd.addAll(diff);
            diff = rd.compute();
         }

         // To avoid closing repository before diff-page serialized.
         // Repository will be closed in DiffPage.writeTo(OutputStream).
         // See also org.eclipse.jgit.lib.Repository.close().
         repository.incrementOpen();

         return new DiffPage(diff, request, repository);
      }
      catch (MissingObjectException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (IncorrectObjectTypeException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (CorruptObjectException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (AmbiguousObjectException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new GitException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#fetch(org.exoplatform.ide.git.shared.FetchRequest)
    */
   @Override
   public void fetch(FetchRequest request) throws GitException
   {
      try
      {
         FetchCommand fetchCommand = new Git(repository).fetch();

         String remote = request.getRemote();
         if (request.getRemote() != null)
            fetchCommand.setRemote(remote);

         String[] refSpec = request.getRefSpec();
         if (refSpec != null && refSpec.length > 0)
         {
            List<RefSpec> refSpecInst = new ArrayList<RefSpec>(refSpec.length);
            for (int i = 0; i < refSpec.length; i++)
               refSpecInst.add(new RefSpec(refSpec[i]));
            fetchCommand.setRefSpecs(refSpecInst);
         }

         fetchCommand.setRemoveDeletedRefs(request.isRemoveDeletedRefs());

         int timeout = request.getTimeout();
         if (timeout > 0)
            fetchCommand.setTimeout(timeout);

         fetchCommand.call();
      }
      catch (JGitInternalException e)
      {
         Throwable cause = e.getCause();
         if (cause != null)
            throw new GitException(cause.getMessage(), cause);
         throw new GitException(e.getMessage(), e);
      }
      catch (InvalidRemoteException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#init(org.exoplatform.ide.git.shared.InitRequest)
    */
   @Override
   public GitConnection init(InitRequest request) throws GitException
   {
      File workDir = repository.getWorkTree();
      if (!(workDir.exists() || workDir.mkdirs()))
         throw new GitException("Can't create working folder " + workDir + ". ");
      try
      {
         repository.create(request.isBare());
         GitUser gitUser = request.getUser();
         if (gitUser != null)
         {
            StoredConfig config = repository.getConfig();
            config.setString("user", null, "name", gitUser.getName());
            config.setString("user", null, "email", gitUser.getEmail());
            config.save();
         }
      }
      catch (IOException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      return this;
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#log(org.exoplatform.ide.git.shared.LogRequest)
    */
   @Override
   public InfoPage log(LogRequest request) throws GitException
   {
      LogCommand logCommand = new Git(repository).log();
      try
      {
         return new LogPage(logCommand.call().iterator());
      }
      catch (NoHeadException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (JGitInternalException e)
      {
         Throwable cause = e.getCause();
         if (cause != null)
            throw new GitException(cause.getMessage(), cause);
         throw new GitException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#merge(org.exoplatform.ide.git.shared.MergeRequest)
    */
   @Override
   public MergeResult merge(MergeRequest request) throws GitException
   {
      try
      {
         Ref ref = repository.getRef(request.getCommit());
         if (ref == null)
            throw new IllegalArgumentException("Invalid reference to commit for merge " + request.getCommit());
         org.eclipse.jgit.api.MergeResult jgitMergeResult = new Git(repository).merge().include(ref).call();
         return new JGitMergeResult(jgitMergeResult);
      }
      catch (NoHeadException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (ConcurrentRefUpdateException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (CheckoutConflictException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (InvalidMergeHeadsException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (WrongRepositoryStateException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (NoMessageException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new GitException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#mv(org.exoplatform.ide.git.shared.MoveRequest)
    */
   @Override
   public void mv(MoveRequest request) throws GitException
   {
      throw new RuntimeException("Not implemented yet. ");
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#pull(org.exoplatform.ide.git.shared.PullRequest)
    */
   @Override
   public void pull(PullRequest request) throws GitException
   {
      PullCommand pullCommand = new Git(repository).pull();
      int timeout = request.getTimeout();
      if (timeout > 0)
         pullCommand.setTimeout(timeout);
      try
      {
         pullCommand.call();
      }
      catch (WrongRepositoryStateException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (InvalidConfigurationException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (DetachedHeadException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (InvalidRemoteException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
      catch (CanceledException e)
      {
         throw new GitException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#push(org.exoplatform.ide.git.shared.PushRequest)
    */
   @Override
   public void push(PushRequest request) throws GitException
   {
      try
      {
         PushCommand pushCommand = new Git(repository).push();

         String remote = request.getRemote();
         if (request.getRemote() != null)
            pushCommand.setRemote(remote);

         String[] refSpec = request.getRefSpec();
         if (refSpec != null && refSpec.length > 0)
         {
            List<RefSpec> refSpecInst = new ArrayList<RefSpec>(refSpec.length);
            for (int i = 0; i < refSpec.length; i++)
               refSpecInst.add(new RefSpec(refSpec[i]));
            pushCommand.setRefSpecs(refSpecInst);
         }

         pushCommand.setForce(request.isForce());

         int timeout = request.getTimeout();
         if (timeout > 0)
            pushCommand.setTimeout(timeout);

         pushCommand.call();
      }
      catch (JGitInternalException e)
      {
         Throwable cause = e.getCause();
         if (cause != null)
            throw new GitException(cause.getMessage(), cause);
         throw new GitException(e.getMessage(), e);
      }
      catch (InvalidRemoteException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#reset(org.exoplatform.ide.git.shared.ResetRequest)
    */
   @Override
   public void reset(ResetRequest request) throws GitException
   {
      String commit = request.getCommit();
      if (commit == null)
         commit = Constants.HEAD;

      ResetType resetType = request.getType();
      String[] paths = request.getPaths();

      boolean moveHead = !(paths != null && paths.length > 0);

      if (!moveHead && resetType != ResetType.MIXED)
         throw new IllegalArgumentException("Invalid reset type " + resetType + ". It can't be used with the paths. ");

      DirCache dirCache = null;
      try
      {
         dirCache = repository.lockDirCache();

         ObjectId objectId = repository.resolve(commit);
         if (objectId == null)
            throw new IllegalArgumentException("Invalid commit " + request.getCommit());

         RevWalk revWalk = new RevWalk(repository);
         RevCommit revCommit;
         try
         {
            revCommit = revWalk.parseCommit(objectId);
         }
         finally
         {
            revWalk.release();
         }

         if (resetType == ResetType.MIXED)
         {
            if (moveHead)
            {
               dirCache.clear();
               DirCacheBuilder cacheBuilder = dirCache.builder();
               cacheBuilder.addTree(new byte[0], 0, repository.newObjectReader(), revCommit.getTree());
               cacheBuilder.commit();
            }
            else
            {
               TreeWalk treeWalk = new TreeWalk(repository);
               treeWalk.reset();
               treeWalk.setRecursive(true);
               try
               {
                  DirCacheBuilder cacheBuilder = dirCache.builder();
                  treeWalk.setFilter(PathFilterGroup.createFromStrings(Arrays.asList(paths)));
                  treeWalk.addTree(new DirCacheBuildIterator(cacheBuilder));
                  while (treeWalk.next());
                  cacheBuilder.commit();
               }
               finally
               {
                  treeWalk.release();
               }
            }
         }
         else if (resetType == ResetType.HARD)
         {
            DirCacheCheckout dirCacheCheckout = new DirCacheCheckout(repository, dirCache, revCommit.getTree());
            dirCacheCheckout.setFailOnConflict(true);
            dirCacheCheckout.checkout();
         }

         if (moveHead)
         {
            RefUpdate ru = repository.updateRef(Constants.HEAD);
            ru.setNewObjectId(revCommit.getId());
            if (ru.forceUpdate() == RefUpdate.Result.LOCK_FAILURE)
               throw new GitException("Can't update HEAD to " + commit);
         }
      }
      catch (AmbiguousObjectException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (MissingObjectException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (IncorrectObjectTypeException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (NoWorkTreeException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (CorruptObjectException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      finally
      {
         if (dirCache != null)
            dirCache.unlock();
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#rm(org.exoplatform.ide.git.shared.RmRequest)
    */
   @Override
   public void rm(RmRequest request) throws GitException
   {
      String[] files = request.getFiles();
      RmCommand rmCommand = new Git(repository).rm();
      if (files != null)
      {
         for (int i = 0; i < files.length; i++)
            rmCommand.addFilepattern(files[i]);
      }
      try
      {
         rmCommand.call();
      }
      catch (NoFilepatternException e)
      {
         throw new IllegalArgumentException("File pattern may not be null or empty. ");
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#status(org.exoplatform.ide.git.shared.StatusRequest)
    */
   @Override
   public InfoPage status(StatusRequest request) throws GitException
   {
      try
      {
         Ref headRef = repository.getRef(Constants.HEAD);
         String currentBranch = Repository.shortenRefName(headRef.getLeaf().getName());

         RevWalk revWalk = new RevWalk(repository);
         RevTree headTree;
         try
         {
            headTree = revWalk.parseTree(headRef.getObjectId());
         }
         finally
         {
            revWalk.release();
         }

         List<GitFile> changedNotUpdated = new ArrayList<GitFile>();
         List<GitFile> changedNotCommited = new ArrayList<GitFile>();
         List<GitFile> untracked = new ArrayList<GitFile>();

         int headTreeN = 0;
         int idxTreeN = 1;
         int wdTreeN = 2;

         DirCache dirCache = repository.readDirCache();

         TreeWalk treeWalk = new TreeWalk(repository);
         treeWalk.reset();
         treeWalk.setRecursive(true);

         try
         {
            treeWalk.addTree(headTree);
            treeWalk.addTree(new DirCacheIterator(dirCache));
            treeWalk.addTree(new FileTreeIterator(repository));

            while (treeWalk.next())
            {
               int headMode = treeWalk.getFileMode(headTreeN).getBits();
               int idxMode = treeWalk.getFileMode(idxTreeN).getBits();
               int wdMode = treeWalk.getFileMode(wdTreeN).getBits();

               boolean tracked =
                  wdMode != FileMode.TYPE_TREE
                     && (idxMode != FileMode.TYPE_MISSING || headMode != FileMode.TYPE_MISSING);
               boolean modified = !treeWalk.idEqual(wdTreeN, headTreeN);
               boolean commited = treeWalk.idEqual(wdTreeN, idxTreeN);

               String path = treeWalk.getPathString();

               if (!tracked)
               {
                  untracked.add(new GitFile(path, GitFile.FileStatus.UNTRACKED));
               }
               else if (modified)
               {
                  GitFile.FileStatus status;
                  if (headMode == FileMode.TYPE_MISSING && idxMode != FileMode.TYPE_MISSING)
                     status = GitFile.FileStatus.NEW;
                  else if (wdMode == FileMode.TYPE_MISSING
                     && (idxMode != FileMode.TYPE_MISSING || headMode != FileMode.TYPE_MISSING))
                     status = GitFile.FileStatus.DELETED;
                  else
                     status = GitFile.FileStatus.MODIFIED;

                  if (commited)
                     changedNotCommited.add(new GitFile(path, status));
                  else
                     changedNotUpdated.add(new GitFile(path, status));
               }
            }
         }
         finally
         {
            treeWalk.release();
         }

         return new StatusPage(currentBranch, changedNotUpdated, changedNotCommited, untracked, request);
      }
      catch (MissingObjectException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (IncorrectObjectTypeException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (CorruptObjectException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new GitException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#tagCreate(org.exoplatform.ide.git.shared.TagCreateRequest)
    */
   @Override
   public Tag tagCreate(TagCreateRequest request) throws GitException
   {
      String commit = request.getCommit();
      if (commit == null)
         commit = Constants.HEAD;

      try
      {
         RevWalk revWalk = new RevWalk(repository);
         RevObject revObject;
         try
         {
            revObject = revWalk.parseAny(repository.resolve(commit));
         }
         finally
         {
            revWalk.release();
         }

         TagCommand tagCommand =
            new Git(repository).tag().setName(request.getName()).setObjectId(revObject)
               .setMessage(request.getMessage()).setForceUpdate(request.isForce());

         GitUser tagger = request.getUser();
         if (tagger != null)
            tagCommand.setTagger(new PersonIdent(tagger.getName(), tagger.getEmail()));

         RevTag revTag = tagCommand.call();

         return new Tag(revTag.getTagName());
      }
      catch (JGitInternalException e)
      {
         Throwable cause = e.getCause();
         if (cause != null)
            throw new GitException(cause.getMessage(), cause);
         throw new GitException(e.getMessage(), e);
      }
      catch (ConcurrentRefUpdateException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (InvalidTagNameException e)
      {
         throw new IllegalArgumentException(e.getMessage(), e);
      }
      catch (NoHeadException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (MissingObjectException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (AmbiguousObjectException e)
      {
         throw new GitException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new GitException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#tagDelete(org.exoplatform.ide.git.shared.TagDeleteRequest)
    */
   @Override
   public void tagDelete(TagDeleteRequest request) throws GitException
   {
      try
      {
         String tagName = request.getName();
         Ref tagRef = repository.getRef(tagName);
         if (tagRef == null)
            throw new IllegalArgumentException("Tag " + tagName + " not found. ");

         RefUpdate updateRef = repository.updateRef(tagRef.getName());
         updateRef.setRefLogMessage("tag deleted", false);
         updateRef.setForceUpdate(true);
         Result deleteResult;
         deleteResult = updateRef.delete();
         if (deleteResult != Result.FORCED && deleteResult != Result.FAST_FORWARD)
            throw new GitException("Can't delete tag " + tagName + ". Result " + deleteResult);
      }
      catch (IOException e)
      {
         throw new GitException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#tagList(org.exoplatform.ide.git.shared.TagListRequest)
    */
   @Override
   public List<Tag> tagList(TagListRequest request) throws GitException
   {
      String patternStr = request.getPattern();
      Pattern pattern = null;
      if (patternStr != null)
      {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < patternStr.length(); i++)
         {
            char c = patternStr.charAt(i);
            if (c == '*' || c == '?')
               sb.append('.');
            else if (c == '.' || c == '(' || c == ')' || c == '[' || c == ']' || c == '^' || c == '$' || c == '|')
               sb.append('\\');
            sb.append(c);
         }
         pattern = Pattern.compile(sb.toString());
      }

      Set<String> tagNames = repository.getTags().keySet();
      List<Tag> tags = new ArrayList<Tag>(tagNames.size());

      for (String tagName : tagNames)
      {
         if (pattern == null)
            tags.add(new Tag(tagName));
         else if (pattern.matcher(tagName).matches())
            tags.add(new Tag(tagName));
      }
      return tags;
   }

   /**
    * @see org.exoplatform.ide.git.server.GitConnection#close()
    */
   @Override
   public void close()
   {
      repository.close();
   }

   public Repository getRepository()
   {
      return repository;
   }
}
