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
package org.exoplatform.ideall.client.gadgets;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.ideall.client.application.component.AbstractApplicationComponent;
import org.exoplatform.ideall.client.gadgets.event.DeployGadgetEvent;
import org.exoplatform.ideall.client.gadgets.event.DeployGadgetHadndler;
import org.exoplatform.ideall.client.gadgets.event.UndeployGadgetEvent;
import org.exoplatform.ideall.client.gadgets.event.UndeployGadgetHandler;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.gadget.GadgetService;
import org.exoplatform.ideall.client.model.gadget.event.GadgetDeployResultEvent;
import org.exoplatform.ideall.client.model.gadget.event.GadgetDeployResultHandler;
import org.exoplatform.ideall.client.model.gadget.event.GadgetUndeployResultEvent;
import org.exoplatform.ideall.client.model.gadget.event.GadgetUndeployResultHandler;
import org.exoplatform.ideall.client.operation.output.OutputEvent;
import org.exoplatform.ideall.client.operation.output.OutputMessage;

import com.google.gwt.user.client.Window.Location;

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

   /**
    * {@inheritDoc}
    */
   public void onDeployGadget(DeployGadgetEvent event)
   {
      System.out.println("gadget url: " + getURL());
      GadgetService.getInstance().deployGadget(getURL());
   }

   /**
    * {@inheritDoc}
    */
   public void onUndeployGadget(UndeployGadgetEvent event)
   {
      GadgetService.getInstance().undeployGadget(getURL());
   }

   @Override
   protected void registerHandlers()
   {
      handlers.addHandler(DeployGadgetEvent.TYPE, this);
      handlers.addHandler(UndeployGadgetEvent.TYPE, this);
      handlers.addHandler(GadgetDeployResultEvent.TYPE, this);
      handlers.addHandler(GadgetUndeployResultEvent.TYPE, this);
   }

   /**
    * @return
    */
   private String getURL()
   {
      //TODO
      String url = 
//         Location.getProtocol() + "//" + Location.getHost() +
//         Configuration.getInstance().getPublicContext() + "/jcr" + 
         context.getActiveFile().getHref();
      return url;
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
