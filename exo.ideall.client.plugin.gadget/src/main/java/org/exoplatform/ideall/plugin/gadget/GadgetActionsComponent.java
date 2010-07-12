/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ideall.plugin.gadget.ui;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.ideall.client.framework.output.event.OutputEvent;
import org.exoplatform.ideall.client.framework.output.event.OutputMessage;
import org.exoplatform.ideall.plugin.gadget.event.DeployGadgetEvent;
import org.exoplatform.ideall.plugin.gadget.event.DeployGadgetHadndler;
import org.exoplatform.ideall.plugin.gadget.event.UndeployGadgetEvent;
import org.exoplatform.ideall.plugin.gadget.event.UndeployGadgetHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GadgetActionsComponent extends AbstractApplicationComponent implements DeployGadgetHadndler,
   UndeployGadgetHandler, GadgetDeployResultHandler, GadgetUndeployResultHandler
{

   public GadgetActionsComponent()
   {
      super(new GadgetActionsComponentInitializer());
   }

   @Override
   protected void registerHandlers()
   {

   }

   /**
    * {@inheritDoc}
    */
   public void onDeployGadget(DeployGadgetEvent event)
   {
     
   }

   /**
    * {@inheritDoc}
    */
   public void onUndeployGadget(UndeployGadgetEvent event)
   {
      GadgetService.getInstance().undeployGadget(context.getActiveFile().getHref());
   }

   /**
    * {@inheritDoc}
    */
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
    * {@inheritDoc}
    */
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
