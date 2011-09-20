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
package org.exoplatform.ide.extension.gadget.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetEvent;
import org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetHandler;
import org.exoplatform.ide.extension.gadget.client.service.GadgetMetadata;
import org.exoplatform.ide.extension.gadget.client.service.GadgetService;
import org.exoplatform.ide.extension.gadget.client.service.TokenRequest;
import org.exoplatform.ide.extension.gadget.client.service.TokenResponse;
import org.exoplatform.ide.extension.gadget.client.ui.GadgetPreviewPane;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Link;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GadgetPluginEventHandler implements EditorActiveFileChangedHandler, PreviewGadgetHandler,
   ConfigurationReceivedSuccessfullyHandler, ViewClosedHandler
{

   private HandlerManager eventBus;

   private FileModel activeFile;

   private IDEConfiguration applicationConfiguration;

   private boolean previewOpened = false;

   private GadgetPreviewPane gadgetPreviewPane;

   public GadgetPluginEventHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(PreviewGadgetEvent.TYPE, this);
      eventBus.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }


   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();
      if (previewOpened)
      {
         IDE.getInstance().closeView(GadgetPreviewPane.ID);
         previewOpened = false;
      }

   }

   /**
    * @see org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetHandler#onPreviewGadget(org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetEvent)
    */
   @Override
   public void onPreviewGadget(PreviewGadgetEvent event)
   {
      String owner = "root";
      String viewer = "root";
      Long moduleId = 0L;
      String container = "default";
      String domain = null;

      String href = activeFile.getLinkByRelation(Link.REL_CONTENT).getHref();
      href = href.replace(applicationConfiguration.getContext(), applicationConfiguration.getPublicContext());

      TokenRequest tokenRequest = new TokenRequest(href, owner, viewer, moduleId, container, domain);
      GadgetService.getInstance().getSecurityToken(tokenRequest, new AsyncRequestCallback<TokenResponse>()
      {

         @Override
         protected void onSuccess(TokenResponse result)
         {
            getGadgetMetadata(result);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exception));
         }
      });
   }

   /**
    * @param tokenResponse
    */
   private void getGadgetMetadata(TokenResponse tokenResponse)
   {
      GadgetService.getInstance().getGadgetMetadata(tokenResponse, new AsyncRequestCallback<GadgetMetadata>()
      {

         @Override
         protected void onSuccess(GadgetMetadata result)
         {
            if (gadgetPreviewPane == null)
            {
               gadgetPreviewPane = new GadgetPreviewPane();
               gadgetPreviewPane.setIcon(new Image(GadgetClientBundle.INSTANCE.preview()));
               IDE.getInstance().openView(gadgetPreviewPane);
            }
            else
            {
               if (!gadgetPreviewPane.isViewVisible())
               {
                  gadgetPreviewPane.setViewVisible();
               }
            }

            gadgetPreviewPane.setConfiguration(applicationConfiguration);
            gadgetPreviewPane.setMetadata(result);
            gadgetPreviewPane.showGadget();

            previewOpened = true;
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exception));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent)
    */
   @Override
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      applicationConfiguration = event.getConfiguration();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (gadgetPreviewPane == null)
         return;
      if (event.getView().getId().equals(gadgetPreviewPane.getId()))
      {
         previewOpened = false;
         gadgetPreviewPane = null;
      }
   }

}
