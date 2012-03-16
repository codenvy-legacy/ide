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

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
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
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Presenter for deleting application from Heroku.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 26, 2011 5:24:52 PM anya $
 * 
 */
public class DeleteApplicationCommandHandler extends GitPresenter implements DeleteApplicationHandler, LoggedInHandler
{
   private static final String NAME_PROPERTY = "name";

   private String application = null;

   /**
    * @param eventBus
    */
   public DeleteApplicationCommandHandler()
   {
      IDE.addHandler(DeleteApplicationEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationHandler#onDeleteApplication(org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationEvent)
    */
   @Override
   public void onDeleteApplication(DeleteApplicationEvent event)
   {
      application = event.getApplication();
      if (application != null && !application.isEmpty())
      {
         askForDelete();
         return;
      }

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
      final ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
      try
      {
         HerokuClientService.getInstance().getApplicationInfo(null, vfs.getId(), project.getId(), false,
            new HerokuAsyncRequestCallback(this)
            {
               @Override
               protected void onSuccess(List<Property> properties)
               {
                  for (Property property : properties)
                  {
                     if (NAME_PROPERTY.equals(property.getName()))
                     {
                        application = property.getValue();
                        break;
                     }
                  }
                  askForDelete();
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Show confirmation message before delete.
    * 
    * @param gitWorkDir
    */
   protected void askForDelete()
   {
      Dialogs.getInstance().ask(HerokuExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(),
         HerokuExtension.LOCALIZATION_CONSTANT.deleteApplicationQuestion(application),
         new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  doDelete();
               }
            }
         });
   }

   /**
    * Perform deleting the application on Heroku.
    * 
    */
   protected void doDelete()
   {
      final String projectId = detectProjectId();
      try
      {
         HerokuClientService.getInstance().deleteApplication(application, vfs.getId(), projectId,
            new HerokuAsyncRequestCallback(this)
            {
               @Override
               protected void onSuccess(List<Property> properties)
               {
                  IDE.fireEvent(new OutputEvent(HerokuExtension.LOCALIZATION_CONSTANT.deleteApplicationSuccess(),
                     Type.INFO));
                  IDE.fireEvent(new ApplicationDeletedEvent(application));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Detects project's id by Heroku application name, if opened project is the following Heroku application.
    * 
    * @return {@link String} project's id or <code>null</code> if not found
    */
   private String detectProjectId()
   {
      String projectId = null;
      if (selectedItems.size() > 0 && selectedItems.get(0) instanceof ItemContext)
      {
         ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
         if (project != null && project.getPropertyValue("heroku-application") != null
            && application.equals((String)project.getPropertyValue("heroku-application")))
         {
            projectId = project.getId();
         }
      }
      return projectId;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      IDE.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         getApplicationInfo();
      }
   }
}
