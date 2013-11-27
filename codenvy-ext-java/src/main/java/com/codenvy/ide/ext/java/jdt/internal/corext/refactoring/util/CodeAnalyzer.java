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
package com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.util;

import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.ArrayInitializer;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.Selection;
import com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.ide.ext.java.jdt.refactoring.RefactoringStatus;

import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;


public class CodeAnalyzer extends StatementAnalyzer {

    public CodeAnalyzer(Document document, Selection selection, boolean traverseSelectedNode) throws CoreException {
        super(document, selection, traverseSelectedNode);
    }

    @Override
    protected final void checkSelectedNodes() {
        super.checkSelectedNodes();
        RefactoringStatus status = getStatus();
        if (status.hasFatalError())
            return;
        ASTNode node = getFirstSelectedNode();
        if (node instanceof ArrayInitializer) {
            status.addFatalError(RefactoringCoreMessages.INSTANCE.CodeAnalyzer_array_initializer());
        }
    }
}
