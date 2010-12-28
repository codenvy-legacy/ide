/**
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
 *
 */

package org.exoplatform.ide.client.application.phases;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.module.preferences.event.SelectWorkspaceEvent;
import org.exoplatform.ide.client.workspace.event.SwitchEntryPointEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CheckEntryPointPhase extends Phase implements EntryPointChangedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private IDEConfiguration applicationConfiguration;

   private ApplicationSettings applicationSettings;

   public CheckEntryPointPhase(HandlerManager eventBus, IDEConfiguration applicationConfiguration,
      ApplicationSettings applicationSettings)
   {
      this.eventBus = eventBus;
      this.applicationConfiguration = applicationConfiguration;
      this.applicationSettings = applicationSettings;

      handlers = new Handlers(eventBus);
      handlers.addHandler(EntryPointChangedEvent.TYPE, this);
   }

   protected void execute()
   {
      /*
       * verify entry point
       */
      if (applicationSettings.getValueAsString("entry-point") == null)
      {
         if (applicationConfiguration.getDefaultEntryPoint() != null)
         {
            String defaultEntryPoint = applicationConfiguration.getDefaultEntryPoint();
            if (!defaultEntryPoint.endsWith("/"))
            {
               defaultEntryPoint += "/";
            }
            applicationSettings.setValue("entry-point", applicationConfiguration.getDefaultEntryPoint(), Store.COOKIES);
         }
      }

      if (applicationSettings.getValueAsString("entry-point") != null)
      {
         String entryPoint = applicationSettings.getValueAsString("entry-point");
         eventBus.fireEvent(new SwitchEntryPointEvent(entryPoint));
      }
      else
      {
         promptToSelectEntryPoint();
      }

   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      handlers.removeHandlers();

      if (event.getEntryPoint() == null)
      {
         promptToSelectEntryPoint();
      }
      else
      {
         new RestoreOpenedFilesPhase(eventBus, applicationSettings);
      }
   }

   protected void promptToSelectEntryPoint()
   {
      // TODO [IDE-307] handle incorrect appConfig["entryPoint"] property value
      Dialogs.getInstance().showError("Workspace was not set!",
         //"Workspace was not set. Please, click on 'Ok' button and select another workspace manually from the next dialog!",
         "Workspace was not set. Please, select another workspace manually from the next dialog!",
         new BooleanValueReceivedHandler()
         {
            public void booleanValueReceived(Boolean value)
            {
               if (value)
               {
                  eventBus.fireEvent(new SelectWorkspaceEvent());
               }
            }
         });
   }

}
