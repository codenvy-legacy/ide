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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.ext.java.jdt.codeassistant.TemplateCompletionProposalComputer;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.texteditor.api.CodeAssistCallback;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;

import java.util.Comparator;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaCodeAssistProcessor implements CodeAssistProcessor {

    private Comparator<JavaCompletionProposal> comparator = new Comparator<JavaCompletionProposal>() {

        @Override
        public int compare(JavaCompletionProposal o1, JavaCompletionProposal o2) {

            if (o1.getRelevance() > o2.getRelevance())
                return -1;
            else if (o1.getRelevance() < o2.getRelevance())
                return 1;
            else
                return 0;
        }
    };

//    class InternalAstListener implements AstListener {
//
//        /** {@inheritDoc} */
//        @Override
//        public void onCompilationUnitChanged(CompilationUnit cUnit) {
//            currentFile = astProvider.getFile();
//            nameEnvironment = astProvider.getNameEnvironment();
//            unit = cUnit;
//        }
//
//    }

    private String docContext;
    private JavaParserWorker worker;

    private CompilationUnit unit;

    private File currentFile;

    private INameEnvironment nameEnvironment;

    private TemplateCompletionProposalComputer templateCompletionProposalComputer =
            new TemplateCompletionProposalComputer();

    /**
     * @param docContext
     */
    public JavaCodeAssistProcessor(String docContext, JavaParserWorker worker) {
        this.docContext = docContext;
        this.worker = worker;
    }

    /** {@inheritDoc} */
    @Override
    public void computeCompletionProposals(TextEditorPartView view, int offset, CodeAssistCallback callback) {
//        if (unit == null) {
//            return null;
//        }
//        String projectId = currentFile.getProject().getId();
//        CompletionProposalCollector collector =
//                //TODO receive vfs id
//                new FillArgumentNamesCompletionProposalCollector(unit, view.getDocument(), offset, projectId, docContext,
//                                                                 "dev-monit");
//        CompletionEngine e = new CompletionEngine(nameEnvironment, collector, JavaCore.getOptions());
//        try {
//            e.complete(new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(
//                    view.getDocument().get().toCharArray(),
//                    currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')), "UTF-8"), offset, 0);
//
//            JavaCompletionProposal[] javaCompletionProposals = collector.getJavaCompletionProposals();
//            List<JavaCompletionProposal> types =
//                    new ArrayList<JavaCompletionProposal>(Arrays.asList(javaCompletionProposals));
//            if (types.size() > 0 && collector.getInvocationContext().computeIdentifierPrefix().length() == 0) {
//                IType expectedType = collector.getInvocationContext().getExpectedType();
//                if (expectedType != null) {
//                    // empty prefix completion - insert LRU types if known, but prune if they already occur in the core list
//
//                    // compute minmimum relevance and already proposed list
//                    int relevance = Integer.MAX_VALUE;
//                    Set<String> proposed = new HashSet<String>();
//                    for (Iterator<JavaCompletionProposal> it = types.iterator(); it.hasNext(); ) {
//                        AbstractJavaCompletionProposal p = (AbstractJavaCompletionProposal)it.next();
//                        IJavaElement element = p.getJavaElement();
//                        if (element instanceof IType)
//                            proposed.add(((IType)element).getFullyQualifiedName());
//                        relevance = Math.min(relevance, p.getRelevance());
//                    }
//
//                    // insert history types
//                    List<String> history =
//                            JavaExtension.get().getContentAssistHistory().getHistory(expectedType.getFullyQualifiedName())
//                                         .getTypes();
//                    relevance -= history.size() + 1;
//                    for (Iterator<String> it = history.iterator(); it.hasNext(); ) {
//                        String type = it.next();
//                        if (proposed.contains(type))
//                            continue;
//
//                        JavaCompletionProposal proposal =
//                                createTypeProposal(relevance, type, collector.getInvocationContext());
//
//                        if (proposal != null)
//                            types.add(proposal);
//                        relevance++;
//                    }
//                }
//            }
//
//            List<JavaCompletionProposal> templateProposals =
//                    templateCompletionProposalComputer.computeCompletionProposals(collector.getInvocationContext());
//            JavaCompletionProposal[] array =
//                    templateProposals.toArray(new JavaCompletionProposal[templateProposals.size()]);
//            javaCompletionProposals = types.toArray(new JavaCompletionProposal[0]);
//            JavaCompletionProposal[] proposals = new JavaCompletionProposal[javaCompletionProposals.length + array.length];
//            System.arraycopy(javaCompletionProposals, 0, proposals, 0, javaCompletionProposals.length);
//            System.arraycopy(array, 0, proposals, javaCompletionProposals.length, array.length);
//
//            Arrays.sort(proposals, comparator);
//            return proposals;
//        } catch (AssertionFailedException ex) {
//            Log.error(getClass(), ex);
//
//        } catch (Exception ex) {
//            Log.error(getClass(), ex);
//        }
//        return new JavaCompletionProposal[0];
    }

//    private JavaCompletionProposal createTypeProposal(int relevance, String fullyQualifiedType,
//                                                      JavaContentAssistInvocationContext context) {
//        IType type = TypeInfoStorage.get().getTypeByFqn(fullyQualifiedType);
//
//        if (type == null)
//            return null;
//
//        com.codenvy.ide.ext.java.jdt.core.CompletionProposal proposal =
//                com.codenvy.ide.ext.java.jdt.core.CompletionProposal.create(
//                        com.codenvy.ide.ext.java.jdt.core.CompletionProposal.TYPE_REF, context.getInvocationOffset());
//        proposal.setCompletion(fullyQualifiedType.toCharArray());
//        proposal.setDeclarationSignature(Signature.getQualifier(type.getFullyQualifiedName().toCharArray()));
//        proposal.setFlags(type.getFlags());
//        proposal.setRelevance(relevance);
//        proposal.setReplaceRange(context.getInvocationOffset(), context.getInvocationOffset());
//        proposal.setSignature(Signature.createTypeSignature(fullyQualifiedType, true).toCharArray());
//
//        return new LazyGenericTypeProposal(proposal, context);
//
//    }

    /** {@inheritDoc} */
    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getErrorMessage() {
        return null;
    }

}
