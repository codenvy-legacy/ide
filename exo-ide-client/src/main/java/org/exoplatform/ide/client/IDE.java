/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.client;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.component.GWTDialogs;
import org.exoplatform.ide.client.app.IDEForm;
import org.exoplatform.ide.client.app.IDEPresenter;
import org.exoplatform.ide.client.application.ApplicationStateSnapshotListener;
import org.exoplatform.ide.client.application.ControlsRegistration;
import org.exoplatform.ide.client.application.MainMenuControlsFormatter;
import org.exoplatform.ide.client.application.NewItemControlsFormatter;
import org.exoplatform.ide.client.autocompletion.AutoCompletionManager;
import org.exoplatform.ide.client.autocompletion.AutoCompletionManagerExt;
import org.exoplatform.ide.client.editor.EditorFactory;
import org.exoplatform.ide.client.framework.control.event.AddControlsFormatterEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.module.development.DevelopmentModule;
import org.exoplatform.ide.client.module.edit.TextEditModule;
import org.exoplatform.ide.client.module.preferences.PreferencesModule;
import org.exoplatform.ide.client.navigation.NavigationModule;
import org.exoplatform.ide.client.test.TestPresenter;
import org.exoplatform.ide.editor.api.EditorProducer;

/**
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
*/
public class IDE extends org.exoplatform.ide.client.framework.module.IDE
{

   private ControlsRegistration controlsRegistration;

   private ApplicationContext context;
   
   private IDEPresenter presenter;

   public IDE()
   {

      new GWTDialogs();

      //HandlerManager eventBus = new HandlerManager(null);
      context = new ApplicationContext();

      new ExceptionThrownEventHandler(EVENT_BUS);

      //new CookieManager(eventBus);

      // new HistoryManager(eventBus, context); // commented to fix the bug with javascript error in IE8 (WBT-321)

      controlsRegistration = new ControlsRegistration(EVENT_BUS);

      EVENT_BUS.fireEvent(new AddControlsFormatterEvent(new MainMenuControlsFormatter()));
      EVENT_BUS.fireEvent(new AddControlsFormatterEvent(new NewItemControlsFormatter()));

      //new IDEForm(EVENT_BUS, context, controlsRegistration);

      IDEForm ideForm = new IDEForm();
      presenter = new IDEPresenter(EVENT_BUS, ideForm, controlsRegistration);
      
      new TestPresenter(EVENT_BUS);
      
      new AutoCompletionManager(EVENT_BUS);

      new AutoCompletionManagerExt(EVENT_BUS);

      new ApplicationStateSnapshotListener(EVENT_BUS);

      /*
       * MODULES INITIALIZATION
       */
      new NavigationModule(EVENT_BUS, context);
      
      new TextEditModule(EVENT_BUS);
      new DevelopmentModule(EVENT_BUS);
      new PreferencesModule(EVENT_BUS);

      //initialize extensions
      for (Extension ext : extensions)
      {
         ext.initialize();
      }

      controlsRegistration.formatControls();
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#addControl(org.exoplatform.gwtframework.ui.client.command.Control, org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget, boolean)
    */
   @Override
   public void addControl(Control<?> control, DockTarget dockTarget, boolean rightDocking)
   {
      controlsRegistration.addControl(control, dockTarget, rightDocking);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#openView(org.exoplatform.ide.client.framework.ui.View)
    */
   @Override
   public void openView(View view)
   {
      context.getCurrentPerspective().openView(view);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#closeView(java.lang.String)
    */
   @Override
   public void closeView(String viewId)
   {
      presenter.closeView(viewId);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#addEditor(org.exoplatform.ide.editor.api.EditorProducer)
    */
   @Override
   public void addEditor(EditorProducer editorProducer)
   {
      EditorFactory.addEditor(editorProducer);
   }

   @Override
   public void openView(ViewEx view)
   {
      presenter.openView(view);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#getEditor(java.lang.String)
    */
   @Override
   public EditorProducer getEditor(String mimeType) throws EditorNotFoundException
   {
      return EditorFactory.getDefaultEditor(mimeType);
   }

}
