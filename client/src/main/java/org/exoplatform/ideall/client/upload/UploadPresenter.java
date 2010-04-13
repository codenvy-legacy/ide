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
package org.exoplatform.ideall.client.upload;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.IDELoader;
import org.exoplatform.ideall.client.Utils;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.event.file.OpenFileEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.util.IDEMimeTypes;
import org.exoplatform.ideall.client.model.util.MimeTypeResolver;
import org.exoplatform.ideall.client.model.util.NodeTypeUtil;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.upload.event.UploadFileSelectedEvent;
import org.exoplatform.ideall.client.upload.event.UploadFileSelectedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.HasClickHandlers;

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
   
   private ApplicationContext context;

   private Display display;

   private HandlerRegistration fileSelectedHandler;

   private String path;

   private boolean openLocalFile;
   
   public UploadPresenter(HandlerManager eventBus, ApplicationContext context, String path, boolean openLocalFile)
   {
      this.eventBus = eventBus;
      this.context = context;
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

      display.disableUploadButton();
      display.disableMimeTypeSelect();
   }

   private void uploadFile()
   {
      String mimeType = display.getMimeType().getValue();
      
      if (mimeType == null || "".equals(mimeType)) {
         return;
      }
      
      String contentNodeType = NodeTypeUtil.getContentNodeType(mimeType);

      String fileName = display.getFileNameField().getValue();
      if (fileName.contains("/")) {
         fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
      }
      
      Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);
      String href = item.getHref();
      if (item instanceof File) {
         href = href.substring(0, href.lastIndexOf("/") + 1);
      }
      href += fileName;
      
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
      // test if server returns an error. In this case the received content will not be started from "<PRE>" and finished by "</PRE>"
      String openBracketPattern = "<PRE>";
      String closeBracketPattern = "</PRE>";

      if (!uploadServiceResponse.matches("^" + openBracketPattern + "(.*)" + closeBracketPattern + "$"))
      {
         // trying to test by using RegExp patterns in lower case
         openBracketPattern = openBracketPattern.toLowerCase();
         closeBracketPattern = closeBracketPattern.toLowerCase();
         if (!uploadServiceResponse.matches(openBracketPattern + "(.*)" + closeBracketPattern))
         {
            return null;
         }
      }

      // extract uploaded file content from upload service response
      String content = uploadServiceResponse.replaceFirst(openBracketPattern, "");

      // replace last occurrence of closeBracketPattern on ""
      int lastIndexOfCloseBracket = content.lastIndexOf(closeBracketPattern);
      content = content.substring(0, lastIndexOfCloseBracket);

      content = Utils.URLDecode_decode(content); // to unescape end of lines
      return content;
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
      display.enableUploadButton();
      display.enableMimeTypeSelect();

      List<String> mimeTypes = new ArrayList<String>();
      mimeTypes.add(MimeType.TEXT_PLAIN);
      mimeTypes.add(MimeType.TEXT_XML);
      mimeTypes.add(MimeType.TEXT_HTML);
      mimeTypes.add(MimeType.TEXT_JAVASCRIPT);
      mimeTypes.add(MimeType.TEXT_CSS);
      mimeTypes.add(MimeType.APPLICATION_GROOVY);
      mimeTypes.add(MimeType.APPLICATION_JAVASCRIPT);
      mimeTypes.add(MimeType.APPLICATION_XML);
      mimeTypes.add(MimeType.GOOGLE_GADGET);

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
      if (openLocalFile)
      {
         completeOpenLocalFile(uploadServiceResponse);
      }
      else
      {
         completeUpload();
      }
   }

   private void completeOpenLocalFile(String uploadServiceResponse)
   {
      if (uploadServiceResponse == null)
      {
         // error - displaying behind the window
         Dialogs.getInstance().showError(
            "There is an error of file '" + display.getFileNameField().getValue() + "' loading.");
         return;
      }

      // extract uploaded file content from response
      //final String submittedFileContent = uploadServiceResponse; // extractRecievedContent(uploadServiceResponse);
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

      eventBus.fireEvent(new OpenFileEvent(submittedFile)); // to save file content
   }

   private void completeUpload()
   {
      System.out.println("UploadPresenter.completeUpload()");
      display.closeDisplay();
      
      Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);
      String href = item.getHref();
      if (item instanceof File) {
         href = href.substring(0,href.lastIndexOf("/") + 1);
      }
      
      System.out.println("href > " + href);
      
      Folder folder = new Folder(href);
      eventBus.fireEvent(new RefreshBrowserEvent(folder));      
   }

}
