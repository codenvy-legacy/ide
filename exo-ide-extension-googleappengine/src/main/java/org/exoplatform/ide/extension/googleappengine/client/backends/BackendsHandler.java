/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.googleappengine.client.backends;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;
import org.exoplatform.ide.extension.googleappengine.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.googleappengine.client.login.PerformOperationHandler;
import org.exoplatform.ide.extension.googleappengine.client.model.State;

/**
 * Handler for operations with backends.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class BackendsHandler extends GoogleAppEnginePresenter implements UpdateBackendsHandler,
   UpdateBackendStateHandler, DeleteBackendHandler, RollbackBackendsHandler, ConfigureBackendHandler
{
   /**
    * Handler for update all backends operation.
    */
   private PerformOperationHandler updateAllOperationHandler = new PerformOperationHandler()
   {

      @Override
      public void onPerformOperation(String email, String password, LoggedInHandler loggedInHandler)
      {
         updateAllBackends(email, password, loggedInHandler);
      }
   };

   /**
    * Handler for update backend operation.
    */
   private PerformOperationHandler updateOperationHandler = new PerformOperationHandler()
   {

      @Override
      public void onPerformOperation(String email, String password, LoggedInHandler loggedInHandler)
      {
         updateBackend(email, password, loggedInHandler);
      }
   };

   /**
    * Handler for rollback all backends operation.
    */
   private PerformOperationHandler rollbackAllOperationHandler = new PerformOperationHandler()
   {

      @Override
      public void onPerformOperation(String email, String password, LoggedInHandler loggedInHandler)
      {
         rollbackAllBackends(email, password, loggedInHandler);
      }
   };

   /**
    * Handler for rollback backend operation.
    */
   private PerformOperationHandler rollbackOperationHandler = new PerformOperationHandler()
   {

      @Override
      public void onPerformOperation(String email, String password, LoggedInHandler loggedInHandler)
      {
         rollbackBackend(email, password, loggedInHandler);
      }
   };

   /**
    * Handler for configure backend operation.
    */
   private PerformOperationHandler configureOperationHandler = new PerformOperationHandler()
   {

      @Override
      public void onPerformOperation(String email, String password, LoggedInHandler loggedInHandler)
      {
         configureBackend(email, password, loggedInHandler);
      }
   };

   /**
    * Handler for delete backend operation.
    */
   private PerformOperationHandler deleteOperationHandler = new PerformOperationHandler()
   {

      @Override
      public void onPerformOperation(String email, String password, LoggedInHandler loggedInHandler)
      {
         deleteBackend(email, password, loggedInHandler);
      }
   };

   /**
    * Handler for update backend's state operation.
    */
   private PerformOperationHandler updateStateOperationHandler = new PerformOperationHandler()
   {

      @Override
      public void onPerformOperation(String email, String password, LoggedInHandler loggedInHandler)
      {
         updateState(email, password, loggedInHandler);
      }
   };

   /**
    * Current backend's name.
    */
   private String backendName;

   /**
    * Current backend's state.
    */
   private State backendState;

   public BackendsHandler()
   {
      IDE.addHandler(UpdateBackendsEvent.TYPE, this);
      IDE.addHandler(UpdateBackendStateEvent.TYPE, this);
      IDE.addHandler(DeleteBackendEvent.TYPE, this);
      IDE.addHandler(ConfigureBackendEvent.TYPE, this);
      IDE.addHandler(RollbackBackendsEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.backends.UpdateBackendsHandler#onUpdateBackend(org.exoplatform.ide.extension.googleappengine.client.backends.UpdateBackendsEvent)
    */
   @Override
   public void onUpdateBackend(UpdateBackendsEvent event)
   {
      if (isAppEngineProject())
      {
         if (event.isAll())
         {
            updateAllBackends(null, null, null);
         }
         else
         {
            backendName = event.getBackendName();
            updateBackend(null, null, null);
         }
      }
      else
      {
         Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
      }
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.backends.UpdateBackendStateHandler#onUpdateBackendState(org.exoplatform.ide.extension.googleappengine.client.backends.UpdateBackendStateEvent)
    */
   @Override
   public void onUpdateBackendState(UpdateBackendStateEvent event)
   {
      if (isAppEngineProject())
      {
         backendName = event.getBackendName();
         backendState = event.getState();
         updateState(null, null, null);
      }
      else
      {
         Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
      }
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.backends.ConfigureBackendHandler#onConfigureBackend(org.exoplatform.ide.extension.googleappengine.client.backends.ConfigureBackendEvent)
    */
   @Override
   public void onConfigureBackend(ConfigureBackendEvent event)
   {
      if (isAppEngineProject())
      {
         backendName = event.getBackendName();
         configureBackend(null, null, null);
      }
      else
      {
         Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
      }
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.backends.RollbackBackendsHandler#onRollbackBackend(org.exoplatform.ide.extension.googleappengine.client.backends.RollbackBackendsEvent)
    */
   @Override
   public void onRollbackBackend(RollbackBackendsEvent event)
   {
      if (isAppEngineProject())
      {
         if (event.isAll())
         {
            rollbackAllBackends(null, null, null);
         }
         else
         {
            backendName = event.getBackendName();
            rollbackBackend(null, null, null);
         }
      }
      else
      {
         Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
      }
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.backends.DeleteBackendHandler#onDeleteBackend(org.exoplatform.ide.extension.googleappengine.client.backends.DeleteBackendEvent)
    */
   @Override
   public void onDeleteBackend(DeleteBackendEvent event)
   {
      if (isAppEngineProject())
      {
         backendName = event.getBackendName();
         deleteBackend(null, null, null);
      }
      else
      {
         Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
      }
   }

   /**
    * Perform updating backend's state.
    * 
    * @param email user's email (can be <code>null</code>)
    * @param password user's password (can be <code>null</code>)
    * @param loggedInHandler handler for logged in operation (can be <code>null</code>)
    */
   private void updateState(String email, String password, LoggedInHandler loggedInHandler)
   {
      try
      {
         GoogleAppEngineClientService.getInstance().setBackendState(currentVfs.getId(), currentProject.getId(),
            backendName, backendState.name(), email, password,
            new GoogleAppEngineAsyncRequestCallback<Object>(updateStateOperationHandler, null)
            {

               @Override
               protected void onSuccess(Object result)
               {
                  IDE.fireEvent(new RefreshBackendListEvent());
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Perform updating backend.
    * 
    * @param email user's email (can be <code>null</code>)
    * @param password user's password (can be <code>null</code>)
    * @param loggedInHandler handler for logged in operation (can be <code>null</code>)
    */
   private void updateBackend(String email, String password, final LoggedInHandler loggedInHandler)
   {
      try
      {
         GoogleAppEngineClientService.getInstance().updateBackend(currentVfs.getId(), currentProject.getId(),
            backendName, email, password, new GoogleAppEngineAsyncRequestCallback<Object>(updateOperationHandler, null)
            {

               @Override
               protected void onSuccess(Object result)
               {
                  IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION
                     .updateBackendSuccessfully(backendName), Type.INFO));
                  IDE.fireEvent(new RefreshBackendListEvent());
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Perform updating backends.
    * 
    * @param email user's email (can be <code>null</code>)
    * @param password user's password (can be <code>null</code>)
    * @param loggedInHandler handler for logged in operation (can be <code>null</code>)
    */
   private void updateAllBackends(String email, String password, final LoggedInHandler loggedInHandler)
   {
      try
      {
         GoogleAppEngineClientService.getInstance().updateAllBackends(currentVfs.getId(), currentProject.getId(),
            email, password, new GoogleAppEngineAsyncRequestCallback<Object>(updateAllOperationHandler, null)
            {

               @Override
               protected void onSuccess(Object result)
               {
                  IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION
                     .updateAllBackendsSuccessfully(), Type.INFO));
                  IDE.fireEvent(new RefreshBackendListEvent());
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Perform rollback backend.
    * 
    * @param email user's email (can be <code>null</code>)
    * @param password user's password (can be <code>null</code>)
    * @param loggedInHandler handler for logged in operation (can be <code>null</code>)
    */
   private void rollbackBackend(String email, String password, final LoggedInHandler loggedInHandler)
   {
      try
      {
         GoogleAppEngineClientService.getInstance().rollbackBackend(currentVfs.getId(), currentProject.getId(),
            backendName, email, password,
            new GoogleAppEngineAsyncRequestCallback<Object>(rollbackOperationHandler, null)
            {

               @Override
               protected void onSuccess(Object result)
               {
                  IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION
                     .rollbackBackendSuccessfully(backendName), Type.INFO));
                  IDE.fireEvent(new RefreshBackendListEvent());
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Perform rollback all backends.
    * 
    * @param email user's email (can be <code>null</code>)
    * @param password user's password (can be <code>null</code>)
    * @param loggedInHandler handler for logged in operation (can be <code>null</code>)
    */
   private void rollbackAllBackends(String email, String password, final LoggedInHandler loggedInHandler)
   {
      try
      {
         GoogleAppEngineClientService.getInstance().rollbackAllBackends(currentVfs.getId(), currentProject.getId(),
            email, password, new GoogleAppEngineAsyncRequestCallback<Object>(rollbackAllOperationHandler, null)
            {

               @Override
               protected void onSuccess(Object result)
               {
                  IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION
                     .rollbackAllBackendsSuccessfully(), Type.INFO));
                  IDE.fireEvent(new RefreshBackendListEvent());
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Perform deleting backend.
    * 
    * @param email user's email (can be <code>null</code>)
    * @param password user's password (can be <code>null</code>)
    * @param loggedInHandler handler for logged in operation (can be <code>null</code>)
    */
   private void deleteBackend(String email, String password, final LoggedInHandler loggedInHandler)
   {
      try
      {
         GoogleAppEngineClientService.getInstance().deleteBackend(currentVfs.getId(), currentProject.getId(),
            backendName, email, password, new GoogleAppEngineAsyncRequestCallback<Object>(deleteOperationHandler, null)
            {

               @Override
               protected void onSuccess(Object result)
               {
                  IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION
                     .deleteBackendSuccessfully(backendName), Type.INFO));
                  IDE.fireEvent(new RefreshBackendListEvent());
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Perform configuring backend.
    * 
    * @param email user's email (can be <code>null</code>)
    * @param password user's password (can be <code>null</code>)
    * @param loggedInHandler handler for logged in operation (can be <code>null</code>)
    */
   private void configureBackend(String email, String password, final LoggedInHandler loggedInHandler)
   {
      try
      {
         GoogleAppEngineClientService.getInstance().configureBackend(currentVfs.getId(), currentProject.getId(),
            backendName, email, password,
            new GoogleAppEngineAsyncRequestCallback<Object>(configureOperationHandler, null)
            {

               @Override
               protected void onSuccess(Object result)
               {
                  IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION
                     .configureBackendSuccessfully(backendName), Type.INFO));
                  IDE.fireEvent(new RefreshBackendListEvent());
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }
}
