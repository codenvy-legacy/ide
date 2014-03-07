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

import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.server.nativegit.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Git commands factory.
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class NativeGit {

    private static final Logger LOG                 = LoggerFactory.getLogger(NativeGit.class);
    private static final String SSH_SCRIPT_TEMPLATE = "META-INF/SshTemplate";
    private static final String SSH_SCRIPT          = "ssh_script";
    private static String sshScriptTemplate;
    private        File   repository;

    /**
     * Loading template, that will be used to store ssh
     */
    static {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Thread.currentThread().getContextClassLoader()
                                            .getResourceAsStream(SSH_SCRIPT_TEMPLATE)))) {
            sshScriptTemplate = "";
            String line;
            while ((line = reader.readLine()) != null) {
                sshScriptTemplate = sshScriptTemplate.concat(line);
            }
        } catch (Exception e) {
            LOG.error("Cant load template " + SSH_SCRIPT_TEMPLATE);
            throw new RuntimeException("Cant load credentials template.", e);
        }
    }

    /**
     * @param repository
     *         directory where will be executed all commands created with
     *         this NativeGit object
     */
    public NativeGit(File repository) {
        this.repository = repository;
    }

    /**
     * Creates clone command that will be used without ssh key
     *
     * @return clone command
     */
    public CloneCommand createCloneCommand() {
        return new CloneCommand(repository);
    }

    /**
     * Creates CloneCommand that will be used with ssh key
     *
     * @param sshKeyPath
     *         path to ssh key that will be used with clone command
     * @return git command with ssh key parameter
     * @throws GitException
     *         when some error with script storing occurs
     */
    public CloneCommand createCloneCommand(String sshKeyPath) throws GitException {
        storeSshScript(sshKeyPath);
        CloneCommand command = new CloneCommand(repository);
        command.setSSHScriptPath(SshKeysManager.getKeyDirectoryPath() + '/' + SSH_SCRIPT);
        return command;
    }

    /** @return commit command */
    public CommitCommand createCommitCommand() {
        return new CommitCommand(repository);
    }

    /** @return branch create command */
    public BranchRenameCommand createBranchRenameCommand() {
        return new BranchRenameCommand(repository);
    }

    /** @return remote add command */
    public RemoteAddCommand createRemoteAddCommand() {
        return new RemoteAddCommand(repository);
    }

    /** @return remote list command */
    public RemoteListCommand createRemoteListCommand() {
        return new RemoteListCommand(repository);
    }

    /** @return remote delete command */
    public RemoteDeleteCommand createRemoteDeleteCommand() {
        return new RemoteDeleteCommand(repository);
    }

    /** @return log command */
    public LogCommand createLogCommand() {
        return new LogCommand(repository);
    }

    /** @return ls-remote command */
    public LsRemoteCommand createLsRemoteCommand() {
        return new LsRemoteCommand(repository);
    }

    /** @return add command */
    public AddCommand createAddCommand() {
        return new AddCommand(repository);
    }

    /** @return init command */
    public InitCommand createInitCommand() {
        return new InitCommand(repository);
    }

    /** @return diff command */
    public DiffCommand createDiffCommand() {
        return new DiffCommand(repository);
    }

    /** @return reset command */
    public ResetCommand createResetCommand() {
        return new ResetCommand(repository);
    }

    /** @return tag create command */
    public TagCreateCommand createTagCreateCommand() {
        return new TagCreateCommand(repository);
    }

    /** @return tag delete command */
    public TagDeleteCommand createTagDeleteCommand() {
        return new TagDeleteCommand(repository);
    }

    /** @return tah list command */
    public TagListCommand createTagListCommand() {
        return new TagListCommand(repository);
    }

    /** @return branch create command */
    public BranchCreateCommand createBranchCreateCommand() {
        return new BranchCreateCommand(repository);
    }

    /** @return config */
    public Config createConfig() {
        return new Config(repository);
    }

    /** @return branch checkout command */
    public BranchCheckoutCommand createBranchCheckoutCommand() {
        return new BranchCheckoutCommand(repository);
    }

    /** @return list files command */
    public ListFilesCommand createListFilesCommand() {
        return new ListFilesCommand(repository);
    }

    /** @return branch list command */
    public BranchListCommand createBranchListCommand() {
        return new BranchListCommand(repository);
    }

    /** @return branch delete command */
    public BranchDeleteCommand createBranchDeleteCommand() {
        return new BranchDeleteCommand(repository);
    }

    /** @return remote command */
    public RemoveCommand createRemoveCommand() {
        return new RemoveCommand(repository);
    }

    /** @return move command */
    public MoveCommand createMoveCommand() {
        return new MoveCommand(repository);
    }

    /** @return status command */
    public StatusCommand createStatusCommand() {
        return new StatusCommand(repository);
    }

    /** @return merge command */
    public MergeCommand createMergeCommand() {
        return new MergeCommand(repository);
    }

    /**
     * Creates fetch command that will be used without ssh key
     *
     * @return fetch command
     */
    public FetchCommand createFetchCommand() {
        return new FetchCommand(repository);
    }

    /**
     * Creates fetch command that will be used with ssh key
     *
     * @param sshKeyPath
     *         path to ssh key that will be used with fetch command
     * @return fetch command with ssh key parameter
     * @throws GitException
     *         when some error with script storing occurs
     */
    public FetchCommand createFetchCommand(String sshKeyPath) throws GitException {
        storeSshScript(sshKeyPath);
        FetchCommand command = new FetchCommand(repository);
        command.setSSHScriptPath(SshKeysManager.getKeyDirectoryPath() + '/' + SSH_SCRIPT);
        return command;
    }

    /**
     * Creates pull command that will be used without ssh key
     *
     * @return pull command
     */
    public PullCommand createPullCommand() {
        return new PullCommand(repository);
    }

    /**
     * Creates pull command that will be used with ssh key
     *
     * @param sshKeyPath
     *         path to ssh key that will be used with pull command
     * @return pull command with ssh key
     * @throws GitException
     *         when some error with script storing occurs
     */
    public PullCommand createPullCommand(String sshKeyPath) throws GitException {
        storeSshScript(sshKeyPath);
        PullCommand command = new PullCommand(repository);
        command.setSSHScriptPath(SshKeysManager.getKeyDirectoryPath() + '/' + SSH_SCRIPT);
        return command;
    }

    /** @return remote update command */
    public RemoteUpdateCommand createRemoteUpdateCommand() {
        return new RemoteUpdateCommand(repository);
    }

    /**
     * Creates push command that will be used without ssh key
     *
     * @return push command
     */
    public PushCommand createPushCommand() {
        return new PushCommand(repository);
    }

    /**
     * Creates push command that will be used with ssh key
     *
     * @param sshKeyPath
     *         path to ssh key that will be used with push command
     * @return pull command with ssh key parameter
     * @throws GitException
     *         when some error with script storing occurs
     */
    public PushCommand createPushCommand(String sshKeyPath) throws GitException {
        storeSshScript(sshKeyPath);
        PushCommand command = new PushCommand(repository);
        command.setSSHScriptPath(SshKeysManager.getKeyDirectoryPath() + '/' + SSH_SCRIPT);
        return command;
    }

    /** @return repository */
    public File getRepository() {
        return repository;
    }

    /**
     * @param repository
     *         repository
     */
    public void setRepository(File repository) {
        this.repository = repository;
    }


    /**
     * Stores ssh script that will be executed with all commands that need ssh.
     *
     * @param pathToSSHKey
     *         path to ssh key
     * @throws GitException
     *         when any error with ssh script storing occurs
     */
    private void storeSshScript(String pathToSSHKey) throws GitException {
        File sshScript = new File(SshKeysManager.getKeyDirectoryPath(), SSH_SCRIPT);
        //creating script
        try (FileOutputStream fos = new FileOutputStream(sshScript)) {
            fos.write(sshScriptTemplate.replace("$ssh_key", pathToSSHKey).getBytes());
        } catch (IOException e) {
            LOG.error("It is not possible to store " + pathToSSHKey + " ssh key");
            throw new GitException("Can't store SSH key");
        }
        if (!sshScript.setExecutable(true)) {
            LOG.error("Can't make " + sshScript + " executable");
            throw new GitException("Can't set permissions to SSH key");
        }
    }
}