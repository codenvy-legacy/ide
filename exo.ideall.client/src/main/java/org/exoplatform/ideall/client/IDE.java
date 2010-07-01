package org.exoplatform.ideall.client;

import org.exoplatform.gwtframework.commons.initializer.event.ApplicationConfigurationReceivedEvent;
import org.exoplatform.gwtframework.ui.client.smartgwt.dialogs.SmartGWTDialogs;
import org.exoplatform.ideall.client.application.DevToolForm;
import org.exoplatform.ideall.client.common.CommonActionsComponent;
import org.exoplatform.ideall.client.common.HelpActionsComponent;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.gadgets.GadgetActionsComponent;
import org.exoplatform.ideall.client.groovy.GroovyActionsComponent;
import org.exoplatform.ideall.client.hotkeys.HotKeyManagerImpl;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.conversation.ConversationServiceImpl;
import org.exoplatform.ideall.client.model.discovery.DiscoveryServiceImpl;
import org.exoplatform.ideall.client.model.settings.SettingsServiceImpl;
import org.exoplatform.ideall.client.model.wadl.WadlServiceImpl;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
*/
public class IDE
{

   public IDE()
   {
      new SmartGWTDialogs();

      HandlerManager eventBus = new HandlerManager(null);

      new Configuration(eventBus);
      
      new CookieManager(eventBus);

      ExceptionThrownEventHandlerInitializer.initialize(eventBus);
      eventBus.addHandler(ApplicationConfigurationReceivedEvent.TYPE, Configuration.getInstance());

      /*
       * Initializing services
       */

      new SettingsServiceImpl(eventBus, IDELoader.getInstance());

      new ConversationServiceImpl(eventBus, IDELoader.getInstance());

//      new WebDavVirtualFileSystem(eventBus, IDELoader.getInstance(), ImageUtil.getIcons(), Configuration.getInstance().getContext());

      //new GroovyServiceImpl(eventBus, IDELoader.getInstance());

      //new GadgetServiceImpl(eventBus, IDELoader.getInstance());

     // new TemplateServiceImpl(eventBus, IDELoader.getInstance());

      new WadlServiceImpl(eventBus, IDELoader.getInstance());

      //new MockDiscoveryServiceImpl(eventBus);
      new DiscoveryServiceImpl(eventBus, IDELoader.getInstance());

      final ApplicationContext context = new ApplicationContext();

      /*
       * PLUGINS INITIALIZATION
       */

      context.getComponents().add(new CommonActionsComponent());
      context.getComponents().add(new GroovyActionsComponent());

      context.getComponents().add(new GadgetActionsComponent());

      context.getComponents().add(new HelpActionsComponent());
      
      new HotKeyManagerImpl(eventBus, context);

      // new HistoryManager(eventBus, context); // commented to fix the bug with javascript error in IE8 (WBT-321)

      new DevToolForm(eventBus, context);

      Configuration.getInstance().loadConfiguration(eventBus, IDELoader.getInstance());
   }

}
