/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.codeassist.complete;

import com.codenvy.ide.ext.java.jdt.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.ImportReference;

/**
 * Completion node build by the parser in any case it was intending to
 * reduce an package statement containing the cursor location.
 * e.g.
 * <p/>
 * package java.io[cursor];
 * class X {
 * void foo() {
 * }
 * }
 * <p/>
 * ---> <CompleteOnPackage:java.io>
 * class X {
 * void foo() {
 * }
 * }
 * <p/>
 * The source range is always of length 0.
 * The arguments of the allocation expression are all the arguments defined
 * before the cursor.
 */
public class CompletionOnPackageReference extends ImportReference {
    public CompletionOnPackageReference(char[][] tokens, long[] positions) {
        super(tokens, positions, false, ClassFileConstants.AccDefault);
    }

    public StringBuffer print(int indent, StringBuffer output, boolean withOnDemand) {

        printIndent(indent, output).append("<CompleteOnPackage:"); //$NON-NLS-1$
        for (int i = 0; i < this.tokens.length; i++) {
            if (i > 0)
                output.append('.');
            output.append(this.tokens[i]);
        }
        return output.append('>');
    }
}
