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

import java.util.Collections;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Presenter for opening local file in editor.
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class OpenLocalFilePresenter extends UploadFilePresenter
{

   interface Display extends UploadFilePresenter.Display
   {
   }

   /**
    * @param eventBus
    * @param selectedItems
    * @param path
    */
   public OpenLocalFilePresenter(HandlerManager eventBus, List<Item> selectedItems, String path)
   {
      super(eventBus, selectedItems, path);
   }
   
   @Override
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
      String href = item.getHref();
      if (item instanceof File)
      {
         href = href.substring(0, href.lastIndexOf("/") + 1);
      }
      href += URL.encodePathSegment(fileName);

      display.setHiddenFields(href, mimeType, "", contentNodeType);
      display.getUploadForm().submit();
   }

   @Override
   protected boolean gotError(String uploadServiceResponse, boolean matches)
   {
      boolean getFileContent = uploadServiceResponse.matches("^<FILECONTENT>(.*)</FILECONTENT>$") 
         || uploadServiceResponse.matches("^<filecontent>(.*)</filecontent>$");
      
      return uploadServiceResponse == null || (uploadServiceResponse.length() > 0 && ! getFileContent) || matches;
   }
   
   @Override
   protected String errorMessage()
   {
      return IDE.ERRORS_CONSTANT.openLocalFileOpeningFailure();
   }
   
   @Override
   protected void completeUpload(String response)
   {
      //@param response is checked in parent method, so we sure that it is not null,
      //* and data is enclose in tag <filecontent></filecontent> (or <FILECONTENT></FILECONTENT>)
      
      // extract uploaded file content from response
      final String submittedFileContent = extractRecievedContent(response);
      if (submittedFileContent == null)
      {
         final String errMsg = IDE.IDE_LOCALIZATION_MESSAGES.openLocalFileOpeningFailure(display.getFileNameField().getValue());
         Dialogs.getInstance().showError(errMsg);
         // error - displaying behind the window
         return;
      }

      display.closeDisplay();
      openFile(submittedFileContent);
      
   }
   
   /**
    * Open new file in editor with known content.
    * 
    * @param submittedFileContent content of new file
    */
   private void openFile(String submittedFileContent)
   {
      String fileName = display.getFileNameField().getValue();

      String mimeType = ((Display)display).getMimeType().getValue();
      File submittedFile = new File(path + "/" + fileName);
      submittedFile.setNewFile(true);
      submittedFile.setContentChanged(true);
      submittedFile.setContent(submittedFileContent);
      submittedFile.setContentType(mimeType);
      submittedFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(mimeType));

      eventBus.fireEvent(new OpenFileEvent(submittedFile));
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
      ((Display)display).enableMimeTypeSelect();

      List<String> mimeTypes = IDEMimeTypes.getSupportedMimeTypes();
      Collections.sort(mimeTypes);

      List<String> proposalMimeTypes = IDEMimeTypes.getMimeTypes(event.getFileName());

      String[] valueMap = mimeTypes.toArray(new String[0]);

      ((Display)display).setMimeTypes(valueMap);

      if (proposalMimeTypes != null && proposalMimeTypes.size() > 0)
      {
         String mimeTYpe = proposalMimeTypes.get(0);
         ((Display)display).setDefaultMimeType(mimeTYpe);
         display.enableUploadButton();
      }
   }
   
   /**
    * Extract uploaded file content from upload service response.
    * File content is included in tag <filecontent>
    * 
    * @param uploadServiceResponse response from server
    * @return extracted content of submitted file.
    */
   private String extractRecievedContent(String uploadServiceResponse)
   {
      String content = uploadServiceResponse.substring("<filecontent>".length());
      content = content.substring(0, content.length() - "</filecontent>".length());

      return Utils.urlDecode_decode(content); // to unescape end of lines
   }

}
