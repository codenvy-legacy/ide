/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.refactoring.code;

import com.codenvy.eclipse.core.filebuffers.FileBuffers;
import com.codenvy.eclipse.core.filebuffers.ITextFileBuffer;
import com.codenvy.eclipse.core.filebuffers.LocationKind;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.core.dom.ASTNode;
import com.codenvy.eclipse.jdt.core.dom.Block;
import com.codenvy.eclipse.jdt.core.dom.CompilationUnit;
import com.codenvy.eclipse.jdt.core.dom.MethodDeclaration;
import com.codenvy.eclipse.jdt.internal.core.util.Util;
import com.codenvy.eclipse.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.eclipse.jdt.internal.corext.dom.Selection;
import com.codenvy.eclipse.jdt.internal.corext.dom.SelectionAnalyzer;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.jdt.internal.corext.util.Messages;
import com.codenvy.eclipse.jdt.internal.corext.util.Strings;
import com.codenvy.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringStatus;

import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IRegion;

public class CodeRefactoringUtil {

    public static RefactoringStatus checkMethodSyntaxErrors(int selectionStart, int selectionLength,
                                                            CompilationUnit cuNode, String invalidSelectionMessage) {
        SelectionAnalyzer analyzer = new SelectionAnalyzer(
                Selection.createFromStartLength(selectionStart, selectionLength), true);
        cuNode.accept(analyzer);
        ASTNode coveringNode = analyzer.getLastCoveringNode();
        if (!(coveringNode instanceof Block) || !(coveringNode.getParent() instanceof MethodDeclaration)) {
            return RefactoringStatus.createFatalErrorStatus(invalidSelectionMessage);
        }
        if (ASTNodes.getMessages(coveringNode, ASTNodes.NODE_ONLY).length == 0) {
            return RefactoringStatus.createFatalErrorStatus(invalidSelectionMessage);
        }

        MethodDeclaration methodDecl = (MethodDeclaration)coveringNode.getParent();
        String message = Messages.format(RefactoringCoreMessages.CodeRefactoringUtil_error_message,
                                         BasicElementLabels.getJavaElementName(methodDecl.getName().getIdentifier()));
        return RefactoringStatus.createFatalErrorStatus(message);
    }

    public static int getIndentationLevel(ASTNode node, ICompilationUnit unit) throws CoreException {
        IPath fullPath = unit.getCorrespondingResource().getFullPath();
        try {
            FileBuffers.getTextFileBufferManager().connect(fullPath, LocationKind.IFILE, new NullProgressMonitor());
            ITextFileBuffer buffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(fullPath,
                                                                                              LocationKind.IFILE);
            try {
                IRegion region = buffer.getDocument().getLineInformationOfOffset(node.getStartPosition());
                return Strings.computeIndentUnits(buffer.getDocument().get(region.getOffset(), region.getLength()),
                                                  unit.getJavaProject());
            } catch (BadLocationException exception) {
                Util.log(exception);
            }
            return 0;
        } finally {
            FileBuffers.getTextFileBufferManager().disconnect(fullPath, LocationKind.IFILE, new NullProgressMonitor());
        }
    }

    private CodeRefactoringUtil() {
    }
}
