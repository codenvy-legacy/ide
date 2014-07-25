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
package com.codenvy.vfs.impl.fs.exceptions;

/**
 * This exception occurs in case LocalPathResolver in some reason can't resolve the path.
 *
 * @author Vitaly Parfonov
 */
@SuppressWarnings("serial")
public class LocalPathResolveException extends RuntimeException {

    public LocalPathResolveException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocalPathResolveException(String message) {
        super(message);
    }

}
