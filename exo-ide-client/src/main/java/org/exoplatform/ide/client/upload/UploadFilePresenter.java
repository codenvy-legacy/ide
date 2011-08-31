/*
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
 */
package org.exoplatform.ide.client.upload;

import com.google.gwt.http.client.URL;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.util.MimeTypeResolver;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedEvent;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.Arrays;
import java.util.List;

/**
 * Presenter for uploading file.
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class UploadFilePresenter extends UploadPresenter
{

   interface Display extends UploadPresenter.UploadDisplay
   {

      HasValue<String> getMimeType();

      void setMimeTypes(String[] mimeTypes);

      void enableMimeTypeSelect();

      void disableMimeTypeSelect();

      void setDefaultMimeType(String mimeType);

      void setHiddenFields(String location, String mimeType, String nodeType, String jcrContentNodeType);

   }

   protected String fileType;
   
   protected boolean isSetAll = false;
   
   /**
    * @param eventBus
    * @param selectedItems
    * @param path
    */
   public UploadFilePresenter(HandlerManager eventBus, List<Item> selectedItems,FolderModel folder)
   {
      super(eventBus, selectedItems, folder);
   }

   @Override
   protected void bindDisplay(UploadDisplay d)
   {
      super.bindDisplay(d);
      
      ((Display)display).getMimeType().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         public void onValueChange(ValueChangeEvent<String> event)
         {
            mimeTypeChanged(event.getValue());
         }
      });

      ((Display)display).disableMimeTypeSelect();
   }
   
   protected void mimeTypeChanged(String value)
   {
      if (((Display)display).getMimeType().getValue() != null && ((Display)display).getMimeType().getValue().length() > 0)
      {
         display.enableUploadButton();
      }
      else
      {
         display.disableUploadButton();
      }
      
      if (!isSetAll)
      {
         String[] allMimeTypes = MimeTypeResolver.getAllMimeTypes().toArray(new String[0]);
         Arrays.sort(allMimeTypes);
         ((Display)display).setMimeTypes(allMimeTypes);
         isSetAll = true;
      }
   }

   @Override
   protected void uploadFileToForm()
   {
      String mimeType = ((Display)display).getMimeType().getValue();

      if (mimeType == null || "".equals(mimeType))
      {
         return;
      }

      String contentNodeType = NodeTypeUtil.getContentNodeType(mimeType);

      String fileName = display.getFileNameField().getValue();
      if (fileName.contains("/"))
      {
         fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
      }

      Item item = selectedItems.get(0);
      String href = item.getPath();
      if (item instanceof FileModel)
      {
         href = href.substring(0, href.lastIndexOf("/") + 1);
      }
      href += URL.encodePathSegment(fileName);

      display.setHiddenFields(href, mimeType, "", contentNodeType);
      display.getUploadForm().submit();
   }
   
   @Override
   protected String errorMessage()
   {
      return IDE.ERRORS_CONSTANT.uploadFileUploadingFailure();
   }

   @Override
   public void onFileSelected(FileSelectedEvent event)
   {
      isSetAll = false;
      
      String file = event.getFileName();
      file = file.replace('\\', '/');

      if (file.indexOf('/') >= 0)
      {
         file = file.substring(file.lastIndexOf("/") + 1);
      }

      display.getFileNameField().setValue(file);
      ((Display)display).enableMimeTypeSelect();

      fileType = file.substring(file.lastIndexOf(".") + 1).toLowerCase();

      List<String> mimeTypes = MimeTypeResolver.getMimeTypes(fileType);

      String[] valueMap = mimeTypes.toArray(new String[mimeTypes.size()]);
      ((Display)display).setMimeTypes(valueMap);

      if (mimeTypes != null && mimeTypes.size() > 0)
      {
         String mimeTYpe = mimeTypes.get(0);
         ((Display)display).setDefaultMimeType(mimeTYpe);
         ((Display)display).enableUploadButton();
      }

   }

}
