package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.LaunchDebuggerEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.RunAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.fqn.FqnResolverFactory;
import org.exoplatform.ide.extension.java.jdi.client.fqn.JavaFqnResolver;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;

import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DebuggerExtension extends Extension implements InitializeServicesHandler
{

   public static final DebuggerAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(DebuggerAutoBeanFactory.class);

   /**
    * 
    */
   public static final DebuggerLocalizationConstant LOCALIZATION_CONSTANT = GWT.create(DebuggerLocalizationConstant.class);

   @Override
   public void initialize()
   {
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.getInstance().addControl(new LaunchDebuggerControl());
      IDE.getInstance().addControl(new RunAppControl());
      
   }

   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      FqnResolverFactory resolverFactory = new FqnResolverFactory();
      resolverFactory.addResolver(MimeType.APPLICATION_JAVA, new JavaFqnResolver());
      new DebuggerClientService(event.getApplicationConfiguration().getContext());
      BreakpointsManager breakpointsManager = new BreakpointsManager(IDE.eventBus(), DebuggerClientService.getInstance(), AUTO_BEAN_FACTORY, resolverFactory);
      
      DebuggerPresenter debuggerPresenter = new DebuggerPresenter(breakpointsManager);
      
      IDE.addHandler(LaunchDebuggerEvent.TYPE, debuggerPresenter);
      IDE.addHandler(DebuggerConnectedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(DebuggerDisconnectedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(BreakPointsUpdatedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(ViewClosedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(RunAppEvent.TYPE, debuggerPresenter);
      IDE.addHandler(ProjectBuiltEvent.TYPE, debuggerPresenter);
      IDE.addHandler(ProjectOpenedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(ProjectClosedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(VfsChangedEvent.TYPE, debuggerPresenter);
   }

}
