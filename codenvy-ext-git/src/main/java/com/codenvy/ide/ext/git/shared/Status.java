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
package com.codenvy.ide.ext.git.shared;

import com.codenvy.ide.dto.DTO;

import java.util.Set;

/**
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 * @version $Id: Status.java 68135 2011-04-08 14:23:36Z andrew00x $
 */
@DTO
public interface Status {
    boolean isClean();

    void setClean(Boolean isClean);
    
    boolean isShortFormat();
    
    void setShortFormat(Boolean isShort);

    String getBranchName();
    
    void setBranchName(String branchName);

    Set<String> getAdded();
    
    void setAdded(Set<String> added);

    Set<String> getChanged();
    
    void setChanged(Set<String> changed);

    Set<String> getRemoved();
    
    void setRemoved(Set<String> removed);

    Set<String> getMissing();
    
    void setMissing(Set<String> missing);

    Set<String> getModified();
    
    void setModified(Set<String> modified);

    Set<String> getUntracked();
    
    void setUntracked(Set<String> untracked);

    Set<String> getUntrackedFolders();
    
    void setUntrackedFolders(Set<String> untrackedFolders);

    Set<String> getConflicting();
    
    void setConflicting(Set<String> added);
}