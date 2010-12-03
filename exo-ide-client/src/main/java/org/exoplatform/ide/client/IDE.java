package org.exoplatform.ide.client;

import org.exoplatform.gwtframework.ui.client.smartgwt.dialogs.SmartGWTDialogs;
import org.exoplatform.ide.client.application.ApplicationStateSnapshotListener;
import org.exoplatform.ide.client.application.ControlsRegistration;
import org.exoplatform.ide.client.application.IDEForm;
import org.exoplatform.ide.client.autocompletion.AutoCompletionManager;
import org.exoplatform.ide.client.autocompletion.AutoCompletionManagerExt;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.module.chromattic.ChromatticModule;
import org.exoplatform.ide.client.module.development.DevelopmentModule;
import org.exoplatform.ide.client.module.edit.TextEditModule;
import org.exoplatform.ide.client.module.gadget.GadgetModule;
import org.exoplatform.ide.client.module.groovy.GroovyModule;
import org.exoplatform.ide.client.module.navigation.NavigationModule;
import org.exoplatform.ide.client.module.netvibes.NetvibesModule;
import org.exoplatform.ide.client.module.preferences.PreferencesModule;

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

      new ExceptionThrownEventHandler(eventBus);

      //new CookieManager(eventBus);

      // new HistoryManager(eventBus, context); // commented to fix the bug with javascript error in IE8 (WBT-321)

      ControlsRegistration controlsRegistration = new ControlsRegistration(eventBus);
      
      new IDEForm(eventBus, context, controlsRegistration);
      
      new AutoCompletionManager(eventBus);
      
      new AutoCompletionManagerExt(eventBus);

      new ApplicationStateSnapshotListener(eventBus);         
      
      /*
       * MODULES INITIALIZATION
       */
      new NavigationModule(eventBus, context);
      new TextEditModule(eventBus);
      new DevelopmentModule(eventBus);
      new PreferencesModule(eventBus);

      new GadgetModule(eventBus);
      new GroovyModule(eventBus);
      new ChromatticModule(eventBus);
      new NetvibesModule(eventBus);
      
      //new TestIFrame();
   }

}
