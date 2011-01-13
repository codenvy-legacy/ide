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

import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.widgets.events.HasClickHandlers;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;

import java.util.List;

/**
 * Presenter for uploading file
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class UploadPresenter implements FileSelectedHandler
{
   interface UploadDisplay
   {

      HasClickHandlers getUploadButton();

      void enableUploadButton();

      void disableUploadButton();

      HasClickHandlers getCloseButton();

      void closeDisplay();

      FormPanel getUploadForm();

      HasValue<String> getFileNameField();

      void setHiddenFields(String location, String mimeType, String nodeType, String jcrContentNodeType);

   }

   protected HandlerManager eventBus;

   protected UploadDisplay display;

   protected String path;

   protected List<Item> selectedItems;

   public UploadPresenter(HandlerManager eventBus, List<Item> selectedItems, String path)
   {
      this.eventBus = eventBus;
      this.selectedItems = selectedItems;
      this.path = path;
   }

   protected void bindDisplay(UploadDisplay d)
   {
      display = d;

      display.getUploadButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            uploadFileToForm();
         }
      });

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
   }

   /**
    * @param url
    * @return result of javaScript function <code>encodeURI(url)</code>
    */
   public static native String encodeURI(String url) /*-{
                                                     return encodeURI(url);
                                                     }-*/;

   protected void uploadFileToForm()
   {
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

      display.setHiddenFields(href, "", "", "");
      display.getUploadForm().submit();
   }

   void destroy()
   {
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
      completeUpload(uploadServiceResponse);
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
      
      if (!gotError(uploadServiceResponse, matches))
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
         return errorMessage();
      }
   }
   
   protected String errorMessage()
   {
      return "Can not upload folder!";
   }
   
   protected boolean gotError(String uploadServiceResponse, boolean matches)
   {
      return uploadServiceResponse == null || uploadServiceResponse.length() > 0 || matches;
   }

   protected void completeUpload(String response)
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
      String file = fileName;
      file = file.replace('\\', '/');

      if (file.indexOf('/') >= 0)
      {
         file = file.substring(file.lastIndexOf("/") + 1);
      }

      display.getFileNameField().setValue(file);
      display.enableUploadButton();
   }

}
