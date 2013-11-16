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
 * Request to update tracked repositories.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RemoteUpdateRequest.java 67651 2011-03-25 16:15:36Z andrew00x $
 */
@DTO
public interface RemoteUpdateRequest extends GitRequest {
    /** @return remote name */
    String getName();

    /** @return list tracked branches */
    List<String> getBranches();

    /**
     * @return if <code>true</code> then {@link #branches} instead of replacing the list of currently tracked branches,
     *         added to that list
     */
    boolean isAddBranches();

    /** @return remote URLs to be added */
    List<String> getAddUrl();

    /** @return remote URLs to be removed */
    List<String> getRemoveUrl();

    /** @return remote push URLs to be added */
    List<String> getAddPushUrl();

    /** @return remote push URLs to be removed */
    List<String> getRemovePushUrl();
}