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
import org.exoplatform.ide.git.server.InfoPage;
import org.exoplatform.ide.git.server.nativegit.commands.StatusCommand;
import org.exoplatform.ide.git.shared.Status;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * NativeGit implementation for org.exoplatform.ide.git.shared.Status and
 * org.exoplatform.ide.git.server.InfoPage.
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class NativeGitStatusImpl implements Status, InfoPage {

    private String branchName;

    private Boolean shortFormat;

    private Boolean clean;

    private Set<String> added;

    private Set<String> changed;

    private Set<String> removed;

    private Set<String> missing;

    private Set<String> modified;

    private Set<String> untracked;

    private Set<String> untrackedFolders;

    private Set<String> conflicting;

    private NativeGit nativeGit;

    /**
     * @param branchName
     *         current repository branch name
     * @param nativeGit
     *         git commands factory
     * @param shortFormat
     *         if <code>true</code> short status will be used
     * @throws GitException
     *         when any error occurs
     */
    public NativeGitStatusImpl(String branchName, NativeGit nativeGit, Boolean shortFormat) throws GitException {
        this.branchName = branchName;
        this.shortFormat = shortFormat;
        this.nativeGit = nativeGit;
        load();
    }

    /** @see org.exoplatform.ide.git.server.InfoPage#writeTo(java.io.OutputStream) */
    @Override
    public void writeTo(OutputStream out) throws IOException {
        StatusCommand status = nativeGit.createStatusCommand().setShort(shortFormat);
        try {
            status.execute();
            out.write(status.getOutputMessage().getBytes());
        } catch (GitException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** @see org.exoplatform.ide.git.shared.Status#isClean() */
    @Override
    public boolean isClean() {
        return clean;
    }

    /** @see org.exoplatform.ide.git.shared.Status#setClean(Boolean) */
    @Override
    public void setClean(Boolean clean) {
        this.clean = clean;
    }

    /** @see org.exoplatform.ide.git.shared.Status#getShortFormat() */
    @Override
    public boolean getShortFormat() {
        return shortFormat;
    }

    /** @see org.exoplatform.ide.git.shared.Status#setShortFormat(Boolean) */
    @Override
    public void setShortFormat(Boolean shortFormat) {
        this.shortFormat = shortFormat;
    }

    /** @see org.exoplatform.ide.git.shared.Status#getBranchName() */
    @Override
    public String getBranchName() {
        return branchName;
    }

    /** @see org.exoplatform.ide.git.shared.Status#setBranchName(String) */
    @Override
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /** @see org.exoplatform.ide.git.shared.Status#getAdded() */
    @Override
    public Set<String> getAdded() {
        if (added == null){
            added = new LinkedHashSet<>();
        }
        return added;
    }

    /** @see org.exoplatform.ide.git.shared.Status#setAdded(java.util.Set) */
    @Override
    public void setAdded(Set<String> added) {
        this.added = added;
    }

    /** @see org.exoplatform.ide.git.shared.Status#getChanged() */
    @Override
    public Set<String> getChanged() {
        if (changed == null){
            changed = new LinkedHashSet<>();
        }
        return changed;
    }

    /** @see org.exoplatform.ide.git.shared.Status#setChanged(java.util.Set) */
    @Override
    public void setChanged(Set<String> changed) {
        this.changed = changed;
    }

    /** @see org.exoplatform.ide.git.shared.Status#getRemoved() */
    @Override
    public Set<String> getRemoved() {
        if (removed == null){
            removed = new LinkedHashSet<>();
        }
        return removed;
    }

    /** @see org.exoplatform.ide.git.shared.Status#setRemoved(java.util.Set) */
    @Override
    public void setRemoved(Set<String> removed) {
        this.removed = removed;
    }

    /** @see org.exoplatform.ide.git.shared.Status#getMissing() */
    @Override
    public Set<String> getMissing() {
        if (missing == null) {
            missing = new LinkedHashSet<>();
        }
        return missing;
    }

    /** @see org.exoplatform.ide.git.shared.Status#setMissing(java.util.Set) */
    @Override
    public void setMissing(Set<String> missing) {
        this.missing = missing;
    }

    /** @see org.exoplatform.ide.git.shared.Status#getModified() */
    @Override
    public Set<String> getModified() {
        if (modified == null) {
            modified = new LinkedHashSet<>();
        }
        return modified;
    }

    /** @see org.exoplatform.ide.git.shared.Status#setModified(java.util.Set) */
    @Override
    public void setModified(Set<String> modified) {
        this.modified = modified;
    }

    /** @see org.exoplatform.ide.git.shared.Status#getUntracked() */
    @Override
    public Set<String> getUntracked() {
        if (untracked == null) {
            untracked = new LinkedHashSet<>();
        }
        return untracked;
    }

    /** @see org.exoplatform.ide.git.shared.Status#setUntracked(java.util.Set) */
    @Override
    public void setUntracked(Set<String> untracked) {
        this.untracked = untracked;
    }

    /** @see org.exoplatform.ide.git.shared.Status#getUntrackedFolders() */
    @Override
    public Set<String> getUntrackedFolders() {
        if (untrackedFolders == null) {
            untrackedFolders = new LinkedHashSet<>();
        }
        return untrackedFolders;
    }

    /** @see org.exoplatform.ide.git.shared.Status#setUntrackedFolders(java.util.Set) */
    @Override
    public void setUntrackedFolders(Set<String> untrackedFolders) {
        this.untrackedFolders = untrackedFolders;
    }

    /** @see org.exoplatform.ide.git.shared.Status#getConflicting() */
    @Override
    public Set<String> getConflicting() {
        if (conflicting == null) {
            conflicting = new LinkedHashSet<>();
        }
        return conflicting;
    }

    /** @see org.exoplatform.ide.git.shared.Status#setConflicting(java.util.Set) */
    @Override
    public void setConflicting(Set<String> conflicting) {
        this.conflicting = conflicting;
    }


    /**
     * loads status information.
     *
     * @throws GitException
     *         when it is not possible to get status information
     */
    public void load() throws GitException {
        StatusCommand status = nativeGit.createStatusCommand().setShort(true);
        List<String> statusOutput = status.execute();
        setClean(statusOutput.size() == 0);
        if (!isClean()) {
            added = new LinkedHashSet<>();
            changed = new LinkedHashSet<>();
            removed = new LinkedHashSet<>();
            missing = new LinkedHashSet<>();
            modified = new LinkedHashSet<>();
            untracked = new LinkedHashSet<>();
            untrackedFolders = new LinkedHashSet<>();
            conflicting = new LinkedHashSet<>();
            for (String statusLine : statusOutput) {
                //add conflict files AA, UU, any of U
                addFileIfAccepted(conflicting, statusLine, 'A', 'A');
                addFileIfAccepted(conflicting, statusLine, 'U', '*');
                addFileIfAccepted(conflicting, statusLine, '*', 'U');
                //add Added files
                addFileIfAccepted(added, statusLine, 'A', 'M');
                addFileIfAccepted(added, statusLine, 'A', ' ');
                //add Changed
                addFileIfAccepted(changed, statusLine, 'M', '*');
                //add removed
                addFileIfAccepted(removed, statusLine, 'D', '*');
                //add missing
                addFileIfAccepted(missing, statusLine, 'A', 'D');
                //add modified
                addFileIfAccepted(modified, statusLine, '*', 'M');
                if (statusLine.endsWith("/")) {
                    //add untracked folders
                    addFileIfAccepted(untrackedFolders, statusLine.substring(0, statusLine.length() - 1), '?', '?');
                } else {
                    //add untracked Files
                    addFileIfAccepted(untracked, statusLine, '?', '?');
                }
            }
        }
    }

    /**
     * Adds files to container if they matched to template.
     *
     * @param statusFiles
     *         container for accepted files
     * @param statusLine
     *         short status command line
     * @param X
     *         first template parameter
     * @param Y
     *         second template parameter
     */
    private void addFileIfAccepted(Set<String> statusFiles, String statusLine, char X, char Y) {
        if (X == '*' && statusLine.charAt(1) == Y
            || Y == '*' && statusLine.charAt(0) == X
            || statusLine.charAt(0) == X && statusLine.charAt(1) == Y) {
            statusFiles.add(statusLine.substring(3));
        }
    }
}
