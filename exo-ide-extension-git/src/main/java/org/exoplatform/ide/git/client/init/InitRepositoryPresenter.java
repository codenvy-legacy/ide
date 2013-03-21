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
package org.exoplatform.ide.git.client.init;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for Init Repository view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 24, 2011 9:07:58 AM anya $
 * 
 */
public class InitRepositoryPresenter extends GitPresenter implements InitRepositoryHandler
{
   public interface Display extends IsView
   {
      /**
       * Get's bare field.
       * 
       * @return {@link HasValue}
       */
      HasValue<Boolean> getBareValue();

      /**
       * Get's working directory field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getWorkDirValue();

      /**
       * Gets initialize repository button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getInitButton();

      /**
       * Gets cancel button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();
   }

   private Display display;

   /**
    * @param eventBus
    */
   public InitRepositoryPresenter()
   {
      IDE.addHandler(InitRepositoryEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getInitButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            initRepository();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.init.InitRepositoryHandler#onInitRepository(org.exoplatform.ide.git.client.init.InitRepositoryEvent)
    */
   @Override
   public void onInitRepository(InitRepositoryEvent event)
   {
      if (makeSelectionCheck())
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         bindDisplay(d);
         display.getWorkDirValue().setValue(getSelectedProject().getPath(), true);
      }
   }

   /**
    * Initialize of the repository by sending request over WebSocket or HTTP.
    */
   private void initRepository()
   {
      final ProjectModel project = getSelectedProject();
      boolean bare = display.getBareValue().getValue();

      try
      {
         GitClientService.getInstance().initWS(vfs.getId(), project.getId(), project.getName(), bare,
            new RequestCallback<String>()
            {
               @Override
               protected void onSuccess(String result)
               {
                  IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.initSuccess(), Type.INFO));
                  IDE.fireEvent(new RefreshBrowserEvent(project));
                  updateProjectProperties();

               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleError(exception);
               }
            });
         IDE.getInstance().closeView(display.asView().getId());
      }
      catch (WebSocketException e)
      {
         initRepositoryREST(project.getId(), project.getName(), bare);
      }
   }

   protected void updateProjectProperties()
   {
      ProjectModel project = getSelectedProject();
      List<Property> properties = new ArrayList<Property>();
      properties.add(new PropertyImpl(GitExtension.GIT_REPOSITORY_PROP, "true"));
      project.getProperties().addAll(properties);
      ItemWrapper item = new ItemWrapper(project);
      ItemUnmarshaller unmarshaller = new ItemUnmarshaller(item);
      try
      {
         VirtualFileSystem.getInstance().updateItem(getSelectedProject(), null,
            new AsyncRequestCallback<ItemWrapper>(unmarshaller)
            {
               @Override
               protected void onSuccess(ItemWrapper result)
               {
                  IDE.fireEvent(new RefreshBrowserEvent((ProjectModel)result.getItem()));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleError(exception);

               }
            });
      }
      catch (RequestException e)
      {
         handleError(e);
      }

   }

   /**
    * Initialize of the repository (sends request over HTTP).
    */
   private void initRepositoryREST(String projectId, String projectName, boolean bare)
   {
      try
      {
         GitClientService.getInstance().init(vfs.getId(), projectId, projectName, bare,
            new AsyncRequestCallback<String>()
            {
               @Override
               protected void onSuccess(String result)
               {
                  IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.initSuccess(), Type.INFO));
                  //                  IDE.fireEvent(new RefreshBrowserEvent(((ItemContext)selectedItems.get(0)).getProject()));
                  IDE.fireEvent(new RefreshBrowserEvent(getSelectedProject()));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleError(exception);
               }
            });
      }
      catch (RequestException e)
      {
         handleError(e);
      }
      IDE.getInstance().closeView(display.asView().getId());
   }

   private void handleError(Throwable e)
   {
      String errorMessage =
         (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES.initFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
   }

}
