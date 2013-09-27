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
package org.exoplatform.ide.git.server;

import org.exoplatform.ide.git.shared.Status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Our implementation of org.eclipse.jgit.api.Status
 * 
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 * @version $Id: StatusImpl.java 68135 2013-18-03 14:23:36Z diam $
 */
public class StatusImpl implements Status, InfoPage {

    protected String      branchName;

    protected Boolean     shortFormat;

    protected Boolean     clean;

    protected Set<String> added;

    protected Set<String> changed;

    protected Set<String> removed;

    protected Set<String> missing;

    protected Set<String> modified;

    protected Set<String> untracked;

    protected Set<String> untrackedFolders;

    protected Set<String> conflicting;

    public StatusImpl(String branchName, Boolean shortFormat, org.eclipse.jgit.api.Status status) {

        this.setBranchName(branchName);
        this.setShortFormat(shortFormat);

        this.setAdded(status.getAdded());
        this.setChanged(status.getChanged());
        this.setRemoved(status.getRemoved());
        this.setMissing(status.getMissing());
        this.setModified(status.getModified());
        this.setUntracked(status.getUntracked());
        this.setConflicting(status.getConflicting());

        if ((getAdded().isEmpty() && getChanged().isEmpty() && getRemoved().isEmpty() && getMissing().isEmpty()
             && getModified().isEmpty() && getUntracked().isEmpty() && getConflicting().isEmpty())) {
            this.setClean(true);
        } else {
            this.setClean(false);
        }
    }

    /** @see org.eclipse.jgit.api.Status#isClean() */
    public boolean isClean() {
        return clean;
    }

    /** Setter for org.eclipse.jgit.api.Status#isClean() */
    public void setClean(Boolean clean) {
        this.clean = clean;
    }

    /** @see org.eclipse.jgit.api.Status#getShortFormat() */
    public boolean getShortFormat() {
        return this.shortFormat;
    }

    /** Setter for org.eclipse.jgit.api.Status#getShortFormat() */
    public void setShortFormat(Boolean shortFormat) {
        this.shortFormat = shortFormat;
    }

    /** @see org.eclipse.jgit.api.Status#getBranchName() */
    public String getBranchName() {
        return this.branchName;
    }

    /** Setter for org.eclipse.jgit.api.Status#getBranchName() */
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /** @see org.eclipse.jgit.api.Status#getAdded() */
    public Set<String> getAdded() {
        return this.added;
    }

    /** Setter for org.eclipse.jgit.api.Status#getAdded() */
    public void setAdded(Set<String> added) {
        this.added = added;
    }

    /** @see org.eclipse.jgit.api.Status#getChanged() */
    public Set<String> getChanged() {
        return this.changed;
    }

    /** Setter for org.eclipse.jgit.api.Status#getChanged() */
    public void setChanged(Set<String> changed) {
        this.changed = changed;
    }

    /** @see org.eclipse.jgit.api.Status#getRemoved() */
    public Set<String> getRemoved() {
        return this.removed;
    }

    /** Setter for org.eclipse.jgit.api.Status#getRemoved() */
    public void setRemoved(Set<String> removed) {
        this.removed = removed;
    }

    /** @see org.eclipse.jgit.api.Status#getMissing() */
    public Set<String> getMissing() {
        return this.missing;
    }

    /** Setter for org.eclipse.jgit.api.Status#getMissing() */
    public void setMissing(Set<String> missing) {
        this.missing = missing;
    }

    /** @see org.eclipse.jgit.api.Status#getModified() */
    public Set<String> getModified() {
        return this.modified;
    }

    /** Setter for org.eclipse.jgit.api.Status#getModified() */
    public void setModified(Set<String> modified) {
        this.modified = modified;
    }

    /** @see org.eclipse.jgit.api.Status#getUntracked() */
    public Set<String> getUntracked() {
        return this.untracked;
    }

    /** Setter for org.eclipse.jgit.api.Status#getUntracked() */
    public void setUntracked(Set<String> untracked) {
        this.untracked = untracked;
    }

    /**
     * @see org.eclipse.jgit.api.Status#getUntrackedFolders() Always empty
     */
    public Set<String> getUntrackedFolders() {
        return Collections.<String> emptySet();
    }

    /** Setter for org.eclipse.jgit.api.Status#getUntrackedFolders() */
    public void setUntrackedFolders(Set<String> untrackedFolders) {
        this.untrackedFolders = untrackedFolders;
    }

    /** @see org.eclipse.jgit.api.Status#getConflicting() */
    public Set<String> getConflicting() {
        return this.conflicting;
    }

