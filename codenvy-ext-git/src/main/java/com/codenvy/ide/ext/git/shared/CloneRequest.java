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

/**
 * Clone repository to {@link #workingDir}.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CloneRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
@DTO
public interface CloneRequest extends GitRequest {
    /** @return URI of repository to be cloned */
    String getRemoteUri();
    
    CloneRequest withRemoteUri(String remoteUri);

    /** @return list of remote branches to fetch in cloned repository */
    List<String> getBranchesToFetch();
    
    CloneRequest withBranchesToFetch(List<String> branchesToFetch);

    /** @return work directory for cloning */
    String getWorkingDir();
    
    void setWorkingDir(String workingDir);
    
    CloneRequest withWorkingDir(String workingDir);

    /** @return remote name. If <code>null</code> then 'origin' will be used */
    String getRemoteName();
    
    CloneRequest withRemoteName(String remoteName);

    /**
     * @return time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository. If 0 then
     *         default timeout may be used. This is implementation specific
     */
    int getTimeout();
    
    CloneRequest withTimeout(int timeout);
}