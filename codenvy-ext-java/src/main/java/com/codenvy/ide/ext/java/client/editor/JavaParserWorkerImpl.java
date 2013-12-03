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
import com.codenvy.ide.ext.java.client.core.compiler.IProblem;
import com.codenvy.ide.ext.java.client.internal.compiler.problem.DefaultProblem;
import com.codenvy.ide.ext.java.messages.Problem;
import com.codenvy.ide.ext.java.messages.ProblemsMessage;
import com.codenvy.ide.ext.java.messages.RoutingTypes;
import com.codenvy.ide.ext.java.messages.impl.MessagesImpls;
import com.codenvy.ide.collections.JsonStringMap;
import com.codenvy.ide.util.UUID;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.webworker.client.ErrorEvent;
import com.google.gwt.webworker.client.ErrorHandler;
import com.google.gwt.webworker.client.MessageEvent;
import com.google.gwt.webworker.client.MessageHandler;
import com.google.gwt.webworker.client.Worker;
import com.google.gwt.webworker.client.messages.MessageFilter;
import com.google.gwt.webworker.client.messages.MessageImpl;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaParserWorkerImpl implements JavaParserWorker, ProjectActionHandler, MessageFilter.MessageRecipient<ProblemsMessage> {

    private final MessageFilter    messageFilter;
    private       Worker           worker;
    private       ResourceProvider resourceProvider;
    private JsonStringMap<JavaParserCallback> callbacks;

    @Inject
    public JavaParserWorkerImpl(ResourceProvider resourceProvider, EventBus eventBus) {
        this.resourceProvider = resourceProvider;
        eventBus.addHandler(ProjectActionEvent.TYPE, this);
        messageFilter = new MessageFilter();
        callbacks = Collections.createStringMap();
        messageFilter.registerMessageRecipient(RoutingTypes.PROBLEMS, this);

    }

    @Override
    public void parse(String content, String fileName, JavaParserCallback callback) {
        if(worker == null){
            return;
        }

        MessagesImpls.ParseMessageImpl parseMessage = MessagesImpls.ParseMessageImpl.make();
        String uuid = UUID.uuid();
        callbacks.put(uuid, callback);
        parseMessage.setSource(content).setFileName(fileName).setId(uuid);
        worker.postMessage(parseMessage.serialize());
    }

    /**
     * Project opened
     *
     * @param event
     */
    @Override
    public void onProjectOpened(ProjectActionEvent event) {
        if (worker != null) {
            worker.terminate();
        }
        //TODO check project type, create worker only if project is Java
        worker = Worker.create("./javaParserWorker/javaParserWorker.nocache.js");
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
                Log.error(JavaParserWorkerImpl.class, event.getMessage());
            }
        });

        MessagesImpls.ConfigMessageImpl config = MessagesImpls.ConfigMessageImpl.make();
        config.setProjectId(event.getProject().getId());
        config.setRestContext("/ide/rest");
        config.setVfsId(resourceProvider.getVfsId());
        config.setWsName("/" + Utils.getWorkspaceName());
        worker.postMessage(config.serialize());
    }

    /**
     * Project opened
     *
     * @param event
     */
    @Override
    public void onProjectClosed(ProjectActionEvent event) {
        if (worker != null) {
            worker.terminate();
            worker = null;
        }
    }

    /**
     * Project Description Changed
     *
     * @param event
     */
    @Override
    public void onProjectDescriptionChanged(ProjectActionEvent event) {
    }


    /** {@inheritDoc} */
    @Override
    public void onMessageReceived(ProblemsMessage message) {
        if(!callbacks.containsKey(message.id())){
            return;
        }
        Array<Problem> problems = message.problems();
        Array<IProblem> iProblems = Collections.createArray();
                for (Problem p : problems.asIterable()) {
                String[] arg = new String[p.stringArguments().size()];
                for(int i = 0; i< p.stringArguments().size(); i++){
                    arg[i] = p.stringArguments().get(i);
                }
               iProblems.add(new DefaultProblem(p.originatingFileName().toCharArray(), p.message(), p.id(), arg, p.severity(), p.startPosition(),
                                           p.endPosition(), p.line(), p.column()));
            }
        JavaParserCallback callback = callbacks.remove(message.id());
        callback.onProblems(iProblems);
    }

}
