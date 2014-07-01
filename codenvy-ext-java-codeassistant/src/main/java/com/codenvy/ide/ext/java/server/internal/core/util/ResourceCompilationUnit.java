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
package com.codenvy.ide.ext.java.server.internal.core.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * An ICompilationUnit that retrieves its contents using an IFile
 */
public class ResourceCompilationUnit extends CompilationUnit {

    private File file;

    public ResourceCompilationUnit(File file) {
        super(null/*no contents*/, file.getAbsolutePath(),
              null/*encoding is used only when retrieving the contents*/);
        this.file = file;
    }

    public char[] getContents() {
        if (this.contents != null)
            return this.contents;   // answer the cached source

        // otherwise retrieve it
        try {
            // Get resource contents
            InputStream stream= null;
            try {
                stream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new JavaModelException(e, IJavaModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
            }
            try {
                return org.eclipse.jdt.internal.compiler.util.Util.getInputStreamAsCharArray(stream, (int) file.length(), "UTF-8");
            } catch (IOException e ) {
                throw new JavaModelException(e, IJavaModelStatusConstants.IO_EXCEPTION);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        } catch (CoreException e) {
            return CharOperation.NO_CHAR;
        }
    }
}
