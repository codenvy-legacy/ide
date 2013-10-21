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
package org.exoplatform.ide.git.server.nativegit.commands;

import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.DiffRequest;

import java.io.File;

/**
 * Show diff
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class DiffCommand extends GitCommand<String> {

    private String[] filesFilter;
    private String   commitA;
    private String   commitB;
    private String   type;
    private boolean  cached;
    private boolean  noRenames;
    private int      renamesCount;

    public DiffCommand(File repository) {
        super(repository);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public String execute() throws GitException {
        clear();
        commandLine.add("diff");
        if (!(type == null || type.equals(DiffRequest.DiffType.RAW.toString()))) {
            commandLine.add(type);
        }
        if (noRenames) {
            commandLine.add("--no-renames");
        }
        if (renamesCount > 0) {
            commandLine.add("--find-renames=" + renamesCount);
        }
        if (cached) {
            commandLine.add("--cached");
        }
        if (commitA != null) {
            commandLine.add(commitA);
        }
        if (commitB != null) {
            commandLine.add(commitB);
        }
        if (filesFilter != null) {
            commandLine.add(filesFilter);
        }
        start();
        if (type == null || type.equals("--raw")) {
            return getOutputMessage() + "\n\n";
        } else {
            return getOutputMessage();
        }
    }

    /**
     * @param commitA
     *         first commit
     * @return DiffCommand with established first commit
     */
    public DiffCommand setCommitA(String commitA) {
        this.commitA = commitA;
        return this;
    }

    /**
     * @param commitB
     *         second commit
     * @return DiffCommand with established second commit
     */
    public DiffCommand setCommitB(String commitB) {
        this.commitB = commitB;
        return this;
    }

    /**
     * @param filesFilter
     *         files to filter
     * @return DiffCommand with established files to filter
     */
    public DiffCommand setFileFilter(String[] filesFilter) {
        this.filesFilter = filesFilter;
        return this;
    }

    /**
     * @param type
     *         of diff command
     * @return DiffCommand with established type
     */
    public DiffCommand setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * @param cached
     *         if <code>true</code> cached parameter will be used
     * @return DiffCommand with established cached parameter
     */
    public DiffCommand setCached(boolean cached) {
        this.cached = cached;
        return this;
    }

    /**
     * @param renamesCount
     *         count of renames
     * @return DiffCommand with established renames count
     */
    public DiffCommand setRenamesCount(int renamesCount) {
        this.renamesCount = renamesCount;
        return this;
    }

    /**
     * @param noRenames
     *         if <code>true</code> command will be executed without renames
     * @return DiffCommand with established no renames parameters
     */
    public DiffCommand setNoRenames(boolean noRenames) {
        this.noRenames = noRenames;
        return this;
    }
}
