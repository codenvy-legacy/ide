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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.util.MimeTypeResolver;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.HasClickHandlers;

/**
 * Presenter for commands upload file and open local file.
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class UploadPresenter implements FileSelectedHandler
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

   private String path;

   private boolean openLocalFile;

   private List<Item> selectedItems;

   private String fileType;

   private boolean isSetAll = false;

   public UploadPresenter(HandlerManager eventBus, List<Item> selectedItems, String path, boolean openLocalFile)
   {
      this.eventBus = eventBus;
      this.selectedItems = selectedItems;
      this.path = path;
      this.openLocalFile = openLocalFile;
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
      else
      {
         display.getMimeType().addValueChangeHandler(new ValueChangeHandler<String>()
         {

            public void onValueChange(ValueChangeEvent<String> event)
            {
               if (!isSetAll)
               {
                  String[] allMimeTypes = MimeTypeResolver.getAllMimeTypes().toArray(new String[0]);
                  Arrays.sort(allMimeTypes);
                  display.setMimeTypes(allMimeTypes);
                  isSetAll = true;
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
      Collections.sort(mimeTypes);

      List<String> proposalMimeTypes = IDEMimeTypes.getMimeTypes(fileName);

      String[] valueMap = mimeTypes.toArray(new String[0]);

      display.setMimeTypes(valueMap);

      if (proposalMimeTypes != null && proposalMimeTypes.size() > 0)
      {
         String mimeTYpe = proposalMimeTypes.get(0);
         display.setDefaultMimeType(mimeTYpe);
         display.enableUploadButton();
      }
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

      fileType = file.substring(file.lastIndexOf(".") + 1).toLowerCase();

      List<String> mimeTypes = MimeTypeResolver.getMimeTypes(fileType);

      String[] valueMap = mimeTypes.toArray(new String[mimeTypes.size()]);
      //      int i = 0;
      //      for (String mimeType : mimeTypes)
      //      {
      //         valueMap[i++] = mimeType;
      //      }
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

      String responseOk = checkResponseOk(uploadServiceResponse);
      if (responseOk != null)
      {
         Dialogs.getInstance().showError(responseOk);
         return;
      }

      //if uploadServiceResponse is correct, than continue uploading (or opening) file
      if (openLocalFile)
      {
         completeOpenLocalFile(uploadServiceResponse);
      }
      else
      {
         completeUpload(uploadServiceResponse);
      }

   }
   
   /**
    * Check response is Ok.
    * If response is Ok, return null,
    * else return error message
    * 
    * @param uploadServiceResponse
    * @return
    */
   private String checkResponseOk(String uploadServiceResponse)
   {
      boolean matches = 
         uploadServiceResponse.matches("^<ERROR>(.*)</ERROR>$") || uploadServiceResponse.matches("^<error>(.*)</error>$");
      
      boolean gotError;
      
      if (openLocalFile)
      {
         boolean getFileContent = uploadServiceResponse.matches("^<FILECONTENT>(.*)</FILECONTENT>$") 
            || uploadServiceResponse.matches("^<filecontent>(.*)</filecontent>$");
         gotError = uploadServiceResponse == null || (uploadServiceResponse.length() > 0 && ! getFileContent) || matches;
      }
      else
      {
         gotError = uploadServiceResponse == null || uploadServiceResponse.length() > 0 || matches;
      }
      
      if (!gotError)
      {
         return null;
      }
      
      if (matches)
      {
         String errorMsg = uploadServiceResponse.substring("<error>".length());
         errorMsg = errorMsg.substring(0, errorMsg.length() - "</error>".length());
         
         return errorMsg;
      }
      else
      {
         return openLocalFile ? "Can not open local file!" : "Can not upload file!";
      }
   }

   /**
    * Opening local file.
    * 
    * @param uploadServiceResponse is checked in parent method, so we sure that it is not null,
    * and data is enclose in tag <filecontent></filecontent> (or <FILECONTENT></FILECONTENT>)
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

   /**
    * Open new file in editor with known content.
    * 
    * @param submittedFileContent content of new file
    */
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

   private void completeUpload(String response)
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

   public void onFileSelected(String fileName)
   {
      if (openLocalFile)
      {
         openInEditor(fileName);
         return;
      }

      uploadFileToServer(fileName);
      isSetAll = false;
   }

}
