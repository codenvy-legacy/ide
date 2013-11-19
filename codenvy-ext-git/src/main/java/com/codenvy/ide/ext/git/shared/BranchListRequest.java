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
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchListRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface BranchListRequest extends GitRequest {
    /**
     * Show both remote and local branches. <br/>
     * Corresponds to -a option in C git.
     */
    public static final String LIST_ALL    = "a";
    /**
     * Show both remote branches. <br/>
     * Corresponds to -r option in C git.
     */
    public static final String LIST_REMOTE = "r";
    public static final String LIST_LOCAL  = null;

    /** @return branches list mode */
    String getListMode();
    
    void setListMode(String listMode);
    
    BranchListRequest withListMode(String listMode);
}