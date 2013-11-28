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
 * Describe single commit.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Revision.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface Revision {
    /**
     * Parameter which shows that this revision is a fake revision (i.e. TO for Exception)
     *
     * @return
     */
    boolean isFake();

    /** @return branch name */
    String getBranch();
    
    void setBranch(String branch);
    
    Revision withBranch(String branch);

    /** @return commit id */
    String getId();
    
    void setId(String id);
    
    Revision withId(String id);

    /** @return commit message */
    String getMessage();
    
    void setMessage(String message);
    
    Revision withMessage(String message);

    /** @return time of commit */
    long getCommitTime();
    
    Revision withCommitTime(long time);

    /** @return committer */
    GitUser getCommitter();
    
    Revision withCommitter(GitUser user);
}