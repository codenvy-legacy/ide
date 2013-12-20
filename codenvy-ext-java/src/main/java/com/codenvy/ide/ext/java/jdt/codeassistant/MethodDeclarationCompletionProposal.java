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
import com.codenvy.ide.ext.java.jdt.JavaPreferencesSettings;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.codeassistant.ui.StyledString;
import com.codenvy.ide.ext.java.jdt.core.JavaConventions;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.Messages;
import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.core.dom.AbstractTypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.TypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite;
import com.codenvy.ide.ext.java.jdt.core.formatter.CodeFormatter;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.StubUtility;
import com.codenvy.ide.ext.java.jdt.internal.corext.util.CodeFormatterUtil;
import com.codenvy.ide.ext.java.jdt.internal.corext.util.Strings;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.runtime.IStatus;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.TextUtilities;

import java.util.Collection;
import java.util.Set;

/** Method declaration proposal. */
public class MethodDeclarationCompletionProposal extends JavaTypeCompletionProposal {

    public static void evaluateProposals(AbstractTypeDeclaration type, String prefix, int offset, int length,
                                         int relevance, Set<String> suggestedMethods, Collection<JavaCompletionProposal> result,
                                         JavaContentAssistInvocationContext context) {
        if (type instanceof TypeDeclaration) {
            TypeDeclaration t = (TypeDeclaration)type;
            MethodDeclaration[] methods = t.getMethods();
            if (!t.isInterface()) {
                String constructorName = t.getName().getFullyQualifiedName();
                if (constructorName.length() > 0 && constructorName.startsWith(prefix)
                    && !hasMethod(methods, constructorName) && suggestedMethods.add(constructorName)) {
                    result.add(new MethodDeclarationCompletionProposal(t, constructorName, null, offset, length,
                                                                       relevance + 500, context));
                }
            }

            if (prefix.length() > 0
                && !"main".equals(prefix) && !hasMethod(methods, prefix) && suggestedMethods.add(prefix)) { //$NON-NLS-1$
                if (!JavaConventions.validateMethodName(prefix, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
                                                        JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE)).matches(IStatus.ERROR))
                    result.add(new MethodDeclarationCompletionProposal(t, prefix, Signature.SIG_VOID, offset, length,
                                                                       relevance, context));
            }
        }
    }

    private static boolean hasMethod(MethodDeclaration[] methods, String name) {
        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration curr = methods[i];
            if (curr.getName().getFullyQualifiedName().equals(name) && curr.parameters().size() == 0) {
                return true;
            }
        }
        return false;
    }

    private final TypeDeclaration fType;

    private final String fReturnTypeSig;

    private final String fMethodName;

    public MethodDeclarationCompletionProposal(TypeDeclaration type, String methodName, String returnTypeSig, int start,
                                               int length, int relevance, JavaContentAssistInvocationContext context) {
        super("", start, length, null, getDisplayName(methodName, returnTypeSig), relevance, returnTypeSig, context); //$NON-NLS-1$
        Assert.isNotNull(type);
        Assert.isNotNull(methodName);

        fType = type;
        fMethodName = methodName;
        fReturnTypeSig = returnTypeSig;

        if (returnTypeSig == null) {
            //         setProposalInfo(new ProposalInfo(type));

            //         ImageDescriptor desc =
            //            new JavaElementImageDescriptor(JavaPluginImages.DESC_MISC_PUBLIC, JavaElementImageDescriptor.CONSTRUCTOR,
            //               JavaElementImageProvider.SMALL_SIZE);
            setImage(Images.publicMethod);
        } else {
            setImage(Images.privateMethod);
        }
    }

    private static StyledString getDisplayName(String methodName, String returnTypeSig) {
        StyledString buf = new StyledString();
        buf.append(methodName);
        buf.append('(');
        buf.append(')');
        if (returnTypeSig != null) {
            buf.append(" : "); //$NON-NLS-1$
            buf.append(Signature.toString(returnTypeSig));
            buf.append(" - ", StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
            buf.append(Messages.INSTANCE.MethodCompletionProposal_method_label(), StyledString.QUALIFIER_STYLER);
        } else {
            buf.append(" - ", StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
            buf.append(Messages.INSTANCE.MethodCompletionProposal_constructor_label(), StyledString.QUALIFIER_STYLER);
        }
        return buf;
    }

    /* (non-Javadoc)
     * @see JavaTypeCompletionProposal#updateReplacementString(IDocument, char, int, ImportRewrite)
     */
    @Override
    protected boolean updateReplacementString(Document document, char trigger, int offset, ImportRewrite impRewrite)
            throws BadLocationException, CoreException {

        CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings();
        boolean addComments = settings.createComments;

        String[] empty = new String[0];
        String lineDelim = TextUtilities.getDefaultLineDelimiter(document);
        String declTypeName = fType.getName().getFullyQualifiedName();
        boolean isInterface = fType.isInterface();

        StringBuffer buf = new StringBuffer();
        if (addComments) {
            String comment =
                    StubUtility.getMethodComment(declTypeName, fMethodName, empty, empty, fReturnTypeSig, empty, false,
                                                 lineDelim);
            if (comment != null) {
                buf.append(comment);
                buf.append(lineDelim);
            }
        }
        if (fReturnTypeSig != null) {
            if (!isInterface) {
                buf.append("private "); //$NON-NLS-1$
            }
        } else {
            //TODO enum
//         if (fType.isEnum())
//            buf.append("private "); //$NON-NLS-1$
//         else
            buf.append("public "); //$NON-NLS-1$
        }

        if (fReturnTypeSig != null) {
            buf.append(Signature.toString(fReturnTypeSig));
        }
        buf.append(' ');
        buf.append(fMethodName);
        if (isInterface) {
            buf.append("();"); //$NON-NLS-1$
            buf.append(lineDelim);
        } else {
            buf.append("() {"); //$NON-NLS-1$
            buf.append(lineDelim);

            String body =
                    StubUtility.getMethodBodyContent(fReturnTypeSig == null, declTypeName, fMethodName, "", lineDelim);
            if (body != null) {
                buf.append(body);
                buf.append(lineDelim);
            }
            buf.append("}"); //$NON-NLS-1$
            buf.append(lineDelim);
        }
        String stub = buf.toString();

        // use the code formatter
        Region region = document.getLineInformationOfOffset(getReplacementOffset());
        int lineStart = region.getOffset();
        int indent =
                Strings.computeIndentUnits(document.get(lineStart, getReplacementOffset() - lineStart), settings.tabWidth,
                                           settings.indentWidth);

        String replacement = CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, stub, indent, lineDelim);

        if (replacement.endsWith(lineDelim)) {
            replacement = replacement.substring(0, replacement.length() - lineDelim.length());
        }

        setReplacementString(Strings.trimLeadingTabsAndSpaces(replacement));
        return true;
    }

    @Override
    public CharSequence getPrefixCompletionText(Document document, int completionOffset) {
        return new String(); // don't let method stub proposals complete incrementally
    }

    /*
     * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension4#isAutoInsertable()
     */
    public boolean isAutoInsertable() {
        return false;
    }

}
