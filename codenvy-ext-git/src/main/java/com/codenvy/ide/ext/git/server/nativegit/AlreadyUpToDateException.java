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
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class AlreadyUpToDateException extends GitException {

    public AlreadyUpToDateException() {
        this("Already up-to-date.");
    }

    /**
     * @param message error message
     */
    public AlreadyUpToDateException(String message) {
        super(message);
    }

    /**
     * @param cause cause
     */
    public AlreadyUpToDateException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message error message
     * @param cause cause
     */
    public AlreadyUpToDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
