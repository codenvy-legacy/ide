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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.collections.js.JsoStringMap;
import com.codenvy.ide.ext.java.jdt.MultiStatus;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.AdvancedQuickAssistProcessor;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.AssistContext;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.CorrectionMessages;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.ProblemLocation;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.QuickAssistProcessorImpl;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.QuickFixProcessorImpl;
import com.codenvy.ide.ext.java.jdt.quickassist.api.InvocationContext;
import com.codenvy.ide.ext.java.jdt.quickassist.api.QuickAssistProcessor;
import com.codenvy.ide.ext.java.jdt.quickassist.api.QuickFixProcessor;
import com.codenvy.ide.ext.java.messages.ComputeCorrMessage;
import com.codenvy.ide.ext.java.messages.ProblemLocationMessage;
import com.codenvy.ide.ext.java.messages.RoutingTypes;
import com.codenvy.ide.ext.java.messages.WorkerProposal;
import com.codenvy.ide.ext.java.messages.impl.MessagesImpls;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.runtime.IStatus;
import com.codenvy.ide.runtime.Status;
import com.codenvy.ide.util.UUID;
import com.google.gwt.webworker.client.messages.MessageFilter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * WorkerCorrectionProcessor 
 *
 * @author Evgen Vidolob
 */
public class WorkerCorrectionProcessor {

    private static QuickFixProcessor fixProcessor;

    private static QuickAssistProcessor[] assistProcessors;


    private String fErrorMessage;

    private CompilationUnit  cu;
    private JavaParserWorker worker;
    private WorkerProposalApplier workerProposalApplier;

    public WorkerCorrectionProcessor(JavaParserWorker worker, MessageFilter messageFilter, WorkerProposalApplier workerProposalApplier) {
        this.worker = worker;
        this.workerProposalApplier = workerProposalApplier;
        if (fixProcessor == null) {
            fixProcessor = new QuickFixProcessorImpl();
            assistProcessors =
                    new QuickAssistProcessor[]{new QuickAssistProcessorImpl(), new AdvancedQuickAssistProcessor()};
        }
        messageFilter.registerMessageRecipient(RoutingTypes.COMPUTE_CORRECTION, new MessageFilter.MessageRecipient<ComputeCorrMessage>() {
            @Override
            public void onMessageReceived(ComputeCorrMessage message) {
                computateProposals(message);
            }
        });
    }

    public void setCu(CompilationUnit cu) {
        this.cu = cu;
    }

    public void computateProposals(ComputeCorrMessage message) {
        JsoStringMap<JavaCompletionProposal> proposalMap = JsoStringMap.create();
        int documentOffset = message.documentOffset();

        WorkerDocument document = new WorkerDocument(message.documentContent());
        AssistContext context = null;
        if (cu != null) {
            int length = message.documentSelectionLength();
            context = new AssistContext(document, documentOffset, length, cu);
        }


        fErrorMessage = null;
        Array<ProblemLocationMessage> problemLocations = message.problemLocations();
        ArrayList<JavaCompletionProposal> proposals = new ArrayList<JavaCompletionProposal>(10);
        try {
            if (context != null && problemLocations != null) {
                IStatus status =
                        collectProposals(context, problemLocations, true, !message.updatedOffset(), proposals);
                if (!status.isOK()) {
                    fErrorMessage = status.getMessage();
                    //TODO
                    //JavaPlugin.log(status);
                }
            }
        } catch (CoreException e) {
            //TODO log error
            throw new RuntimeException(e);
        }
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
        workerProposalApplier.setQuickDocument(document);
        workerProposalApplier.setQuickProposalMap(proposalMap);
        caComputedMessage.setProposals(workerProposals);
        worker.sendMessage(caComputedMessage.serialize());
    }

    public static IStatus collectProposals(InvocationContext context,
                                           Array<ProblemLocationMessage> plMessage, boolean addQuickFixes, boolean addQuickAssists,
                                           Collection<JavaCompletionProposal> proposals)
            throws CoreException {

        ProblemLocation[] problemLocations = new ProblemLocation[plMessage.size()];
        for (int i = 0; i < plMessage.size(); i++) {
            problemLocations[i] = new ProblemLocation(plMessage.get(i));
        }
//        ProblemLocation[] problems = problems.toArray(new ProblemLocation[problems.size()]);
        MultiStatus resStatus = null;
        //
        if (addQuickFixes) {
            IStatus status = collectCorrections(context, problemLocations, proposals);
            if (!status.isOK()) {
                resStatus =
                        new MultiStatus(JavaCore.PLUGIN_ID, IStatus.ERROR,
                                        CorrectionMessages.INSTANCE.JavaCorrectionProcessor_error_quickfix_message(), null);
                resStatus.add(status);
            }
        }
        if (addQuickAssists) {
            IStatus status = collectAssists(context, problemLocations, proposals);
            if (!status.isOK()) {
                if (resStatus == null) {
                    resStatus =
                            new MultiStatus(JavaCore.PLUGIN_ID, IStatus.ERROR,
                                            CorrectionMessages.INSTANCE.JavaCorrectionProcessor_error_quickassist_message(), null);
                }
                resStatus.add(status);
            }
        }
        if (resStatus != null) {
            return resStatus;
        }
        return Status.OK_STATUS;
    }

    public static IStatus collectAssists(InvocationContext context, IProblemLocation[] locations,
                                         Collection<JavaCompletionProposal> proposals) throws CoreException {

        for (QuickAssistProcessor curr : assistProcessors) {
            JavaCompletionProposal[] res;
            res = curr.getAssists(context, locations);
            if (res != null) {
                for (int k = 0; k < res.length; k++) {
                    proposals.add(res[k]);
                }
            }
        }
        return Status.OK_STATUS;

    }

    public static IStatus collectCorrections(InvocationContext context, IProblemLocation[] locations,
                                             Collection<JavaCompletionProposal> proposals) throws CoreException {
        JavaCompletionProposal[] res;
        res = fixProcessor.getCorrections(context, locations);
        if (res != null) {
            for (int k = 0; k < res.length; k++) {
                proposals.add(res[k]);
            }
        }
        return Status.OK_STATUS;
    }

    public static boolean hasAssists(InvocationContext context) {
        for (QuickAssistProcessor curr : assistProcessors) {
            try {
                if (curr.hasAssists(context))
                    return true;
            } catch (CoreException e) {
//                Log.error(WorkerCorrectionProcessor.class, e);
                return false;
            }
        }

        return false;
    }

}
