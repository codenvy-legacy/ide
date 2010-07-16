package org.exoplatform.ideall.client;

import org.exoplatform.gwtframework.ui.client.smartgwt.dialogs.SmartGWTDialogs;
import org.exoplatform.ideall.client.application.IDEallForm;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.hotkeys.HotKeyManagerImpl;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.module.development.DevelopmentModule;
import org.exoplatform.ideall.client.module.edit.FileEditModule;
import org.exoplatform.ideall.client.module.gadget.GadgetPlugin;
import org.exoplatform.ideall.client.module.groovy.GroovyModule;
import org.exoplatform.ideall.client.module.navigation.NavigationModule;
import org.exoplatform.ideall.client.module.netvibes.NetvibesModule;
import org.exoplatform.ideall.client.module.preferences.PreferencesModule;

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
      ApplicationContext context = new ApplicationContext();

      ExceptionThrownEventHandlerInitializer.initialize(eventBus);

      /*
       * PLUGINS INITIALIZATION
       */      
      context.getModules().add(new NavigationModule(eventBus, context));      
      context.getModules().add(new FileEditModule(eventBus, context));      
      context.getModules().add(new DevelopmentModule(eventBus, context));
      context.getModules().add(new PreferencesModule(eventBus, context));
      
      context.getModules().add(new GadgetPlugin(eventBus, context));
      context.getModules().add(new GroovyModule(eventBus, context));
      context.getModules().add(new NetvibesModule(eventBus, context));

      new CookieManager(eventBus);
      new HotKeyManagerImpl(eventBus, context);

      // new HistoryManager(eventBus, context); // commented to fix the bug with javascript error in IE8 (WBT-321)

      new IDEallForm(eventBus, context);

      Configuration configuration = new Configuration(eventBus, context);
      configuration.loadConfiguration(IDELoader.getInstance());         
   }

}
