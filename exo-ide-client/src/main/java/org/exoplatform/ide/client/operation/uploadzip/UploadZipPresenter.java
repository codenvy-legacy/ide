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
package org.exoplatform.ide.client.operation.uploadzip;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedEvent;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedHandler;
import org.exoplatform.ide.client.framework.ui.upload.HasFileSelectedHandler;
import org.exoplatform.ide.client.messages.IdeUploadLocalizationConstant;
import org.exoplatform.ide.client.operation.uploadfile.UploadHelper;
import org.exoplatform.ide.client.operation.uploadfile.UploadHelper.ErrorData;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.ExitCodes;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.List;

/**
 * Presenter for uploading zipped folder form.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UploadZipPresenter implements UploadZipHandler, ViewClosedHandler, ItemsSelectedHandler,
   FileSelectedHandler
{

   public interface Display extends IsView
   {

      HasClickHandlers getUploadButton();

      void setUploadButtonEnabled(boolean enabled);

      HasClickHandlers getCancelButton();

      FormPanel getUploadForm();

      HasFileSelectedHandler getFileUploadInput();

      HasValue<String> getFileNameField();
      
      HasValue<Boolean> getOverwriteAllField();

      void setHiddenFields(String location, String mimeType, String nodeType, String jcrContentNodeType);

      void setOverwriteHiddedField(Boolean overwrite);

   }

   IdeUploadLocalizationConstant lb = IDE.UPLOAD_CONSTANT;

   private Display display;

   protected List<Item> selectedItems;

   public UploadZipPresenter()
   {
      IDE.getInstance().addControl(new UploadZipControl());

      IDE.addHandler(UploadZipEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   @Override
   public void onUploadZip(UploadZipEvent event)
   {
      if (display != null)
      {
         Dialogs.getInstance().showError("Upload zipped folder display must be null");
         return;
      }

      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }

   private void bindDisplay()
   {
      display.getUploadForm().setMethod(FormPanel.METHOD_POST);
      display.getUploadForm().setEncoding(FormPanel.ENCODING_MULTIPART);

      display.getUploadButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.getUploadForm().setAction(getUploadUrl(selectedItems.get(0)));
            //server handle only hidden overwrite field, but not form check box item "Overwrite"
            display.setOverwriteHiddedField(display.getOverwriteAllField().getValue());
            display.getUploadForm().submit();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getUploadForm().addSubmitHandler(new SubmitHandler()
      {
         public void onSubmit(SubmitEvent event)
         {
            IDELoader.getInstance().show();
         }
      });

      display.getUploadForm().addSubmitCompleteHandler(new SubmitCompleteHandler()
      {
         public void onSubmitComplete(SubmitCompleteEvent event)
         {
            submitComplete(event.getResults());
         }
      });

      display.getFileUploadInput().addFileSelectedHandler(this);
      display.setUploadButtonEnabled(false);
   }

   private String getUploadUrl(Item item)
   {
      if (item instanceof FileModel)
      {
         return ((FileModel)item).getParent().getLinkByRelation(Link.REL_UPLOAD_ZIP).getHref();
      }
      else
      {
         return item.getLinkByRelation(Link.REL_UPLOAD_ZIP).getHref();
      }
   }

   private void submitComplete(String uploadServiceResponse)
   {
      IDELoader.getInstance().hide();

      if (uploadServiceResponse == null || uploadServiceResponse.isEmpty())
      {
         //if response is null or empty - than complete upload
         closeView();
         refreshFolder();
         return;
      }

      ErrorData errData = UploadHelper.parseError(uploadServiceResponse);
      if (ExitCodes.ITEM_EXISTS == errData.code)
      {
         Dialogs.getInstance().ask(lb.uploadOverwriteTitle(), errData.text + "<br>" + lb.uploadOverwriteAsk(),
            new BooleanValueReceivedHandler()
            {

               @Override
               public void booleanValueReceived(Boolean value)
               {
                  if (value == null || !value)
                  {
                     closeView();
                     refreshFolder();
                     return;
                  }
                  if (value)
                  {
                     display.setOverwriteHiddedField(true);
                     display.getUploadForm().submit();
                  }
               }
            });
      }
      else
      {
         Dialogs.getInstance().showError(errData.text);
      }
   }

   private void refreshFolder()
   {
      Item item = selectedItems.get(0);
      if (item instanceof FileModel)
      {
         IDE.fireEvent(new RefreshBrowserEvent(((FileModel)item).getParent()));
      }
      else if (item instanceof Folder)
      {
         IDE.fireEvent(new RefreshBrowserEvent((Folder)item));
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   @Override
   public void onFileSelected(FileSelectedEvent event)
   {
      String file = event.getFileName();
      file = file.replace('\\', '/');

      if (file.indexOf('/') >= 0)
      {
         file = file.substring(file.lastIndexOf("/") + 1);
      }

      display.getFileNameField().setValue(file);
      display.setUploadButtonEnabled(true);
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

}
