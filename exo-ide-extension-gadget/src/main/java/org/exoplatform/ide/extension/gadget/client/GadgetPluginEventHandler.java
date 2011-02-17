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

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.ViewType;
import org.exoplatform.ide.client.framework.ui.event.CloseViewEvent;
import org.exoplatform.ide.client.framework.ui.event.OpenViewEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.extension.gadget.client.event.DeployGadgetEvent;
import org.exoplatform.ide.extension.gadget.client.event.DeployGadgetHadndler;
import org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetEvent;
import org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetHandler;
import org.exoplatform.ide.extension.gadget.client.event.UndeployGadgetEvent;
import org.exoplatform.ide.extension.gadget.client.event.UndeployGadgetHandler;
import org.exoplatform.ide.extension.gadget.client.service.DeployUndeployGadgetCallback;
import org.exoplatform.ide.extension.gadget.client.service.GadgetMetadataCallback;
import org.exoplatform.ide.extension.gadget.client.service.GadgetService;
import org.exoplatform.ide.extension.gadget.client.service.SecurityTokenCallback;
import org.exoplatform.ide.extension.gadget.client.service.TokenRequest;
import org.exoplatform.ide.extension.gadget.client.service.TokenResponse;
import org.exoplatform.ide.extension.gadget.client.ui.GadgetPreviewPane;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GadgetPluginEventHandler implements DeployGadgetHadndler, UndeployGadgetHandler,
   EditorActiveFileChangedHandler, PreviewGadgetHandler,
   ConfigurationReceivedSuccessfullyHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private File activeFile;

   private IDEConfiguration  applicationConfiguration;
   
   private boolean previewOpened = false;

   public GadgetPluginEventHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      handlers = new Handlers(eventBus);

      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(DeployGadgetEvent.TYPE, this);
      handlers.addHandler(UndeployGadgetEvent.TYPE, this);
      handlers.addHandler(PreviewGadgetEvent.TYPE, this);
      handlers.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);

   }

   /**
    * @see org.exoplatform.ideall.plugin.gadget.event.DeployGadgetHadndler#onDeployGadget(org.exoplatform.ideall.plugin.gadget.event.DeployGadgetEvent)
    */
   public void onDeployGadget(DeployGadgetEvent event)
   {
      GadgetService.getInstance().deployGadget(activeFile.getHref(), new DeployUndeployGadgetCallback()
      {
         
         @Override
         public void onResponseReceived(Request request, Response response)
         {
            String outputContent = "<b>" + this.getUrl() + "</b> deployed successfully.";
            eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
         }
         
         @Override
         public void handleError(Throwable exc)
         {
            if (exc instanceof ServerException)
            {
               ServerException exception = (ServerException)exc;
               String outputContent = "<b>" + this.getUrl() + "</b> deploy failed.&nbsp;";
               sendExceptionEvent(exception, outputContent);
            }
            else
            {
               eventBus.fireEvent(new ExceptionThrownEvent(exc));
            }
         }
      });
   }

   /**
    * @see org.exoplatform.ideall.plugin.gadget.event.UndeployGadgetHandler#onUndeployGadget(org.exoplatform.ideall.plugin.gadget.event.UndeployGadgetEvent)
    */
   public void onUndeployGadget(UndeployGadgetEvent event)
   {
      GadgetService.getInstance().undeployGadget(activeFile.getHref(), new DeployUndeployGadgetCallback()
      {
         
         @Override
         public void onResponseReceived(Request request, Response response)
         {
            String outputContent = "<b>" + this.getUrl() + "</b> undeployed successfully.";
            eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));            
         }
         
         @Override
         public void handleError(Throwable exc)
         {
            if (exc instanceof ServerException)
            {
               ServerException exception = (ServerException)exc;
               String outputContent = "<b>" + this.getUrl() + "</b> undeploy failed.&nbsp;";
               sendExceptionEvent(exception, outputContent);
            }
            else
            {
               eventBus.fireEvent(new ExceptionThrownEvent(exc));
            }
            
         }
      });
   }

   /**
    * @param exception
    * @param message
    */
   private void sendExceptionEvent(ServerException exception, String message)
   {
      message += "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
      if (!exception.getMessage().equals(""))
      {
         message += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
      }
      eventBus.fireEvent(new OutputEvent(message, OutputMessage.Type.ERROR));
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();
   }

   /**
    * @see org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetHandler#onPreviewGadget(org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetEvent)
    */
   @Override
   public void onPreviewGadget(PreviewGadgetEvent event)
   {
      if(previewOpened)
      {
         eventBus.fireEvent(new CloseViewEvent(GadgetPreviewPane.ID));
         previewOpened = false;
      }
      String owner = "root";
      String viewer = "root";
      Long moduleId = 0L;
      String container = "default";
      String domain = null;

      String href = activeFile.getHref();
      href = href.replace(applicationConfiguration.getContext(), applicationConfiguration.getPublicContext());

      TokenRequest tokenRequest = new TokenRequest(URL.encode(href), owner, viewer, moduleId, container, domain);
      GadgetService.getInstance().getSecurityToken(tokenRequest, new SecurityTokenCallback()
      {
         
         @Override
         public void onResponseReceived(Request request, Response response)
         {
            TokenResponse tokenResponse = this.getTokenResponse();
            getGadgetMetadata(tokenResponse);
         }
         
         @Override
         public void handleError(Throwable exc)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exc));
         }
      });
   }
   
   /**
    * @param tokenResponse
    */
   private void getGadgetMetadata(TokenResponse tokenResponse)
   {
      GadgetService.getInstance().getGadgetMetadata(tokenResponse, new GadgetMetadataCallback()
      {
         
         public void onResponseReceived(Request request, Response response)
         {
            GadgetPreviewPane gadgetPreviewPane = new GadgetPreviewPane(eventBus, applicationConfiguration, this.getMetadata());
            gadgetPreviewPane.setType(ViewType.PREVIEW);
            gadgetPreviewPane.setImage(new Image(GadgetClientBundle.INSTANCE.preview()));
            eventBus.fireEvent(new OpenViewEvent(gadgetPreviewPane));
            previewOpened = true;
         }
         
         public void handleError(Throwable exc)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exc));
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


}
