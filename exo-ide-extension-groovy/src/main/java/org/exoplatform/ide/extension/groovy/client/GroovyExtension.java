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
package org.exoplatform.ide.extension.groovy.client;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.PreviewForm;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesSavedEvent;
import org.exoplatform.ide.editor.codeassistant.java.service.CodeAssistantService;
import org.exoplatform.ide.extension.groovy.client.classpath.ui.ConfigureBuildPathPresenter;
import org.exoplatform.ide.extension.groovy.client.controls.ConfigureBuildPathCommand;
import org.exoplatform.ide.extension.groovy.client.controls.DeployGroovyCommand;
import org.exoplatform.ide.extension.groovy.client.controls.DeployGroovySandboxCommand;
import org.exoplatform.ide.extension.groovy.client.controls.PreviewWadlOutputCommand;
import org.exoplatform.ide.extension.groovy.client.controls.RunGroovyServiceCommand;
import org.exoplatform.ide.extension.groovy.client.controls.SetAutoloadCommand;
import org.exoplatform.ide.extension.groovy.client.controls.ShowGroovyTemplatePreviewControl;
import org.exoplatform.ide.extension.groovy.client.controls.UndeployGroovyCommand;
import org.exoplatform.ide.extension.groovy.client.controls.UndeployGroovySandboxCommand;
import org.exoplatform.ide.extension.groovy.client.controls.ValidateGroovyCommand;
import org.exoplatform.ide.extension.groovy.client.event.PreviewWadlOutputEvent;
import org.exoplatform.ide.extension.groovy.client.event.PreviewWadlOutputHandler;
import org.exoplatform.ide.extension.groovy.client.event.SetAutoloadEvent;
import org.exoplatform.ide.extension.groovy.client.event.SetAutoloadHandler;
import org.exoplatform.ide.extension.groovy.client.event.ShowGroovyTemplatePreviewEvent;
import org.exoplatform.ide.extension.groovy.client.event.ShowGroovyTemplatePreviewHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.DeployGroovyCommandHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.RunGroovyServiceCommandHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.UndeployGroovyCommandHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.ValidateGroovyCommandHandler;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyServiceImpl;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.RestServiceOutputReceivedEvent;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.RestServiceOutputReceivedHandler;
import org.exoplatform.ide.extension.groovy.client.service.wadl.WadlService;
import org.exoplatform.ide.extension.groovy.client.service.wadl.WadlServiceImpl;
import org.exoplatform.ide.extension.groovy.client.ui.GroovyServiceOutputPreviewForm;
import org.exoplatform.ide.extension.groovy.client.util.GroovyPropertyUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class GroovyExtension extends Extension implements RestServiceOutputReceivedHandler, SetAutoloadHandler,
   PreviewWadlOutputHandler, InitializeServicesHandler, ApplicationSettingsReceivedHandler,
   EditorActiveFileChangedHandler, ShowGroovyTemplatePreviewHandler
{

   private HandlerManager eventBus;

   /**
    * Used to remove handlers when they are no longer needed.
    */
   private Map<GwtEvent.Type<?>, HandlerRegistration> handlerRegistrations =
      new HashMap<GwtEvent.Type<?>, HandlerRegistration>();

   private File activeFile;

   private IDEConfiguration configuration;

   private Map<String, String> lockTokens;

   //need for http://jira.exoplatform.org/browse/IDE-347
   //undeploy service on cancel 
   private boolean undeployOnCancel = false;

   private boolean previewOpened = false;

   private CodeAssistantService codeAssistantService;

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize()
   {
      this.eventBus = IDE.EVENT_BUS;
      handlerRegistrations.put(InitializeServicesEvent.TYPE, eventBus.addHandler(InitializeServicesEvent.TYPE, this));

      IDE.getInstance().addControl(new SetAutoloadCommand(), DockTarget.TOOLBAR, true);
      IDE.getInstance().addControl(new ConfigureBuildPathCommand(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new ValidateGroovyCommand(), DockTarget.TOOLBAR, true);
      IDE.getInstance().addControl(new DeployGroovyCommand(), DockTarget.TOOLBAR, true);
      IDE.getInstance().addControl(new UndeployGroovyCommand(), DockTarget.TOOLBAR, true);
      IDE.getInstance().addControl(new RunGroovyServiceCommand(), DockTarget.TOOLBAR, true);
      IDE.getInstance().addControl(new DeployGroovySandboxCommand(eventBus), DockTarget.TOOLBAR, true);
      IDE.getInstance().addControl(new UndeployGroovySandboxCommand(eventBus), DockTarget.TOOLBAR, true);
      IDE.getInstance().addControl(new PreviewWadlOutputCommand(), DockTarget.TOOLBAR, true);
      IDE.getInstance().addControl(new ShowGroovyTemplatePreviewControl(), DockTarget.TOOLBAR, true);

      handlerRegistrations.put(InitializeServicesEvent.TYPE, eventBus.addHandler(RestServiceOutputReceivedEvent.TYPE, this));
      handlerRegistrations.put(InitializeServicesEvent.TYPE, eventBus.addHandler(SetAutoloadEvent.TYPE, this));
      handlerRegistrations.put(InitializeServicesEvent.TYPE, eventBus.addHandler(PreviewWadlOutputEvent.TYPE, this));
      handlerRegistrations.put(InitializeServicesEvent.TYPE, eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this));
      handlerRegistrations.put(InitializeServicesEvent.TYPE, eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this));
      handlerRegistrations.put(InitializeServicesEvent.TYPE, eventBus.addHandler(ShowGroovyTemplatePreviewEvent.TYPE, this));

      new RunGroovyServiceCommandHandler(eventBus);
      new ValidateGroovyCommandHandler(eventBus);
      new DeployGroovyCommandHandler(eventBus);
      new UndeployGroovyCommandHandler(eventBus);
      new ConfigureBuildPathPresenter(eventBus);

      GroovyClientBundle.INSTANCE.css().ensureInjected();
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      configuration = event.getApplicationConfiguration();
      new GroovyServiceImpl(eventBus, event.getApplicationConfiguration().getContext(), event.getLoader());
      new WadlServiceImpl(eventBus, event.getLoader());
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

      VirtualFileSystem.getInstance().saveProperties(activeFile, lockTokens.get(activeFile.getHref()),
         new ItemPropertiesCallback()
         {
            @Override
            protected void onSuccess(Item result)
            {
               eventBus.fireEvent(new ItemPropertiesSavedEvent(result));
            }
         });
   }

   /**
    * {@inheritDoc}
    */
   public void onPreviewWadlOutput(PreviewWadlOutputEvent event)
   {
      undeployOnCancel = event.isUndeployOnCansel();
      String content = activeFile.getContent();
      int indStart = content.indexOf("\"");
      int indEnd = content.indexOf("\"", indStart + 1);
      String path = content.substring(indStart + 1, indEnd);
      if (!path.startsWith("/"))
      {
         path = "/" + path;
      }

      String url = configuration.getContext() + path;
      WadlService.getInstance().getWadl(url, new AsyncRequestCallback<WadlApplication>()
      {
         
         @Override
         protected void onSuccess(WadlApplication result)
         {
            new GroovyServiceOutputPreviewForm(eventBus, result, undeployOnCancel);
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            ExceptionThrownEvent exc = new ExceptionThrownEvent("Service is not deployed.");
            exc.setException(exception);
            eventBus.fireEvent(exc);
         }
      });
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
      if(previewOpened)
      {
         IDE.getInstance().closeView(PreviewForm.ID);
         previewOpened = false;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
      {
         event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
   }


   /**
    * @see org.exoplatform.ide.extension.groovy.client.event.ShowGroovyTemplatePreviewHandler#onShowGroovyTemplatePreview(org.exoplatform.ide.extension.groovy.client.event.ShowGroovyTemplatePreviewEvent)
    */
   @Override
   public void onShowGroovyTemplatePreview(ShowGroovyTemplatePreviewEvent event)
   {

      PreviewForm form = new PreviewForm();
      form.setIcon(new Image(GroovyClientBundle.INSTANCE.preview()));
      form.showPreview(configuration.getContext() + "/ide/gtmpl/render?url=" + activeFile.getHref());

      IDE.getInstance().openView(form);
      previewOpened = true;
   }

}
