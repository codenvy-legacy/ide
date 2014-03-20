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
package com.codenvy.ide.ext.java.worker;

import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.collections.js.JsoStringMap;
import com.codenvy.ide.ext.java.jdt.codeassistant.AbstractJavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.codeassistant.CompletionProposalCollector;
import com.codenvy.ide.ext.java.jdt.codeassistant.FillArgumentNamesCompletionProposalCollector;
import com.codenvy.ide.ext.java.jdt.codeassistant.JavaContentAssistInvocationContext;
import com.codenvy.ide.ext.java.jdt.codeassistant.LazyGenericTypeProposal;
import com.codenvy.ide.ext.java.jdt.codeassistant.TemplateCompletionProposalComputer;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.CompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.IJavaElement;
import com.codenvy.ide.ext.java.jdt.core.IType;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.codeassist.CompletionEngine;
import com.codenvy.ide.ext.java.messages.ComputeCAProposalsMessage;
import com.codenvy.ide.ext.java.messages.RoutingTypes;
import com.codenvy.ide.ext.java.messages.WorkerProposal;
import com.codenvy.ide.ext.java.messages.impl.MessagesImpls;
import com.codenvy.ide.runtime.AssertionFailedException;
import com.codenvy.ide.util.UUID;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.webworker.client.messages.MessageFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WorkerCodeAssist {


    private final WorkerProposalApplier workerProposalApplier;
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
    private JavaParserWorker                   worker;
    private WorkerNameEnvironment              nameEnvironment;
    private TemplateCompletionProposalComputer templateCompletionProposalComputer;
    private String                             projectPath;
    private String                             docContext;
    private String                             vfsId;
    private CompilationUnit                    unit;
    private String                             documentContent;
    private WorkerDocument                     document;

    public WorkerCodeAssist(JavaParserWorker worker, MessageFilter messageFilter, WorkerProposalApplier workerProposalApplier,
                            WorkerNameEnvironment nameEnvironment,
                            TemplateCompletionProposalComputer templateCompletionProposalComputer, String docContext) {
        this.worker = worker;
        this.workerProposalApplier = workerProposalApplier;
        this.nameEnvironment = nameEnvironment;
        this.templateCompletionProposalComputer = templateCompletionProposalComputer;
        this.docContext = docContext;
        messageFilter.registerMessageRecipient(RoutingTypes.CA_COMPUTE_PROPOSALS,
                                               new MessageFilter.MessageRecipient<ComputeCAProposalsMessage>() {
                                                   @Override
                                                   public void onMessageReceived(final ComputeCAProposalsMessage message) {
                                                       GWT.runAsync(new RunAsyncCallback() {
                                                           @Override
                                                           public void onFailure(Throwable throwable) {
                                                               throw new RuntimeException(throwable);
                                                               //TODO log error
                                                           }

                                                           @Override
                                                           public void onSuccess() {
                                                               handleCAMessage(message);
                                                           }
                                                       });
                                                   }
                                               });
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    private void handleCAMessage(ComputeCAProposalsMessage message) {
        setProjectPath(message.projectPath());
        nameEnvironment.setProjectPath(message.projectPath());
        JsoStringMap<JavaCompletionProposal> proposalMap = JsoStringMap.create();
        documentContent = message.docContent();
        JavaCompletionProposal[] proposals =
                computeCompletionProposals(unit, message.offset(), documentContent, message.fileName());

        MessagesImpls.CAProposalsComputedMessageImpl caComputedMessage = MessagesImpls.CAProposalsComputedMessageImpl.make();
        caComputedMessage.setId(message.id());
        JsoArray<WorkerProposal> workerProposals = JsoArray.create();
        for (JavaCompletionProposal proposal : proposals) {
            MessagesImpls.WorkerProposalImpl prop = MessagesImpls.WorkerProposalImpl.make();
            prop.setAutoInsertable(proposal.isAutoInsertable()).setDisplayText(proposal.getDisplayString())
                .setImage(proposal.getImage() == null ? null : proposal.getImage().name());
            String uuid = UUID.uuid();
            prop.setId(uuid);
            proposalMap.put(uuid, proposal);
            workerProposals.add(prop);
        }
        workerProposalApplier.setCaDocument(document);
        workerProposalApplier.setCaProposalMap(proposalMap);
        caComputedMessage.setProposals(workerProposals);
        worker.sendMessage(caComputedMessage.serialize());
    }

    public JavaCompletionProposal[] computeCompletionProposals(CompilationUnit unit, int offset, String documentContent,
                                                               String fileName) {
        if (unit == null) {
            return null;
        }
        document = new WorkerDocument(documentContent);
        CompletionProposalCollector collector =
                //TODO receive vfs id
                new FillArgumentNamesCompletionProposalCollector(unit, document, offset, projectPath, docContext, vfsId);

        collector
                .setAllowsRequiredProposals(
                        CompletionProposal.CONSTRUCTOR_INVOCATION,
                        CompletionProposal.TYPE_REF, true);
        collector.setAllowsRequiredProposals(CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION,
                                             CompletionProposal.TYPE_REF, true);
        collector.setAllowsRequiredProposals(CompletionProposal.ANONYMOUS_CLASS_DECLARATION,
                                             CompletionProposal.TYPE_REF,
                                             true);

        collector.setIgnored(CompletionProposal.ANNOTATION_ATTRIBUTE_REF, false);
        collector.setIgnored(CompletionProposal.ANONYMOUS_CLASS_DECLARATION, false);
        collector.setIgnored(CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION, false);
        collector.setIgnored(CompletionProposal.FIELD_REF, false);
        collector.setIgnored(CompletionProposal.FIELD_REF_WITH_CASTED_RECEIVER, false);
        collector.setIgnored(CompletionProposal.KEYWORD, false);
        collector.setIgnored(CompletionProposal.LABEL_REF, false);
        collector.setIgnored(CompletionProposal.LOCAL_VARIABLE_REF, false);
        collector.setIgnored(CompletionProposal.METHOD_DECLARATION, false);
        collector.setIgnored(CompletionProposal.METHOD_NAME_REFERENCE, false);
        collector.setIgnored(CompletionProposal.METHOD_REF, false);
        collector.setIgnored(CompletionProposal.CONSTRUCTOR_INVOCATION, false);
        collector.setIgnored(CompletionProposal.METHOD_REF_WITH_CASTED_RECEIVER, false);
        collector.setIgnored(CompletionProposal.PACKAGE_REF, false);
        collector.setIgnored(CompletionProposal.POTENTIAL_METHOD_DECLARATION, false);
        collector.setIgnored(CompletionProposal.VARIABLE_DECLARATION, false);
        collector.setIgnored(CompletionProposal.TYPE_REF, false);

        CompletionEngine e = new CompletionEngine(nameEnvironment, collector, JavaCore.getOptions());
        try {
            e.complete(new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(
                    documentContent.toCharArray(),
                    fileName.substring(0, fileName.lastIndexOf('.')), "UTF-8"), offset, 0);

            JavaCompletionProposal[] javaCompletionProposals = collector.getJavaCompletionProposals();
            List<JavaCompletionProposal> types =
                    new ArrayList<JavaCompletionProposal>(Arrays.asList(javaCompletionProposals));
            if (types.size() > 0 && collector.getInvocationContext().computeIdentifierPrefix().length() == 0) {
                IType expectedType = collector.getInvocationContext().getExpectedType();
                if (expectedType != null) {
                    // empty prefix completion - insert LRU types if known, but prune if they already occur in the core list

                    // compute minmimum relevance and already proposed list
                    int relevance = Integer.MAX_VALUE;
                    Set<String> proposed = new HashSet<String>();
                    for (Iterator<JavaCompletionProposal> it = types.iterator(); it.hasNext(); ) {
                        AbstractJavaCompletionProposal p = (AbstractJavaCompletionProposal)it.next();
                        IJavaElement element = p.getJavaElement();
                        if (element instanceof IType)
                            proposed.add(((IType)element).getFullyQualifiedName());
                        relevance = Math.min(relevance, p.getRelevance());
                    }

                    // insert history types
                    List<String> history =
                            WorkerMessageHandler.get().getContentAssistHistory().getHistory(expectedType.getFullyQualifiedName())
                                                .getTypes();
                    relevance -= history.size() + 1;
                    for (Iterator<String> it = history.iterator(); it.hasNext(); ) {
                        String type = it.next();
                        if (proposed.contains(type))
                            continue;

                        JavaCompletionProposal proposal =
                                createTypeProposal(relevance, type, collector.getInvocationContext());

                        if (proposal != null)
                            types.add(proposal);
                        relevance++;
                    }
                }
            }

            List<JavaCompletionProposal> templateProposals =
                    templateCompletionProposalComputer.computeCompletionProposals(collector.getInvocationContext());
            JavaCompletionProposal[] array =
                    templateProposals.toArray(new JavaCompletionProposal[templateProposals.size()]);
            javaCompletionProposals = types.toArray(new JavaCompletionProposal[0]);
            JavaCompletionProposal[] proposals = new JavaCompletionProposal[javaCompletionProposals.length + array.length];
            System.arraycopy(javaCompletionProposals, 0, proposals, 0, javaCompletionProposals.length);
            System.arraycopy(array, 0, proposals, javaCompletionProposals.length, array.length);

            Arrays.sort(proposals, comparator);
            return proposals;
        } catch (AssertionFailedException ex) {
            //todo log errors
//            Log.error(getClass(), ex);
            throw new RuntimeException(ex);

        } catch (Exception ex) {
            //todo log errors
//            Log.error(getClass(), ex);
            throw new RuntimeException(ex);
        }
//        return new JavaCompletionProposal[0];
    }

    private JavaCompletionProposal createTypeProposal(int relevance, String fullyQualifiedType,
                                                      JavaContentAssistInvocationContext context) {
        IType type = WorkerTypeInfoStorage.get().getTypeByFqn(fullyQualifiedType);

        if (type == null)
            return null;

        com.codenvy.ide.ext.java.jdt.core.CompletionProposal proposal =
                com.codenvy.ide.ext.java.jdt.core.CompletionProposal.create(
                        com.codenvy.ide.ext.java.jdt.core.CompletionProposal.TYPE_REF, context.getInvocationOffset());
        proposal.setCompletion(fullyQualifiedType.toCharArray());
        proposal.setDeclarationSignature(Signature.getQualifier(type.getFullyQualifiedName().toCharArray()));
        proposal.setFlags(type.getFlags());
        proposal.setRelevance(relevance);
        proposal.setReplaceRange(context.getInvocationOffset(), context.getInvocationOffset());
        proposal.setSignature(Signature.createTypeSignature(fullyQualifiedType, true).toCharArray());

        return new LazyGenericTypeProposal(proposal, context);

    }

    public void setCu(CompilationUnit unit) {
        this.unit = unit;
    }
}
