/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Nathan Beyer (Cerner) <nbeyer@cerner.com> - [content assist][5.0] when selected method from favorites is a member of a type with a
 *     type variable an invalid static import is added - https://bugs.eclipse.org/bugs/show_bug.cgi?id=202221
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.codeassistant;

import com.codenvy.ide.ext.java.jdt.core.CompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.StubUtility;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.edits.TextEdit;


/** Completion proposal for required imports. */
public class ImportCompletionProposal extends AbstractJavaCompletionProposal {

    private final int fParentProposalKind;

    private ImportRewrite fImportRewrite;

    private ContextSensitiveImportRewriteContext fImportContext;

    private final CompletionProposal fProposal;

    private boolean fReplacementStringComputed;

    private CompilationUnit fCompilationUnit;

    private Document document;

    public ImportCompletionProposal(Document document, CompilationUnit fCompilationUnit, CompletionProposal proposal,
                                    JavaContentAssistInvocationContext context, int parentProposalKind) {
        super(context);
        fProposal = proposal;
        fParentProposalKind = parentProposalKind;
        this.fCompilationUnit = fCompilationUnit;
        this.document = document;
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal#getReplacementString()
     */
    @Override
    public final String getReplacementString() {
        if (!fReplacementStringComputed) {
            setReplacementString(computeReplacementString());
        }
        return super.getReplacementString();
    }

    /**
     * Computes the replacement string.
     *
     * @return the replacement string
     */
    private String computeReplacementString() {
        int proposalKind = fProposal.getKind();
        String qualifiedTypeName = null;
        char[] qualifiedType = null;
        if (proposalKind == CompletionProposal.TYPE_IMPORT) {
            qualifiedType = fProposal.getSignature();
            qualifiedTypeName = String.valueOf(Signature.toCharArray(qualifiedType));
        } else if (proposalKind == CompletionProposal.METHOD_IMPORT || proposalKind == CompletionProposal.FIELD_IMPORT) {
            qualifiedType = Signature.getTypeErasure(fProposal.getDeclarationSignature());
            qualifiedTypeName = String.valueOf(Signature.toCharArray(qualifiedType));
        } else {
         /*
          * In 3.3 we only support the above import proposals, see CompletionProposal#getRequiredProposals()
          */
            Assert.isTrue(false);
        }

      /* Add imports if the preference is on. */
        fImportRewrite = createImportRewrite();
        if (fImportRewrite != null) {
            if (proposalKind == CompletionProposal.TYPE_IMPORT) {
                String simpleType = fImportRewrite.addImport(qualifiedTypeName, fImportContext);
                if (fParentProposalKind == CompletionProposal.METHOD_REF) {
                    return simpleType + "."; //$NON-NLS-1$
                }
            } else {
                String res =
                        fImportRewrite.addStaticImport(qualifiedTypeName, String.valueOf(fProposal.getName()),
                                                       proposalKind == CompletionProposal.FIELD_IMPORT, fImportContext);
                int dot = res.lastIndexOf('.');
                if (dot != -1) {
                    String typeName = fImportRewrite.addImport(res.substring(0, dot), fImportContext);
                    return typeName + '.';
                }
            }
            return ""; //$NON-NLS-1$
        }

        // Case where we don't have an import rewrite (see allowAddingImports)
        // TODO
        // if (fCompilationUnit != null && JavaModelUtil.isImplicitImport(Signature.getQualifier(qualifiedTypeName),
        // fCompilationUnit)) {
        // /* No imports for implicit imports. */
        //
        // if (fProposal.getKind() == CompletionProposal.TYPE_IMPORT && fParentProposalKind == CompletionProposal.FIELD_REF)
        //            return ""; //$NON-NLS-1$
        // qualifiedTypeName= String.valueOf(Signature.getSignatureSimpleName(qualifiedType));
        // }

        return qualifiedTypeName + "."; //$NON-NLS-1$
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal#apply(org.eclipse.jface.text.Document, char, int)
     */
    @Override
    public void apply(Document document, char trigger, int offset) {
        try {
            super.apply(document, trigger, offset);

            if (fImportRewrite != null && fImportRewrite.hasRecordedChanges()) {
                int oldLen = document.getLength();
                fImportRewrite.rewriteImports().apply(document, TextEdit.UPDATE_REGIONS);
                setReplacementOffset(getReplacementOffset() + document.getLength() - oldLen);
            }
        } catch (BadLocationException e) {
            // TODO log error
            e.printStackTrace();//NOSONAR
        }
    }

    /**
     * Creates and returns the import rewrite if imports should be added at all.
     *
     * @return the import rewrite or <code>null</code> if no imports can or should be added
     */
    private ImportRewrite createImportRewrite() {
        if (fCompilationUnit != null && shouldAddImports()) {

            ImportRewrite rewrite = StubUtility.createImportRewrite(document, fCompilationUnit, true);
            fImportContext =
                    new ContextSensitiveImportRewriteContext(fCompilationUnit, fInvocationContext.getInvocationOffset(),
                                                             rewrite);
            return rewrite;
        }
        return null;
    }

    // private CompilationUnit getASTRoot(ICompilationUnit compilationUnit) {
    // return SharedASTProvider.getAST(compilationUnit, SharedASTProvider.WAIT_NO, null);
    // }

    /**
     * Returns <code>true</code> if imports should be added. The return value depends on the context and preferences only and does
     * not take into account the contents of the compilation unit or the kind of proposal. Even if <code>true</code> is returned,
     * there may be cases where no imports are added for the proposal. For example:
     * <ul>
     * <li>when completing within the import section</li>
     * <li>when completing informal javadoc references (e.g. within <code>&lt;code&gt;</code> tags)</li>
     * <li>when completing a type that conflicts with an existing import</li>
     * <li>when completing an implicitly imported type (same package, <code>java.lang</code> types)</li>
     * </ul>
     * <p>
     * The decision whether a qualified type or the simple type name should be inserted must take into account these different
     * scenarios.
     * </p>
     *
     * @return <code>true</code> if imports may be added, <code>false</code> if not
     */
    private boolean shouldAddImports() {
        if (isInJavadoc() && !isJavadocProcessingEnabled()) {
            return false;
        }
        //
        // IPreferenceStore preferenceStore= JavaPlugin.getDefault().getPreferenceStore();
        // return preferenceStore.getBoolean(PreferenceConstants.CODEASSIST_ADDIMPORT);
        return true;
    }

    /**
     * Returns whether Javadoc processing is enabled.
     *
     * @return <code>true</code> if Javadoc processing is enabled, <code>false</code> otherwise
     */
    private boolean isJavadocProcessingEnabled() {
        // IJavaProject project= fCompilationUnit.getJavaProject();
        boolean processJavadoc;
        // if (project == null)
        processJavadoc = JavaCore.ENABLED.equals(JavaCore.getOption(JavaCore.COMPILER_DOC_COMMENT_SUPPORT));
        // else
        // processJavadoc= JavaCore.ENABLED.equals(project.getOption(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, true));
        return processJavadoc;
    }

    /** @see com.codenvy.ide.editor.api.contentassist.CompletionProposal#isAutoInsertable() */
    @Override
    public boolean isAutoInsertable() {
        return true;
    }
}
