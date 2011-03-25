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

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.event.perspective.RestorePerspectiveEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputHandler;
import org.exoplatform.ide.client.framework.ui.PreviewForm;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.module.development.event.PreviewFileEvent;
import org.exoplatform.ide.client.module.development.event.PreviewFileHandler;
import org.exoplatform.ide.client.operation.output.OutputForm;
import org.exoplatform.ide.client.operation.properties.PropertiesForm;
import org.exoplatform.ide.client.operation.properties.event.ShowItemPropertiesEvent;
import org.exoplatform.ide.client.operation.properties.event.ShowItemPropertiesHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OperationPresenter implements ShowItemPropertiesHandler, EditorActiveFileChangedHandler, OutputHandler,
   PreviewFileHandler, ViewClosedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private File activeFile;

   private PreviewForm previewForm;

   private PropertiesForm propertiesForm;

   private OutputForm outputForm;

   public OperationPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      handlers = new Handlers(eventBus);
      handlers.addHandler(ShowItemPropertiesEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(OutputEvent.TYPE, this);
      handlers.addHandler(PreviewFileEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void bindDisplay()
   {

      //      handlers.addHandler(GadgetMetadaRecievedEvent.TYPE, this);
      //      handlers.addHandler(SecurityTokenRecievedEvent.TYPE, this);

   }

   public void onShowItemProperties(ShowItemPropertiesEvent event)
   {
      eventBus.fireEvent(new RestorePerspectiveEvent());
      showProperies(activeFile);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      if (previewForm != null)
      {
         IDE.getInstance().closeView(PreviewForm.ID);
         previewForm = null;
      }

      if (event.getFile() == null)
      {
         IDE.getInstance().closeView(PropertiesForm.ID);
         propertiesForm = null;
      }
      else
      {
         if (propertiesForm != null)
         {
            propertiesForm.refreshProperties(event.getFile());
            propertiesForm.setViewVisible();
         }
         //         else
         //         {
         //            showProperies(event.getFile());
         //         }
      }
   }

   /**
    * @param event
    */
   private void showProperies(File file)
   {
      if (propertiesForm == null)
      {
         propertiesForm = new PropertiesForm();
         propertiesForm.refreshProperties(file);
         IDE.getInstance().openView(propertiesForm);
      }
      else
      {
         propertiesForm.refreshProperties(activeFile);
         propertiesForm.setViewVisible();
      }
   }

   public void onOutput(OutputEvent event)
   {
      eventBus.fireEvent(new RestorePerspectiveEvent());
      if (outputForm == null)
      {
         outputForm = new OutputForm(IDE.EVENT_BUS);
         IDE.getInstance().openView(outputForm);
      }
      else
      {
         outputForm.setViewVisible();
      }
   }

   public void onPreviewFile(PreviewFileEvent event)
   {
      if (previewForm != null)
      {
         IDE.getInstance().closeView(PreviewForm.ID);
         previewForm = null;
      }

      if (activeFile.isNewFile())
      {
         Dialogs.getInstance().showInfo("You should save the file!");
         return;
      }

      previewForm = new PreviewForm();
      previewForm.showPreview(activeFile.getHref());
      IDE.getInstance().openView(previewForm);
      eventBus.fireEvent(new RestorePerspectiveEvent());

   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() == previewForm)
         previewForm = null;
      if (event.getView() == propertiesForm)
         propertiesForm = null;

      if (event.getView() == outputForm)
         outputForm = null;
   }

}
