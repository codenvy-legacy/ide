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
package com.codenvy.ide.ext.java.jdt.codeassistant;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.codeassistant.ui.StyledString;
import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.core.dom.AbstractTypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.StubUtility;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.edits.TextEdit;

import java.util.List;

/** If passed compilation unit is not null, the replacement string will be seen as a qualified type name. */
public class JavaTypeCompletionProposal extends JavaCompletionProposalImpl {

    /** The unqualified type name. */
    private final String fUnqualifiedTypeName;

    /** The fully qualified type name. */
    private final String fFullyQualifiedTypeName;

    // public JavaTypeCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
    // Image image, StyledString displayString, int relevance)
    // {
    // this(replacementString, replacementOffset, replacementLength, image, displayString, relevance, null);
    // }
    //
    // public JavaTypeCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
    // Image image, StyledString displayString, int relevance, String fullyQualifiedTypeName)
    // {
    // this(replacementString, replacementOffset, replacementLength, image, displayString, relevance,
    // fullyQualifiedTypeName, null);
    // }

    public JavaTypeCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
                                      Images image, StyledString displayString, int relevance, String fullyQualifiedTypeName,
                                      JavaContentAssistInvocationContext invocationContext) {
        super(replacementString, replacementOffset, replacementLength, image, displayString, relevance, false,
              invocationContext);
        fFullyQualifiedTypeName = fullyQualifiedTypeName;
        fUnqualifiedTypeName = fullyQualifiedTypeName != null ? Signature.getSimpleName(fullyQualifiedTypeName) : null;
    }

    /**
     * Updates the replacement string.
     *
     * @param document
     *         the document
     * @param trigger
     *         the trigger
     * @param offset
     *         the offset
     * @param impRewrite
     *         the import rewrite
     * @return <code>true</code> if the cursor position should be updated, <code>false</code> otherwise
     * @throws BadLocationException
     *         if accessing the document fails
     * @throws CoreException
     *         if something else fails
     */
    protected boolean updateReplacementString(Document document, char trigger, int offset, ImportRewrite impRewrite)
            throws CoreException, BadLocationException {
        // avoid adding imports when inside imports container
        if (impRewrite != null && fFullyQualifiedTypeName != null) {
            String replacementString = getReplacementString();
            String qualifiedType = fFullyQualifiedTypeName;
            if (qualifiedType.indexOf('.') != -1 && replacementString.startsWith(qualifiedType)
                && !replacementString.endsWith(String.valueOf(';'))) {
                // IType[] types= impRewrite.getCompilationUnit().getTypes();
                List<AbstractTypeDeclaration> types = fInvocationContext.getCompilationUnit().types();
                if (types.size() > 0 && types.get(0).getStartPosition() <= offset) {
                    // ignore positions above type.
                    setReplacementString(impRewrite.addImport(getReplacementString()));
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see ICompletionProposalExtension#apply(IDocument, char, int)
     */
    @Override
    public void apply(Document document, char trigger, int offset) {
        try {
            ImportRewrite impRewrite = null;

            if (allowAddingImports()) {
                impRewrite = StubUtility.createImportRewrite(document, fInvocationContext.getCompilationUnit(), true);
            }

            boolean updateCursorPosition = updateReplacementString(document, trigger, offset, impRewrite);

            if (updateCursorPosition)
                setCursorPosition(getReplacementString().length());

            super.apply(document, trigger, offset);

            if (impRewrite != null && impRewrite.getCreatedImports() == null) {
                int oldLen = document.getLength();
                impRewrite.rewriteImports().apply(document, TextEdit.UPDATE_REGIONS);
                setReplacementOffset(getReplacementOffset() + document.getLength() - oldLen);
            }
        } catch (CoreException e) {
            // TODO log exception
            e.printStackTrace(); //NOSOANR

        } catch (BadLocationException e) {
            // TODO log exception
            e.printStackTrace();//NOSONAR
        }
    }

    private boolean allowAddingImports() {
        // IPreferenceStore preferenceStore= JavaPlugin.getDefault().getPreferenceStore();
        // return preferenceStore.getBoolean(PreferenceConstants.CODEASSIST_ADDIMPORT);
        return true;
    }

    // /*
    // * @see org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal#isValidPrefix(java.lang.String)
    // */
    // @Override
    // protected boolean isValidPrefix(String prefix) {
    // return super.isValidPrefix(prefix) || isPrefix(prefix, fUnqualifiedTypeName) || isPrefix(prefix, fFullyQualifiedTypeName);
    // }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal#getCompletionText()
     */
    @Override
    public CharSequence getPrefixCompletionText(Document document, int completionOffset) {
        return fUnqualifiedTypeName;
    }

}
