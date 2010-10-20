/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.upload;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.HasClickHandlers;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.util.MimeTypeResolver;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.module.navigation.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.upload.event.UploadFileSelectedEvent;
import org.exoplatform.ide.client.upload.event.UploadFileSelectedHandler;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class UploadPresenter implements UploadFileSelectedHandler
{

   interface Display
   {

      HasClickHandlers getUploadButton();

      void enableUploadButton();

      void disableUploadButton();

      HasClickHandlers getCloseButton();

      void closeDisplay();

      FormPanel getUploadForm();

      HasValue<String> getFileNameField();

      HasValue<String> getMimeType();

      void setMimeTypes(String[] mimeTypes);

      void enableMimeTypeSelect();

      void disableMimeTypeSelect();

      void setDefaultMimeType(String mimeType);

      void setHiddenFields(String location, String mimeType, String nodeType, String jcrContentNodeType);

   }

   private HandlerManager eventBus;

   //private ApplicationContext context;

   private Display display;

   private HandlerRegistration fileSelectedHandler;

   private String path;

   private boolean openLocalFile;

   private List<Item> selectedItems;

   public UploadPresenter(HandlerManager eventBus, List<Item> selectedItems, String path, boolean openLocalFile)
   {
      this.eventBus = eventBus;
      this.selectedItems = selectedItems;
      this.path = path;
      this.openLocalFile = openLocalFile;
      fileSelectedHandler = eventBus.addHandler(UploadFileSelectedEvent.TYPE, this);
   }

   void bindDisplay(Display d)
   {
      display = d;

      if (openLocalFile)
      {
         display.getUploadButton().addClickHandler(new ClickHandler()
         {
            public void onClick(ClickEvent event)
            {
               openLocalFile();
            }
         });
      }
      else
      {
         display.getUploadButton().addClickHandler(new ClickHandler()
         {
            public void onClick(ClickEvent event)
            {
               uploadFile();
            }
         });
      }

      display.getCloseButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeDisplay();
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
      
      if (openLocalFile)
      {
         display.getMimeType().addValueChangeHandler(new ValueChangeHandler<String>()
         {
            public void onValueChange(ValueChangeEvent<String> event)
            {
               if (display.getMimeType().getValue() != null && display.getMimeType().getValue().length() > 0)
               {
                  display.enableUploadButton();
               }
               else
               {
                  display.disableUploadButton();
               }
            }
         });
      }

      display.disableUploadButton();
      display.disableMimeTypeSelect();
   }

   /**
    * @param url
    * @return result of javaScript function <code>encodeURI(url)</code>
    */
   public static native String encodeURI(String url) /*-{
        return encodeURI(url);
     }-*/;

   private void uploadFile()
   {
      String mimeType = display.getMimeType().getValue();

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
      href += fileName;
      href = encodeURI(href);

      display.setHiddenFields(href, mimeType, "", contentNodeType);
      display.getUploadForm().submit();
   }

   void destroy()
   {
      if (fileSelectedHandler != null)
      {
         fileSelectedHandler.removeHandler();
      }
   }

   /**
    * Extract uploaded file content from upload service response.
    * @param uploadServiceResponse
    * @return extracted content of submitted file.
    */
   private String extractRecievedContent(String uploadServiceResponse)
   {
      //extract content from uploadServiceResponse
      //5 - index of letter, that follows after tag <pre>
      //6 - number of letters in </pre> closing tag
      String content = uploadServiceResponse.substring(5, uploadServiceResponse.length() - 6);
      
      return Utils.urlDecode_decode(content); // to unescape end of lines
   }

   private void openInEditor(String fileName)
   {
      String file = fileName;
      file = file.replace('\\', '/');

      if (file.indexOf('/') >= 0)
      {
         file = file.substring(file.lastIndexOf("/") + 1);
      }

      display.getFileNameField().setValue(file);
      display.enableMimeTypeSelect();

      List<String> mimeTypes = IDEMimeTypes.getSupportedMimeTypes();
      
      List<String> proposalMimeTypes = IDEMimeTypes.getMimeTypes(fileName);

      String[] valueMap = new String[mimeTypes.size()];
      int i = 0;
      for (String mimeType : mimeTypes)
      {
         valueMap[i++] = mimeType;
      }
      display.setMimeTypes(valueMap);

      if (proposalMimeTypes != null && proposalMimeTypes.size() > 0)
      {
         String mimeTYpe = proposalMimeTypes.get(0);
         display.setDefaultMimeType(mimeTYpe);
         display.enableUploadButton();
      }
   }

   public void onUploadFileSelected(UploadFileSelectedEvent event)
   {
      if (openLocalFile)
      {
         openInEditor(event.getFileName());
         return;
      }

      uploadFileToServer(event.getFileName());

   }

   private void uploadFileToServer(String fileName)
   {
      String file = fileName;
      file = file.replace('\\', '/');

      if (file.indexOf('/') >= 0)
      {
         file = file.substring(file.lastIndexOf("/") + 1);
      }

      display.getFileNameField().setValue(file);
      display.enableUploadButton();
      display.enableMimeTypeSelect();

      String fileType = file.substring(file.lastIndexOf(".") + 1).toLowerCase();

      List<String> mimeTypes = MimeTypeResolver.getMimeTypes(fileType);

      String[] valueMap = new String[mimeTypes.size()];
      int i = 0;
      for (String mimeType : mimeTypes)
      {
         valueMap[i++] = mimeType;
      }
      display.setMimeTypes(valueMap);

      if (mimeTypes != null && mimeTypes.size() > 0)
      {
         String mimeTYpe = mimeTypes.get(0);
         display.setDefaultMimeType(mimeTYpe);
      }

   }

   protected void openLocalFile()
   {
      display.getUploadForm().submit();
   }

   protected void submit(SubmitEvent event)
   {
      IDELoader.getInstance().show();
   }

   private void submitComplete(String uploadServiceResponse)
   {
      IDELoader.getInstance().hide();

      final String errorMsg = openLocalFile ? "Can not open local file!" : "Can not upload file!";

      boolean matches = false;
      //check is uploadServiceResponse enclosed in xml tag <pre></pre> (do not case sensitive)
      if (openLocalFile)
      {
         matches =
            uploadServiceResponse.matches("^<PRE>(.*)</PRE>$") || uploadServiceResponse.matches("^<pre>(.*)</pre>$");
      }
      else
      {
         //in browser chrome tag <pre> can have some attributes, that's why regexp is another
         matches =
            uploadServiceResponse.matches("^<PRE(.*)</PRE>$") || uploadServiceResponse.matches("^<pre(.*)</pre>$");
      }

      if (uploadServiceResponse == null || !matches)
      {
         Dialogs.getInstance().showError(errorMsg);
         return;
      }

      //if uploadServiceResponse is correct, than continue uploading (or opening) file
      if (openLocalFile)
      {
         completeOpenLocalFile(uploadServiceResponse);
      }
      else
      {
         completeUpload();
      }

   }

   /**
    * Opening local file.
    * 
    * @param uploadServiceResponse is checked in parent method, so we sure that it is not null,
    * and data is enclose in tag <pre></pre> (or <PRE></PRE>)
    */
   private void completeOpenLocalFile(String uploadServiceResponse)
   {

      // extract uploaded file content from response
      final String submittedFileContent = extractRecievedContent(uploadServiceResponse);
      if (submittedFileContent == null)
      {
         Dialogs.getInstance().showError(
            "There is an error of parsing of loopback service response with file '"
               + display.getFileNameField().getValue() + "' content.");
         // error - displaying behind the window
         return;
      }

      display.closeDisplay();
      openFile(submittedFileContent);
   }

   private void openFile(String submittedFileContent)
   {
      String fileName = display.getFileNameField().getValue();

      String mimeType = display.getMimeType().getValue();
      File submittedFile = new File(path + "/" + fileName);
      submittedFile.setNewFile(true);
      submittedFile.setContentChanged(true);
      submittedFile.setContent(submittedFileContent);
      submittedFile.setContentType(mimeType);
      submittedFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(mimeType));
      
      
      
      eventBus.fireEvent(new OpenFileEvent(submittedFile));
   }

   private void completeUpload()
   {
      display.closeDisplay();

      Item item = selectedItems.get(0);
      String href = item.getHref();
      if (item instanceof File)
      {
         href = href.substring(0, href.lastIndexOf("/") + 1);
      }

      Folder folder = new Folder(href);
      eventBus.fireEvent(new RefreshBrowserEvent(folder));
   }

}
