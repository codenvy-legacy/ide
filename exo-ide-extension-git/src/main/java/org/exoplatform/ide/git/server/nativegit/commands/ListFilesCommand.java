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

import java.io.File;
import java.util.List;

/**
 * Get list of files
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class ListFilesCommand extends GitCommand<List<String>> {

    private boolean others;
    private boolean modified;
    private boolean staged;
    private boolean cached;
    private boolean deleted;
    private boolean ignored;
    private boolean excludeStandard;

    public ListFilesCommand(File place) {
        super(place);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public List<String> execute() throws GitException {
        clear();
        commandLine.add("ls-files");
        if (others) {
            commandLine.add("-o");
        }
        if (modified) {
            commandLine.add("-m");
        }
        if (staged) {
            commandLine.add("-s");
        }
        if (cached) {
            commandLine.add("-c");
        }
        if (deleted) {
            commandLine.add("-d");
        }
        if (ignored) {
            commandLine.add("-i");
        }
        if (excludeStandard) {
            commandLine.add("--exclude-standard");
        }
        start();
        return getOutput();
    }

    /**
     * @param others
     *         if <code>true</code> other files will be selected
     * @return ListFilesCommand with established others parameter
     */
    public ListFilesCommand setOthers(boolean others) {
        this.others = others;
        return this;
    }

    /**
     * @param modified
     *         if <code>true</code> modified files will be selected
     * @return ListFilesCommand with established modified files parameter
     */
    public ListFilesCommand setModified(boolean modified) {
        this.modified = modified;
        return this;
    }

    /**
     * @param staged
     *         if <code>true</code> staged files will be selected
     * @return ListFilesCommand with established staged files parameter
     */
    public ListFilesCommand setStaged(boolean staged) {
        this.staged = staged;
        return this;
    }

    /**
     * @param ignored
     *         if <code>true</code> ignored files will be selected
     * @return ListFilesCommand with established ignored files parameter
     */
    public ListFilesCommand setIgnored(boolean ignored) {
        this.ignored = ignored;
        return this;
    }

    /**
     * @param deleted
     *         if <code>true</code> deleted files will be selected
     * @return ListFilesCommand with established deleted files parameter
     */
    public ListFilesCommand setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    /**
     * @param cached
     *         if <code>true</code> cached files will be selected
     * @return ListFilesCommand with established cached files parameter
     */
    public ListFilesCommand setCached(boolean cached) {
        this.cached = cached;
        return this;
    }

    /**
     * @param excludeStandard
     *         if <code>true</code> excludeStandard parameter will be used
     * @return ListFilesCommand with established excludeStandard parameter
     */
    public ListFilesCommand setExcludeStandard(boolean excludeStandard) {
        this.excludeStandard = excludeStandard;
        return this;
    }
}


