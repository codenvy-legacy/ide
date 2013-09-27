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
package org.exoplatform.ide.git.shared;

/**
 * Request to show changes between commits. Use {@link #commitA} and {@link #commitB} to specify values for comparison.
 * <ul>
 * <li>If both are omitted then view changes between index and working tree.</li>
 * <li>If both are specified then view changes between two commits.</li>
 * <li>If {@link #commitA} is specified ONLY then behavior is dependent on state of {@link #cached}. If
 * <code>cached==false<code> then view changes between specified commit and working tree. If
 * <code>cached==true<code> then view changes between specified commit and index.</li>
 * </ul>
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: DiffRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class DiffRequest extends GitRequest {
    /** Type of diff output. */
    public enum DiffType {
        /** Only names of modified, added, deleted files. */
        NAME_ONLY {
            @Override
            public String toString() {
                return "--name-only";
            }
        },
        /**
         * Names staus of modified, added, deleted files.
         * <p/>
         * Example:
         * <p/>
         * 
         * <pre>
         * D   README.txt
         * A   HOW-TO.txt
         * </pre>
         */
        NAME_STATUS {
            @Override
            public String toString() {
                return "--name-status";
            }
        },
        RAW {
            @Override
            public String toString() {
                return "--raw";
            }
        }
    }

    /**
     * Filter of file to show diff. It may be either list of file names to show diff or name of directory to show all files under them.
     */
    private String[] fileFilter;

    /**
     * Type of output.
     * 
     * @see DiffType
     */
    private DiffType type      = DiffType.RAW;

    /** Do not show renames in diff output. */
    private boolean  noRenames = true;

    /** Limit of showing renames in diff output. This attribute has sense if {@link #noRenames} is <code>false</code>. */
    private int      renameLimit;

    /** First commit to view changes. */
    private String   commitA;

    /** Second commit to view changes. */
    private String   commitB;

    /**
     * If <code>false</code> (default) view changes between {@link #commitA} and working tree otherwise between {@link #commitA} and index.
     */
    private boolean  cached;

    public DiffRequest(String[] fileFilter, DiffType type, boolean noRenames, int renameLimit, String commitA,
                       String commitB) {
        this.fileFilter = fileFilter;
        this.type = type;
        this.noRenames = noRenames;
        this.renameLimit = renameLimit;
        this.commitA = commitA;
        this.commitB = commitB;
    }

    public DiffRequest(String[] fileFilter, DiffType type, boolean noRenames, int renameLimit, String commitA,
                       boolean cached) {
        this.fileFilter = fileFilter;
        this.type = type;
        this.noRenames = noRenames;
        this.renameLimit = renameLimit;
        this.commitA = commitA;
        this.cached = cached;
    }

    /**
     * @param fileFilter filter of file to show diff. It may be either list of file names to show diff or name of directory to show all
     *            files under them
     * @param type type of diff output
     * @param noRenames do not show renames. Default is <code>true</code>
     * @param renameLimit limit of showing renames in diff output. This attribute has sense if {@link #noRenames} is <code>false</code>
     */
    public DiffRequest(String[] fileFilter, DiffType type, boolean noRenames, int renameLimit) {
        this.fileFilter = fileFilter;
        this.type = type;
        this.noRenames = noRenames;
        this.renameLimit = renameLimit;
    }

    /** "Empty" diff request. Corresponding setters used to setup required parameters. */
    public DiffRequest() {
    }

    /**
     * @return filter of file to show diff. It may be either list of file names or name of directory to show all files under them
     */
    public String[] getFileFilter() {
        return fileFilter;
    }

    /**
     * @param fileFilter filter of file to show diff. It may be either list of file names or name of directory to show all files under them
     */
    public void setFileFilter(String[] fileFilter) {
        this.fileFilter = fileFilter;
    }

    /** @return type of diff output */
    public DiffType getType() {
        return type;
    }

    /**
     * @param type type of diff output
     */
    public void setType(DiffType type) {
        this.type = type;
    }

    /** @return <code>true</code> if renames must not be showing in diff result */
    public boolean isNoRenames() {
        return noRenames;
    }

    /**
     * @param noRenames <code>true</code> if renames must not be showing in diff result
     */
    public void setNoRenames(boolean noRenames) {
        this.noRenames = noRenames;
    }

    /**
     * @return limit of showing renames in diff output. This attribute has sense if {@link #noRenames} is <code>false</code>
     */
    public int getRenameLimit() {
        return renameLimit;
    }

    /**
     * @param renameLimit limit of showing renames in diff output. This attribute has sense if {@link #noRenames} is <code>false</code>
     */
    public void setRenameLimit(int renameLimit) {
        this.renameLimit = renameLimit;
    }

    /** @return first commit to view changes */
    public String getCommitA() {
        return commitA;
    }

    /**
     * @param commitA first commit to view changes
     */
    public void setCommitA(String commitA) {
        this.commitA = commitA;
    }

    /** @return second commit to view changes */
    public String getCommitB() {
        return commitB;
    }

    /**
     * @param commitB second commit to view changes
     */
    public void setCommitB(String commitB) {
        this.commitB = commitB;
    }

    /**
     * @return if <code>false</code> (default) view changes between {@link #commitA} and working tree otherwise between {@link #commitA} and
     *         index
     */
    public boolean isCached() {
        return cached;
    }

    /**
     * @param cached if <code>false</code> (default) view changes between {@link #commitA} and working tree otherwise between
     *            {@link #commitA} and index
     */
    public void setCached(boolean cached) {
        this.cached = cached;
    }
}
