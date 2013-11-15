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

/**
 * Git branch description.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Branch.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface Branch {
    /** @return full name of branch, e.g. 'refs/heads/master' */
    String getName();

    /** @return <code>true</code> if branch is checked out and false otherwise */
    boolean isActive();

    /** @return display name of branch, e.g. 'refs/heads/master' -> 'master' */
    String getDisplayName();

    /** @return <code>true</code> if branch is a remote branch */
    boolean isRemote();
    
    Branch withName(String name);
    
    Branch withDisplayName(String displayName);

    Branch withActive(boolean isActive);
    
    Branch withRemote(boolean isRemote);
}