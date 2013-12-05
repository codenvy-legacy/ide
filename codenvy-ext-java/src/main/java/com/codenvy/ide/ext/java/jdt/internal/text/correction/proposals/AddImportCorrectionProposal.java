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
import com.codenvy.ide.ext.java.jdt.codeassistant.QualifiedTypeNameHistory;
import com.codenvy.ide.ext.java.jdt.core.dom.SimpleName;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;


public class AddImportCorrectionProposal extends ASTRewriteCorrectionProposal {

    private final String fTypeName;

    private final String fQualifierName;

    public AddImportCorrectionProposal(String name, int relevance, Document document, Images image, String qualifierName,
                                       String typeName, SimpleName node) {
        super(name, ASTRewrite.create(node.getAST()), relevance, document, image);
        fTypeName = typeName;
        fQualifierName = qualifierName;
    }

    public String getQualifiedTypeName() {
        return fQualifierName + '.' + fTypeName;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.ChangeCorrectionProposal#performChange(org.eclipse.ui.IEditorPart,
     * org.eclipse.jface.text.IDocument)
     */
    @Override
    protected void performChange(Document document) throws CoreException {
        super.performChange(document);
        rememberSelection();
    }

    private void rememberSelection() {
        QualifiedTypeNameHistory.remember(getQualifiedTypeName());
    }

}
