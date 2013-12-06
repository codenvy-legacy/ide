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

import com.codenvy.ide.ext.java.jdt.CUVariables;
import com.codenvy.ide.ext.java.jdt.codeassistant.ContentAssistHistory;
import com.codenvy.ide.ext.java.jdt.codeassistant.TemplateCompletionProposalComputer;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTParser;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.codeassist.impl.AssistOptions;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.CompilerOptions;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.DefaultProblem;
import com.codenvy.ide.ext.java.jdt.templates.CodeTemplateContextType;
import com.codenvy.ide.ext.java.jdt.templates.ContextTypeRegistry;
import com.codenvy.ide.ext.java.jdt.templates.ElementTypeResolver;
import com.codenvy.ide.ext.java.jdt.templates.ExceptionVariableNameResolver;
import com.codenvy.ide.ext.java.jdt.templates.FieldResolver;
import com.codenvy.ide.ext.java.jdt.templates.ImportsResolver;
import com.codenvy.ide.ext.java.jdt.templates.JavaContextType;
import com.codenvy.ide.ext.java.jdt.templates.JavaDocContextType;
import com.codenvy.ide.ext.java.jdt.templates.LinkResolver;
import com.codenvy.ide.ext.java.jdt.templates.LocalVarResolver;
import com.codenvy.ide.ext.java.jdt.templates.NameResolver;
import com.codenvy.ide.ext.java.jdt.templates.StaticImportResolver;
import com.codenvy.ide.ext.java.jdt.templates.TemplateStore;
import com.codenvy.ide.ext.java.jdt.templates.TypeResolver;
import com.codenvy.ide.ext.java.jdt.templates.TypeVariableResolver;
import com.codenvy.ide.ext.java.jdt.templates.VarResolver;
import com.codenvy.ide.ext.java.messages.ConfigMessage;
import com.codenvy.ide.ext.java.messages.ParseMessage;
import com.codenvy.ide.ext.java.messages.Problem;
import com.codenvy.ide.ext.java.messages.RoutingTypes;
import com.codenvy.ide.ext.java.messages.impl.MessagesImpls;
import com.codenvy.ide.collections.js.JsoArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
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

    private static WorkerMessageHandler instance;
    private        INameEnvironment     nameEnvironment;
    private HashMap<String, String> options = new HashMap<String, String>();
    private MessageFilter                      messageFilter;
    private JavaParserWorker                   worker;
    private ContentAssistHistory               contentAssistHistory;
    private ContextTypeRegistry                fCodeTemplateContextTypeRegistry;
    private TemplateStore                      templateStore;
    private String                             projectName;
    private CUVariables                        cuVar;
    private TemplateCompletionProposalComputer templateCompletionProposalComputer;

    private WorkerCodeAssist workerCodeAssist;

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
                projectName = config.projectName();
                workerCodeAssist =
                        new WorkerCodeAssist(WorkerMessageHandler.this.worker, messageFilter, nameEnvironment,
                                             templateCompletionProposalComputer, config.projectId(),
                                             config.javaDocContext());
            }
        };
        messageFilter.registerMessageRecipient(RoutingTypes.CONFIG, configMessageRecipient);
        messageFilter.registerMessageRecipient(RoutingTypes.PARSE, this);
        templateCompletionProposalComputer = new TemplateCompletionProposalComputer(getTemplateContextRegistry());
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
    public void onMessageReceived(final ParseMessage message) {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                throw new RuntimeException(throwable);
                //TODO log error
            }

            @Override
            public void onSuccess() {
                cuVar = new CUVariables(message.fileName(), message.packageName(), projectName);

                ASTParser parser = ASTParser.newParser(AST.JLS3);
                parser.setSource(message.source());
                parser.setKind(ASTParser.K_COMPILATION_UNIT);
                parser.setUnitName(message.fileName().substring(0, message.fileName().lastIndexOf('.')));
                parser.setResolveBindings(true);
                parser.setNameEnvironment(nameEnvironment);
                ASTNode ast = parser.createAST();
                CompilationUnit unit = (CompilationUnit)ast;
                workerCodeAssist.setUnit(unit);
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
        });

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

    public ContentAssistHistory getContentAssistHistory() {
        if (contentAssistHistory == null) {
            Preferences preferences = new Preferences();
            // TODO use user name
            contentAssistHistory =
                    ContentAssistHistory.load(preferences, Preferences.CODEASSIST_LRU_HISTORY +"change me" /*userInfo.getName()*/);

            if (contentAssistHistory == null)
                contentAssistHistory = new ContentAssistHistory();
        }

        return contentAssistHistory;
    }

    /** @return  */
    public ContextTypeRegistry getTemplateContextRegistry() {
        if (fCodeTemplateContextTypeRegistry == null) {
            fCodeTemplateContextTypeRegistry = new ContextTypeRegistry();

            CodeTemplateContextType.registerContextTypes(fCodeTemplateContextTypeRegistry);
            JavaContextType contextTypeAll = new JavaContextType(JavaContextType.ID_ALL);

            contextTypeAll.initializeContextTypeResolvers();

            FieldResolver fieldResolver = new FieldResolver();
            fieldResolver.setType("field");
            contextTypeAll.addResolver(fieldResolver);

            LocalVarResolver localVarResolver = new LocalVarResolver();
            localVarResolver.setType("localVar");
            contextTypeAll.addResolver(localVarResolver);
            VarResolver varResolver = new VarResolver();
            varResolver.setType("var");
            contextTypeAll.addResolver(varResolver);
            NameResolver nameResolver = new NameResolver();
            nameResolver.setType("newName");
            contextTypeAll.addResolver(nameResolver);
            TypeResolver typeResolver = new TypeResolver();
            typeResolver.setType("newType");
            contextTypeAll.addResolver(typeResolver);
            ElementTypeResolver elementTypeResolver = new ElementTypeResolver();
            elementTypeResolver.setType("elemType");
            contextTypeAll.addResolver(elementTypeResolver);
            TypeVariableResolver typeVariableResolver = new TypeVariableResolver();
            typeVariableResolver.setType("argType");
            contextTypeAll.addResolver(typeVariableResolver);
            LinkResolver linkResolver = new LinkResolver();
            linkResolver.setType("link");
            contextTypeAll.addResolver(linkResolver);
            ImportsResolver importsResolver = new ImportsResolver();
            importsResolver.setType("import");
            StaticImportResolver staticImportResolver = new StaticImportResolver();
            staticImportResolver.setType("importStatic");
            contextTypeAll.addResolver(staticImportResolver);
            ExceptionVariableNameResolver exceptionVariableNameResolver = new ExceptionVariableNameResolver();
            exceptionVariableNameResolver.setType("exception_variable_name");
            contextTypeAll.addResolver(exceptionVariableNameResolver);
            fCodeTemplateContextTypeRegistry.addContextType(contextTypeAll);
            fCodeTemplateContextTypeRegistry.addContextType(new JavaDocContextType());
            JavaContextType contextTypeMembers = new JavaContextType(JavaContextType.ID_MEMBERS);
            JavaContextType contextTypeStatements = new JavaContextType(JavaContextType.ID_STATEMENTS);
            contextTypeMembers.initializeResolvers(contextTypeAll);
            contextTypeStatements.initializeResolvers(contextTypeAll);
            fCodeTemplateContextTypeRegistry.addContextType(contextTypeMembers);
            fCodeTemplateContextTypeRegistry.addContextType(contextTypeStatements);
        }

        return fCodeTemplateContextTypeRegistry;
    }

    /** @return  */
    public TemplateStore getTemplateStore() {
        if (templateStore == null)
            templateStore = new TemplateStore();
        return templateStore;
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

    public INameEnvironment getNameEnvironment() {
        return nameEnvironment;
    }

    public CUVariables getCUVariables() {
        return cuVar;
    }
}
