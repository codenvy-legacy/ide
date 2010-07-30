/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.application;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.ui.client.component.command.Control;
import org.exoplatform.gwtframework.ui.client.component.command.StatusTextControl;
import org.exoplatform.ide.client.framework.application.ApplicationConfiguration;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlHandler;
import org.exoplatform.ide.client.framework.ui.event.ClearFocusEvent;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.model.settings.ApplicationSettings;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class IDEallPresenter implements ExceptionThrownHandler, RegisterControlHandler
{

   public interface Display
   {

      void showDefaultPerspective();

   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display display;

   private ApplicationContext context;

   private ApplicationSettings applicationSettings;

   private ApplicationConfiguration applicationConfiguration;

   private List<Control> controls = new ArrayList<Control>();

   private List<String> toolbarItems = new ArrayList<String>();

   private List<String> statusBarItems = new ArrayList<String>();

   public IDEallPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      toolbarItems.add("");

      handlers = new Handlers(eventBus);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      handlers.addHandler(RegisterControlEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;
      display.showDefaultPerspective();
      new IDEApplicationLoader(eventBus, context, controls, toolbarItems, statusBarItems);
   }

   public void onError(ExceptionThrownEvent event)
   {
      eventBus.fireEvent(new ClearFocusEvent());
   }

   public void onRegisterControl(RegisterControlEvent event)
   {
      addControl(event.getControl(), event.isDockOnToolbar(), event.isRightDocking());

      if (event.getControl() instanceof StatusTextControl)
      {
         statusBarItems.add(event.getControl().getId());
      }
   }

   protected void addControl(Control control, boolean dockOnToolbar, boolean rightDocking)
   {
      controls.add(control);
      if (!dockOnToolbar)
      {
         return;
      }

      if (rightDocking)
      {
         toolbarItems.add(control.getId());
      }
      else
      {
         int position = 0;
         for (String curId : toolbarItems)
         {
            if ("".equals(curId))
            {
               break;
            }
            position++;
         }

         if (control.hasDelimiterBefore())
         {
            toolbarItems.add(position, "---");
            position++;
         }

         toolbarItems.add(position, control.getId());
      }
   }

}
