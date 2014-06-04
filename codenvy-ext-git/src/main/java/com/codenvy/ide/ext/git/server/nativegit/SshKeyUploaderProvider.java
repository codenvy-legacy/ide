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
package com.codenvy.ide.ext.git.server.nativegit;

import com.codenvy.ide.ext.git.server.GitException;

/**
 * Provide functionality to upload Public SSH key part to GitHub, BitBucket, etc.
 *
 * @author Vladyslav Zhukovskii
 */
public interface SshKeyUploaderProvider {
    /**
     * Upload public key part to GitRepository management.
     *
     * @return true if upload was successful, otherwise false
     * @throws GitException
     *         {@link com.codenvy.ide.ext.git.server.NotAuthorizedException} in case if upload to selected site need authorization
     */
    boolean uploadKey() throws GitException;

    /**
     * Check if specified url matched to use current upload provider.
     *
     * @param url
     *         input url to check
     * @return true if current uploader can be applied to upload key to host specified in url, passed as parameter
     */
    boolean match(String url);
}
