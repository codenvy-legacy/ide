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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.vfs.impl.fs.exceptions.LocalPathResolveException;

import javax.inject.Singleton;

/**
 * Resolves location of virtual filesystem item on local filesystem.
 *
 * @author Vitaly Parfonov
 */
@Singleton
public class LocalPathResolver {
    public String resolve(VirtualFile virtualFile) {
        if (!(virtualFile instanceof VirtualFileImpl)) {
            throw new LocalPathResolveException(String.format("Cannot resolve path on the local filesystem for %s", virtualFile));
        }
        return ((VirtualFileImpl)virtualFile).getIoFile().getAbsolutePath();
    }
}
