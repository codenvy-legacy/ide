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

package org.exoplatform.ide.client.remote;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.client.remote.service.RemoteFileService;
import org.exoplatform.ide.editor.api.EditorProducer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;

/**
 * Presenter for opening file by URL.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenFileByURLPresenter implements OpenFileByURLHandler, ViewClosedHandler, InitializeServicesHandler,
   ItemsSelectedHandler
{

   public interface Display extends IsView
   {

      /**
       * Get URL field.
       * 
       * @return {@link TextFieldItem}
       */
      TextFieldItem getURLField();

      /**
       * Get Open button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getOpenButton();

      /**
       * Enable or disable open button.
       * 
       * @param enabled <b>true</b> or <b>false</b> to make Open button enabled or disabled.
       */
      void setOpenButtonEnabled(boolean enabled);

      /**
       * Get Cancel button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();

   }

   /**
    * Instance of {@link Display} implementation.
    */
   private Display display;

   /**
    * Selected items in Workspace view.
    */
   private List<Item> selectedItems = new ArrayList<Item>();

   /**
    * Creates new instance of this presenter.
    * 
    * @param eventBus event handler manager.
    */
   public OpenFileByURLPresenter()
   {
      IDE.getInstance().addControl(new OpenFileByURLControl(), DockTarget.NONE, false);

      IDE.EVENT_BUS.addHandler(OpenFileByURLEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      new RemoteFileService(IDE.EVENT_BUS, event.getLoader(), event.getApplicationConfiguration().getContext());
   }

   /**
    * @see org.exoplatform.ide.client.remote.OpenFileByURLHandler#onOpenFileByURL(org.exoplatform.ide.client.remote.OpenFileByURLEvent)
    */
   @Override
   public void onOpenFileByURL(OpenFileByURLEvent event)
   {
      if (display != null)
      {
         return;
      }

      if (selectedItems.size() != 1)
      {
         return;
      }

      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }

   /**
    * Binds display.
    */
   private void bindDisplay()
   {
      display.getOpenButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doOpenFile();
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

      display.getURLField().addKeyPressHandler(new KeyPressHandler()
      {
         @Override
         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
            {
               try
               {
                  doOpenFile();
               }
               catch (Exception e)
               {
                  e.printStackTrace();
               }
            }
         }
      });

      display.getURLField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String url = display.getURLField().getValue().trim();
            if (url == null || url.trim().isEmpty())
            {
               display.setOpenButtonEnabled(false);
            }
            else
            {
               display.setOpenButtonEnabled(true);
            }
         }
      });

      display.setOpenButtonEnabled(false);
   }

   /**
    * Opens file after pressing "Open" button.
    */
   private void doOpenFile()
   {
      String url = display.getURLField().getValue().trim();
      if (url == null || url.isEmpty())
      {
         display.setOpenButtonEnabled(false);
         return;
      }

      RemoteFileService.getInstance().getRemoteFile(url, new AsyncRequestCallback<File>()
      {
         @Override
         protected void onSuccess(File result)
         {
            openFileInEditor(result);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            if (exception instanceof ServerException)
            {
               ServerException se = (ServerException)exception;
               String msg = se.isErrorMessageProvided() ? se.getLocalizedMessage() : "Status:&nbsp;" + se.getHTTPStatus() + "&nbsp;" + se.getStatusText();
               String message = IDE.IDE_LOCALIZATION_MESSAGES.openFileByURLErrorMessage(msg);
               Dialogs.getInstance().showError(message);
            }
            else
            {
               super.onFailure(exception);
            }
         }
      });
   }

   /**
    * Opens file in editor.
    * 
    * @param file file to be opened in editor.
    */
   private void openFileInEditor(File file)
   {
      Item selectedItem = selectedItems.get(0);

      String folder = null;

      if (selectedItem instanceof Folder)
      {
         folder = selectedItem.getHref().substring(0, selectedItem.getHref().lastIndexOf("/"));
      }
      else
      {
         folder = selectedItem.getHref();
      }

      if (!folder.endsWith("/"))
      {
         folder += "/";
      }

      //String newFileURL = folder + file.getName();
      String newFileURL = folder + URL.encodePathSegment(file.getName());

      File fileToOpen = new File(newFileURL);
      fileToOpen.setName(file.getName());
      fileToOpen.setContentType(file.getContentType());
      fileToOpen.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(file.getContentType()));
      fileToOpen.setContent(file.getContent());
      fileToOpen.setNewFile(true);
      fileToOpen.setContentChanged(true);
      fileToOpen.setIcon(ImageUtil.getIcon(file.getContentType()));

      try
      {
         EditorProducer editorProducer = IDE.getInstance().getEditor(file.getContentType());
         IDE.EVENT_BUS.fireEvent(new EditorOpenFileEvent(fileToOpen, editorProducer));
         IDE.getInstance().closeView(display.asView().getId());
      }
      catch (EditorNotFoundException e)
      {
         Dialogs.getInstance().showError(
            IDE.IDE_LOCALIZATION_MESSAGES.openFileCantFindEditorForType(file.getContentType()));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

}
