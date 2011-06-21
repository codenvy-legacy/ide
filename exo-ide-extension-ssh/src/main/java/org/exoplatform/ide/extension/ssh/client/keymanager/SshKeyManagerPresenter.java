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
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.StringValueReceivedHandler;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.ssh.client.JsonpAsyncCallback;
import org.exoplatform.ide.extension.ssh.client.SshKeyService;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowPublicSshKeyEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerHandler;
import org.exoplatform.ide.extension.ssh.client.keymanager.ui.HasSshGrid;
import org.exoplatform.ide.extension.ssh.client.marshaller.SshKeysUnmarshaller;
import org.exoplatform.ide.extension.ssh.shared.GenKeyRequest;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshKeyManagerPresenter May 18, 2011 10:16:44 AM evgen $
 *
 */
public class SshKeyManagerPresenter implements ShowSshKeyManagerHandler, ViewClosedHandler,
   ConfigurationReceivedSuccessfullyHandler
{
   public interface Display extends IsView
   {
      String ID = "ideSshKeyManagerView";

      HasSshGrid<KeyItem> getKeyItemGrid();

      HasClickHandlers getCloseButton();

      HasClickHandlers getGenerateButton();

      HasClickHandlers getUploadButton();

   }

   private Display display;

   private IDEConfiguration configuration;

   /**
   * 
   */
   public SshKeyManagerPresenter()
   {
      IDE.EVENT_BUS.addHandler(ShowSshKeyManagerEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      //add hendler to handle Upload ssh key form closing, and refresh list of ssh keys
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerHandler#onShowSshKeyManager(org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerEvent)
    */
   @Override
   public void onShowSshKeyManager(ShowSshKeyManagerEvent event)
   {
      if (display != null)
         return;

      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();

      refreshKeys();
   }

   /**
    * 
    */
   private void refreshKeys()
   {
      SshKeyService.get().getAllKeys(new JsonpAsyncCallback<JavaScriptObject>()
      {

         @Override
         public void onSuccess(JavaScriptObject result)
         {
            getLoader().hide();
            try
            {
               display.getKeyItemGrid().setValue(SshKeysUnmarshaller.unmarshal(result));
            }
            catch (UnmarshallerException e)
            {
               IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(e));
            }
         }

         @Override
         public void onFailure(Throwable exception)
         {
            getLoader().hide();
            IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exception));
         }
      });
   }

   /**
    * 
    */
   private void bindDisplay()
   {
      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
            display = null;
         }
      });

      display.getKeyItemGrid().addViewButtonSelectionHandler(new SelectionHandler<KeyItem>()
      {

         @Override
         public void onSelection(SelectionEvent<KeyItem> event)
         {
            if (event.getSelectedItem().getPublicKeyURL() != null)
               IDE.EVENT_BUS.fireEvent(new ShowPublicSshKeyEvent(event.getSelectedItem()));
         }
      });

      display.getKeyItemGrid().addDeleteButtonSelectionHandler(new SelectionHandler<KeyItem>()
      {

         @Override
         public void onSelection(SelectionEvent<KeyItem> event)
         {
            deleteSshPublicKey(event.getSelectedItem());
         }
      });

      display.getGenerateButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            Dialogs.getInstance().askForValue("Generate Ssh Key", "Host name (w/o port): ", "",
               new StringValueReceivedHandler()
               {

                  @Override
                  public void stringValueReceived(String value)
                  {
                     if (value != null && !"".equals(value))
                     {
                        generateKey(value);
                     }
                  }
               });
         }
      });

      display.getUploadButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            new UploadSshKeyPresenter(configuration.getContext());
         }
      });
   }

   /**
    * @param keyItem
    */
   private void deleteSshPublicKey(final KeyItem keyItem)
   {
      Dialogs.getInstance().ask("IDE", "Do you want to delete ssh keys for <b>" + keyItem.getHost() + "</b> host?",
         new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  doDeleteKey(keyItem);
               }
            }
         });
   }

   /**
    * @param keyItem
    */
   private void doDeleteKey(KeyItem keyItem)
   {
      SshKeyService.get().deleteKey(keyItem, new JsonpAsyncCallback<Void>()
      {

         @Override
         public void onSuccess(Void result)
         {
            getLoader().hide();
            refreshKeys();
         }

         @Override
         public void onFailure(Throwable exception)
         {
            getLoader().hide();
            IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exception));
         }
      });
   }

   private void generateKey(String host)
   {
      SshKeyService.get().generateKey(new GenKeyRequest(host, null, null), new AsyncRequestCallback<GenKeyRequest>()
      {

         @Override
         protected void onSuccess(GenKeyRequest result)
         {
            refreshKeys();
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exception));
         }
      });
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
      }
      if (event.getView() instanceof org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display)
      {
         refreshKeys();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent)
    */
   @Override
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      configuration = event.getConfiguration();
   }

}
