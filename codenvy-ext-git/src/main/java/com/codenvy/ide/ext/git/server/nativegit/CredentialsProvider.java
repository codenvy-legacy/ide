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
 * Provides credentials to use with git commands that need it
 *
 * @author Eugene Voevodin
 */
public interface CredentialsProvider {
    public boolean get(String url, CredentialItem... items) throws GitException;

    /** Provides the information about authenticated user over the specified OAuth provider. */
    public boolean getUser(String url, CredentialItem... items) throws GitException;
}
