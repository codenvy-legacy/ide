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
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.debug.DebuggerManager;
import com.codenvy.ide.ext.java.jdi.client.actions.DebugAction;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerPresenter;
import com.codenvy.ide.ext.java.jdi.client.fqn.FqnResolverFactory;
import com.codenvy.ide.ext.java.jdi.client.fqn.JavaFqnResolver;
import com.codenvy.ide.ext.java.shared.Constants;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.MimeType.APPLICATION_JAVA;
import static com.codenvy.ide.api.ui.action.Anchor.AFTER;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_TOOLBAR;

/**
 * Extension allows debug Java web applications.
 *
 * @author Andrey Plotnikov
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Java debugger", version = "3.0.0")
public class JavaRuntimeExtension {
    /** Channel for the messages containing debugger events. */
    public static final String EVENTS_CHANNEL     = "debugger:events:";
    /** Channel for the messages containing message which informs about debugger is disconnected. */
    public static final String DISCONNECT_CHANNEL = "debugger:disconnected:";

    @Inject
    public JavaRuntimeExtension(ActionManager actionManager, DebugAction debugAction, DebuggerManager debuggerManager,
                                DebuggerPresenter debuggerPresenter, FqnResolverFactory resolverFactory, JavaFqnResolver javaFqnResolver,
                                JavaRuntimeLocalizationConstant localizationConstant,
                                RunnerLocalizationConstant runnerLocalizationConstants) {
        // register actions
        actionManager.registerAction(localizationConstant.debugAppActionId(), debugAction);

        // add actions in main menu
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN);
        runMenuActionGroup.add(debugAction, new Constraints(AFTER, runnerLocalizationConstants.customRunAppActionId()));

        // add actions on main toolbar
        DefaultActionGroup runToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_TOOLBAR);
        runToolbarGroup.add(debugAction);
        runToolbarGroup.addSeparator();

        // add actions in context menu
        DefaultActionGroup runContextGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_CONTEXT_MENU);
        runContextGroup.add(debugAction);

        debuggerManager.registeredDebugger(Constants.WAR_ID, debuggerPresenter);
        debuggerManager.registeredDebugger(Constants.SPRING_ID, debuggerPresenter);
        debuggerManager.registeredDebugger(com.codenvy.ide.Constants.CODENVY_PLUGIN_ID, debuggerPresenter);
        resolverFactory.addResolver(APPLICATION_JAVA, javaFqnResolver);
    }
}