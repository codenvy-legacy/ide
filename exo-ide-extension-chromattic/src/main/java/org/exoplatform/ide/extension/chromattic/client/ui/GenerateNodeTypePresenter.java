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
package org.exoplatform.ide.extension.chromattic.client.ui;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.chromattic.client.event.GenerateNodeTypeEvent;
import org.exoplatform.ide.extension.chromattic.client.event.GenerateNodeTypeHandler;
import org.exoplatform.ide.extension.chromattic.client.model.EnumNodeTypeFormat;
import org.exoplatform.ide.extension.chromattic.client.model.service.ChrommaticService;
import org.exoplatform.ide.extension.chromattic.client.model.service.event.NodeTypeGenerationResultReceivedEvent;
import org.exoplatform.ide.extension.chromattic.client.model.service.marshaller.GenerateNodeTypeResultUnmarshaller;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FileModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Presenter for generating new node type definition view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 6, 2010 $
 * 
 */
public class GenerateNodeTypePresenter implements GenerateNodeTypeHandler, EditorActiveFileChangedHandler,
   ViewClosedHandler
{

   interface Display extends IsView
   {
      /**
       * Get cancel button.
       * 
       * @return {@link HasClickHandlers} cancel button
       */
      HasClickHandlers getCancelButton();

      /**
       * Get generate button.
       * 
       * @return {@link HasClickHandlers} generate button
       */
      HasClickHandlers getGenerateButton();

      /**
       * Get node type definition field.
       * 
       * @return {@link HasValue} node type format field
       */
      HasValue<String> getNodeTypeFormat();

      /**
       * Set values for node type format field.
       * 
       * @param values
       */
      void setNodeTypeFormatValues(String[] values);
   }

   /**
    * Display.
    */
   private Display display;

   /**
    * Active file in editor.
    */
   private FileModel activeFile;

   /**
    * @param eventBus handler manager
    */
   public GenerateNodeTypePresenter()
   {
      IDE.addHandler(GenerateNodeTypeEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      new GeneratedNodeTypePreviewPresenter();
   }

   /**
    * Bind view with presenter.
    */
   public void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getGenerateButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doGenerateNodeType();
         }
      });

   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.event.GenerateNodeTypeHandler#onGenerateNodeType(org.exoplatform.ide.client.module.chromattic.event.GenerateNodeTypeEvent)
    */
   @Override
   public void onGenerateNodeType(GenerateNodeTypeEvent event)
   {
      if (activeFile == null)
         return;

      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         display.setNodeTypeFormatValues(EnumNodeTypeFormat.getValues());
      }

      IDE.getInstance().openView(display.asView());
   }

   /**
    * Generate node type definition.
    */
   private void doGenerateNodeType()
   {
      if (activeFile == null)
         return;
      EnumNodeTypeFormat nodeTypeFormat = EnumNodeTypeFormat.valueOf(display.getNodeTypeFormat().getValue());
      try
      {
         ChrommaticService.getInstance().generateNodeType(activeFile,
            VirtualFileSystem.getInstance().getInfo().getId(), nodeTypeFormat,
            new AsyncRequestCallback<StringBuilder>(new GenerateNodeTypeResultUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  IDE.fireEvent(new NodeTypeGenerationResultReceivedEvent(result));
                  closeView();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  NodeTypeGenerationResultReceivedEvent event =
                     new NodeTypeGenerationResultReceivedEvent(this.getPayload());
                  event.setException(exception);
                  IDE.fireEvent(event);
                  closeView();
               }
            });
      }
      catch (RequestException e)
      {
         NodeTypeGenerationResultReceivedEvent event = new NodeTypeGenerationResultReceivedEvent(null);
         event.setException(e);
         IDE.fireEvent(event);
         closeView();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

}
