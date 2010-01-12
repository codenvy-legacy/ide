package org.exoplatform.ideall.client;

import org.exoplatform.gwt.commons.initializer.ApplicationConfigurationReceivedEvent;
import org.exoplatform.gwt.commons.smartgwt.SmartGWTLoader;
import org.exoplatform.ideall.client.application.DevToolForm;
import org.exoplatform.ideall.client.common.CommonActionsComponent;
import org.exoplatform.ideall.client.common.HelpActionsComponent;
import org.exoplatform.ideall.client.groovy.GroovyActionsComponent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.conversation.ConversationServiceImpl;
import org.exoplatform.ideall.client.model.data.DataServiceImpl;
import org.exoplatform.ideall.client.model.gadget.GadgetServiceImpl;
import org.exoplatform.ideall.client.model.groovy.GroovyServiceImpl;
import org.exoplatform.ideall.client.model.jcrservice.RepositoryServiceImpl;
import org.exoplatform.ideall.client.model.settings.SettingsService;
import org.exoplatform.ideall.client.model.settings.SettingsServiceImpl;
import org.exoplatform.ideall.client.model.template.TemplateServiceImpl;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
*/
public class IDE
{

   public IDE()
   {
      for (int i = 0; i < 30; i++)
      {
         System.out.println();
      }

      new SmartGWTLoader();

      HandlerManager eventBus = new HandlerManager(null);

      new Configuration(eventBus);

      //eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandlerImpl());
      ExceptionThrownEventHandlerInitializer.initialize(eventBus);
      eventBus.addHandler(ApplicationConfigurationReceivedEvent.TYPE, Configuration.getInstance());

      /*
       * Initializing services
       */

      new SettingsServiceImpl(eventBus);

      new ConversationServiceImpl(eventBus);

      new RepositoryServiceImpl(eventBus);

      new DataServiceImpl(eventBus);

      new GroovyServiceImpl(eventBus);

      new GadgetServiceImpl(eventBus);

      new TemplateServiceImpl(eventBus);

      final ApplicationContext context = new ApplicationContext();

      /*
       * PLUGINS INITIALIZATION
       */

      context.getComponents().add(new CommonActionsComponent());
      context.getComponents().add(new GroovyActionsComponent());
      context.getComponents().add(new HelpActionsComponent());

      new DevToolForm(eventBus, context);

      Configuration.getInstance().loadConfiguration(eventBus);

      Window.addCloseHandler(new CloseHandler<Window>()
      {
         public void onClose(CloseEvent<Window> event)
         {
            SettingsService.getInstance().saveSetting(context);
         }
      });

   }

}
