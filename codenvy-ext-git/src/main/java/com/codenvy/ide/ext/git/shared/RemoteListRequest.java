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
 * Request to get list of remotes. If {@link #remote} is specified then info about this remote only given.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RemoteListRequest.java 68015 2011-04-06 09:21:31Z anya $
 */
@DTO
public interface RemoteListRequest extends GitRequest {
    /** @return if <code>true</code> show remote url and name otherwise show remote name only */
    boolean verbose();

    /** @return remote name */
    String getRemote();
}