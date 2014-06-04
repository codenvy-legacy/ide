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

import java.util.List;

/**
 * Request to update tracked repositories.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RemoteUpdateRequest.java 67651 2011-03-25 16:15:36Z andrew00x $
 */
@DTO
public interface RemoteUpdateRequest extends GitRequest {
    /** @return remote name */
    String getName();
    
    void setName(String name);

    /** @return list tracked branches */
    List<String> getBranches();

    void setBranches(List<String> branches);
    
    /**
     * @return if <code>true</code> then {@link #branches} instead of replacing the list of currently tracked branches,
     *         added to that list
     */
    boolean isAddBranches();
    
    void setAddBranches(boolean isAddBranches);

    /** @return remote URLs to be added */
    List<String> getAddUrl();
    
    void setAddUrl(List<String> addUrl);

    /** @return remote URLs to be removed */
    List<String> getRemoveUrl();

    void setRemoveUrl(List<String> removeUrl);
    
    /** @return remote push URLs to be added */
    List<String> getAddPushUrl();
    
    void setAddPushUrl(List<String> addPushUrl);

    /** @return remote push URLs to be removed */
    List<String> getRemovePushUrl();
    
    void setRemovePushUrl(List<String> removePushUrl);
}