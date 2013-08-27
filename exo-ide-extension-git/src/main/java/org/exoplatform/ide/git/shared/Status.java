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

import java.util.Set;

/**
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 * @version $Id: Status.java 68135 2011-04-08 14:23:36Z andrew00x $
 */
public interface Status {

    public boolean isClean();

    public void setClean(Boolean clean);

    public boolean getShortFormat();

    public void setShortFormat(Boolean shortFormat);

    public String getBranchName();

    public void setBranchName(String branchName);

    public Set<String> getAdded();

    public void setAdded(Set<String> added);

    public Set<String> getChanged();

    public void setChanged(Set<String> changed);

    public Set<String> getRemoved();

    public void setRemoved(Set<String> removed);

    public Set<String> getMissing();

    public void setMissing(Set<String> missing);

    public Set<String> getModified();

    public void setModified(Set<String> modified);

    public Set<String> getUntracked();

    public void setUntracked(Set<String> untracked);

    public Set<String> getUntrackedFolders();

    public void setUntrackedFolders(Set<String> untrackedFolders);

    public Set<String> getConflicting();

    public void setConflicting(Set<String> conflicting);

}
