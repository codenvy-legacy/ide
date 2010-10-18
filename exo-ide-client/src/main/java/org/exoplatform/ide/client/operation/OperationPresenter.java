/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.ide.client.operation;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.event.perspective.RestorePerspectiveEvent;
import org.exoplatform.ide.client.framework.application.ApplicationConfiguration;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.model.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.model.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.module.development.event.PreviewFileEvent;
import org.exoplatform.ide.client.module.development.event.PreviewFileHandler;
import org.exoplatform.ide.client.module.gadget.service.GadgetMetadata;
import org.exoplatform.ide.client.module.gadget.service.GadgetService;
import org.exoplatform.ide.client.module.gadget.service.TokenRequest;
import org.exoplatform.ide.client.module.gadget.service.TokenResponse;
import org.exoplatform.ide.client.module.gadget.service.event.GadgetMetadaRecievedEvent;
import org.exoplatform.ide.client.module.gadget.service.event.GadgetMetadaRecievedHandler;
import org.exoplatform.ide.client.module.gadget.service.event.SecurityTokenRecievedEvent;
import org.exoplatform.ide.client.module.gadget.service.event.SecurityTokenRecievedHandler;
import org.exoplatform.ide.client.operation.properties.event.ShowPropertiesEvent;
import org.exoplatform.ide.client.operation.properties.event.ShowPropertiesHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OperationPresenter implements ShowPropertiesHandler, EditorActiveFileChangedHandler, OutputHandler,
   PreviewFileHandler, GadgetMetadaRecievedHandler, SecurityTokenRecievedHandler,
   ConfigurationReceivedSuccessfullyHandler
{

   public interface Display
   {

      void showOutput();

      void showProperties(File file);

      void showPreview(String path);

      void closePreviewTab();

      void closePropertiesTab();

      void changeActiveFile(File file);

      void showGadget(GadgetMetadata metadata, ApplicationConfiguration applicationConfiguration);

      void closeGadgetPreviewTab();

   }

   private Display display;

   private HandlerManager eventBus;

   private Handlers handlers;

   private File activeFile;

   private ApplicationConfiguration applicationConfiguration;

   public OperationPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void bindDisplay(Display d)
   {
      display = d;

      handlers.addHandler(ShowPropertiesEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(OutputEvent.TYPE, this);
      handlers.addHandler(PreviewFileEvent.TYPE, this);
      handlers.addHandler(GadgetMetadaRecievedEvent.TYPE, this);
      handlers.addHandler(SecurityTokenRecievedEvent.TYPE, this);

   }

   public void onShowProperties(ShowPropertiesEvent event)
   {
      eventBus.fireEvent(new RestorePerspectiveEvent());
      display.showProperties(activeFile);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      display.closePreviewTab();
      display.closeGadgetPreviewTab();

      if (event.getFile() == null)
      {
         display.closePropertiesTab();
      }
      else
      {
         display.changeActiveFile(event.getFile());
      }
   }

   public void onOutput(OutputEvent event)
   {
      eventBus.fireEvent(new RestorePerspectiveEvent());
      display.showOutput();
   }

   public void onPreviewFile(PreviewFileEvent event)
   {
      display.closePreviewTab();
      display.closeGadgetPreviewTab();

      if (activeFile.isNewFile())
      {
         Dialogs.getInstance().showInfo("You should save the file!");
         return;
      }

      eventBus.fireEvent(new RestorePerspectiveEvent());

      if (MimeType.GOOGLE_GADGET.equals(activeFile.getContentType()))
      {
         previewGadget();
      }
      else if (MimeType.UWA_WIDGET.equals(activeFile.getContentType()))
      {
         previewUWAWidget(activeFile);
      }
      else if (MimeType.GROOVY_TEMPLATE.equals(activeFile.getContentType()))
      {
         previewGroovyTemplate(activeFile);
      }
      else
      {
         display.showPreview(activeFile.getHref());
      }
   }

   private void previewUWAWidget(File file)
   {
      String href = file.getHref();
      href = href.replace("jcr", "ideall/netvibes");
      display.showPreview(href);
   }
   
   private void previewGroovyTemplate(File file)
   {
      display.showPreview(applicationConfiguration.getContext() + "/ide/gtmpl/render?url=" + file.getHref());
   }

   private void previewGadget()
   {
      String owner = "root";
      String viewer = "root";
      Long moduleId = 0L;
      String container = "default";
      String domain = null;

      String href = activeFile.getHref();
      href = href.replace(applicationConfiguration.getContext(), applicationConfiguration.getPublicContext());

      TokenRequest tokenRequest = new TokenRequest(URL.encode(href), owner, viewer, moduleId, container, domain);
      GadgetService.getInstance().getSecurityToken(tokenRequest);
   }

   public void onSecurityTokenRecieved(SecurityTokenRecievedEvent securityTokenRecievedEvent)
   {
      TokenResponse tokenResponse = securityTokenRecievedEvent.getTokenResponse();
      GadgetService.getInstance().getGadgetMetadata(tokenResponse);
   }

   public void onMetadataRecieved(GadgetMetadaRecievedEvent event)
   {
      display.showGadget(event.getMetadata(), applicationConfiguration);
   }

   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      applicationConfiguration = event.getConfiguration();
   }

}
