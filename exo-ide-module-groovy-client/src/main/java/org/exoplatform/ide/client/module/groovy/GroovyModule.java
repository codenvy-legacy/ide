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
package org.exoplatform.ide.client.module.groovy;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.ide.client.framework.application.ApplicationConfiguration;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDEModule;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.module.groovy.controls.DeployGroovyCommand;
import org.exoplatform.ide.client.module.groovy.controls.PreviewWadlOutputCommand;
import org.exoplatform.ide.client.module.groovy.controls.RunGroovyServiceCommand;
import org.exoplatform.ide.client.module.groovy.controls.SetAutoloadCommand;
import org.exoplatform.ide.client.module.groovy.controls.UndeployGroovyCommand;
import org.exoplatform.ide.client.module.groovy.controls.ValidateGroovyCommand;
import org.exoplatform.ide.client.module.groovy.event.DeployGroovyScriptEvent;
import org.exoplatform.ide.client.module.groovy.event.DeployGroovyScriptHandler;
import org.exoplatform.ide.client.module.groovy.event.DeployGroovyScriptSandboxEvent;
import org.exoplatform.ide.client.module.groovy.event.DeployGroovyScriptSandboxHandler;
import org.exoplatform.ide.client.module.groovy.event.PreviewWadlOutputEvent;
import org.exoplatform.ide.client.module.groovy.event.PreviewWadlOutputHandler;
import org.exoplatform.ide.client.module.groovy.event.SetAutoloadEvent;
import org.exoplatform.ide.client.module.groovy.event.SetAutoloadHandler;
import org.exoplatform.ide.client.module.groovy.event.UndeployGroovyScriptEvent;
import org.exoplatform.ide.client.module.groovy.event.UndeployGroovyScriptHandler;
import org.exoplatform.ide.client.module.groovy.event.UndeployGroovyScriptSandboxEvent;
import org.exoplatform.ide.client.module.groovy.event.UndeployGroovyScriptSandboxHandler;
import org.exoplatform.ide.client.module.groovy.event.ValidateGroovyScriptEvent;
import org.exoplatform.ide.client.module.groovy.event.ValidateGroovyScriptHandler;
import org.exoplatform.ide.client.module.groovy.handlers.RunGroovyServiceCommandHandler;
import org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService;
import org.exoplatform.ide.client.module.groovy.service.groovy.GroovyServiceImpl;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyDeployResultReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyDeployResultReceivedHandler;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyUndeployResultReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyUndeployResultReceivedHandler;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyValidateResultReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyValidateResultReceivedHandler;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.RestServiceOutputReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.RestServiceOutputReceivedHandler;
import org.exoplatform.ide.client.module.groovy.service.wadl.WadlService;
import org.exoplatform.ide.client.module.groovy.service.wadl.WadlServiceImpl;
import org.exoplatform.ide.client.module.groovy.service.wadl.event.WadlServiceOutputReceiveHandler;
import org.exoplatform.ide.client.module.groovy.service.wadl.event.WadlServiceOutputReceivedEvent;
import org.exoplatform.ide.client.module.groovy.ui.GroovyServiceOutputPreviewForm;
import org.exoplatform.ide.client.module.groovy.util.GroovyPropertyUtil;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class GroovyModule implements IDEModule, ValidateGroovyScriptHandler, DeployGroovyScriptHandler,
   UndeployGroovyScriptHandler, GroovyValidateResultReceivedHandler, GroovyDeployResultReceivedHandler,
   GroovyUndeployResultReceivedHandler, RestServiceOutputReceivedHandler, SetAutoloadHandler, PreviewWadlOutputHandler,
   WadlServiceOutputReceiveHandler, EditorActiveFileChangedHandler, InitializeServicesHandler, ExceptionThrownHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, DeployGroovyScriptSandboxHandler, UndeployGroovyScriptSandboxHandler
   
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private File activeFile;

   private ApplicationConfiguration configuration;

   private Map<String, File> openedFiles = new HashMap<String, File>();

   /**
    * Number of line, which extracts from error message and
    * paths as parameter to javascript method.
    */
   private int errLineNumber;

   /**
    * Number of column, which extracts from error message and
    * paths as parameter to javascript method.
    */
   private int errColumnNumber;

   /**
    * Number of line, where to after,
    * after user click on error message.
    */
   private int lineNumberToGo;

   /**
    * Number of column, where to after,
    * after user click on error message.
    */
   private int columnNumberToGo;

   /**
    * Is need to go to position in active file.
    */
   private boolean isGoToPosition;

   /**
    * Href of file which contains an exception and in which need to go to position.
    */
   private String errFileHref = "";

   public GroovyModule(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(InitializeServicesEvent.TYPE, this);

      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New REST Service", "REST Service",
         "Create REST Service", Images.FileType.REST_SERVICE, MimeType.GROOVY_SERVICE)));

      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New Groovy Script", "Groovy Script",
         "Create Groovy Script", Images.FileType.GROOVY, MimeType.APPLICATION_GROOVY)));

      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New Groovy Template", "Groovy Template",
         "Create Groovy Template", Images.FileType.GROOVY_TEMPLATE, MimeType.GROOVY_TEMPLATE)));

      

      eventBus.fireEvent(new RegisterControlEvent(new SetAutoloadCommand(), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new ValidateGroovyCommand(), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new DeployGroovyCommand(), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new UndeployGroovyCommand(), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new RunGroovyServiceCommand(), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new PreviewWadlOutputCommand(), true, true));

      handlers.addHandler(ValidateGroovyScriptEvent.TYPE, this);
      handlers.addHandler(GroovyValidateResultReceivedEvent.TYPE, this);

      handlers.addHandler(DeployGroovyScriptEvent.TYPE, this);
      handlers.addHandler(DeployGroovyScriptSandboxEvent.TYPE, this);
      handlers.addHandler(GroovyDeployResultReceivedEvent.TYPE, this);

      handlers.addHandler(UndeployGroovyScriptEvent.TYPE, this);
      handlers.addHandler(UndeployGroovyScriptSandboxEvent.TYPE, this);
      handlers.addHandler(GroovyUndeployResultReceivedEvent.TYPE, this);

      handlers.addHandler(RestServiceOutputReceivedEvent.TYPE, this);

      handlers.addHandler(SetAutoloadEvent.TYPE, this);

      handlers.addHandler(PreviewWadlOutputEvent.TYPE, this);
      handlers.addHandler(WadlServiceOutputReceivedEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      handlers.addHandler(EditorFileOpenedEvent.TYPE, this);
      handlers.addHandler(EditorFileClosedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);

      new RunGroovyServiceCommandHandler(eventBus);
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      configuration = event.getApplicationConfiguration();
      new GroovyServiceImpl(eventBus, event.getApplicationConfiguration().getContext(), event.getLoader());
      new WadlServiceImpl(eventBus, event.getLoader());
   }

   /**
    * @see org.exoplatform.ide.plugin.groovy.event.ValidateGroovyScriptHandler#onValidateGroovyScript(org.exoplatform.ide.plugin.groovy.event.ValidateGroovyScriptEvent)
    */
   public void onValidateGroovyScript(ValidateGroovyScriptEvent event)
   {
      GroovyService.getInstance().validate(activeFile.getName(), activeFile.getHref(), activeFile.getContent());
   }

   /**
    * @see org.exoplatform.ide.plugin.groovy.event.DeployGroovyScriptHandler#onDeployGroovyScript(org.exoplatform.ide.plugin.groovy.event.DeployGroovyScriptEvent)
    */
   public void onDeployGroovyScript(DeployGroovyScriptEvent event)
   {
      GroovyService.getInstance().deploy(activeFile.getHref());
   }

   /**
    * @see org.exoplatform.ide.plugin.groovy.event.UndeployGroovyScriptHandler#onUndeployGroovyScript(org.exoplatform.ide.plugin.groovy.event.UndeployGroovyScriptEvent)
    */
   public void onUndeployGroovyScript(UndeployGroovyScriptEvent event)
   {
     GroovyService.getInstance().undeploy(activeFile.getHref());
   }

   private native void initGoToErrorFunction() /*-{
                                               var instance = this;       
                                               var goToErrorFunction = function(lineNumber, columnNumber, fileHref, contentType) {
                                               instance.@org.exoplatform.ide.client.module.groovy.GroovyModule::goToError(Ljava/lang/String;II)(
                                               fileHref, lineNumber, columnNumber);
                                               };
                                               
                                               $wnd.groovyGoToErrorFunction = goToErrorFunction;
                                               }-*/;

   public void goToError(String fileHref, int lineNumber, int columnNumber)
   {
      if (activeFile != null && fileHref.equals(activeFile.getHref()))
      {
         eventBus.fireEvent(new EditorGoToLineEvent(lineNumber, columnNumber));
         return;
      }

      lineNumberToGo = lineNumber;
      columnNumberToGo = columnNumber;

      //TODO:
      //When FileOpenedEvent will be use,
      //remove this additional variable, 
      //and listen to that event
      if (openedFiles != null && openedFiles.containsKey(fileHref))
      {
         isGoToPosition = true;
         eventBus.fireEvent(new OpenFileEvent(openedFiles.get(fileHref)));
      }
      else
      {
         errFileHref = fileHref;
         eventBus.fireEvent(new OpenFileEvent(fileHref));
      }
   }

   /**
    * Parse text and find number of column and line number of error
    * 
    * @param text validation text, which contains number of column and line number of error
    */
   private void findLineNumberAndColNumberOfError(String text)
   {
      try
      {
         //find line number
         int firstIndex = text.indexOf("@ line") + 7;
         int lastIndex = text.indexOf(", column");
         errLineNumber = Integer.valueOf(text.substring(firstIndex, lastIndex));

         //find column number
         firstIndex = lastIndex + 9;
         lastIndex = text.indexOf(".", firstIndex);
         errColumnNumber = Integer.valueOf(text.substring(firstIndex, lastIndex));

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.groovy.event.GroovyValidateResultReceivedHandler#onGroovyValidateResultReceived(org.exoplatform.ide.groovy.event.GroovyValidateResultReceivedEvent)
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
         initGoToErrorFunction();
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

         findLineNumberAndColNumberOfError(exception.getMessage());

         outputContent =
            "<span title=\"Go to error\" onClick=\"window.groovyGoToErrorFunction(" + String.valueOf(errLineNumber)
               + "," + String.valueOf(errColumnNumber) + ", '" + event.getFileHref() + "', '"
               + "');\" style=\"cursor:pointer;\">" + outputContent + "</span>";

         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }
   }

   /**
    * @see org.exoplatform.ide.groovy.event.GroovyDeployResultReceivedHandler#onGroovyDeployResultReceived(org.exoplatform.ide.groovy.event.GroovyDeployResultReceivedEvent)
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
    * @see org.exoplatform.ide.groovy.event.GroovyUndeployResultReceivedHandler#onGroovyUndeployResultReceived(org.exoplatform.ide.groovy.event.GroovyUndeployResultReceivedEvent)
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
    * @see org.exoplatform.ide.groovy.event.RestServiceOutputReceivedHandler#onRestServiceOutputReceived(org.exoplatform.ide.groovy.event.RestServiceOutputReceivedEvent)
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
    * {@inheritDoc}
    */
   public void onSetAutoload(SetAutoloadEvent event)
   {
      Property jcrContentProperty =
         GroovyPropertyUtil.getProperty(activeFile.getProperties(), ItemProperty.JCR_CONTENT);
      Property autoloadProperty =
         GroovyPropertyUtil.getProperty(jcrContentProperty.getChildProperties(), ItemProperty.EXO_AUTOLOAD);
      autoloadProperty.setValue("" + event.isAutoload());

      VirtualFileSystem.getInstance().saveProperties(activeFile);
   }

 
   /**
    * {@inheritDoc}
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
    * {@inheritDoc}
    */
   public void onWadlServiceOutputReceived(WadlServiceOutputReceivedEvent event)
   {
      new GroovyServiceOutputPreviewForm(eventBus, event.getApplication());
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
      if (isGoToPosition)
      {
         isGoToPosition = false;
         new Timer()
         {
            @Override
            public void run()
            {
               eventBus.fireEvent(new EditorGoToLineEvent(lineNumberToGo, columnNumberToGo));
            }

         }.schedule(200);
      }
   }

   
   /**
    * {@inheritDoc}
    */
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();

      if (errFileHref.equals(event.getFile().getHref()))
      {
         errFileHref = "";
         eventBus.fireEvent(new EditorGoToLineEvent(lineNumberToGo, columnNumberToGo));
      }
   }

  
   /**
    * {@inheritDoc}
    */
   public void onError(ExceptionThrownEvent event)
   {
      errFileHref = "";
   }

   
   /**
    * {@inheritDoc}
    */
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * {@inheritDoc}
    */
   public void onDeployGroovyScriptSandbox(DeployGroovyScriptSandboxEvent event)
   {
      GroovyService.getInstance().deploySandbox(activeFile.getHref());
   }

   /**
    * {@inheritDoc}
    */
   public void onUndeployGroovyScriptSandbox(UndeployGroovyScriptSandboxEvent event)
   {
      GroovyService.getInstance().undeploySandbox(activeFile.getHref());
   }

}
