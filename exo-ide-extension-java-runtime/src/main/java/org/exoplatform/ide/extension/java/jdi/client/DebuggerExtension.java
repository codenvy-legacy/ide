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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStoppedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebugAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.RunAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.StopAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeEvent;
import org.exoplatform.ide.extension.java.jdi.client.fqn.FqnResolverFactory;
import org.exoplatform.ide.extension.java.jdi.client.fqn.JavaFqnResolver;



/** Entry point classes define <code>onModuleLoad()</code>. */
public class DebuggerExtension extends Extension implements InitializeServicesHandler {

    public static final DebuggerAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(DebuggerAutoBeanFactory.class);

    public static final DebuggerLocalizationConstant LOCALIZATION_CONSTANT = GWT
            .create(DebuggerLocalizationConstant.class);

    /** Channel for the messages containing debugger events. */
    public static final String EVENTS_CHANNEL = "debugger:events:";

    /** Channel for the messages containing the application names which may be stopped soon. */
    public static final String EXPIRE_SOON_APP_CHANNEL = "debugger:expireSoonApp:";

    /** Channel for the messages containing message which informs about debugger is disconnected. */
    public static final String DISCONNECT_CHANNEL = "debugger:disconnected:";

    /** Channel for the messages containing message which informs about application is stopped. */
    public static final String APPLICATION_STOP_CHANNEL = "runner:application-stopped:";


    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
        IDE.getInstance().addControl(new DebugAppControl());
        IDE.getInstance().addControl(new RunAppControl(), Docking.TOOLBAR_RIGHT);
        IDE.getInstance().addControl(new StopAppControl());
//        IDE.getInstance().addControl(new UpdateAppControl());
        IDE.getInstance().addControl(new ShowBreakpointPropertiesControl());
        new LogsHandler();
    }

    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        FqnResolverFactory resolverFactory = new FqnResolverFactory();
        resolverFactory.addResolver(MimeType.APPLICATION_JAVA, new JavaFqnResolver());
        new DebuggerClientService();
        new ApplicationRunnerClientService(IDE.messageBus(), Utils.getWorkspaceName(), Utils.getRestContext());
        BreakpointsManager breakpointsManager =
                new BreakpointsManager(IDE.eventBus(), DebuggerClientService.getInstance(), AUTO_BEAN_FACTORY, resolverFactory);

        DebuggerPresenter debuggerPresenter = new DebuggerPresenter(breakpointsManager);
        new ChangeValuePresenter();
        new EvaluateExpressionPresenter();
        new BreakpointPropertiesPresenter();
        new JRebelUserInfoPresenter();

        IDE.addHandler(DebuggerConnectedEvent.TYPE, debuggerPresenter);
        IDE.addHandler(DebuggerDisconnectedEvent.TYPE, debuggerPresenter);
        IDE.addHandler(BreakPointsUpdatedEvent.TYPE, debuggerPresenter);
        IDE.addHandler(ViewClosedEvent.TYPE, debuggerPresenter);
        IDE.addHandler(RunAppEvent.TYPE, debuggerPresenter);
        IDE.addHandler(DebugAppEvent.TYPE, debuggerPresenter);
        IDE.addHandler(StopAppEvent.TYPE, debuggerPresenter);
        IDE.addHandler(UpdateAppEvent.TYPE, debuggerPresenter);
        IDE.addHandler(AppStoppedEvent.TYPE, debuggerPresenter);
        IDE.addHandler(UpdateVariableValueInTreeEvent.TYPE, debuggerPresenter);

        IDE.addHandler(ProjectClosedEvent.TYPE, debuggerPresenter);
        IDE.addHandler(ProjectOpenedEvent.TYPE, debuggerPresenter);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, debuggerPresenter);
    }

}
