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
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerPresenter;
import com.codenvy.ide.ext.java.jdi.client.fqn.FqnResolverFactory;
import com.codenvy.ide.ext.java.jdi.client.fqn.JavaFqnResolver;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.MimeType.APPLICATION_JAVA;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_MAIN_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_TOOLBAR;
import static com.codenvy.ide.ext.java.client.JavaExtension.SPRING_APPLICATION_PROJECT_TYPE;

/**
 * Extension allows debug Java web applications.
 *
 * @author Andrey Plotnikov
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Java Debugger.", version = "3.0.0")
public class JavaRuntimeExtension {
    /** Channel for the messages containing debugger events. */
    public static final String EVENTS_CHANNEL     = "debugger:events:";
    /** Channel for the messages containing message which informs about debugger is disconnected. */
    public static final String DISCONNECT_CHANNEL = "debugger:disconnected:";

    @Inject
    public JavaRuntimeExtension(ActionManager actionManager, DebugAction debugAction, DebuggerManager debuggerManager,
                                DebuggerPresenter debuggerPresenter, FqnResolverFactory resolverFactory, JavaFqnResolver javaFqnResolver,
                                JavaRuntimeLocalizationConstant localizationConstant) {
        // register actions
        actionManager.registerAction(localizationConstant.debugAppActionId(), debugAction);

        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_MAIN_MENU);
        runMenuActionGroup.add(debugAction);

        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);
        DefaultActionGroup runToolbarGroup = new DefaultActionGroup(GROUP_RUN_TOOLBAR, false, actionManager);
        actionManager.registerAction(GROUP_RUN_TOOLBAR, runToolbarGroup);
        runToolbarGroup.add(debugAction);
        mainToolbarGroup.add(runToolbarGroup);

        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);

        DefaultActionGroup runContextGroup = new DefaultActionGroup(GROUP_RUN_CONTEXT_MENU, false, actionManager);
        actionManager.registerAction(GROUP_RUN_CONTEXT_MENU, runContextGroup);

        runContextGroup.addSeparator();
        runContextGroup.add(debugAction);

        contextMenuGroup.add(runContextGroup);

        debuggerManager.registeredDebugger(SPRING_APPLICATION_PROJECT_TYPE, debuggerPresenter);

        resolverFactory.addResolver(APPLICATION_JAVA, javaFqnResolver);
    }
}