package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.LaunchDebuggerEvent;
import org.exoplatform.ide.extension.java.jdi.client.fqn.FqnResolverFactory;
import org.exoplatform.ide.extension.java.jdi.client.fqn.JavaFqnResolver;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DebuggerExtension extends Extension
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
      IDE.getInstance().addControl(new LaunchDebuggerControl());
      DebuggerPresenter debuggerPresenter = new DebuggerPresenter();

      IDE.addHandler(LaunchDebuggerEvent.TYPE, debuggerPresenter);
      IDE.addHandler(DebuggerConnectedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(DebuggerDisconnectedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(BreakPointsUpdatedEvent.TYPE, debuggerPresenter);
      IDE.addHandler(ViewClosedEvent.TYPE, debuggerPresenter);
      FqnResolverFactory resolverFactory = new FqnResolverFactory();
      resolverFactory.addResolver(MimeType.APPLICATION_JAVA, new JavaFqnResolver());
      
      new BreakpointsManager(IDE.eventBus(), DebuggerClientService.getInstance(), AUTO_BEAN_FACTORY, resolverFactory);
   }

}
