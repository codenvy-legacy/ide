/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.sorround;

import com.codenvy.ide.ext.java.jdt.core.dom.BodyDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ASTResolving;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.Selection;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;


public class SurroundWithTryCatchAnalyzer extends SurroundWithAnalyzer {
    private ITypeBinding[] fExceptions;

    public SurroundWithTryCatchAnalyzer(Document document, Selection selection) throws CoreException {
        super(document, selection);
    }

    public ITypeBinding[] getExceptions() {
        return fExceptions;
    }

    @Override
    public void endVisit(CompilationUnit node) {
        BodyDeclaration enclosingNode = null;
        if (!getStatus().hasFatalError() && hasSelectedNodes())
            enclosingNode = ASTResolving.findParentBodyDeclaration(getFirstSelectedNode());

        super.endVisit(node);
        if (enclosingNode != null && !getStatus().hasFatalError()) {
            fExceptions = ExceptionAnalyzer.perform(enclosingNode, getSelection());
            if (fExceptions == null || fExceptions.length == 0) {
                fExceptions = new ITypeBinding[]{node.getAST().resolveWellKnownType("java.lang.Exception")}; //$NON-NLS-1$
            }
        }
    }
}
