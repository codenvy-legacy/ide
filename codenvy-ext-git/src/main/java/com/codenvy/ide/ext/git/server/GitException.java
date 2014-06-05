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
package com.codenvy.ide.ext.git.server;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitException.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@SuppressWarnings("serial")
public class GitException extends Exception {
    protected GitException() {
    }

    /**
     * @param message error message
     */
    public GitException(String message) {
        super(message);
    }

    /**
     * @param cause cause
     */
    public GitException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message error message
     * @param cause cause
     */
    public GitException(String message, Throwable cause) {
        super(message, cause);
    }
}
