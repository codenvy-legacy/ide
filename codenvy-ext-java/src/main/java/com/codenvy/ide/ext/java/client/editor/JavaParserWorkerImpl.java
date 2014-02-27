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

import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.DefaultProblem;
import com.codenvy.ide.ext.java.messages.CAProposalsComputedMessage;
import com.codenvy.ide.ext.java.messages.FormatResultMessage;
import com.codenvy.ide.ext.java.messages.Problem;
import com.codenvy.ide.ext.java.messages.ProblemLocationMessage;
import com.codenvy.ide.ext.java.messages.ProblemsMessage;
import com.codenvy.ide.ext.java.messages.ProposalAppliedMessage;
import com.codenvy.ide.ext.java.messages.RoutingTypes;
import com.codenvy.ide.ext.java.messages.WorkerProposal;
import com.codenvy.ide.ext.java.messages.impl.MessagesImpls;
import com.codenvy.ide.ext.java.messages.impl.OutlineUpdateMessage;
import com.codenvy.ide.ext.java.messages.impl.WorkerCodeBlock;
import com.codenvy.ide.text.edits.CopySourceEdit;
import com.codenvy.ide.text.edits.CopyTargetEdit;
import com.codenvy.ide.text.edits.CopyingRangeMarker;
import com.codenvy.ide.text.edits.DeleteEdit;
import com.codenvy.ide.text.edits.InsertEdit;
import com.codenvy.ide.text.edits.MoveSourceEdit;
import com.codenvy.ide.text.edits.MoveTargetEdit;
import com.codenvy.ide.text.edits.MultiTextEdit;
import com.codenvy.ide.text.edits.RangeMarker;
import com.codenvy.ide.text.edits.ReplaceEdit;
import com.codenvy.ide.text.edits.TextEdit;
import com.codenvy.ide.text.edits.TextEditVisitor;
import com.codenvy.ide.texteditor.api.TextEditorOperations;
import com.codenvy.ide.util.UUID;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.webworker.client.ErrorEvent;
import com.google.gwt.webworker.client.ErrorHandler;
import com.google.gwt.webworker.client.MessageEvent;
import com.google.gwt.webworker.client.MessageHandler;
import com.google.gwt.webworker.client.Worker;
import com.google.gwt.webworker.client.messages.MessageFilter;
import com.google.gwt.webworker.client.messages.MessageImpl;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaParserWorkerImpl implements JavaParserWorker, ProjectActionHandler, MessageFilter.MessageRecipient<ProblemsMessage> {

    private final MessageFilter                messageFilter;
    private final String                       workspaceId;
    private       Worker                       worker;
    private       ResourceProvider             resourceProvider;
    private       String                       restContext;
    private       StringMap<WorkerCallback<?>> callbacks;
    private StringMap<ApplyCallback>                   applyCallback    = Collections.createStringMap();
    private StringMap<WorkerCallback<WorkerCodeBlock>> outlineCallbacks = Collections.createStringMap();
    private StringMap<ApplyFormatCallback>                   applyFormatCallback    = Collections.createStringMap();

    @Inject
    public JavaParserWorkerImpl(ResourceProvider resourceProvider, EventBus eventBus, @Named("restContext") String restContext,
                                @Named("workspaceId") String workspaceId) {
        this.resourceProvider = resourceProvider;
        this.restContext = restContext;
        this.workspaceId = workspaceId;
        eventBus.addHandler(ProjectActionEvent.TYPE, this);
        messageFilter = new MessageFilter();
        callbacks = Collections.createStringMap();
        messageFilter.registerMessageRecipient(RoutingTypes.PROBLEMS, this);
        messageFilter.registerMessageRecipient(RoutingTypes.CA_PROPOSALS_COMPUTED,
                                               new MessageFilter.MessageRecipient<CAProposalsComputedMessage>() {
                                                   @Override
                                                   public void onMessageReceived(CAProposalsComputedMessage message) {
                                                       handleCAComputed(message);
                                                   }
                                               });

        messageFilter
                .registerMessageRecipient(RoutingTypes.CA_PROPOSAL_APPLIED, new MessageFilter.MessageRecipient<ProposalAppliedMessage>() {
                    @Override
                    public void onMessageReceived(ProposalAppliedMessage message) {
                        handleProposalApplied(message);
                    }
                });

        messageFilter
                .registerMessageRecipient(RoutingTypes.OUTLINE_CODE_BLOCKS, new MessageFilter.MessageRecipient<OutlineUpdateMessage>() {
                    @Override
                    public void onMessageReceived(OutlineUpdateMessage message) {
                        handleUpdateOutline(message);
                    }
                });
        messageFilter
                .registerMessageRecipient(RoutingTypes.FORMAT_RESULT, new MessageFilter.MessageRecipient<FormatResultMessage>() {
                    @Override
                    public void onMessageReceived(FormatResultMessage message) {
                        handleFormatApplied(message);
                    }
                });
    }
    private void handleFormatApplied(FormatResultMessage message){
        ApplyFormatCallback callback = applyFormatCallback.remove(message.id());
        Jso textEditJso = message.textEdit();
        TextEdit textEdit = convertTextEditFromJso(textEditJso);
        callback.onApplyFormat(textEdit);
    }
    private TextEdit convertTextEditFromJso(Jso editJso) {
        TextEdit textEdit = null;
        int length;
        String text;

        int offSet = editJso.getFieldCastedToInteger("offSet");

        String type = editJso.getStringField("type");

        switch (type) {
            case "ReplaceEdit":
                text = editJso.getStringField("text");
                length = editJso.getFieldCastedToInteger("length");
                textEdit = new ReplaceEdit(offSet,length, text);
                break;
            case "DeleteEdit":
                length = editJso.getFieldCastedToInteger("length");
                textEdit = new DeleteEdit(offSet,length);
                break;
            case "InsertEdit":
                text = editJso.getStringField("text");
                textEdit = new InsertEdit(offSet, text);
                break;
            case "CopyingRangeMarker":
                length = editJso.getFieldCastedToInteger("length");
                textEdit = new CopyingRangeMarker(offSet,length);
                break;
            case "CopySourceEdit":
                length = editJso.getFieldCastedToInteger("length");
                Jso copyTargetEditJso = (Jso)editJso.getObjectField("CopyTargetEdit");
                if (copyTargetEditJso != null) {
                    int offSetCopyTargetEdit = copyTargetEditJso.getFieldCastedToInteger("offSet");

                    CopyTargetEdit copyTargetEdit = new CopyTargetEdit(offSetCopyTargetEdit);
                    textEdit = new CopySourceEdit(offSet, length, copyTargetEdit);
                } else textEdit = new CopySourceEdit(offSet, length);
                break;
            case "MoveSourceEdit":
                length = editJso.getFieldCastedToInteger("length");
                Jso moveTargetEditJso = (Jso)editJso.getObjectField("MoveTargetEdit");
                if (moveTargetEditJso != null) {
                    int offSetMoveTargetEdit = moveTargetEditJso.getFieldCastedToInteger("offSet");
                    MoveTargetEdit moveTargetEdit = new MoveTargetEdit(offSetMoveTargetEdit);
                    textEdit = new MoveSourceEdit(offSet, length, moveTargetEdit);
                } else textEdit = new MoveSourceEdit(offSet, length);
                break;
            case "MoveTargetEdit":
                Jso moveSourceEditJso = (Jso)editJso.getObjectField("MoveSourceEdit");
                if(moveSourceEditJso != null){
                    int offSetMoveSourceEdit = moveSourceEditJso.getFieldCastedToInteger("offSet");
                    int lengthMoveSourceEdit = moveSourceEditJso.getFieldCastedToInteger("length");
                    MoveSourceEdit moveSourceEdit = new MoveSourceEdit(offSetMoveSourceEdit, lengthMoveSourceEdit);
                    textEdit = new MoveTargetEdit(offSet, moveSourceEdit);
                }else textEdit = new MoveTargetEdit(offSet);
                break;
            case "MultiTextEdit":
                length = editJso.getFieldCastedToInteger("length");
                textEdit = new MultiTextEdit(offSet, length);
                break;
            case "RangeMarker":
                length = editJso.getFieldCastedToInteger("length");
                textEdit = new RangeMarker(offSet,length);
                break;
            case "CopyTargetEdit":
                Jso copySourceEditJso = (Jso)editJso.getObjectField("CopySourceEdit");
                if(copySourceEditJso != null){
                    int offSetCopySourceEdit = copySourceEditJso.getFieldCastedToInteger("offSet");
                    int lengthCopySourceEdit = copySourceEditJso.getFieldCastedToInteger("length");

                    CopySourceEdit copySourceEdit = new CopySourceEdit(offSetCopySourceEdit, lengthCopySourceEdit);
                    textEdit = new CopyTargetEdit(offSet, copySourceEdit);
                }else textEdit = new CopyTargetEdit(offSet);
                break;
        }

        JsoArray children = editJso.getArrayField("children");

        if (children != null) {
            textEdit.addChildren(convertChildrenTextEditFromJso(children));
        }
        return textEdit;
    }

    private TextEdit[] convertChildrenTextEditFromJso(JsoArray childrenJso){
        TextEdit[] edits = null;
        for (int i = 0; i<childrenJso.size();i++) {
            Jso child =(Jso) childrenJso.get(i);
            edits[i] = convertTextEditFromJso(child);
        }
        return edits;
    }
    private void handleUpdateOutline(OutlineUpdateMessage message) {
        if (outlineCallbacks.containsKey(message.getFileId())) {
            WorkerCallback<WorkerCodeBlock> callback = outlineCallbacks.get(message.getFileId());
            callback.onResult(message.getBlocks());
        }
    }

    private void handleProposalApplied(ProposalAppliedMessage message) {
        if (applyCallback.containsKey(message.id())) {
            ApplyCallback callback = applyCallback.remove(message.id());
            callback.onApply(message);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleCAComputed(CAProposalsComputedMessage message) {
        if (!callbacks.containsKey(message.id())) {
            return;
        }

        WorkerCallback<WorkerProposal> callback = (WorkerCallback<WorkerProposal>)callbacks.remove(message.id());
        callback.onResult(message.proposals());
    }

    @Override
    public void removeFanFromCache(String fqn) {
        if (worker == null) {
            return;
        }
        
        MessagesImpls.RemoveFqnMessageImpl message = MessagesImpls.RemoveFqnMessageImpl.make();
        message.setFqn(fqn);
        worker.postMessage(message.serialize());
    }

    @Override
    public void format(int offset, int length, String content, ApplyFormatCallback callback) {
        String uuid = UUID.uuid();
        applyFormatCallback.put(uuid, callback);
        MessagesImpls.FormatMessageImpl message = MessagesImpls.FormatMessageImpl.make();
        message.setId(uuid).setOffset(offset).setLength(length).setContent(content);
        worker.postMessage(message.serialize());
    }

    /** {@inheritDoc} */
    @Override
    public void parse(String content, String fileName, String fileId, String packageName, String projectId,
                      WorkerCallback<IProblem> callback) {
        if (worker == null) {
            return;
        }

        MessagesImpls.ParseMessageImpl parseMessage = MessagesImpls.ParseMessageImpl.make();
        String uuid = UUID.uuid();
        callbacks.put(uuid, callback);
        parseMessage.setSource(content).setFileName(fileName).setFileId(fileId).setId(uuid).setPackageName(packageName)
                    .setProjectId(projectId);
        worker.postMessage(parseMessage.serialize());
    }

    /** {@inheritDoc} */
    @Override
    public void computeCAProposals(String content, int offset, String fileName, String projectId, WorkerCallback<WorkerProposal> callback) {
        if (worker == null) {
            return;
        }

        MessagesImpls.ComputeCAProposalsMessageImpl computeMessage = MessagesImpls.ComputeCAProposalsMessageImpl.make();
        String uuid = UUID.uuid();
        callbacks.put(uuid, callback);
        computeMessage.setDocContent(content).setOffset(offset).setFileName(fileName).setId(uuid).setProjectId(projectId);
        worker.postMessage(computeMessage.serialize());
    }

    /** {@inheritDoc} */
    @Override
    public void applyCAProposal(String id, ApplyCallback callback) {
        if (worker == null) {
            return;
        }
        MessagesImpls.ApplyProposalMessageImpl message = MessagesImpls.ApplyProposalMessageImpl.make();
        message.setId(id);
        applyCallback.put(id, callback);
        worker.postMessage(message.serialize());
    }

    @Override
    public void addOutlineUpdateHandler(String fileId, WorkerCallback<WorkerCodeBlock> callback) {
        outlineCallbacks.put(fileId, callback);
    }

    @Override
    public void computeQAProposals(String content, int offset, int selectionLength, boolean updatedContent,
                                   JsoArray<ProblemLocationMessage> problems, WorkerCallback<WorkerProposal> callback) {
        MessagesImpls.ComputeCorrMessageImpl corrMessage = MessagesImpls.ComputeCorrMessageImpl.make();
        corrMessage.setDocumentContent(content).setDocumentOffset(offset).setDocumentSelectionLength(selectionLength)
                   .setUpdatedOffset(updatedContent).setProblemLocations(problems);
        String uuid = UUID.uuid();
        callbacks.put(uuid, callback);
        corrMessage.setId(uuid);
        worker.postMessage(corrMessage.serialize());
    }

    @Override
    public void onProjectOpened(ProjectActionEvent event) {
        if (worker != null) {
            worker.terminate();
        }
        //TODO check project type, create worker only if project is Java
//        worker = Worker.create("./javaParserWorker/javaParserWorker.nocache.js");
        worker = Worker.create("/ide/_app/javaParserWorker/javaParserWorker.nocache.js");
        worker.setOnMessage(new MessageHandler() {
            @Override
            public void onMessage(MessageEvent event) {
                MessageImpl message = event.getDataAsJSO().cast();
                messageFilter.dispatchMessage(message);
            }
        });
        worker.setOnError(new ErrorHandler() {
            @Override
            public void onError(ErrorEvent event) {
                Log.error(JavaParserWorkerImpl.class, event.getMessage(), event.getFilename(), event.getLineNumber());
            }
        });

        MessagesImpls.ConfigMessageImpl config = MessagesImpls.ConfigMessageImpl.make();
        config.setRestContext(restContext);
        config.setVfsId(resourceProvider.getVfsInfo().getId());
        config.setWsId("/" + workspaceId);
        config.setProjectName(event.getProject().getName());
        config.setJavaDocContext(""); //TODO configure doc context
        worker.postMessage(config.serialize());
    }

    @Override
    public void onProjectClosed(ProjectActionEvent event) {
        if (worker != null) {
            worker.terminate();
            worker = null;
        }
    }

    @Override
    public void onProjectDescriptionChanged(ProjectActionEvent event) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMessageReceived(ProblemsMessage message) {
        if (!callbacks.containsKey(message.id())) {
            return;
        }
        Array<Problem> problems = message.problems();
        Array<IProblem> iProblems = Collections.createArray();
        for (Problem p : problems.asIterable()) {
            String[] arg = new String[p.stringArguments().size()];
            for (int i = 0; i < p.stringArguments().size(); i++) {
                arg[i] = p.stringArguments().get(i);
            }
            iProblems.add(new DefaultProblem(p.originatingFileName().toCharArray(), p.message(), p.id(), arg, p.severity(),
                                             p.startPosition(),
                                             p.endPosition(), p.line(), p.column()));
        }

        WorkerCallback<IProblem> callback = (WorkerCallback<IProblem>)callbacks.remove(message.id());
        callback.onResult(iProblems);
    }

}
