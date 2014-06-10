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
package com.codenvy.ide.ext.java.jdt.compiler.batch;

import com.codenvy.ide.ext.java.jdt.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 34360 2009-07-22 23:58:59Z evgen $
 */
public class CompilationUnit implements ICompilationUnit {
    public char[] contents;

    public char[] fileName;

    public char[] mainTypeName;

    // a specific destination path for this compilation unit; coding is
    // aligned with Main.destinationPath:
    // == null: unspecified, use whatever value is set by the enclosing
    // context, id est Main;
    // == Main.NONE: absorbent element, do not output class files;
    // else: use as the path of the directory into which class files must
    // be written.

    public CompilationUnit(char[] contents, String fileName, String encoding) {
        this(contents, fileName, encoding, null);
    }

    /**
     * @param contents
     * @param string
     * @param encoding
     */
    public CompilationUnit(char[] contents, String fileName, String encoding, String destinationPath) {
        this.contents = contents;
        char[] fileNameCharArray = fileName.toCharArray();
        if (CharOperation.indexOf('\\', fileNameCharArray) != -1) {
            CharOperation.replace(fileNameCharArray, '\\', '/');
        }

        this.fileName = fileNameCharArray;
        int start = CharOperation.lastIndexOf('/', fileNameCharArray) + 1;

        int end = CharOperation.lastIndexOf('.', fileNameCharArray);
        if (end == -1) {
            end = fileNameCharArray.length;
        }

        this.mainTypeName = CharOperation.subarray(fileNameCharArray, start, end);
    }

    /** {@inheritDoc} */
    @Override
    public char[] getFileName() {
        return fileName;
    }

    /** {@inheritDoc} */
    @Override
    public char[] getContents() {
        return contents;
    }

    /** {@inheritDoc} */
    @Override
    public char[] getMainTypeName() {
        return mainTypeName;
    }

    /** {@inheritDoc} */
    @Override
    public char[][] getPackageName() {
        return null;
    }

}
