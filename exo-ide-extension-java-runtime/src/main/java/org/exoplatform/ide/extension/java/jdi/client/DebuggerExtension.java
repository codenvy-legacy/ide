package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStopedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebugAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.RunAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.StopAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeEvent;
import org.exoplatform.ide.extension.java.jdi.client.fqn.FqnResolverFactory;
import org.exoplatform.ide.extension.java.jdi.client.fqn.JavaFqnResolver;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DebuggerExtension extends Extension implements InitializeServicesHandler
{

   public static final DebuggerAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(DebuggerAutoBeanFactory.class);

   /**
    * 
    */
   public static final DebuggerLocalizationConstant LOCALIZATION_CONSTANT = GWT
      .create(DebuggerLocalizationConstant.class);

   @Override
   public void initialize()
   {
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.getInstance().addControl(new DebugAppControl());
      IDE.getInstance().addControl(new RunAppControl());
      IDE.getInstance().addControl(new StopAppControl());
      IDE.getInstance().addControl(new ShowBreakpointPropertiesControl());
   }

   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      FqnResolverFactory resolverFactory = new FqnResolverFactory();
      resolverFactory.addResolver(MimeType.APPLICATION_JAVA, new JavaFqnResolver());
      new DebuggerClientService(event.getApplicationConfiguration().getContext());
      new ApplicationRunnerClientService(event.getApplicationConfiguration().getContext());
      BreakpointsManager breakpointsManager =
         new BreakpointsManager(IDE.eventBus(), DebuggerClientService.getInstance(), AUTO_BEAN_FACTORY, resolverFactory);

      DebuggerPresenter debuggerPresenter = new DebuggerPresenter(breakpointsManager);
      new ChangeValuePresenter();
      new EvaluateExpressionPresenter();
      new BreakpointPropertiesPresenter();

      IDE.addHandler(DebuggerConnectedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(DebuggerDisconnectedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(BreakPointsUpdatedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(ViewClosedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(RunAppEvent.TYPE, debuggerPresenter);
      IDE.addHandler(DebugAppEvent.TYPE, debuggerPresenter);
      IDE.addHandler(StopAppEvent.TYPE, debuggerPresenter);
      IDE.addHandler(AppStopedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(UpdateVariableValueInTreeEvent.TYPE, debuggerPresenter);

      IDE.addHandler(ProjectClosedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(ProjectOpenedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, debuggerPresenter);
   }

}
