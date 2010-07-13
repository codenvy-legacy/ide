package org.exoplatform.ideall.client;

import org.exoplatform.gwtframework.ui.client.smartgwt.dialogs.SmartGWTDialogs;
import org.exoplatform.ideall.client.application.DevToolForm;
import org.exoplatform.ideall.client.common.CommonActionsComponent;
import org.exoplatform.ideall.client.common.HelpActionsComponent;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.hotkeys.HotKeyManagerImpl;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.groovy.model.wadl.WadlServiceImpl;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
*/
public class IDE extends VerticalPanel
{

   public IDE()
   {
      new SmartGWTDialogs();

      HandlerManager eventBus = new HandlerManager(null);

      final ApplicationContext context = new ApplicationContext();
      
      new CookieManager(eventBus);

      ExceptionThrownEventHandlerInitializer.initialize(eventBus);

      /*
       * Initializing services
       */

//      new SettingsServiceImpl(eventBus, IDELoader.getInstance());
//
//      new ConversationServiceImpl(eventBus, IDELoader.getInstance());

//      new WebDavVirtualFileSystem(eventBus, IDELoader.getInstance(), ImageUtil.getIcons(), Configuration.getInstance().getContext());

      //new GroovyServiceImpl(eventBus, IDELoader.getInstance());

      //new GadgetServiceImpl(eventBus, IDELoader.getInstance());

     // new TemplateServiceImpl(eventBus, IDELoader.getInstance());

      new WadlServiceImpl(eventBus, IDELoader.getInstance());

      //new MockDiscoveryServiceImpl(eventBus);
//      new DiscoveryServiceImpl(eventBus, IDELoader.getInstance());

      /*
       * PLUGINS INITIALIZATION
       */

      context.getComponents().add(new CommonActionsComponent());
      //context.getComponents().add(new GroovyActionsComponent());

      //context.getComponents().add(new GadgetActionsComponent());

      context.getComponents().add(new HelpActionsComponent());
      
      new HotKeyManagerImpl(eventBus, context);

      // new HistoryManager(eventBus, context); // commented to fix the bug with javascript error in IE8 (WBT-321)

      new DevToolForm(eventBus, context);

      Configuration configuration = new Configuration(eventBus, context);
      configuration.loadConfiguration(IDELoader.getInstance());         

      try {
      } catch (Exception exc) {
         exc.printStackTrace();
      }
      
      //Configuration.getInstance().loadConfiguration(eventBus, IDELoader.getInstance());
   }

}
