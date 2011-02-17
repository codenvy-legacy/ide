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
package org.exoplatform.ide.client.operation;

import java.util.List;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.event.perspective.RestorePerspectiveEvent;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.module.development.event.PreviewFileEvent;
import org.exoplatform.ide.client.module.development.event.PreviewFileHandler;
import org.exoplatform.ide.client.operation.properties.event.ShowItemPropertiesEvent;
import org.exoplatform.ide.client.operation.properties.event.ShowItemPropertiesHandler;
import org.exoplatform.ide.extension.gadget.client.service.GadgetMetadata;
import org.exoplatform.ide.extension.gadget.client.service.GadgetMetadataCallback;
import org.exoplatform.ide.extension.gadget.client.service.GadgetService;
import org.exoplatform.ide.extension.gadget.client.service.TokenResponse;
import org.exoplatform.ide.extension.gadget.client.service.event.GadgetMetadaRecievedEvent;
import org.exoplatform.ide.extension.gadget.client.service.event.SecurityTokenRecievedEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OperationPresenter implements ShowItemPropertiesHandler, EditorActiveFileChangedHandler, OutputHandler,
   PreviewFileHandler, ConfigurationReceivedSuccessfullyHandler
{

   public interface Display
   {
      
      void showOutput();

      void showProperties(File file);

      void showPreview(String path);

      void closePreviewTab();

      void closePropertiesTab();

      void changeActiveFile(File file);
      
      List<String> getViewTypes();

   }

   private Display display;

   private HandlerManager eventBus;

   private Handlers handlers;

   private File activeFile;

   private IDEConfiguration applicationConfiguration;

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

      handlers.addHandler(ShowItemPropertiesEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(OutputEvent.TYPE, this);
      handlers.addHandler(PreviewFileEvent.TYPE, this);
//      handlers.addHandler(GadgetMetadaRecievedEvent.TYPE, this);
//      handlers.addHandler(SecurityTokenRecievedEvent.TYPE, this);

   }

   public void onShowItemProperties(ShowItemPropertiesEvent event)
   {
      eventBus.fireEvent(new RestorePerspectiveEvent());
      display.showProperties(activeFile);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      display.closePreviewTab();

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

      if (activeFile.isNewFile())
      {
         Dialogs.getInstance().showInfo("You should save the file!");
         return;
      }

      eventBus.fireEvent(new RestorePerspectiveEvent());


     if(MimeType.UWA_WIDGET.equals(activeFile.getContentType()))
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
      href = href.replace("jcr", "ide/netvibes");
      display.showPreview(href);
   }
   
   private void previewGroovyTemplate(File file)
   {
      display.showPreview(applicationConfiguration.getContext() + "/ide/gtmpl/render?url=" + file.getHref());
   }

   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      applicationConfiguration = event.getConfiguration();
   }

}
