/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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