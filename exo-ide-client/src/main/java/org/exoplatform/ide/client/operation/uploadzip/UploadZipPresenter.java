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

import java.util.List;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedEvent;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedHandler;
import org.exoplatform.ide.client.framework.ui.upload.HasFileSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UploadZipPresenter implements UploadZipHandler, ViewClosedHandler, ItemsSelectedHandler, FileSelectedHandler, ConfigurationReceivedSuccessfullyHandler
{

   public interface Display extends IsView
   {
      
      HasClickHandlers getUploadButton();

      void setUploadButtonEnabled(boolean enabled);

      HasClickHandlers getCancelButton();

      FormPanel getUploadForm();
      
      HasFileSelectedHandler getFileUploadInput();

      HasValue<String> getFileNameField();

      void setHiddenFields(String location, String mimeType, String nodeType, String jcrContentNodeType);
      
   }

   private Display display;
   
   protected List<Item> selectedItems;
   
   private IDEConfiguration configuration;

   public UploadZipPresenter()
   {
      IDE.getInstance().addControl(new UploadZipControl());

      IDE.EVENT_BUS.addHandler(UploadZipEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
   }

   @Override
   public void onUploadZip(UploadZipEvent event)
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
      display.getUploadForm().setAction(configuration.getUploadServiceContext() + "/folder/");
      
      display.getUploadButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            upload();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
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
      
      display.getFileUploadInput().addFileSelectedHandler(this);
      display.setUploadButtonEnabled(false);
   }
   
   private void upload() {
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
      
      display.setHiddenFields(href, "", "", "");
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
         uploadServiceResponse.matches("^<ERROR>(.*)</ERROR>$")
            || uploadServiceResponse.matches("^<error>(.*)</error>$");

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
      return IDE.ERRORS_CONSTANT.uploadFolderUploadingFailure();
   }
   
   protected boolean gotError(String uploadServiceResponse, boolean matches)
   {
      return uploadServiceResponse == null || uploadServiceResponse.length() > 0 || matches;
   }   
   
   protected void completeUpload(String response)
   {
      IDE.getInstance().closeView(display.asView().getId());

      Item item = selectedItems.get(0);
      String href = item.getHref();
      if (item instanceof File)
      {
         href = href.substring(0, href.lastIndexOf("/") + 1);
      }

      Folder folder = new Folder(href);
      IDE.EVENT_BUS.fireEvent(new RefreshBrowserEvent(folder));
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

   @Override
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      configuration = event.getConfiguration();
   }   

}
