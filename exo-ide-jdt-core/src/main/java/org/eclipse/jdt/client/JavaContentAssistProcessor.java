/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.eclipse.jdt.client;

import com.codenvy.ide.client.util.logging.Log;

import org.eclipse.jdt.client.codeassistant.AbstractJavaCompletionProposal;
import org.eclipse.jdt.client.codeassistant.CompletionProposalCollector;
import org.eclipse.jdt.client.codeassistant.FillArgumentNamesCompletionProposalCollector;
import org.eclipse.jdt.client.codeassistant.JavaContentAssistInvocationContext;
import org.eclipse.jdt.client.codeassistant.LazyGenericTypeProposal;
import org.eclipse.jdt.client.codeassistant.TemplateCompletionProposalComputer;
import org.eclipse.jdt.client.codeassistant.api.IJavaCompletionProposal;
import org.eclipse.jdt.client.compiler.batch.CompilationUnit;
import org.eclipse.jdt.client.core.IJavaElement;
import org.eclipse.jdt.client.core.IType;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.Signature;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.event.CancelParseEvent;
import org.eclipse.jdt.client.internal.codeassist.CompletionEngine;
import org.eclipse.jdt.client.internal.compiler.flow.UnconditionalFlowInfo.AssertionFailedException;
import org.eclipse.jdt.client.runtime.NullProgressMonitor;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaContentAssistProcessor implements ContentAssistProcessor {

    private static final TemplateCompletionProposalComputer templateCompletionProposalComputer =
            new TemplateCompletionProposalComputer();

    /** {@inheritDoc} */
    @Override
    public CompletionProposal[] computeCompletionProposals(Editor viewer, int offset) {
        try {
        IDE.fireEvent(new CancelParseEvent());

        IDocument document = viewer.getDocument();
//         if (!useOldAST)
//         {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(document.get().toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        FileModel currentFile = JavaCodeController.get().getActiveFile();
        parser.setUnitName(currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')));
        parser.setNameEnvironment(new NameEnvironment(currentFile.getProject().getId()));
        parser.setResolveBindings(true);
        ASTNode ast = parser.createAST(null);
        org.eclipse.jdt.client.core.dom.CompilationUnit unit = (org.eclipse.jdt.client.core.dom.CompilationUnit)ast;

        // unit.getPosition(currentEditor.getCursorRow(), currentEditor.getCursorCol() - 1);
        CompletionProposalCollector collector = createCollector(document, unit, offset, currentFile);
        char[] fileContent = document.get().toCharArray();
        CompletionEngine e =
                new CompletionEngine(new NameEnvironment(currentFile.getProject().getId()), collector, JavaCore.getOptions(),
                                     new NullProgressMonitor());
            e.complete(
                    new CompilationUnit(fileContent,
                                        currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')), "UTF-8"),
                    offset, 0);

            IJavaCompletionProposal[] javaCompletionProposals = collector.getJavaCompletionProposals();
            List<IJavaCompletionProposal> types =
                    new ArrayList<IJavaCompletionProposal>(Arrays.asList(javaCompletionProposals));
            if (types.size() > 0 && collector.getInvocationContext().computeIdentifierPrefix().length() == 0) {
                IType expectedType = collector.getInvocationContext().getExpectedType();
                if (expectedType != null) {
                    // empty prefix completion - insert LRU types if known, but prune if they already occur in the core list

                    // compute minmimum relevance and already proposed list
                    int relevance = Integer.MAX_VALUE;
                    Set<String> proposed = new HashSet<String>();
                    for (Iterator<IJavaCompletionProposal> it = types.iterator(); it.hasNext(); ) {
                        AbstractJavaCompletionProposal p = (AbstractJavaCompletionProposal)it.next();
                        IJavaElement element = p.getJavaElement();
                        if (element instanceof IType)
                            proposed.add(((IType)element).getFullyQualifiedName());
                        relevance = Math.min(relevance, p.getRelevance());
                    }

                    // insert history types
                    List<String> history =
                            JdtExtension.get().getContentAssistHistory().getHistory(expectedType.getFullyQualifiedName())
                                        .getTypes();
                    relevance -= history.size() + 1;
                    for (Iterator<String> it = history.iterator(); it.hasNext(); ) {
                        String type = it.next();
                        if (proposed.contains(type))
                            continue;

                        IJavaCompletionProposal proposal =
                                createTypeProposal(relevance, type, collector.getInvocationContext());

                        if (proposal != null)
                            types.add(proposal);
                        relevance++;
                    }
                }
            }

            List<IJavaCompletionProposal> templateProposals =
                    templateCompletionProposalComputer.computeCompletionProposals(collector.getInvocationContext(), null);
            IJavaCompletionProposal[] array =
                    templateProposals.toArray(new IJavaCompletionProposal[templateProposals.size()]);
            javaCompletionProposals = types.toArray(new IJavaCompletionProposal[0]);
            IJavaCompletionProposal[] proposals =
                    new IJavaCompletionProposal[javaCompletionProposals.length + array.length];
            System.arraycopy(javaCompletionProposals, 0, proposals, 0, javaCompletionProposals.length);
            System.arraycopy(array, 0, proposals, javaCompletionProposals.length, array.length);

            Arrays.sort(proposals, comparator);
            return proposals;
        } catch (AssertionFailedException ex) {
            IDE.fireEvent(new OutputEvent(ex.getMessage(), Type.ERROR));

        } catch (Exception ex) {
            Log.error(getClass(), ex);
        }
        return new IJavaCompletionProposal[0];

    }

    private Comparator<IJavaCompletionProposal> comparator = new Comparator<IJavaCompletionProposal>() {

        @Override
        public int compare(IJavaCompletionProposal o1, IJavaCompletionProposal o2) {

            if (o1.getRelevance() > o2.getRelevance())
                return -1;
            else if (o1.getRelevance() < o2.getRelevance())
                return 1;
            else
                return 0;
        }
    };

    private IJavaCompletionProposal createTypeProposal(int relevance, String fullyQualifiedType,
                                                       JavaContentAssistInvocationContext context) {
        IType type = TypeInfoStorage.get().getTypeByFqn(fullyQualifiedType);

        if (type == null)
            return null;

        org.eclipse.jdt.client.core.CompletionProposal proposal =
                org.eclipse.jdt.client.core.CompletionProposal.create(org.eclipse.jdt.client.core.CompletionProposal.TYPE_REF,
                                                                      context.getInvocationOffset());
        proposal.setCompletion(fullyQualifiedType.toCharArray());
        proposal.setDeclarationSignature(Signature.getQualifier(type.getFullyQualifiedName().toCharArray()));
        proposal.setFlags(type.getFlags());
        proposal.setRelevance(relevance);
        proposal.setReplaceRange(context.getInvocationOffset(), context.getInvocationOffset());
        proposal.setSignature(Signature.createTypeSignature(fullyQualifiedType, true).toCharArray());

        return new LazyGenericTypeProposal(proposal, context);

    }

    /**
     * @param document
     * @param currentFile
     * @return
     */
    private CompletionProposalCollector createCollector(IDocument document, org.eclipse.jdt.client.core.dom.CompilationUnit unit,
                                                        int completionPosition,
                                                        FileModel currentFile) {
        CompletionProposalCollector collector =
                new FillArgumentNamesCompletionProposalCollector(unit, document, completionPosition, currentFile.getProject()
                                                                                                                .getId(),
                                                                 JdtExtension.DOC_CONTEXT);
        collector
                .setAllowsRequiredProposals(
                        org.eclipse.jdt.client.core.CompletionProposal.CONSTRUCTOR_INVOCATION,
                        org.eclipse.jdt.client.core.CompletionProposal.TYPE_REF, true);
        collector.setAllowsRequiredProposals(org.eclipse.jdt.client.core.CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION,
                                             org.eclipse.jdt.client.core.CompletionProposal.TYPE_REF, true);
        collector.setAllowsRequiredProposals(org.eclipse.jdt.client.core.CompletionProposal.ANONYMOUS_CLASS_DECLARATION,
                                             org.eclipse.jdt.client.core.CompletionProposal.TYPE_REF,
                                             true);

        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.ANNOTATION_ATTRIBUTE_REF, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.ANONYMOUS_CLASS_DECLARATION, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.FIELD_REF, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.FIELD_REF_WITH_CASTED_RECEIVER, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.KEYWORD, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.LABEL_REF, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.LOCAL_VARIABLE_REF, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.METHOD_DECLARATION, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.METHOD_NAME_REFERENCE, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.METHOD_REF, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.CONSTRUCTOR_INVOCATION, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.METHOD_REF_WITH_CASTED_RECEIVER, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.PACKAGE_REF, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.POTENTIAL_METHOD_DECLARATION, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.VARIABLE_DECLARATION, false);
        collector.setIgnored(org.eclipse.jdt.client.core.CompletionProposal.TYPE_REF, false);
        collector.setRequireExtendedContext(true);
        return collector;
    }


    /** {@inheritDoc} */
    @Override
    public ContextInformation[] computeContextInformation(Editor viewer, int offset) {
        return new ContextInformation[0];
    }

    /** {@inheritDoc} */
    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return new char[]{'.'};
    }

    /** {@inheritDoc} */
    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        return new char[0];
    }

    /** {@inheritDoc} */
    @Override
    public String getErrorMessage() {
        return null;
    }
}
