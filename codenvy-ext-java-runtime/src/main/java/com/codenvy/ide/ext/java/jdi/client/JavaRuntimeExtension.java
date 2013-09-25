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
package com.codenvy.ide.ext.java.jdi.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.debug.DebuggerManager;
import com.codenvy.ide.ext.java.jdi.client.actions.DebugAction;
import com.codenvy.ide.ext.java.jdi.client.actions.LogsAction;
import com.codenvy.ide.ext.java.jdi.client.actions.RunAction;
import com.codenvy.ide.ext.java.jdi.client.actions.StopAction;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerPresenter;
import com.codenvy.ide.ext.java.jdi.client.fqn.FqnResolverFactory;
import com.codenvy.ide.ext.java.jdi.client.fqn.JavaFqnResolver;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.*;
import static com.codenvy.ide.extension.maven.client.BuilderExtension.SPRING_APPLICATION_PROJECT_TYPE;
import static com.codenvy.ide.rest.MimeType.APPLICATION_JAVA;

/**
 * Extension add Java Runtime support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "Java Runtime Support.", version = "3.0.0")
public class JavaRuntimeExtension {
    /** Channel for the messages containing debugger events. */
    public static final String EVENTS_CHANNEL           = "debugger:events:";
    /** Channel for the messages containing the application names which may be stopped soon. */
    public static final String EXPIRE_SOON_APP_CHANNEL  = "debugger:expireSoonApp:";
    /** Channel for the messages containing message which informs about debugger is disconnected. */
    public static final String DISCONNECT_CHANNEL       = "debugger:disconnected:";
    /** Channel for the messages containing message which informs about application is stopped. */
    public static final String APPLICATION_STOP_CHANNEL = "runner:application-stopped:";

    @Inject
    public JavaRuntimeExtension(ActionManager actionManager, RunAction runAction, DebugAction debugAction, DebuggerManager debuggerManager,
                                DebuggerPresenter debuggerPresenter, FqnResolverFactory resolverFactory, JavaFqnResolver javaFqnResolver,
                                StopAction stopAction, LogsAction logsAction) {
        actionManager.registerAction("runJavaProject", runAction);
        actionManager.registerAction("debugJavaProject", debugAction);
        actionManager.registerAction("stopJavaProject", stopAction);
        actionManager.registerAction("logsJavaProject", logsAction);
        DefaultActionGroup run = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_MAIN_MENU);
        run.add(runAction);
        run.add(debugAction);
        run.add(stopAction);
        run.add(logsAction);

        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);
        DefaultActionGroup runGroup = new DefaultActionGroup(GROUP_RUN_TOOLBAR, false, actionManager);
        actionManager.registerAction(GROUP_RUN_TOOLBAR, runGroup);
        runGroup.add(runAction);
        runGroup.add(debugAction);
        mainToolbarGroup.add(runGroup);

        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);

        DefaultActionGroup runContextGroup = new DefaultActionGroup(GROUP_RUN_CONTEXT_MENU, false, actionManager);
        actionManager.registerAction(GROUP_RUN_CONTEXT_MENU, runContextGroup);

        runContextGroup.addSeparator();
        runContextGroup.add(runAction);
        runContextGroup.add(debugAction);
        runContextGroup.add(stopAction);
        runContextGroup.add(logsAction);

        contextMenuGroup.add(runContextGroup);

        debuggerManager.registeredDebugger(SPRING_APPLICATION_PROJECT_TYPE, debuggerPresenter);

        resolverFactory.addResolver(APPLICATION_JAVA, javaFqnResolver);
    }
}