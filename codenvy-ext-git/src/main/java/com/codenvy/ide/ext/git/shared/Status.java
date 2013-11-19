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

import com.codenvy.dto.shared.DTO;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 * @version $Id: Status.java 68135 2011-04-08 14:23:36Z andrew00x $
 */
@DTO
public interface Status {
    boolean isClean();

    void setClean(boolean isClean);
    
    boolean isShortFormat();
    
    void setShortFormat(boolean isShort);

    String getBranchName();
    
    void setBranchName(String branchName);

    List<String> getAdded();
    
    void setAdded(List<String> added);

    List<String> getChanged();
    
    void setChanged(List<String> changed);

    List<String> getRemoved();
    
    void setRemoved(List<String> removed);

    List<String> getMissing();
    
    void setMissing(List<String> missing);

    List<String> getModified();
    
    void setModified(List<String> modified);

    List<String> getUntracked();
    
    void setUntracked(List<String> untracked);

    List<String> getUntrackedFolders();
    
    void setUntrackedFolders(List<String> untrackedFolders);

    List<String> getConflicting();
    
    void setConflicting(List<String> added);
}