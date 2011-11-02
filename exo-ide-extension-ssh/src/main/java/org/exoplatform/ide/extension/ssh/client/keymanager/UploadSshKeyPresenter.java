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
package org.exoplatform.ide.extension.ssh.client.keymanager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedEvent;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedHandler;
import org.exoplatform.ide.client.framework.ui.upload.HasFileSelectedHandler;
import org.exoplatform.ide.extension.ssh.client.SshKeyExtension;
import org.exoplatform.ide.extension.ssh.client.keymanager.ui.UploadSshKeyView;

/**
 * This class is presenter for {@link UploadSshKeyView}.
 * Main appointment of this class is upload private SSH key to the server.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class UploadSshKeyPresenter implements ViewClosedHandler, FileSelectedHandler
{
   public interface Display extends IsView
   {

      /**
       * Get host filed
       * @return instance of {@link HasValue} interface
       */
      HasValue<String> getHostField();

      /**
       * @return {@link HasClickHandlers} instance for Cancel button
       */
      HasClickHandlers getCancelButon();

      /**
       * @return {@link HasClickHandlers} instance for Upload button
       */
      HasClickHandlers getUploadButton();

      /**
       * Get file name filed
       * @return instance of {@link HasValue} interface
       */
      HasValue<String> getFileNameField();

      /**
       * Form that do upload
       * @return {@link FormPanel} instance
       */
      FormPanel getFormPanel();

      /**
       * Set error message
       * @param message the message
       */
      void setMessage(String message);

      HasFileSelectedHandler getFileUploadInput();

      /**
       * Enable Upload button
       */
      void setUploadButtonEnabled();
   }

   /**
    * Instance of display
    */
   private Display display;

   /**
    * Registration of {@link ViewClosedEvent} handler 
    */
   private HandlerRegistration viewClosedHandler;

   /**
    * IDE REST Context URL
    */
   private String restContext;

   
   /**
    * @param restContext part of URL to IDE REST Context
    */
   public UploadSshKeyPresenter(String restContext)
   {
      this.restContext = restContext;
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      display = GWT.create(Display.class);

      bind();

      IDE.getInstance().openView(display.asView());
      viewClosedHandler = IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Add all handlers to controls.
    */
   private void bind()
   {
      display.getCancelButon().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getUploadButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            upload();
         }
      });

      display.getFormPanel().addSubmitCompleteHandler(new SubmitCompleteHandler()
      {
         @Override
         public void onSubmitComplete(SubmitCompleteEvent event)
         {
            if(event.getResults().contains("Success"))
               IDE.getInstance().closeView(display.asView().getId());
            else
            {
               IDE.fireEvent(new ExceptionThrownEvent(event.getResults()));
            }
         }
      });

      display.getFileUploadInput().addFileSelectedHandler(this);
   }

   /**
    * Validate <b>host</b> parameter and do submit action.
    * If <b>host</b> parameter is null or empty string, show error message.
    */
   private void upload()
   {
      String host = display.getHostField().getValue();
      if (host == null || host.isEmpty())
      {
         display.setMessage(SshKeyExtension.CONSTANTS.hostValidationError());
         return;
      }

      display.getFormPanel().setEncoding(FormPanel.ENCODING_MULTIPART);
      display.getFormPanel().setAction(restContext + "/ide/ssh-keys/add?host=" + host);
      display.getFormPanel().submit();
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
         viewClosedHandler.removeHandler();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.upload.FileSelectedHandler#onFileSelected(org.exoplatform.ide.client.framework.ui.upload.FileSelectedEvent)
    */
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
      display.setUploadButtonEnabled();
   }

}
