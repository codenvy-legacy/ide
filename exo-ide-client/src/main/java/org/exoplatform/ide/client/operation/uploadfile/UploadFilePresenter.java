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

package org.exoplatform.ide.client.operation.uploadfile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasValue;

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
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.operation.overwrite.ui.AbstarctOverwriteDialog;
import org.exoplatform.ide.client.operation.uploadfile.UploadHelper.ErrorData;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.ExitCodes;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * Presenter for uploading file form.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class UploadFilePresenter implements UploadFileHandler, ViewClosedHandler, ItemsSelectedHandler
{

   public interface Display extends IsView
   {

      HasValue<String> getMimeTypeField();

      void setSelectedMimeType(String mimeType);

      void setMimeTypes(String[] mimeTypes);

      void setMimeTypeFieldEnabled(boolean enabled);

      HasClickHandlers getOpenButton();

      void setOpenButtonEnabled(boolean enabled);

      HasClickHandlers getCloseButton();

      FormPanel getUploadForm();

      HasValue<String> getFileNameField();

      HasFileSelectedHandler getFileUploadInput();

      void setMimeTypeHiddedField(String mimeType);

      void setNameHiddedField(String name);

      void setOverwriteHiddedField(Boolean overwrite);

   }

   IdeUploadLocalizationConstant lb = IDE.UPLOAD_CONSTANT;

   private Display display;

   private List<Item> selectedItems = new ArrayList<Item>();

   private String fileName;

   public UploadFilePresenter()
   {
      IDE.getInstance().addControl(new UploadFileControl());
      IDE.addHandler(UploadFileEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
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
   public void onUploadFile(UploadFileEvent event)
   {
      if (display != null)
      {
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

      display.setOpenButtonEnabled(false);
      display.setMimeTypeFieldEnabled(false);
      display.getFileUploadInput().addFileSelectedHandler(fileSelectedHandler);
      display.getMimeTypeField().addValueChangeHandler(mimeTypeChangedHandler);

      display.getOpenButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doUploadFile();
         }
      });

      display.getCloseButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getUploadForm().addSubmitHandler(new SubmitHandler()
      {
         public void onSubmit(SubmitEvent event)
         {
            submit(event);
         }
      });

      display.getUploadForm().addSubmitCompleteHandler(new SubmitCompleteHandler()
      {
         public void onSubmitComplete(SubmitCompleteEvent event)
         {
            submitComplete(event.getResults());
         }
      });
   }

   private FileSelectedHandler fileSelectedHandler = new FileSelectedHandler()
   {
      @Override
      public void onFileSelected(FileSelectedEvent event)
      {
         fileName = event.getFileName();
         fileName = fileName.replace('\\', '/');

         if (fileName.indexOf('/') >= 0)
         {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
         }

         display.getFileNameField().setValue(fileName);
         display.setMimeTypeFieldEnabled(true);

         List<String> mimeTypes = IDEMimeTypes.getSupportedMimeTypes();
         Collections.sort(mimeTypes);

         List<String> proposalMimeTypes = IDEMimeTypes.getMimeTypes(event.getFileName());

         String[] valueMap = mimeTypes.toArray(new String[0]);

         display.setMimeTypes(valueMap);

         if (proposalMimeTypes != null && proposalMimeTypes.size() > 0)
         {
            String mimeType = proposalMimeTypes.get(0);
            display.setSelectedMimeType(mimeType);
            display.setOpenButtonEnabled(true);
         }
         else
         {
            display.setOpenButtonEnabled(false);
         }
      }
   };

   ValueChangeHandler<String> mimeTypeChangedHandler = new ValueChangeHandler<String>()
   {
      @Override
      public void onValueChange(ValueChangeEvent<String> event)
      {
         if (display.getMimeTypeField().getValue() != null && display.getMimeTypeField().getValue().length() > 0)
         {
            display.setOpenButtonEnabled(true);
         }
         else
         {
            display.setOpenButtonEnabled(false);
         }
      }
   };

   private void doUploadFile()
   {
      String mimeType = display.getMimeTypeField().getValue();

      if (mimeType == null || "".equals(mimeType))
      {
         mimeType = null;
      }

      Item item = selectedItems.get(0);
      String uploadUrl = "";
      if (item instanceof FileModel)
      {
         uploadUrl = ((FileModel)item).getParent().getLinkByRelation(Link.REL_UPLOAD_FILE).getHref();
      }
      else
      {
         uploadUrl = item.getLinkByRelation(Link.REL_UPLOAD_FILE).getHref();
      }
      display.getUploadForm().setAction(uploadUrl);
      display.setMimeTypeHiddedField(mimeType);
      display.getUploadForm().submit();
   }

   protected void submit(SubmitEvent event)
   {
      IDELoader.getInstance().show();
   }

   private void submitComplete(String uploadServiceResponse)
   {
      IDELoader.getInstance().hide();

      if (uploadServiceResponse == null || uploadServiceResponse.isEmpty())
      {
         // if response is null or empty - than complete upload
         completeUpload();
         return;
      }

      ErrorData errData = UploadHelper.parseError(uploadServiceResponse);
      if (ExitCodes.ITEM_EXISTS == errData.code)
      {
         AbstarctOverwriteDialog dialog = new AbstarctOverwriteDialog(fileName, errData.text)
         {

            @Override
            public void onOverwrite()
            {
               display.setOverwriteHiddedField(true);
               display.getUploadForm().submit();
            }

            @Override
            public void onRename(String value)
            {
               display.setNameHiddedField(value);
               display.getUploadForm().submit();
            }

            @Override
            public void onCancel()
            {
               closeView();
            }
         };
         IDE.getInstance().openView(dialog);
      }
      else
      {
         // in this case show the error, received from server.
         Dialogs.getInstance().showError(errData.text);
      }
   }

   private void completeUpload()
   {
      closeView();

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

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

}
