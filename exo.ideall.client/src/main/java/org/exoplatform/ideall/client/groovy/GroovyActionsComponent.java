/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.groovy;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.ideall.client.application.component.AbstractApplicationComponent;
import org.exoplatform.ideall.client.groovy.event.DeployGroovyScriptEvent;
import org.exoplatform.ideall.client.groovy.event.DeployGroovyScriptHandler;
import org.exoplatform.ideall.client.groovy.event.PreviewGroovyOutputEvent;
import org.exoplatform.ideall.client.groovy.event.PreviewGroovyOutputHandler;
import org.exoplatform.ideall.client.groovy.event.PreviewWadlOutputEvent;
import org.exoplatform.ideall.client.groovy.event.PreviewWadlOutputHandler;
import org.exoplatform.ideall.client.groovy.event.SetAutoloadEvent;
import org.exoplatform.ideall.client.groovy.event.SetAutoloadHandler;
import org.exoplatform.ideall.client.groovy.event.UndeployGroovyScriptEvent;
import org.exoplatform.ideall.client.groovy.event.UndeployGroovyScriptHandler;
import org.exoplatform.ideall.client.groovy.event.ValidateGroosyScriptHandler;
import org.exoplatform.ideall.client.groovy.event.ValidateGroovyScriptEvent;
import org.exoplatform.ideall.client.model.groovy.GroovyService;
import org.exoplatform.ideall.client.model.groovy.event.GroovyDeployResultReceivedEvent;
import org.exoplatform.ideall.client.model.groovy.event.GroovyDeployResultReceivedHandler;
import org.exoplatform.ideall.client.model.groovy.event.GroovyUndeployResultReceivedEvent;
import org.exoplatform.ideall.client.model.groovy.event.GroovyUndeployResultReceivedHandler;
import org.exoplatform.ideall.client.model.groovy.event.GroovyValidateResultReceivedEvent;
import org.exoplatform.ideall.client.model.groovy.event.GroovyValidateResultReceivedHandler;
import org.exoplatform.ideall.client.model.groovy.event.RestServiceOutputReceivedEvent;
import org.exoplatform.ideall.client.model.groovy.event.RestServiceOutputReceivedHandler;
import org.exoplatform.ideall.client.model.wadl.WadlService;
import org.exoplatform.ideall.client.model.wadl.event.WadlServiceOutputReceiveHandler;
import org.exoplatform.ideall.client.model.wadl.event.WadlServiceOutputReceivedEvent;
import org.exoplatform.ideall.client.operation.output.OutputEvent;
import org.exoplatform.ideall.client.operation.output.OutputMessage;
import org.exoplatform.ideall.vfs.api.File;
import org.exoplatform.ideall.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.vfs.property.ItemProperty;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GroovyActionsComponent extends AbstractApplicationComponent implements ValidateGroosyScriptHandler,
   DeployGroovyScriptHandler, UndeployGroovyScriptHandler, PreviewGroovyOutputHandler,
   GroovyValidateResultReceivedHandler, GroovyDeployResultReceivedHandler, GroovyUndeployResultReceivedHandler,
   RestServiceOutputReceivedHandler, SetAutoloadHandler, PreviewWadlOutputHandler, WadlServiceOutputReceiveHandler
{

   public GroovyActionsComponent()
   {
      super(new GroovyActionsComponentInitializer());
   }

   @Override
   protected void registerHandlers()
   {
      handlers.addHandler(ValidateGroovyScriptEvent.TYPE, this);
      handlers.addHandler(GroovyValidateResultReceivedEvent.TYPE, this);

      handlers.addHandler(DeployGroovyScriptEvent.TYPE, this);
      handlers.addHandler(GroovyDeployResultReceivedEvent.TYPE, this);

      handlers.addHandler(UndeployGroovyScriptEvent.TYPE, this);
      handlers.addHandler(GroovyUndeployResultReceivedEvent.TYPE, this);

      handlers.addHandler(PreviewGroovyOutputEvent.TYPE, this);
      handlers.addHandler(RestServiceOutputReceivedEvent.TYPE, this);

      handlers.addHandler(SetAutoloadEvent.TYPE, this);

      handlers.addHandler(PreviewWadlOutputEvent.TYPE, this);
      handlers.addHandler(WadlServiceOutputReceivedEvent.TYPE, this);

   }

   public void onValidateGroovyScript(ValidateGroovyScriptEvent event)
   {
      GroovyService.getInstance().validate(context.getActiveFile().getName(), context.getActiveFile().getContent());
   }

   public void onDeployGroovyScript(DeployGroovyScriptEvent event)
   {
      //TODO
      GroovyService.getInstance().deploy(context.getActiveFile().getHref());
   }

   public void onUndeployGroovyScript(UndeployGroovyScriptEvent event)
   {
//TODO
      GroovyService.getInstance().undeploy(context.getActiveFile().getHref());
   }

   public void onPreviewGroovyOutput(PreviewGroovyOutputEvent event)
   {
      new RestServiceOutputPreviewForm(eventBus, context);
   }

   public void onGroovyValidateResultReceived(GroovyValidateResultReceivedEvent event)
   {
      if (event.getException() == null)
      {
         /*
          * Validation successfully
          */
         String outputContent = "<b>" + event.getFileName() + "</b> validated successfully.";
         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
      }
      else
      {
         /*
          * Validation failed
          */
         ServerException exception = (ServerException)event.getException();

         String outputContent = "<b>" + event.getFileName() + "</b> validation failed.&nbsp;";
         outputContent += "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
         if (!exception.getMessage().equals(""))
         {
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }

         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }
   }

   public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
   {
      if (event.getException() == null)
      {
         /*
          * Deploying successfully
          */
         String outputContent = "<b>" + event.getPath() + "</b> deployed successfully.";
         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
      }
      else
      {
         /*
          * Deploying failed
          */
         ServerException exception = (ServerException)event.getException();

         String outputContent = "<b>" + event.getPath() + "</b> deploy failed.&nbsp;";
         outputContent += "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
         if (!exception.getMessage().equals(""))
         {
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }

         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }

   }

   public void onGroovyUndeployResultReceived(GroovyUndeployResultReceivedEvent event)
   {
      if (event.getException() == null)
      {
         /*
          * Undeploy successfully
          */
         String outputContent = "<b>" + event.getPath() + "</b> undeployed successfully.";
         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
      }
      else
      {
         /*
          * Undeploy failed
          */
         ServerException exception = (ServerException)event.getException();

         String outputContent = "<b>" + event.getPath() + "</b> undeploy failed.&nbsp;";
         outputContent += "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
         if (!exception.getMessage().equals(""))
         {
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }
         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }

   }

   public void onRestServiceOutputReceived(RestServiceOutputReceivedEvent event)
   {
      if (event.getException() == null)
      {
         String response = event.getOutput().getResponseAsHtmlString();
         
         OutputEvent outputEvent = new OutputEvent(response, OutputMessage.Type.OUTPUT);
         eventBus.fireEvent(outputEvent);
      }
      else
      {
         ServerException exception = (ServerException)event.getException();

         if (exception.isErrorMessageProvided())
         {
            String message =
               "<b>" + event.getOutput().getUrl() + "</b>&nbsp;" + exception.getHTTPStatus() + "&nbsp;"
                  + exception.getStatusText() + "<hr>" + exception.getMessage();

            OutputEvent errorEvent = new OutputEvent(message, OutputMessage.Type.ERROR);
            eventBus.fireEvent(errorEvent);
         }
         else
         {
            String message =
               "<b>" + event.getOutput().getUrl() + "</b>&nbsp;" + exception.getHTTPStatus() + "&nbsp;"
                  + exception.getStatusText();
            OutputEvent errorEvent = new OutputEvent(message, OutputMessage.Type.ERROR);
            eventBus.fireEvent(errorEvent);
         }
      }
   }

   public void onSetAutoload(SetAutoloadEvent event)
   {
      File file = context.getActiveFile();
      Property jcrContentProperty = GroovyPropertyUtil.getProperty(file.getProperties(), ItemProperty.JCR_CONTENT);
      Property autoloadProperty =
         GroovyPropertyUtil.getProperty(jcrContentProperty.getChildProperties(), ItemProperty.EXO_AUTOLOAD);
      autoloadProperty.setValue("" + event.isAutoload());

      VirtualFileSystem.getInstance().saveProperties(file);
   }

   public void onPreviewWadlOutput(PreviewWadlOutputEvent event)
   {
      String content = context.getActiveFile().getContent();
      int indStart = content.indexOf("\"");
      int indEnd = content.indexOf("\"", indStart + 1);
      String path = content.substring(indStart + 1, indEnd);
      if (!path.startsWith("/"))
      {
         path = "/" + path;
      }
      String url = "/rest" + path;
      WadlService.getInstance().getWadl(url);
   }

   public void onWadlServiceOutputReceived(WadlServiceOutputReceivedEvent event)
   {
      new GroovyServiceOutputPreviewForm(eventBus, context, event.getApplication());
   }

}
