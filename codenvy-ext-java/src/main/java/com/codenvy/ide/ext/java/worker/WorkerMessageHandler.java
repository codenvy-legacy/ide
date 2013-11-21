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

import com.codenvy.ide.ext.java.messages.ConfigMessage;
import com.codenvy.ide.ext.java.messages.ParseMessage;
import com.codenvy.ide.ext.java.messages.Problem;
import com.codenvy.ide.ext.java.messages.RoutingTypes;
import com.codenvy.ide.ext.java.messages.impl.MessagesImpls;
import com.codenvy.ide.ext.java.worker.core.JavaCore;
import com.codenvy.ide.ext.java.worker.core.compiler.IProblem;
import com.codenvy.ide.ext.java.worker.core.dom.AST;
import com.codenvy.ide.ext.java.worker.core.dom.ASTNode;
import com.codenvy.ide.ext.java.worker.core.dom.ASTParser;
import com.codenvy.ide.ext.java.worker.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.worker.internal.codeassist.impl.AssistOptions;
import com.codenvy.ide.ext.java.worker.internal.compiler.impl.CompilerOptions;
import com.codenvy.ide.ext.java.worker.internal.compiler.problem.DefaultProblem;
import com.codenvy.ide.json.js.JsoArray;
import com.google.gwt.webworker.client.MessageEvent;
import com.google.gwt.webworker.client.MessageHandler;
import com.google.gwt.webworker.client.messages.MessageFilter;
import com.google.gwt.webworker.client.messages.MessageImpl;

import java.util.HashMap;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WorkerMessageHandler implements MessageHandler, MessageFilter.MessageRecipient<ParseMessage> {

    private static WorkerMessageHandler                          instance;
    private        WorkerNameEnvironment                         nameEnvironment;
    private HashMap<String, String> options = new HashMap<String, String>();
    private MessageFilter    messageFilter;
    private JavaParserWorker worker;


    public WorkerMessageHandler(JavaParserWorker worker) {
        this.worker = worker;
        instance = this;
        initOptions();
        messageFilter = new MessageFilter();
        MessageFilter.MessageRecipient<ConfigMessage> configMessageRecipient = new MessageFilter.MessageRecipient<ConfigMessage>() {
            @Override
            public void onMessageReceived(ConfigMessage config) {
                nameEnvironment =
                        new WorkerNameEnvironment(config.projectId(), config.restContext(), config.vfsId(), config.wsName());
            }
        };
        messageFilter.registerMessageRecipient(RoutingTypes.CONFIG, configMessageRecipient);
        messageFilter.registerMessageRecipient(RoutingTypes.PARSE, this);
    }

    public static WorkerMessageHandler get() {
        return instance;
    }

    private void initOptions() {
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6);
        options.put(JavaCore.CORE_ENCODING, "UTF-8");
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
        options.put(CompilerOptions.OPTION_TargetPlatform, JavaCore.VERSION_1_6);
        options.put(AssistOptions.OPTION_PerformVisibilityCheck, AssistOptions.ENABLED);
        options.put(CompilerOptions.OPTION_ReportUnusedLocal, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_TaskTags, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_ReportUnusedPrivateMember, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_SuppressWarnings, CompilerOptions.DISABLED);
        options.put(JavaCore.COMPILER_TASK_TAGS, "TODO,FIXME,XXX");
        options.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER_INCLUDE_DOC_COMMENT_REFERENCE, JavaCore.ENABLED);
        options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED);
        options.put(CompilerOptions.OPTION_Process_Annotations, JavaCore.DISABLED);

    }

    /** {@inheritDoc} */
    @Override
    public void onMessage(MessageEvent event) {
        MessageImpl message = event.getDataAsJSO().cast();
        messageFilter.dispatchMessage(message);
    }

    public HashMap<String, String> getOptions() {
        return options;
    }

    @Override
    public void onMessageReceived(ParseMessage message) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(message.source());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setUnitName(message.fileName().substring(0, message.fileName().lastIndexOf('.')));
        parser.setResolveBindings(true);
        parser.setNameEnvironment(nameEnvironment);
        ASTNode ast = parser.createAST();
        CompilationUnit unit = (CompilationUnit)ast;
        IProblem[] problems = unit.getProblems();
        MessagesImpls.ProblemsMessageImpl problemsMessage = MessagesImpls.ProblemsMessageImpl.make();
        JsoArray<Problem> problemsArray = JsoArray.create();
        for (IProblem p : problems) {
            problemsArray.add(convertProblem(p));
        }
        IProblem[] tasks = (IProblem[])unit.getProperty("tasks");
        if (tasks != null) {
            for (IProblem p : tasks) {
                problemsArray.add(convertProblem(p));
            }
        }
        problemsMessage.setProblems(problemsArray);
        problemsMessage.setId(message.id());
        worker.sendMessage(problemsMessage.serialize());
    }

    private MessagesImpls.ProblemImpl convertProblem(IProblem p) {
        MessagesImpls.ProblemImpl problem = MessagesImpls.ProblemImpl.make();
        DefaultProblem prop = (DefaultProblem)p;

        problem.setOriginatingFileName(new String(prop.getOriginatingFileName()));
        problem.setMessage(prop.getMessage());
        problem.setId(prop.getID());
        problem.setStringArguments(from(prop.getArguments()));
        problem.setSeverity(prop.getSeverity());
        problem.setStartPosition(prop.getSourceStart());
        problem.setEndPosition(prop.getSourceEnd());
        problem.setLine(prop.getSourceLineNumber());
        problem.setColumn(prop.getSourceColumnNumber());

        return problem;
    }

    /** Creates a JsoArray from a Java array. */
    public static <M> JsoArray<M> from(M... array) {
        JsoArray<M> result = JsoArray.create();
        for (M s : array) {
            if(s != null)
              result.add(s);
        }
        return result;
    }
}
