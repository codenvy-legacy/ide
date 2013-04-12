/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.internal.corext.fix;

import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.ITypeBinding;
import org.eclipse.jdt.client.core.dom.Type;
import org.eclipse.jdt.client.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.client.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.client.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.jdt.client.internal.corext.refactoring.code.CompilationUnitChange;
import org.eclipse.jdt.client.internal.corext.refactoring.structure.CompilationUnitRewrite;
import org.eclipse.jdt.client.ltk.refactoring.GroupCategory;
import org.eclipse.jdt.client.ltk.refactoring.GroupCategorySet;
import org.eclipse.jdt.client.runtime.CoreException;
import org.eclipse.jdt.client.runtime.IProgressMonitor;
import org.eclipse.jdt.client.runtime.IStatus;
import org.eclipse.jdt.client.runtime.Status;
import org.exoplatform.ide.editor.shared.runtime.Assert;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.TextEditGroup;

public class CompilationUnitRewriteOperationsFix extends AbstractFix {

    public abstract static class CompilationUnitRewriteOperation {

        public abstract void rewriteAST(CompilationUnitRewrite cuRewrite) throws CoreException;

        protected Type importType(final ITypeBinding toImport, final ASTNode accessor, ImportRewrite imports,
                                  final CompilationUnit compilationUnit) {
            ImportRewriteContext importContext =
                    new ContextSensitiveImportRewriteContext(compilationUnit, accessor.getStartPosition(), imports);
            return imports.addImport(toImport, compilationUnit.getAST(), importContext);
        }

        protected TextEditGroup createTextEditGroup(String label, CompilationUnitRewrite rewrite) {
            if (label.length() > 0) {
                return rewrite.createCategorizedGroupDescription(label, new GroupCategorySet(new GroupCategory(label,
                                                                                                               label, label)));
            } else {
                return rewrite.createGroupDescription(label);
            }
        }

        public String getAdditionalInfo() {
            return null;
        }
    }

    private final CompilationUnitRewriteOperation[] fOperations;

    private final CompilationUnit fCompilationUnit;

    private final IDocument document;

    //	private final LinkedProposalModel fLinkedProposalModel;

    public CompilationUnitRewriteOperationsFix(String name, CompilationUnit compilationUnit,
                                               CompilationUnitRewriteOperation operation, IDocument document) {
        this(name, compilationUnit, new CompilationUnitRewriteOperation[]{operation}, document);
        Assert.isNotNull(operation);
    }

    public CompilationUnitRewriteOperationsFix(String name, CompilationUnit compilationUnit,
                                               CompilationUnitRewriteOperation[] operations, IDocument document) {
        super(name);
        this.document = document;
        Assert.isNotNull(operations);
        Assert.isLegal(operations.length > 0);
        fCompilationUnit = compilationUnit;
        fOperations = operations;
        //		fLinkedProposalModel= new LinkedProposalModel();
    }

    //	/**
    //	 * {@inheritDoc}
    //	 */
    //	@Override
    //	public LinkedProposalModel getLinkedPositions() {
    //		if (!fLinkedProposalModel.hasLinkedPositions())
    //			return null;
    //
    //		return fLinkedProposalModel;
    //	}

    /** {@inheritDoc} */
    public CompilationUnitChange createChange(IProgressMonitor progressMonitor) throws CoreException {
        CompilationUnitRewrite cuRewrite = new CompilationUnitRewrite(fCompilationUnit, document);

        //		fLinkedProposalModel.clear();
        for (int i = 0; i < fOperations.length; i++) {
            CompilationUnitRewriteOperation operation = fOperations[i];
            operation.rewriteAST(cuRewrite);
        }

        CompilationUnitChange result = cuRewrite.createChange(getDisplayString(), true, null);
        if (result == null)
            throw new CoreException(new Status(IStatus.ERROR, JavaCore.PLUGIN_ID,
                                               FixMessages.INSTANCE
                                                          .CompilationUnitRewriteOperationsFix_nullChangeError(getDisplayString())));

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String getAdditionalProposalInfo() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fOperations.length; i++) {
            CompilationUnitRewriteOperation operation = fOperations[i];
            String info = operation.getAdditionalInfo();
            if (info != null)
                sb.append(info);
        }

        if (sb.length() == 0)
            return null;

        return sb.toString();
    }

}
