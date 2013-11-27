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
package com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.NodeFinder;
import com.codenvy.ide.ext.java.jdt.core.dom.SimpleName;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.LinkedNodeFinder;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.edits.ReplaceEdit;
import com.codenvy.ide.text.edits.TextEdit;


public class RenameNodeCorrectionProposal extends CUCorrectionProposal {

    private String fNewName;

    private int fOffset;

    private int fLength;

    private final CompilationUnit unit;

    public RenameNodeCorrectionProposal(String name, CompilationUnit cu, int offset, int length, String newName,
                                        int relevance, Document document) {
        super(name, relevance, document, Images.correction_change);
        this.unit = cu;
        fOffset = offset;
        fLength = length;
        fNewName = newName;
    }

    /*(non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.CUCorrectionProposal#addEdits(org.eclipse.jface.text.IDocument)
     */
    @Override
    protected void addEdits(Document doc, TextEdit root) throws CoreException {
        super.addEdits(doc, root);

        ASTNode name = NodeFinder.perform(unit, fOffset, fLength);
        if (name instanceof SimpleName) {

            SimpleName[] names = LinkedNodeFinder.findByProblems(unit, (SimpleName)name);
            if (names != null) {
                for (int i = 0; i < names.length; i++) {
                    SimpleName curr = names[i];
                    root.addChild(new ReplaceEdit(curr.getStartPosition(), curr.getLength(), fNewName));
                }
                return;
            }
        }
        root.addChild(new ReplaceEdit(fOffset, fLength, fNewName));
    }
}
