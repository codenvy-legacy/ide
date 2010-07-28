/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.module.groovy;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.ideall.client.framework.application.ApplicationConfiguration;
import org.exoplatform.ideall.client.framework.application.event.ConfigurationReceivedEvent;
import org.exoplatform.ideall.client.framework.application.event.ConfigurationReceivedHandler;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.framework.output.event.OutputEvent;
import org.exoplatform.ideall.client.framework.output.event.OutputMessage;
import org.exoplatform.ideall.client.module.groovy.event.DeployGroovyScriptEvent;
import org.exoplatform.ideall.client.module.groovy.event.DeployGroovyScriptHandler;
import org.exoplatform.ideall.client.module.groovy.event.PreviewWadlOutputEvent;
import org.exoplatform.ideall.client.module.groovy.event.PreviewWadlOutputHandler;
import org.exoplatform.ideall.client.module.groovy.event.SetAutoloadEvent;
import org.exoplatform.ideall.client.module.groovy.event.SetAutoloadHandler;
import org.exoplatform.ideall.client.module.groovy.event.UndeployGroovyScriptEvent;
import org.exoplatform.ideall.client.module.groovy.event.UndeployGroovyScriptHandler;
import org.exoplatform.ideall.client.module.groovy.event.ValidateGroovyScriptEvent;
import org.exoplatform.ideall.client.module.groovy.event.ValidateGroovyScriptHandler;
import org.exoplatform.ideall.client.module.groovy.service.groovy.GroovyService;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.GroovyDeployResultReceivedEvent;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.GroovyDeployResultReceivedHandler;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.GroovyUndeployResultReceivedEvent;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.GroovyUndeployResultReceivedHandler;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.GroovyValidateResultReceivedEvent;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.GroovyValidateResultReceivedHandler;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.RestServiceOutputReceivedEvent;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.RestServiceOutputReceivedHandler;
import org.exoplatform.ideall.client.module.groovy.service.wadl.WadlService;
import org.exoplatform.ideall.client.module.groovy.service.wadl.event.WadlServiceOutputReceiveHandler;
import org.exoplatform.ideall.client.module.groovy.service.wadl.event.WadlServiceOutputReceivedEvent;
import org.exoplatform.ideall.client.module.groovy.ui.GroovyServiceOutputPreviewForm;
import org.exoplatform.ideall.client.module.groovy.util.GroovyPropertyUtil;
import org.exoplatform.ideall.client.module.vfs.api.File;
import org.exoplatform.ideall.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.module.vfs.property.ItemProperty;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class GroovyPluginEventHandler implements ValidateGroovyScriptHandler, DeployGroovyScriptHandler,
   UndeployGroovyScriptHandler, GroovyValidateResultReceivedHandler, GroovyDeployResultReceivedHandler,
   GroovyUndeployResultReceivedHandler, RestServiceOutputReceivedHandler, SetAutoloadHandler, PreviewWadlOutputHandler,
   WadlServiceOutputReceiveHandler, EditorActiveFileChangedHandler, ConfigurationReceivedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;
   
   private File activeFile;
   
   private ApplicationConfiguration configuration;

   public GroovyPluginEventHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      handlers = new Handlers(eventBus);
      handlers.addHandler(ConfigurationReceivedEvent.TYPE, this);

      handlers.addHandler(ValidateGroovyScriptEvent.TYPE, this);
      handlers.addHandler(GroovyValidateResultReceivedEvent.TYPE, this);

      handlers.addHandler(DeployGroovyScriptEvent.TYPE, this);
      handlers.addHandler(GroovyDeployResultReceivedEvent.TYPE, this);

      handlers.addHandler(UndeployGroovyScriptEvent.TYPE, this);
      handlers.addHandler(GroovyUndeployResultReceivedEvent.TYPE, this);

      handlers.addHandler(RestServiceOutputReceivedEvent.TYPE, this);

      handlers.addHandler(SetAutoloadEvent.TYPE, this);

      handlers.addHandler(PreviewWadlOutputEvent.TYPE, this);
      handlers.addHandler(WadlServiceOutputReceivedEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ideall.plugin.groovy.event.ValidateGroovyScriptHandler#onValidateGroovyScript(org.exoplatform.ideall.plugin.groovy.event.ValidateGroovyScriptEvent)
    */
   public void onValidateGroovyScript(ValidateGroovyScriptEvent event)
   {
      System.out.println("GroovyPluginEventHandler.onValidateGroovyScript()");
      
      System.out.println("active file: " + activeFile);
      
      GroovyService.getInstance().validate(activeFile.getName(), activeFile.getContent());
   }

   /**
    * @see org.exoplatform.ideall.plugin.groovy.event.DeployGroovyScriptHandler#onDeployGroovyScript(org.exoplatform.ideall.plugin.groovy.event.DeployGroovyScriptEvent)
    */
   public void onDeployGroovyScript(DeployGroovyScriptEvent event)
   {
      GroovyService.getInstance().deploy(activeFile.getHref());
   }

   /**
    * @see org.exoplatform.ideall.plugin.groovy.event.UndeployGroovyScriptHandler#onUndeployGroovyScript(org.exoplatform.ideall.plugin.groovy.event.UndeployGroovyScriptEvent)
    */
   public void onUndeployGroovyScript(UndeployGroovyScriptEvent event)
   {
      GroovyService.getInstance().undeploy(activeFile.getHref());
   }

   /**
    * @see org.exoplatform.ideall.groovy.event.GroovyValidateResultReceivedHandler#onGroovyValidateResultReceived(org.exoplatform.ideall.groovy.event.GroovyValidateResultReceivedEvent)
    */
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

   /**
    * @see org.exoplatform.ideall.groovy.event.GroovyDeployResultReceivedHandler#onGroovyDeployResultReceived(org.exoplatform.ideall.groovy.event.GroovyDeployResultReceivedEvent)
    */
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

   /**
    * @see org.exoplatform.ideall.groovy.event.GroovyUndeployResultReceivedHandler#onGroovyUndeployResultReceived(org.exoplatform.ideall.groovy.event.GroovyUndeployResultReceivedEvent)
    */
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

   /**
    * @see org.exoplatform.ideall.groovy.event.RestServiceOutputReceivedHandler#onRestServiceOutputReceived(org.exoplatform.ideall.groovy.event.RestServiceOutputReceivedEvent)
    */
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

   /**
    * @see org.exoplatform.ideall.plugin.groovy.event.SetAutoloadHandler#onSetAutoload(org.exoplatform.ideall.plugin.groovy.event.SetAutoloadEvent)
    */
   public void onSetAutoload(SetAutoloadEvent event)
   {
      Property jcrContentProperty = GroovyPropertyUtil.getProperty(activeFile.getProperties(), ItemProperty.JCR_CONTENT);
      Property autoloadProperty =
         GroovyPropertyUtil.getProperty(jcrContentProperty.getChildProperties(), ItemProperty.EXO_AUTOLOAD);
      autoloadProperty.setValue("" + event.isAutoload());

      VirtualFileSystem.getInstance().saveProperties(activeFile);
   }

   /**
    * @see org.exoplatform.ideall.plugin.groovy.event.PreviewWadlOutputHandler#onPreviewWadlOutput(org.exoplatform.ideall.plugin.groovy.event.PreviewWadlOutputEvent)
    */
   public void onPreviewWadlOutput(PreviewWadlOutputEvent event)
   {
      String content = activeFile.getContent();
      int indStart = content.indexOf("\"");
      int indEnd = content.indexOf("\"", indStart + 1);
      String path = content.substring(indStart + 1, indEnd);
      if (!path.startsWith("/"))
      {
         path = "/" + path;
      }
      String url = configuration.getContext() + path;

      WadlService.getInstance().getWadl(url);
   }

   /**
    * @see org.exoplatform.ideall.groovy.model.wadl.event.WadlServiceOutputReceiveHandler#onWadlServiceOutputReceived(org.exoplatform.ideall.groovy.model.wadl.event.WadlServiceOutputReceivedEvent)
    */
   public void onWadlServiceOutputReceived(WadlServiceOutputReceivedEvent event)
   {
      new GroovyServiceOutputPreviewForm(eventBus, event.getApplication());
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      System.out.println("active file changed: " + event.getFile());
      if (event.getFile() != null) {
         System.out.println("href: " + event.getFile().getHref());
      }
      
      activeFile = event.getFile();
   }

   public void onConfigurationReceived(ConfigurationReceivedEvent event)
   {
      configuration = event.getConfiguration();
   }

}
