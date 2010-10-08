/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.component.command.Control;
import org.exoplatform.ide.client.framework.application.ApplicationConfiguration;
import org.exoplatform.ide.client.framework.application.event.InitializeApplicationEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.module.vfs.api.File;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class IDEConfigurationLoader
{

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private ApplicationConfiguration applicationConfiguration;

   private ApplicationSettings applicationSettings;

   private List<Control> controls;

   private List<String> toolbarDefaultItems;

   private List<String> statusBarItems;

   public IDEConfigurationLoader(HandlerManager eventBus, ApplicationContext context, List<Control> controls,
      List<String> toolbarDefaultItems, List<String> statusBarItems)
   {
      this.eventBus = eventBus;
      this.context = context;
      this.controls = controls;
      this.toolbarDefaultItems = toolbarDefaultItems;
      this.statusBarItems = statusBarItems;

      handlers = new Handlers(eventBus);

   }

   private void initializeApplication()
   {
      new Timer()
      {
         @Override
         public void run()
         {
            Window.alert("here 2");
            //eventBus.fireEvent(new RegisterEventHandlersEvent());

            new Timer()
            {
               @Override
               public void run()
               {
                  try
                  {
                     Map<String, File> openedFiles = new LinkedHashMap<String, File>();
                     eventBus.fireEvent(new InitializeApplicationEvent(openedFiles, null));
                  }
                  catch (Throwable e)
                  {
                     e.printStackTrace();
                  }
               }

            }.schedule(10);

         }
      }.schedule(10);
   }

}
