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

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.PreviewForm;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.groovy.client.classpath.ui.ConfigureBuildPathPresenter;
import org.exoplatform.ide.extension.groovy.client.controls.ConfigureClasspathCommand;
import org.exoplatform.ide.extension.groovy.client.controls.DeployGroovyCommand;
import org.exoplatform.ide.extension.groovy.client.controls.DeployGroovySandboxCommand;
import org.exoplatform.ide.extension.groovy.client.controls.PreviewGroovyTemplateControl;
import org.exoplatform.ide.extension.groovy.client.controls.RunGroovyServiceCommand;
import org.exoplatform.ide.extension.groovy.client.controls.SetAutoloadCommand;
import org.exoplatform.ide.extension.groovy.client.controls.UndeployGroovyCommand;
import org.exoplatform.ide.extension.groovy.client.controls.UndeployGroovySandboxCommand;
import org.exoplatform.ide.extension.groovy.client.controls.ValidateGroovyCommand;
import org.exoplatform.ide.extension.groovy.client.event.PreviewGroovyTemplateEvent;
import org.exoplatform.ide.extension.groovy.client.event.PreviewGroovyTemplateHandler;
import org.exoplatform.ide.extension.groovy.client.event.SetAutoloadEvent;
import org.exoplatform.ide.extension.groovy.client.event.SetAutoloadHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.DeployGroovyCommandHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.RunGroovyServiceCommandHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.UndeployGroovyCommandHandler;
import org.exoplatform.ide.extension.groovy.client.handlers.ValidateGroovyCommandHandler;
import org.exoplatform.ide.extension.groovy.client.jar.AvailableDependenciesPresenter;
import org.exoplatform.ide.extension.groovy.client.launch_service.LaunchRestServicePresenter;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyServiceImpl;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.RestServiceOutputReceivedEvent;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.RestServiceOutputReceivedHandler;
import org.exoplatform.ide.extension.groovy.client.service.wadl.WadlServiceImpl;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FileModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class GroovyExtension extends Extension implements RestServiceOutputReceivedHandler, SetAutoloadHandler,
   InitializeServicesHandler, ApplicationSettingsReceivedHandler, EditorActiveFileChangedHandler,
   PreviewGroovyTemplateHandler, ViewClosedHandler
{

   /**
    * Used to remove handlers when they are no longer needed.
    */
   private Map<GwtEvent.Type<?>, HandlerRegistration> handlerRegistrations =
      new HashMap<GwtEvent.Type<?>, HandlerRegistration>();

   private FileModel activeFile;

   private IDEConfiguration configuration;

   //TODO: currently not use lock
   // private Map<String, String> lockTokens;

   //need for http://jira.exoplatform.org/browse/IDE-347
   //undeploy service on cancel 
   //   private boolean undeployOnCancel = false;

   private boolean previewOpened = false;

   private PreviewForm previewForm;

   public static final GroovyLocalizationConstant LOCALIZATION_CONSTANT = GWT.create(GroovyLocalizationConstant.class);

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize()
   {
      handlerRegistrations.put(InitializeServicesEvent.TYPE, IDE.addHandler(InitializeServicesEvent.TYPE, this));

      IDE.getInstance().addControl(new SetAutoloadCommand(), Docking.TOOLBAR_RIGHT);
      IDE.getInstance().addControl(new ConfigureClasspathCommand());
      IDE.getInstance().addControl(new ValidateGroovyCommand(), Docking.TOOLBAR_RIGHT);
      IDE.getInstance().addControl(new DeployGroovyCommand(), Docking.TOOLBAR_RIGHT);
      IDE.getInstance().addControl(new UndeployGroovyCommand(), Docking.TOOLBAR_RIGHT);
      IDE.getInstance().addControl(new RunGroovyServiceCommand(), Docking.TOOLBAR_RIGHT);
      IDE.getInstance().addControl(new DeployGroovySandboxCommand(), Docking.TOOLBAR_RIGHT);
      IDE.getInstance().addControl(new UndeployGroovySandboxCommand(), Docking.TOOLBAR_RIGHT);

      new LaunchRestServicePresenter();

      IDE.getInstance().addControl(new PreviewGroovyTemplateControl(), Docking.TOOLBAR_RIGHT);

      handlerRegistrations.put(InitializeServicesEvent.TYPE, IDE.addHandler(RestServiceOutputReceivedEvent.TYPE, this));
      handlerRegistrations.put(InitializeServicesEvent.TYPE, IDE.addHandler(SetAutoloadEvent.TYPE, this));

      //handlerRegistrations.put(InitializeServicesEvent.TYPE, eventBus.addHandler(PreviewWadlOutputEvent.TYPE, this));

      handlerRegistrations.put(InitializeServicesEvent.TYPE, IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this));
      handlerRegistrations.put(InitializeServicesEvent.TYPE,
         IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this));
      handlerRegistrations.put(InitializeServicesEvent.TYPE, IDE.addHandler(PreviewGroovyTemplateEvent.TYPE, this));
      handlerRegistrations.put(InitializeServicesEvent.TYPE, IDE.addHandler(ViewClosedEvent.TYPE, this));

      new RunGroovyServiceCommandHandler();
      new ValidateGroovyCommandHandler();
      new DeployGroovyCommandHandler();
      new UndeployGroovyCommandHandler();
      new ConfigureBuildPathPresenter();
      new AvailableDependenciesPresenter();

      GroovyClientBundle.INSTANCE.css().ensureInjected();
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      configuration = event.getApplicationConfiguration();
      new GroovyServiceImpl(IDE.eventBus(), event.getApplicationConfiguration().getContext(), event.getLoader());
      new WadlServiceImpl(IDE.eventBus(), event.getLoader());
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
         IDE.fireEvent(outputEvent);
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
            IDE.fireEvent(errorEvent);
         }
         else
         {
            String message =
               "<b>" + event.getOutput().getUrl() + "</b>&nbsp;" + exception.getHTTPStatus() + "&nbsp;"
                  + exception.getStatusText();
            OutputEvent errorEvent = new OutputEvent(message, OutputMessage.Type.ERROR);
            IDE.fireEvent(errorEvent);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void onSetAutoload(SetAutoloadEvent event)
   {
      //TODO
      //      Property jcrContentProperty =
      //         GroovyPropertyUtil.getProperty(activeFile.getProperties(), ItemProperty.JCR_CONTENT);
      //      Property autoloadProperty =
      //         GroovyPropertyUtil.getProperty(jcrContentProperty.getChildProperties(), ItemProperty.EXO_AUTOLOAD);
      //      autoloadProperty.setValue("" + event.isAutoload());
      //
      //      VirtualFileSystem.getInstance().saveProperties(activeFile, lockTokens.get(activeFile.getHref()),
      //         new ItemPropertiesCallback()
      //         {
      //            @Override
      //            protected void onSuccess(Item result)
      //            {
      //               eventBus.fireEvent(new ItemPropertiesSavedEvent(result));
      //            }
      //         });
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
      if (previewOpened)
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
      /* TODO currently not use lock
       if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
        {
           event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
        }

        lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");*/
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.client.event.PreviewGroovyTemplateHandler#onPreviewGroovyTemplate(org.exoplatform.ide.extension.groovy.client.event.PreviewGroovyTemplateEvent)
    */
   @Override
   public void onPreviewGroovyTemplate(PreviewGroovyTemplateEvent event)
   {

      if (previewForm == null)
      {
         previewForm = new PreviewForm();
         previewForm.setIcon(new Image(GroovyClientBundle.INSTANCE.preview()));
      }

      previewForm.showPreview(configuration.getContext() + "/ide/gtmpl/render?id=" + activeFile.getId() + "&vfsid="
         + VirtualFileSystem.getInstance().getInfo().getId());

      if (previewOpened)
      {
         previewForm.setViewVisible();
      }
      else
      {
         IDE.getInstance().openView(previewForm);
      }
      previewOpened = true;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (previewForm == null)
         return;

      if (event.getView().getId().equals(previewForm.getId()))
      {
         previewOpened = false;
         previewForm = null;
      }
   }

}