    /** Setter for org.eclipse.jgit.api.Status#getConflicting() */
    public void setConflicting(Set<String> conflicting) {
        this.conflicting = conflicting;
    }

    public String createString(Boolean shortFormat) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        if (getShortFormat()) {
            writeShort(writer);
        } else {
            writeLong(writer);
        }

        writer.flush();
        return out.toString("UTF-8");
    }

    /** @see org.exoplatform.ide.git.server.InfoPage#writeTo(java.io.OutputStream) */
    public void writeTo(OutputStream out) throws IOException {
        PrintWriter writer = new PrintWriter(out);

        if (getShortFormat()) {
            writeShort(writer);
        } else {
            writeLong(writer);
        }

        writer.flush();
    }

    /**
     * Writes into out all paths with given status
     * 
     * @param out is a stream to write to
     * @param paths is a set of paths
     * @param status is a status to be written
     */
    private void writeList(PrintWriter out, Set<String> paths, String status) {
        for (String path : paths) {
            if (getShortFormat()) {
                out.format("%s %s\n", status, path);
            } else {
                if (status != null) {
                    out.format("#  %s:   %s\n", status, path);
                } else {
                    out.format("#  %s\n", path);
                }
            }
        }
    }

    /**
     * Equivalent of 'git status --short'
     * 
     * @param out is a stream to write status output.
     * @throws IOException
     */
    private void writeShort(PrintWriter out) throws IOException {
        Map<String, String> dataMap = new LinkedHashMap<String, String>();

        for (String path : getAdded()) {
            dataMap.put(path, "A");
        }

        for (String path : getChanged()) {
            dataMap.put(path, "M");
        }

        for (String path : getRemoved()) {
            dataMap.put(path, "D");
        }

        for (String path : getMissing()) {
            if (dataMap.containsKey(path)) {
                dataMap.put(path, dataMap.get(path) + "D");
            } else {
                dataMap.put(path, "D");
            }
        }

        for (String path : getModified()) {
            if (dataMap.containsKey(path)) {
                dataMap.put(path, dataMap.get(path) + "M");
            } else {
                dataMap.put(path, "M");
            }
        }

        writeList(out, getConflicting(), "U");
        writeList(out, getUntracked(), "??");
    }

    /**
     * Equivalent of 'git status'
     * 
     * @param out is a stream to write status output.
     * @throws IOException
     */
    private void writeLong(PrintWriter out) throws IOException {
        out.format("# On branch %s\n", getBranchName());

        if (isClean()) {
            out.write("nothing to commit, working directory clean\n");
        }

        if (!(getAdded().isEmpty() && getChanged().isEmpty() && getRemoved().isEmpty())) {
            // write changes to be committed
            out.write("# Changes to be committed:\n");
            out.write("#   (use \"git reset HEAD <file>...\" to unstage)\n");
            out.write("#\n");

            writeList(out, getAdded(), "new file");
            writeList(out, getChanged(), "modified");
            writeList(out, getRemoved(), "deleted");
        }

        if (!(getMissing().isEmpty() && getModified().isEmpty())) {
            // write changes not staged for commit
            out.write("#\n");
            out.write("# Changes not staged for commit:\n");
            out.write("#   (use \"git add/rm <file>...\" to update what will be committed)\n");
            out.write("#   (use \"git checkout -- <file>...\" to discard changes in working directory)\n");
            out.write("#\n");

            writeList(out, getMissing(), "deleted");
            writeList(out, getModified(), "modified");
        }

        if (!getConflicting().isEmpty()) {
            // write information about conflicting
            out.write("#\n");
            out.write("# You have unmerged paths.\n");
            out.write("#   (fix conflicts and run \"git commit\")\n");
            out.write("#\n");
            out.write("# Unmerged paths:\n");
            out.write("#   (use \"git add <file>...\" to mark resolution)\n");
            out.write("#\n");

            writeList(out, getConflicting(), "both modified");
        }

        if (!getUntracked().isEmpty()) {
            // write untracked files
            out.write("#\n");
            out.write("# Untracked files:\n");
            out.write("#   (use \"git add <file>...\" to include in what will be committed)\n");
            out.write("#\n");

            writeList(out, getUntracked(), null);

            if (getAdded().isEmpty() && getChanged().isEmpty() && getRemoved().isEmpty() && getMissing().isEmpty()
                && getModified().isEmpty() && getConflicting().isEmpty()) {
                out.write("nothing added to commit but untracked files present (use \"git add\" to track)\n");
            }
        }
    }
}
