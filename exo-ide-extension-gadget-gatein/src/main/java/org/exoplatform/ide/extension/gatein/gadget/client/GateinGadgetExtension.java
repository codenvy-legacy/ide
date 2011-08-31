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
package org.exoplatform.ide.extension.gatein.gadget.client;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.extension.gatein.gadget.client.controls.DeployGadgetCommand;
import org.exoplatform.ide.extension.gatein.gadget.client.controls.UndeployGadgetCommand;
import org.exoplatform.ide.extension.gatein.gadget.client.event.DeployGadgetEvent;
import org.exoplatform.ide.extension.gatein.gadget.client.event.DeployGadgetHadndler;
import org.exoplatform.ide.extension.gatein.gadget.client.event.UndeployGadgetEvent;
import org.exoplatform.ide.extension.gatein.gadget.client.event.UndeployGadgetHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GateinGadgetExtension extends Extension implements InitializeServicesHandler,DeployGadgetHadndler, UndeployGadgetHandler,
EditorActiveFileChangedHandler
{

   private HandlerManager eventBus;
   
   private FileModel activeFile;

   private GateinGadgetService gateinGadgetService;

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize()
   {
      this.eventBus = IDE.EVENT_BUS;
      IDE.getInstance().addControl(new DeployGadgetCommand(), DockTarget.TOOLBAR, true);
      IDE.getInstance().addControl(new UndeployGadgetCommand(), DockTarget.TOOLBAR, true);
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      gateinGadgetService = new GateinGadgetService(eventBus, event.getLoader(), event.getApplicationConfiguration().getContext(), event
         .getApplicationConfiguration().getGadgetServer(), event.getApplicationConfiguration().getPublicContext());
   }
   
   /**
    * @see org.exoplatform.ideall.plugin.gadget.event.DeployGadgetHadndler#onDeployGadget(org.exoplatform.ideall.plugin.gadget.event.DeployGadgetEvent)
    */
   @Override
   public void onDeployGadget(DeployGadgetEvent event)
   {
      gateinGadgetService.deployGadget(activeFile.getId(), new AsyncRequestCallback<String>()
      {
         
         @Override
         protected void onSuccess(String result)
         {
            String outputContent = "<b>" + result + "</b> deployed successfully.";
            eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));      
         }
         
         @Override
         protected void onFailure(Throwable exc)
         {
            if (exc instanceof ServerException)
            {
               ServerException exception = (ServerException)exc;
               String outputContent = "<b>" + this.getResult() + "</b> deploy failed.&nbsp;";
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
   @Override
   public void onUndeployGadget(UndeployGadgetEvent event)
   {
      gateinGadgetService.undeployGadget(activeFile.getId(), new AsyncRequestCallback<String>()
      {
         
         @Override
         protected void onSuccess(String result)
         {
            String outputContent = "<b>" + result + "</b> undeployed successfully.";
            eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
         }
         
         @Override
         protected void onFailure(Throwable exc)
         {
            if (exc instanceof ServerException)
            {
               ServerException exception = (ServerException)exc;
               String outputContent = "<b>" + this.getResult() + "</b> undeploy failed.&nbsp;";
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

   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();
   }

}
