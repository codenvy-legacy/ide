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

/**
 * Request to pull (fetch and merge) changes from remote repository to local branch.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PullRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface PullRequest extends GitRequest {
    /** @return refspec to fetch */
    String getRefSpec();
    
    void setRefSpec(String refSpec);
    
    PullRequest withRefSpec(String refSpec);

    /** @return remote name. If <code>null</code> then 'origin' will be used */
    String getRemote();
    
    void setRemote(String remote);
    
    PullRequest withRemote(String remote);

    /** @return time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository */
    int getTimeout();
    
    void setTimeout(int timeout);
    
    PullRequest withTimeout(int timeout);
}