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
package org.eclipse.jdt.client.internal.corext.refactoring.util;

import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ArrayInitializer;

import org.eclipse.jdt.client.internal.corext.dom.Selection;
import org.eclipse.jdt.client.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.jdt.client.ltk.refactoring.RefactoringStatus;
import org.eclipse.jdt.client.runtime.CoreException;
import org.exoplatform.ide.editor.shared.text.IDocument;

public class CodeAnalyzer extends StatementAnalyzer {

    public CodeAnalyzer(IDocument document, Selection selection, boolean traverseSelectedNode) throws CoreException {
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
