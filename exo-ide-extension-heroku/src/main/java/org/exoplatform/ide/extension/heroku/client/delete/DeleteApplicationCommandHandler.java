/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.heroku.client.delete;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;

import java.util.List;

/**
 * Presenter for deleting application from Heroku.
 *  Performs following actions on delete:
 * 1. Gets the Git working directory location.
 * 2. Gets application name (application info) by Git working directory location.
 * 3. Asks user to confirm the deleting of the application.
 * 4. When user confirms - performs deleting the application.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 26, 2011 5:24:52 PM anya $
 *
 */
public class DeleteApplicationCommandHandler extends GitPresenter implements DeleteApplicationHandler, LoggedInHandler
{
   private static final String NAME_PROPERTY = "name";

   /**
    * @param eventBus
    */
   public DeleteApplicationCommandHandler(HandlerManager eventBus)
   {
      super(eventBus);
      eventBus.addHandler(DeleteApplicationEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationHandler#onDeleteApplication(org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationEvent)
    */
   @Override
   public void onDeleteApplication(DeleteApplicationEvent event)
   {
      if (makeSelectionCheck())
      {
         getApplicationInfo();
      }
   }

   /**
    * Get information about application.
    */
   protected void getApplicationInfo()
   {
      final String workdir = ((ItemContext)selectedItems.get(0)).getProject().getPath();
      HerokuClientService.getInstance().getApplicationInfo(null, vfs.getId(), workdir, false,
         new HerokuAsyncRequestCallback(eventBus, this)
         {
            @Override
            protected void onSuccess(List<Property> result)
            {
               String name = null;
               for (Property property : result)
               {
                  if (NAME_PROPERTY.equals(property.getName()))
                  {
                     name = property.getValue();
                     break;
                  }
               }
               askForDelete(name, workdir);
            }
         });
   }

   /**
    * Show confirmation message before delete.
    * 
    * @param gitWorkDir
    */
   protected void askForDelete(final String deleteName, final String workdir)
   {
      final boolean isName = (deleteName != null);
      String deletion = (isName) ? deleteName : workdir;

      Dialogs.getInstance().ask(HerokuExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(),
        HerokuExtension.LOCALIZATION_CONSTANT.deleteApplicationQuestion(deletion),
         new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  doDelete(workdir);
               }
            }
         });
   }

   /**
    * Perform deleting the application on Heroku.
    *
    */
   protected void doDelete(String workdir)
   {
      HerokuClientService.getInstance().deleteApplication(null, vfs.getId(), workdir, new HerokuAsyncRequestCallback(eventBus, this)
      {
         @Override
         protected void onSuccess(List<Property> result)
         {
            eventBus.fireEvent(new OutputEvent(HerokuExtension.LOCALIZATION_CONSTANT.deleteApplicationSuccess(),
               Type.INFO));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      eventBus.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         getApplicationInfo();
      }
   }
}
