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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.extension.gadget.client.event.DeployGadgetEvent;
import org.exoplatform.ide.extension.gadget.client.event.DeployGadgetHadndler;
import org.exoplatform.ide.extension.gadget.client.event.UndeployGadgetEvent;
import org.exoplatform.ide.extension.gadget.client.event.UndeployGadgetHandler;
import org.exoplatform.ide.extension.gadget.client.service.DeployUndeployGadgetCallback;
import org.exoplatform.ide.extension.gadget.client.service.GadgetService;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GadgetPluginEventHandler implements DeployGadgetHadndler, UndeployGadgetHandler,
   EditorActiveFileChangedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private File activeFile;

   public GadgetPluginEventHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      handlers = new Handlers(eventBus);

      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(DeployGadgetEvent.TYPE, this);
      handlers.addHandler(UndeployGadgetEvent.TYPE, this);
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

}
