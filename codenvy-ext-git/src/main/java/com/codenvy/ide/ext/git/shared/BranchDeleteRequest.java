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
 * Request to delete branch.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchDeleteRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface BranchDeleteRequest extends GitRequest {
    /** @return name of branch to delete */
    String getName();
    
    BranchDeleteRequest withName(String name);

    /** @return if <code>true</code> then delete branch {@link #name} even if it is not fully merged */
    boolean isForce();
    
    BranchDeleteRequest withForce(boolean force);
}