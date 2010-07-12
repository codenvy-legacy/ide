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
package org.exoplatform.ideall.plugin.gadget;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.ideall.client.framework.model.AbstractApplicationContext;
import org.exoplatform.ideall.client.framework.output.event.OutputEvent;
import org.exoplatform.ideall.client.framework.output.event.OutputMessage;
import org.exoplatform.ideall.gadget.GadgetService;
import org.exoplatform.ideall.gadget.event.GadgetDeployResultEvent;
import org.exoplatform.ideall.gadget.event.GadgetDeployResultHandler;
import org.exoplatform.ideall.gadget.event.GadgetUndeployResultEvent;
import org.exoplatform.ideall.gadget.event.GadgetUndeployResultHandler;

import org.exoplatform.ideall.plugin.gadget.event.DeployGadgetEvent;
import org.exoplatform.ideall.plugin.gadget.event.DeployGadgetHadndler;
import org.exoplatform.ideall.plugin.gadget.event.UndeployGadgetEvent;
import org.exoplatform.ideall.plugin.gadget.event.UndeployGadgetHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GadgetPluginEventHandler implements DeployGadgetHadndler, UndeployGadgetHandler,
   GadgetDeployResultHandler, GadgetUndeployResultHandler
{

   private HandlerManager eventBus;

   private AbstractApplicationContext context;

   private Handlers handlers;

   public GadgetPluginEventHandler(HandlerManager eventBus, AbstractApplicationContext context)
   {
      this.context = context;
      this.eventBus = eventBus;

      handlers = new Handlers(eventBus);

      handlers.addHandler(DeployGadgetEvent.TYPE, this);
      handlers.addHandler(UndeployGadgetEvent.TYPE, this);
      handlers.addHandler(GadgetDeployResultEvent.TYPE, this);
      handlers.addHandler(GadgetUndeployResultEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ideall.plugin.gadget.event.DeployGadgetHadndler#onDeployGadget(org.exoplatform.ideall.plugin.gadget.event.DeployGadgetEvent)
    */
   @Override
   public void onDeployGadget(DeployGadgetEvent event)
   {
      GadgetService.getInstance().deployGadget(context.getActiveFile().getHref());
   }

   /**
    * @see org.exoplatform.ideall.plugin.gadget.event.UndeployGadgetHandler#onUndeployGadget(org.exoplatform.ideall.plugin.gadget.event.UndeployGadgetEvent)
    */
   @Override
   public void onUndeployGadget(UndeployGadgetEvent event)
   {
      GadgetService.getInstance().undeployGadget(context.getActiveFile().getHref());
   }

   /**
    * @see org.exoplatform.ideall.gadget.event.GadgetDeployResultHandler#onGadgetDeployResultReceived(org.exoplatform.ideall.gadget.event.GadgetDeployResultEvent)
    */
   @Override
   public void onGadgetDeployResultReceived(GadgetDeployResultEvent event)
   {
      if (event.getException() == null)
      {
         //Deploying successfully
         String outputContent = "<b>" + event.getUrl() + "</b> deployed successfully.";
         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
      }
      else
      {
         //Deploying failed
         ServerException exception = (ServerException)event.getException();
         String outputContent = "<b>" + event.getUrl() + "</b> deploy failed.&nbsp;";
         sendExceptionEvent(exception, outputContent);
      }
   }

   /**
    * @see org.exoplatform.ideall.gadget.event.GadgetUndeployResultHandler#onGadgetUndeployResultReceived(org.exoplatform.ideall.gadget.event.GadgetUndeployResultEvent)
    */
   @Override
   public void onGadgetUndeployResultReceived(GadgetUndeployResultEvent event)
   {
      if (event.getException() == null)
      {
         // Deploying successfully
         String outputContent = "<b>" + event.getUrl() + "</b> undeployed successfully.";
         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
      }
      else
      {
         //Deploying failed
         ServerException exception = (ServerException)event.getException();
         String outputContent = "<b>" + event.getUrl() + "</b> undeploy failed.&nbsp;";
         sendExceptionEvent(exception, outputContent);
      }
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

}
