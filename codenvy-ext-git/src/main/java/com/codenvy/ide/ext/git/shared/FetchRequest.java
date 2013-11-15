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
 * Request to fetch data from remote repository.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FetchRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
@DTO
public interface FetchRequest extends GitRequest {
    /** @return list of refspec to fetch */
    String[] getRefSpec();
    
    FetchRequest withRefSpec(String[] refSpec);

    /** @return remote name. If <code>null</code> then 'origin' will be used */
    String getRemote();
    
    FetchRequest withRemote(String remote);

    /** @return <code>true</code> if local refs must be deleted if they deleted in remote repository and <code>false</code> otherwise */
    boolean isRemoveDeletedRefs();
    
    FetchRequest withRemoveDeletedRefs(boolean isRemoveDeletedRefs);

    /** @return time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository */
    int getTimeout();
    
    FetchRequest withTimeout(int timeout);
}