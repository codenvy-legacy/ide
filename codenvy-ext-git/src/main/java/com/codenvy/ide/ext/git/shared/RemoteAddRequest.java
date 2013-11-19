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
 * Request to add remote configuration {@link #name} for repository at {@link #url}.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RemoteAddRequest.java 67651 2011-03-25 16:15:36Z andrew00x $
 */
@DTO
public interface RemoteAddRequest extends GitRequest {
    /** @return remote name */
    String getName();
    
    void setName(String name);
    
    RemoteAddRequest withName(String name);

    /** @return repository url */
    String getUrl();
    
    void setUrl(String url);
    
    RemoteAddRequest withUrl(String url);

    /** @return list of tracked branches in remote repository */
    List<String> getBranches();
    
    void setBranches(List<String> branches);
}