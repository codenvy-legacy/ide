/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.core.util;

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.jdt.core.compiler.CharOperation;
import com.codenvy.eclipse.jdt.internal.compiler.batch.CompilationUnit;

import java.net.URI;


/** An ICompilationUnit that retrieves its contents using an IFile */
public class ResourceCompilationUnit extends CompilationUnit {

    private IFile file;

    public ResourceCompilationUnit(IFile file, URI location) {
        super(null/*no contents*/, location == null ? file.getFullPath().toString() : location.getPath(),
              null/*encoding is used only when retrieving the contents*/);
        this.file = file;
    }

    public char[] getContents() {
        if (this.contents != null) {
            return this.contents;   // answer the cached source
        }

        // otherwise retrieve it
        try {
            return Util.getResourceContentsAsCharArray(this.file);
        } catch (CoreException e) {
            return CharOperation.NO_CHAR;
        }
    }
}
