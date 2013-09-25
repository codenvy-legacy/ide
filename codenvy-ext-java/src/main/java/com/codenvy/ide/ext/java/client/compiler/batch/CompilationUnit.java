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
package com.codenvy.ide.ext.java.client.compiler.batch;

import com.codenvy.ide.ext.java.client.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.client.internal.compiler.env.ICompilationUnit;


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

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IDependent#getFileName() */
    @Override
    public char[] getFileName() {
        return fileName;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.ICompilationUnit#getContents() */
    @Override
    public char[] getContents() {
        return contents;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.ICompilationUnit#getMainTypeName() */
    @Override
    public char[] getMainTypeName() {
        return mainTypeName;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.ICompilationUnit#getPackageName() */
    @Override
    public char[][] getPackageName() {
        return null;
    }

}
